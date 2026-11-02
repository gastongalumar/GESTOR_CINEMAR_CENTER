package GESTOR_CINEMAR_CENTER.DEV.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImagenesService {
    String guardarImagenPelicula(MultipartFile file);

    String guardarImagenPerfil(MultipartFile file);

    void eliminarImagen(String rutaImagen);
}
