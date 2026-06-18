package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.customizacion.CustomizacionRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.customizacion.CustomizacionResponse;
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

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

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
        return customizacionMapper.toResponse(customizacionRepository.save(custom));
    }

    @Override
    public String guardarImagen(MultipartFile file, String tipo) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ReglaNegocioException("No se recibió ningún archivo");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ReglaNegocioException("El archivo supera el tamaño máximo permitido (5MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null
                || (!contentType.equals("image/jpeg")
                && !contentType.equals("image/png")
                && !contentType.equals("image/webp"))) {
            throw new ReglaNegocioException("Tipo de archivo no permitido. Solo se aceptan JPEG, PNG o WEBP");
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir, tipo);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        try {
            resizeImageIfNeeded(filePath);
        } catch (Exception e) {
            System.err.println("No se pudo redimensionar la imagen: " + e.getMessage());
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
        if (ruta != null && !ruta.isEmpty() && ruta.startsWith("/uploads/")) {
            try {
                Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
                String relative = ruta.replace("/uploads/", "");
                Path filePath = base.resolve(relative).normalize();
                if (filePath.startsWith(base) && Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                System.err.println("No se pudo borrar el archivo: " + e.getMessage());
            }
            if ("logos".equals(tipo)) {
                custom.setLogoUrl(null);
            } else if ("fondos".equals(tipo)) {
                custom.setFondoUrl(null);
            }
            customizacionRepository.save(custom);
        }
    }

    private Customizacion obtenerEntidad() {
        Customizacion custom = customizacionRepository.findFirst();
        if (custom == null) {
            custom = new Customizacion();
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

    private String getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx > 0 && idx < filename.length() - 1) {
            return filename.substring(idx + 1);
        }
        return "jpg";
    }
}
