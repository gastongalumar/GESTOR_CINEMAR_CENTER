package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.funcion.FuncionResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.FuncionMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import GESTOR_CINEMAR_CENTER.DEV.repository.FuncionRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.ReservaRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.FuncionService;
import GESTOR_CINEMAR_CENTER.DEV.service.PeliculaService;
import GESTOR_CINEMAR_CENTER.DEV.service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service("funcionService")
@RequiredArgsConstructor
public class FuncionServiceImpl implements FuncionService {

    private final FuncionRepository funcionRepository;
    private final ReservaRepository reservaRepository;
    private final PeliculaService peliculaService;
    private final SalaService salaService;
    private final FuncionMapper funcionMapper;

    private static final List<EstadoReserva> ESTADOS_RESERVA_ACTIVA = List.of(
            EstadoReserva.PENDIENTE,
            EstadoReserva.CONFIRMADA,
            EstadoReserva.VALIDADA
    );

    /**
     * Obtiene la entidad Funcion solo si está activa.
     * Lanza RecursoNoEncontradoException si no existe o está desactivada.
     */
    @Override
    public Funcion obtenerFuncion(Long id) {
        return funcionRepository.findByIdAndActivaTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Función", id));
    }

    @Override
    public List<FuncionResponseDTO> listarTodas() {
        return funcionMapper.toResponseList(funcionRepository.findByActivaTrue());
    }

    @Override
    public List<FuncionResponseDTO> listarVigentes() {
        return funcionMapper.toResponseList(
                funcionRepository.findByActivaTrueAndHorarioAfter(LocalDateTime.now()));
    }

    @Override
    public List<FuncionResponseDTO> listarPorPelicula(Long peliculaId) {
        Pelicula pelicula = peliculaService.obtenerPelicula(peliculaId);
        return funcionMapper.toResponseList(funcionRepository.findByActivaTrueAndPelicula(pelicula));
    }

    @Override
    public FuncionResponseDTO buscarPorId(Long id) {
        return funcionMapper.toResponse(obtenerFuncion(id));
    }

    @Override
    @Transactional
    public FuncionResponseDTO crear(CrearFuncionRequestDTO request) {
        Sala sala = salaService.obtenerSalaActiva(request.getSalaId());
        Pelicula pelicula = peliculaService.obtenerPelicula(request.getPeliculaId());

        // 1. Validar que la fecha de la función esté dentro del rango de estreno de la
        // película
        LocalDate fechaFuncion = request.getHorario().toLocalDate();
        if (fechaFuncion.isBefore(pelicula.getFechaEstreno()) || fechaFuncion.isAfter(pelicula.getFechaSalida())) {
            throw new ReglaNegocioException(
                    "El horario de la función debe estar dentro del rango de cartelera de la película ("
                            + pelicula.getFechaEstreno() + " a " + pelicula.getFechaSalida() + ")");
        }

        // 2. Validar que no haya solapamiento de horarios en la misma sala para esa
        // fecha
        LocalDateTime inicioNueva = request.getHorario();
        LocalDateTime finNueva = inicioNueva.plusMinutes(pelicula.getDuracionMinutos());

        List<Funcion> funcionesMismaSala = funcionRepository.findByActivaTrueAndSalaAndHorarioBetween(
                sala,
                inicioNueva.toLocalDate().atStartOfDay(),
                inicioNueva.toLocalDate().plusDays(1).atStartOfDay());

        for (Funcion f : funcionesMismaSala) {
            LocalDateTime inicioExistente = f.getHorario();
            LocalDateTime finExistente = inicioExistente.plusMinutes(f.getPelicula().getDuracionMinutos());

            if (inicioNueva.isBefore(finExistente) && finNueva.isAfter(inicioExistente)) {
                throw new ReglaNegocioException(
                        "Conflicto de horario en la sala: ya existe la función de '" + f.getPelicula().getNombre()
                                + "' de " + inicioExistente.toLocalTime() + " a " + finExistente.toLocalTime());
            }
        }

        // 3. Verificar colisión exacta de horario (solo activas)
        if (funcionRepository.existsBySalaAndHorarioAndActivaTrue(sala, request.getHorario())) {
            throw new ReglaNegocioException("Ya existe una función activa en esa sala a esa hora exacta");
        }

        Funcion funcion = funcionMapper.toEntity(request, sala, pelicula);
        funcion.setActiva(true);
        return funcionMapper.toResponse(funcionRepository.save(funcion));
    }

    /**
     * Borrado lógico: marca la función como inactiva en lugar de eliminarla
     * físicamente.
     */
    @Override
    @Transactional
    public void eliminar(Long id) {
        Funcion funcion = obtenerFuncion(id);

        if (reservaRepository.existsByFuncionAndEstadoReservaIn(funcion, ESTADOS_RESERVA_ACTIVA)) {
            throw new ReglaNegocioException(
                    "No se puede desactivar la función porque tiene reservas activas asociadas");
        }

        funcion.setActiva(false);
        funcionRepository.save(funcion);
    }

    @Override
    public boolean existeFuncionFuturaPorSala(Sala sala) {
        return funcionRepository.findByActivaTrueAndSala(sala)
                .stream()
                .anyMatch(f -> f.getHorario().isAfter(LocalDateTime.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerAsientosOcupados(Long funcionId) {
        Funcion funcion = obtenerFuncion(funcionId);

        return reservaRepository.findByFuncionAndEstadoReservaIn(funcion, ESTADOS_RESERVA_ACTIVA)
                .stream()
                .flatMap(reserva -> reserva.getAsientos().stream())
                .map(Asiento::getEtiqueta)
                .distinct()
                .toList();
    }
}
