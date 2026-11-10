package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.customizacion.CustomizacionRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.customizacion.CustomizacionResponse;
import GESTOR_CINEMAR_CENTER.DEV.enums.EfectoFestivo;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.CustomizacionMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Customizacion;
import GESTOR_CINEMAR_CENTER.DEV.repository.CustomizacionRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.CustomizacionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import javax.imageio.ImageIO;

@Service("customizacionService")
public class CustomizacionServiceImpl implements CustomizacionService {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024;

    private final CustomizacionRepository customizacionRepository;
    private final CustomizacionMapper customizacionMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public CustomizacionServiceImpl(CustomizacionRepository customizacionRepository,
                                   CustomizacionMapper customizacionMapper) {
        this.customizacionRepository = customizacionRepository;
        this.customizacionMapper = customizacionMapper;
    }

    @Override
    public CustomizacionResponse obtener() {
        return customizacionMapper.toResponse(obtenerEntidad());
    }

    @Override
    public CustomizacionResponse guardar(CustomizacionRequest request) {
        Customizacion custom = obtenerEntidad();
        customizacionMapper.updateEntity(request, custom);
        custom.setEfectoFestivo(EfectoFestivo.normalizar(request.getEfectoFestivo()));
        // Asegurar que si viene una URL absoluta (ej: http://localhost:8080/uploads/...), guardamos la ruta relativa
        if (custom.getLogoUrl() != null && custom.getLogoUrl().contains("/uploads/")) {
            custom.setLogoUrl(custom.getLogoUrl().substring(custom.getLogoUrl().indexOf("/uploads/")));
        }
        if (custom.getFondoUrl() != null && custom.getFondoUrl().contains("/uploads/")) {
            custom.setFondoUrl(custom.getFondoUrl().substring(custom.getFondoUrl().indexOf("/uploads/")));
        }
        return customizacionMapper.toResponse(customizacionRepository.save(custom));
    }

    @Override
    public String guardarImagen(MultipartFile file, String tipo) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ReglaNegocioException("No se recibió ningún archivo");
        }

        String contentType = file.getContentType();
        boolean esVideo = "video/mp4".equals(contentType) || "video/webm".equals(contentType);
        boolean esImagen = "image/jpeg".equals(contentType)
                || "image/png".equals(contentType)
                || "image/webp".equals(contentType);

        if (esVideo && !"fondos".equals(tipo)) {
            throw new ReglaNegocioException("Los videos solo se aceptan como fondo de pantalla");
        }
        if (!esVideo && !esImagen) {
            throw new ReglaNegocioException(
                    "Tipo de archivo no permitido. Se aceptan JPEG, PNG, WEBP o video MP4/WEBM");
        }

        long maxSize = esVideo ? MAX_VIDEO_SIZE : MAX_IMAGE_SIZE;
        if (file.getSize() > maxSize) {
            String limite = esVideo ? "50MB" : "5MB";
            throw new ReglaNegocioException("El archivo supera el tamaño máximo permitido (" + limite + ")");
        }

        String fileName = UUID.randomUUID() + "." + extensionPara(contentType);
        Path uploadPath = Paths.get(uploadDir, tipo);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        if (esImagen) {
            try {
                resizeImageIfNeeded(filePath);
            } catch (Exception e) {
                System.err.println("No se pudo redimensionar la imagen: " + e.getMessage());
            }
        }

        String rutaRelativa = "/uploads/" + tipo + "/" + fileName;

        // Persisto la ruta relativa en la BD
        Customizacion custom = obtenerEntidad();
        if ("logos".equals(tipo)) {
            custom.setLogoUrl(rutaRelativa);
        } else if ("fondos".equals(tipo)) {
            custom.setFondoUrl(rutaRelativa);
        }
        customizacionRepository.save(custom);

        return rutaRelativa;
    }

    @Override
    public void eliminarImagen(String tipo) {
        Customizacion custom = customizacionRepository.findFirst();
        if (custom == null) {
            return;
        }
        String ruta = null;
        if ("logos".equals(tipo)) {
            ruta = custom.getLogoUrl();
        } else if ("fondos".equals(tipo)) {
            ruta = custom.getFondoUrl();
        }
        if (ruta != null && !ruta.isEmpty() && ruta.contains("/uploads/")) {
            try {
                Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
                String relative = ruta.substring(ruta.indexOf("/uploads/") + 9);
                Path filePath = base.resolve(relative).normalize();
                if (filePath.startsWith(base) && Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                System.err.println("No se pudo borrar el archivo: " + e.getMessage());
            }
        }
        if ("logos".equals(tipo)) {
            custom.setLogoUrl(null);
        } else if ("fondos".equals(tipo)) {
            custom.setFondoUrl(null);
        }
        customizacionRepository.save(custom);
    }

    private Customizacion obtenerEntidad() {
        Customizacion custom = customizacionRepository.findFirst();
        if (custom == null) {
            custom = new Customizacion();
            custom.setEfectoFestivo(EfectoFestivo.DEFAULT);
            custom = customizacionRepository.save(custom);
        } else if (custom.getEfectoFestivo() == null || custom.getEfectoFestivo().isBlank()) {
            custom.setEfectoFestivo(EfectoFestivo.DEFAULT);
            custom = customizacionRepository.save(custom);
        }
        return custom;
    }

    private void resizeImageIfNeeded(Path filePath) throws IOException {
        final int maxDim = 1200;
        BufferedImage img = ImageIO.read(filePath.toFile());
        if (img == null) {
            return;
        }
        int w = img.getWidth();
        int h = img.getHeight();
        if (w <= maxDim && h <= maxDim) {
            return;
        }
        double scale = Math.min((double) maxDim / w, (double) maxDim / h);
        int nw = (int) Math.round(w * scale);
        int nh = (int) Math.round(h * scale);
        BufferedImage resized = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, nw, nh, null);
        g.dispose();

        String ext = getFileExtension(filePath.getFileName().toString()).toLowerCase();
        if ("jpeg".equals(ext)) {
            ext = "jpg";
        }
        if (!ImageIO.write(resized, ext, filePath.toFile())) {
            ImageIO.write(resized, "jpg", filePath.toFile());
        }
    }

    private String extensionPara(String contentType) {
        if (contentType == null) {
            return "jpg";
        }
        switch (contentType) {
            case "image/png":
                return "png";
            case "image/webp":
                return "webp";
            case "video/mp4":
                return "mp4";
            case "video/webm":
                return "webm";
            default:
                return "jpg";
        }
    }

    private String getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx > 0 && idx < filename.length() - 1) {
            return filename.substring(idx + 1);
        }
        return "jpg";
    }
}
