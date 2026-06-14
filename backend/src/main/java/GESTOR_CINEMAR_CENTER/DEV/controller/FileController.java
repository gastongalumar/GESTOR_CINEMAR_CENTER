package GESTOR_CINEMAR_CENTER.DEV.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/**")
    public ResponseEntity<byte[]> serveFile(HttpServletRequest request) {
        try {
            String uri = request.getRequestURI();
            String prefix = request.getContextPath() + "/uploads/";
            if (!uri.startsWith(prefix)) {
                return ResponseEntity.notFound().build();
            }

            String relativePath = uri.substring(prefix.length());
            if (relativePath.isBlank() || relativePath.contains("..")) {
                return ResponseEntity.badRequest().build();
            }

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(relativePath).normalize();

            if (!filePath.startsWith(uploadPath) || !Files.isRegularFile(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] content = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(content);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
