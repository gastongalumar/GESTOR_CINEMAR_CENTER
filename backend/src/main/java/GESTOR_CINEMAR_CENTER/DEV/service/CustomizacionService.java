package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.customizacion.CustomizacionRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.customizacion.CustomizacionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CustomizacionService {
    CustomizacionResponse obtener();
    CustomizacionResponse guardar(CustomizacionRequest request);
    String guardarImagen(MultipartFile file, String tipo) throws IOException;
    void eliminarImagen(String tipo);
}
