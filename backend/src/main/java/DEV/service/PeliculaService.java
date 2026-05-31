package DEV.service;

import DEV.dto.request.pelicula.ActualizarPeliculaRequestDTO;
import DEV.dto.request.pelicula.CrearPeliculaRequestDTO;
import DEV.dto.response.pelicula.PeliculaPageResponse;
import DEV.dto.response.pelicula.PeliculaResponseDTO;
import DEV.exception.RecursoNoEncontradoException;
import DEV.exception.ReglaNegocioException;
import DEV.mapper.PeliculaMapper;
import DEV.model.Pelicula;
import DEV.repository.PeliculaRepository;
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
    private final ImagenesService imagenesService;


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
                .orElseThrow(() -> new RecursoNoEncontradoException("Película: " + id));
    }

    public PeliculaResponseDTO buscarPorId(Long id) {
        return peliculaMapper.toResponse(obtenerPelicula(id));
    }

    public PeliculaPageResponse listarVigentesPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pelicula> result = peliculaRepository.findVigentesEnFecha(LocalDate.now(), pageable);
        return peliculaMapper.toPageResponse(result);
    }


    public void validarNombreDisponible(String nombre) {
        if (peliculaRepository.existsByNombre(nombre)) {
            throw new ReglaNegocioException("Ya existe una película con ese nombre");
        }
    }

    public void validarFechas(LocalDate fechaEstreno, LocalDate fechaSalida) {
        if (!fechaSalida.isAfter(fechaEstreno)) {
            throw new ReglaNegocioException("La fecha de salida de cartelera debe ser al menos un día posterior a la fecha de estreno");
        }
    }

    public PeliculaResponseDTO crear(CrearPeliculaRequestDTO request) {

        validarNombreDisponible(request.getNombre());
        validarFechas(request.getFechaEstreno(), request.getFechaSalida());

        Pelicula pelicula = peliculaMapper.toEntity(request);

        return peliculaMapper.toResponse(peliculaRepository.save(pelicula));
    }



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

    public void eliminarImagenDePelicula(Long id) {

        Pelicula pelicula = obtenerPelicula(id);

        imagenesService.eliminarImagen(pelicula.getRutaImagen());

        pelicula.setRutaImagen(null);

        peliculaRepository.save(pelicula);
    }

    public String guardarImagen(MultipartFile file) {
        return imagenesService.guardarImagenPelicula(file);
    }

    public void eliminar(Long id) {
        peliculaRepository.delete(obtenerPelicula(id));
    }
}