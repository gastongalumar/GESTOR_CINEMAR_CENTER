package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.ActualizarPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.CrearPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaPageResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.PeliculaMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import GESTOR_CINEMAR_CENTER.DEV.repository.FuncionRepository;
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
import java.time.LocalDateTime;
import java.util.List;

@Service("peliculaService")
@RequiredArgsConstructor
public class PeliculaServiceImpl implements PeliculaService {

    private final PeliculaRepository peliculaRepository;
    private final FuncionRepository funcionRepository;
    private final PeliculaMapper peliculaMapper;
    private final ImagenesService imagenesService;

    /**
     * Obtiene la entidad Pelicula solo si está activa.
     * Lanza RecursoNoEncontradoException si no existe o está desactivada.
     */
    @Override
    public Pelicula obtenerPelicula(Long id) {
        return peliculaRepository.findByIdAndActivaTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Película", id));
    }

    @Override
    public List<PeliculaResponseDTO> listarPeliculas() {
        return peliculaMapper.toResponseList(peliculaRepository.findByActivaTrue());
    }

    @Override
    public List<PeliculaResponseDTO> listarPeliculasVigentes() {
        return peliculaMapper.toResponseList(
                peliculaRepository.findVigentesEnFecha(LocalDate.now()));
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

    /**
     * Valida que no exista otra película ACTIVA con el mismo nombre.
     * Las películas desactivadas no bloquean la creación.
     */
    private void validarNombreDisponible(String nombre) {
        if (peliculaRepository.existsByNombreAndActivaTrue(nombre)) {
            throw new ReglaNegocioException("Ya existe una película activa con ese nombre");
        }
    }

    private void validarFechas(LocalDate fechaEstreno, LocalDate fechaSalida) {
        if (!fechaSalida.isAfter(fechaEstreno)) {
            throw new ReglaNegocioException("La fecha de salida de cartelera debe ser al menos un día posterior a la fecha de estreno");
        }
    }

    /**
     * Valida que las nuevas fechas de la película no dejen funciones activas fuera del rango.
     * Si una función activa tiene horario antes de la nueva fechaEstreno o después de la nueva
     * fechaSalida, se lanza una excepción indicando cuál función genera el conflicto.
     */
    private void validarFechasConFunciones(Pelicula pelicula, LocalDate nuevaFechaEstreno, LocalDate nuevaFechaSalida) {
        List<Funcion> funciones = funcionRepository.findByActivaTrueAndPelicula(pelicula);

        for (Funcion funcion : funciones) {
            LocalDate fechaFuncion = funcion.getHorario().toLocalDate();

            if (fechaFuncion.isBefore(nuevaFechaEstreno)) {
                throw new ReglaNegocioException(
                        "La nueva fecha de estreno (" + nuevaFechaEstreno + ") es posterior a la función del "
                        + fechaFuncion + " en sala '" + funcion.getSala().getNombre() + "'. Primero eliminá o reprogramá esa función."
                );
            }

            if (fechaFuncion.isAfter(nuevaFechaSalida)) {
                throw new ReglaNegocioException(
                        "La nueva fecha de salida de cartelera (" + nuevaFechaSalida + ") es anterior a la función del "
                        + fechaFuncion + " en sala '" + funcion.getSala().getNombre() + "'. Primero eliminá o reprogramá esa función."
                );
            }
        }
    }

    @Override
    public PeliculaResponseDTO crear(CrearPeliculaRequestDTO request) {
        validarNombreDisponible(request.getNombre());
        validarFechas(request.getFechaEstreno(), request.getFechaSalida());

        Pelicula pelicula = peliculaMapper.toEntity(request);
        pelicula.setActiva(true);
        return peliculaMapper.toResponse(peliculaRepository.save(pelicula));
    }

    @Override
    public PeliculaResponseDTO actualizar(Long id, ActualizarPeliculaRequestDTO request) {
        Pelicula existente = obtenerPelicula(id);

        if (request.getNombre() != null && !existente.getNombre().equals(request.getNombre())) {
            validarNombreDisponible(request.getNombre());
        }

        LocalDate fechaEstreno = request.getFechaEstreno() != null ? request.getFechaEstreno()
                : existente.getFechaEstreno();
        LocalDate fechaSalida = request.getFechaSalida() != null ? request.getFechaSalida()
                : existente.getFechaSalida();

        // Validar que la fecha de estreno sea al menos mañana (mínimo 1 día de anticipación)
        if (fechaEstreno.isBefore(LocalDate.now().plusDays(1))) {
            throw new ReglaNegocioException(
                    "La fecha de estreno debe ser al menos 1 día en el futuro (a partir de " +
                    LocalDate.now().plusDays(1) + ")");
        }

        // Validar rango mínimo entre fechas
        validarFechas(fechaEstreno, fechaSalida);

        // Validar que las nuevas fechas no dejen funciones activas fuera del rango
        validarFechasConFunciones(existente, fechaEstreno, fechaSalida);

        peliculaMapper.actualizarEntity(request, existente);
        return peliculaMapper.toResponse(peliculaRepository.save(existente));
    }

    @Override
    public void eliminar(Long id) {
        Pelicula pelicula = obtenerPelicula(id);

        boolean tieneFuncionesFuturas = funcionRepository.existsByPeliculaActivaFutura(pelicula, LocalDateTime.now());
        if (tieneFuncionesFuturas) {
            throw new ReglaNegocioException(
                    "No se puede desactivar la película porque tiene funciones futuras activas asociadas");
        }

        pelicula.setActiva(false);
        peliculaRepository.save(pelicula);
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

    @Override
    public List<PeliculaResponseDTO> filtrarPorNombre(String nombre) {
        return peliculaMapper.toResponseList(peliculaRepository.findByNombreContainsIgnoreCase(nombre));
    }

    @Override
    public List<PeliculaResponseDTO> filtrarPorGenero(String genero) {
        return peliculaMapper.toResponseList(peliculaRepository.findByGeneroContainsIgnoreCase(genero));
    }

    @Override
    public List<PeliculaResponseDTO> filtrarVigentesPorNombre(String nombre) {
        return peliculaMapper.toResponseList(
                peliculaRepository.findVigentesPorNombre(nombre, LocalDate.now()));
    }

    @Override
    public List<PeliculaResponseDTO> filtrarVigentesPorGenero(String genero) {
        return peliculaMapper.toResponseList(
                peliculaRepository.findVigentesPorGenero(genero, LocalDate.now()));
    }

    @Override
    public List<PeliculaResponseDTO> filtrarPorFuncion(Long funcionId) {
        Funcion funcion = funcionRepository.findByIdAndActivaTrue(funcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Función", funcionId));
        return peliculaMapper.toResponseList(List.of(funcion.getPelicula()));
    }
}
