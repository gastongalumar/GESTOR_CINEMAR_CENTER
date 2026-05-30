package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.ActualizarPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.CrearPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaPageResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontrado;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.PeliculaMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import GESTOR_CINEMAR_CENTER.DEV.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor

public class PeliculaService {

    private final PeliculaRepository peliculaRepository;
    private final PeliculaMapper peliculaMapper;


    public List<Pelicula> listarPeliculasEntity(){
        return peliculaRepository.findAll();
    }
    public List<PeliculaResponseDTO> listarPeliculas() {
        return peliculaMapper.toResponseList(listarPeliculasEntity());
    }

    public List<Pelicula> listarPeliculasEntityVigentes() {
        return peliculaRepository.findVigentesEnFecha(LocalDate.now());
    }

    public List<PeliculaResponseDTO> listarPeliculasVigentes(){
        return peliculaMapper.toResponseList(listarPeliculasEntityVigentes());
    }


    public Pelicula obtenerPelicula(Long id) {
        return peliculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontrado("Película: " + id));
    }

    public PeliculaResponseDTO buscarPorId(Long id) {
        return peliculaMapper.toResponse(obtenerPelicula(id));
    }
}