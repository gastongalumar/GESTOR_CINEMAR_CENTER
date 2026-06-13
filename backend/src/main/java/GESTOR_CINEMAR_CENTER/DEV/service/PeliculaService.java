package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.ActualizarPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.CrearPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaPageResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PeliculaService {
    List<PeliculaResponseDTO> listarPeliculas();

    List<PeliculaResponseDTO> listarPeliculasVigentes();

    Pelicula obtenerPelicula(Long id);

    PeliculaResponseDTO buscarPorId(Long id);

    PeliculaPageResponse listarVigentesPaginado(int page, int size);

    PeliculaResponseDTO crear(CrearPeliculaRequestDTO request);

    PeliculaResponseDTO actualizar(Long id, ActualizarPeliculaRequestDTO request);

    void eliminar(Long id);

    String guardarImagen(Long id, MultipartFile file);

    void eliminarImagenDePelicula(Long id);

    // Métodos para filtrado de películas
    List<PeliculaResponseDTO> filtrarPorNombre(String nombre);

    List<PeliculaResponseDTO> filtrarPorGenero(String genero);

    List<PeliculaResponseDTO> filtrarVigentesPorNombre(String nombre);

    List<PeliculaResponseDTO> filtrarVigentesPorGenero(String genero);

    List<PeliculaResponseDTO> filtrarPorFuncion(Long funcionId);
}
