package DEV.service;

import DEV.exception.GuardadoImagenException;
import DEV.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class ImagenesService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final List<String> ALLOWED_EXT = List.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String guardarImagenPelicula(MultipartFile file) {

        validarArchivo(file);

        try {

            String ext = getFileExtension(
                    file.getOriginalFilename()
            ).toLowerCase();

            String fileName = UUID.randomUUID() + "." + ext;

            Path base = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize();

            Path uploadPath = base.resolve("peliculas");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath
                    .resolve(fileName)
                    .normalize();

            if (!filePath.startsWith(base)) {
                throw new ReglaNegocioException(
                        "Ruta de destino inválida"
                );
            }

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            resizeImageIfNeeded(filePath);

            return "/uploads/peliculas/" + fileName;

        } catch (IOException e) {

            throw new GuardadoImagenException(
                    "Error al guardar la imagen"
            );
        }
    }

    public void eliminarImagen(String rutaImagen) {

        if (rutaImagen == null
                || rutaImagen.isBlank()
                || !rutaImagen.startsWith("/uploads/")) {
            return;
        }

        try {

            Path base = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize();

            String filename =
                    rutaImagen.substring(
                            "/uploads/peliculas/".length()
                    );

            Path filePath = base
                    .resolve("peliculas")
                    .resolve(filename)
                    .normalize();

            if (filePath.startsWith(base)
                    && Files.exists(filePath)) {

                Files.delete(filePath);
            }

        } catch (IOException e) {

            throw new GuardadoImagenException(
                    "Error al eliminar la imagen"
            );
        }
    }

    private void validarArchivo(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ReglaNegocioException(
                    "No se recibió ningún archivo"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ReglaNegocioException(
                    "El archivo supera el tamaño máximo permitido (5MB)"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || (!contentType.equals("image/jpeg")
                && !contentType.equals("image/png")
                && !contentType.equals("image/webp"))) {

            throw new ReglaNegocioException(
                    "Tipo de archivo no permitido. Solo se aceptan JPEG, PNG o WEBP"
            );
        }

        String ext = getFileExtension(
                file.getOriginalFilename()
        ).toLowerCase();

        if (!ALLOWED_EXT.contains(ext)) {
            throw new ReglaNegocioException(
                    "Extensión no permitida: " + ext
            );
        }
    }

    private void resizeImageIfNeeded(Path filePath) {

        try {

            final int maxDim = 1200;

            BufferedImage img =
                    ImageIO.read(filePath.toFile());

            if (img == null) {
                return;
            }

            int width = img.getWidth();
            int height = img.getHeight();

            if (width <= maxDim
                    && height <= maxDim) {
                return;
            }

            double scale = Math.min(
                    (double) maxDim / width,
                    (double) maxDim / height
            );

            int newWidth =
                    (int) Math.round(width * scale);

            int newHeight =
                    (int) Math.round(height * scale);

            BufferedImage resized =
                    new BufferedImage(
                            newWidth,
                            newHeight,
                            BufferedImage.TYPE_INT_RGB
                    );

            Graphics2D graphics =
                    resized.createGraphics();

            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );

            graphics.drawImage(
                    img,
                    0,
                    0,
                    newWidth,
                    newHeight,
                    null
            );

            graphics.dispose();

            String ext =
                    getFileExtension(
                            filePath.getFileName().toString()
                    ).toLowerCase();

            if ("jpeg".equals(ext)) {
                ext = "jpg";
            }

            if (!ImageIO.write(
                    resized,
                    ext,
                    filePath.toFile())) {

                ImageIO.write(
                        resized,
                        "jpg",
                        filePath.toFile()
                );
            }

        } catch (IOException e) {

            throw new GuardadoImagenException(
                    "Error al procesar la imagen"
            );
        }
    }

    private String getFileExtension(String filename) {

        if (filename == null) {
            return "jpg";
        }

        int idx = filename.lastIndexOf('.');

        if (idx > 0
                && idx < filename.length() - 1) {

            return filename.substring(idx + 1);
        }

        return "jpg";
    }
}