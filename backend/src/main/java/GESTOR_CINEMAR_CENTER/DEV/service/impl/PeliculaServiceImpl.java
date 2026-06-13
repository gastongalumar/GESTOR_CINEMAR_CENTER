package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.ActualizarPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.CrearPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaPageResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.PeliculaMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import GESTOR_CINEMAR_CENTER.DEV.repository.PeliculaRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.ImagenesService;
import GESTOR_CINEMAR_CENTER.DEV.service.PeliculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service("peliculaService")
@RequiredArgsConstructor
public class PeliculaServiceImpl implements PeliculaService {

    private final PeliculaRepository peliculaRepository;
    private final PeliculaMapper peliculaMapper;
    private final ImagenesService imagenesService;

    private List<Pelicula> listarPeliculasEntity() {
        return peliculaRepository.findAll();
    }

    @Override
    public List<PeliculaResponseDTO> listarPeliculas() {
        return peliculaMapper.toResponseList(listarPeliculasEntity());
    }

    private List<Pelicula> listarPeliculasEntityVigentes() {
        return peliculaRepository.findVigentesEnFecha(LocalDate.now());
    }

    @Override
    public List<PeliculaResponseDTO> listarPeliculasVigentes() {
        return peliculaMapper.toResponseList(listarPeliculasEntityVigentes());
    }

    @Override
    public Pelicula obtenerPelicula(Long id) {
        return peliculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Película", id));
    }

    @Override
    public PeliculaResponseDTO buscarPorId(Long id) {
        return peliculaMapper.toResponse(obtenerPelicula(id));
    }

    @Override
    public PeliculaPageResponse listarVigentesPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pelicula> result = peliculaRepository.findVigentesEnFecha(LocalDate.now(), pageable);
        return peliculaMapper.toPageResponse(result);
    }

    private void validarNombreDisponible(String nombre) {
        if (peliculaRepository.existsByNombre(nombre)) {
            throw new ReglaNegocioException("Ya existe una película con ese nombre");
        }
    }

    private void validarFechas(LocalDate fechaEstreno, LocalDate fechaSalida) {
        if (!fechaSalida.isAfter(fechaEstreno)) {
            throw new ReglaNegocioException("La fecha de salida de cartelera debe ser al menos un día posterior a la fecha de estreno");
        }
    }

    @Override
    public PeliculaResponseDTO crear(CrearPeliculaRequestDTO request) {
        validarNombreDisponible(request.getNombre());
        validarFechas(request.getFechaEstreno(), request.getFechaSalida());

        Pelicula pelicula = peliculaMapper.toEntity(request);
        return peliculaMapper.toResponse(peliculaRepository.save(pelicula));
    }

    @Override
    public PeliculaResponseDTO actualizar(Long id, ActualizarPeliculaRequestDTO request) {
        Pelicula existente = obtenerPelicula(id);

        if (request.getNombre() != null && !existente.getNombre().equals(request.getNombre())) {
            validarNombreDisponible(request.getNombre());
        }

        LocalDate fechaEstreno = request.getFechaEstreno() != null ? request.getFechaEstreno() : existente.getFechaEstreno();
        LocalDate fechaSalida = request.getFechaSalida() != null ? request.getFechaSalida() : existente.getFechaSalida();

        validarFechas(fechaEstreno, fechaSalida);

        peliculaMapper.actualizarEntity(request, existente);
        return peliculaMapper.toResponse(peliculaRepository.save(existente));
    }

    @Override
    public void eliminar(Long id) {
        peliculaRepository.delete(obtenerPelicula(id));
    }

    @Override
    public String guardarImagen(Long id, MultipartFile file) {
        Pelicula pelicula = obtenerPelicula(id);
        String ruta = imagenesService.guardarImagenPelicula(file);
        pelicula.setRutaImagen(ruta);
        peliculaRepository.save(pelicula);
        return ruta;
    }

    @Override
    public void eliminarImagenDePelicula(Long id) {
        Pelicula pelicula = obtenerPelicula(id);
        if (pelicula.getRutaImagen() != null) {
            imagenesService.eliminarImagen(pelicula.getRutaImagen());
            pelicula.setRutaImagen(null);
            peliculaRepository.save(pelicula);
        }
    }
}
