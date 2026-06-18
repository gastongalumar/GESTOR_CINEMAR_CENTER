package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.ActualizarHorarioFuncionRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionesPorRangoRequestDTO;
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
import GESTOR_CINEMAR_CENTER.DEV.service.AsientoService;
import GESTOR_CINEMAR_CENTER.DEV.service.FuncionService;
import GESTOR_CINEMAR_CENTER.DEV.service.PeliculaService;
import GESTOR_CINEMAR_CENTER.DEV.service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("funcionService")
@RequiredArgsConstructor
public class FuncionServiceImpl implements FuncionService {

    private final FuncionRepository funcionRepository;
    private final ReservaRepository reservaRepository;
    private final PeliculaService peliculaService;
    private final SalaService salaService;
    private final AsientoService asientoService;
    private final FuncionMapper funcionMapper;

    private static final int MAX_DURACION_PELICULA_MINUTOS = 600;

    private static final List<EstadoReserva> ESTADOS_RESERVA_ACTIVA = List.of(
            EstadoReserva.PENDIENTE,
            EstadoReserva.CONFIRMADA,
            EstadoReserva.VALIDADA
    );

    // Busca la funcion activa o tira 404
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

        LocalDateTime inicioNueva = request.getHorario();
        LocalDateTime finNueva = inicioNueva.plusMinutes(pelicula.getDuracionMinutos());

        validarFechaDentroDeCartelera(inicioNueva.toLocalDate(), pelicula);
        if (!finNueva.toLocalDate().equals(inicioNueva.toLocalDate())) {
            validarFechaDentroDeCartelera(finNueva.toLocalDate(), pelicula);
        }

        List<Funcion> funcionesMismaSala = obtenerFuncionesCandidatasSolapamiento(
                sala, inicioNueva, finNueva, null);

        validarSinSolapamiento(inicioNueva, finNueva, funcionesMismaSala);

        if (funcionRepository.existsBySalaAndHorarioAndActivaTrue(sala, request.getHorario())) {
            throw new ReglaNegocioException("Ya existe una función activa en esa sala a esa hora exacta");
        }

        Funcion funcion = funcionMapper.toEntity(request, sala, pelicula);
        funcion.setActiva(true);
        return funcionMapper.toResponse(funcionRepository.save(funcion));
    }

    @Override
    @Transactional
    public List<FuncionResponseDTO> crearFuncionesPorRango(CrearFuncionesPorRangoRequestDTO request) {
        Sala sala = salaService.obtenerSalaActiva(request.getSalaId());
        Pelicula pelicula = peliculaService.obtenerPelicula(request.getPeliculaId());

        validarRangoDentroDeCartelera(request.getFechaDesde(), request.getFechaHasta(), pelicula);

        List<LocalDateTime> horariosPropuestos = generarHorariosConsecutivos(
                request.getFechaDesde(),
                request.getFechaHasta(),
                request.getHoraInicio(),
                request.getHoraFin(),
                pelicula.getDuracionMinutos());

        if (horariosPropuestos.isEmpty()) {
            throw new ReglaNegocioException(
                    "No se puede generar ninguna función: la duración de la película no entra en el horario diario indicado");
        }

        LocalDateTime ahora = LocalDateTime.now();
        for (LocalDateTime horario : horariosPropuestos) {
            if (horario.isBefore(ahora)) {
                throw new ReglaNegocioException(
                        "El rango incluye funciones en el pasado (" + horario.toLocalDate() + " "
                                + horario.toLocalTime() + ")");
            }
        }

        int duracionMinutos = pelicula.getDuracionMinutos();
        LocalDateTime minInicio = horariosPropuestos.stream().min(LocalDateTime::compareTo).orElseThrow();
        LocalDateTime maxFin = horariosPropuestos.stream()
                .max(LocalDateTime::compareTo)
                .orElseThrow()
                .plusMinutes(duracionMinutos);

        List<Funcion> funcionesExistentes = obtenerFuncionesCandidatasSolapamiento(
                sala, minInicio, maxFin, null);
        for (LocalDateTime inicioPropuesto : horariosPropuestos) {
            LocalDateTime finPropuesto = inicioPropuesto.plusMinutes(duracionMinutos);
            validarSinSolapamiento(inicioPropuesto, finPropuesto, funcionesExistentes);
        }

        List<Funcion> funcionesNuevas = horariosPropuestos.stream()
                .map(horario -> {
                    Funcion funcion = new Funcion();
                    funcion.setSala(sala);
                    funcion.setPelicula(pelicula);
                    funcion.setHorario(horario);
                    funcion.setPrecio(request.getPrecio());
                    funcion.setActiva(true);
                    return funcion;
                })
                .toList();

        return funcionMapper.toResponseList(funcionRepository.saveAll(funcionesNuevas));
    }

    private void validarFechaDentroDeCartelera(LocalDate fecha, Pelicula pelicula) {
        if (fecha.isBefore(pelicula.getFechaEstreno()) || fecha.isAfter(pelicula.getFechaSalida())) {
            throw new ReglaNegocioException(
                    "El horario de la función debe estar dentro del rango de cartelera de la película ("
                            + pelicula.getFechaEstreno() + " a " + pelicula.getFechaSalida() + ")");
        }
    }

    private void validarRangoDentroDeCartelera(LocalDate fechaDesde, LocalDate fechaHasta, Pelicula pelicula) {
        if (fechaDesde.isBefore(pelicula.getFechaEstreno()) || fechaHasta.isAfter(pelicula.getFechaSalida())) {
            throw new ReglaNegocioException(
                    "El rango de fechas debe estar completamente dentro de la cartelera de la película ("
                            + pelicula.getFechaEstreno() + " a " + pelicula.getFechaSalida() + ")");
        }
    }

    private List<LocalDateTime> generarHorariosConsecutivos(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            LocalTime horaInicio,
            LocalTime horaFin,
            int duracionMinutos) {

        List<LocalDateTime> horarios = new ArrayList<>();
        LocalDate dia = fechaDesde;

        while (!dia.isAfter(fechaHasta)) {
            LocalDateTime inicio = LocalDateTime.of(dia, horaInicio);
            LocalDateTime limiteDia = LocalDateTime.of(dia, horaFin);

            while (true) {
                LocalDateTime fin = inicio.plusMinutes(duracionMinutos);
                if (fin.isAfter(limiteDia)) {
                    break;
                }
                horarios.add(inicio);
                inicio = fin;
            }

            dia = dia.plusDays(1);
        }

        return horarios;
    }

    private List<Funcion> obtenerFuncionesCandidatasSolapamiento(
            Sala sala,
            LocalDateTime inicio,
            LocalDateTime fin,
            Long excluirFuncionId) {

        LocalDateTime ventanaInicio = inicio.minusMinutes(MAX_DURACION_PELICULA_MINUTOS);
        List<Funcion> candidatas = funcionRepository.findByActivaTrueAndSalaAndHorarioBetween(
                sala, ventanaInicio, fin);

        if (excluirFuncionId == null) {
            return candidatas;
        }

        return candidatas.stream()
                .filter(f -> !f.getId().equals(excluirFuncionId))
                .toList();
    }

    private void validarSinSolapamiento(
            LocalDateTime inicioNueva,
            LocalDateTime finNueva,
            List<Funcion> funcionesExistentes) {

        for (Funcion funcionExistente : funcionesExistentes) {
            LocalDateTime inicioExistente = funcionExistente.getHorario();
            LocalDateTime finExistente = inicioExistente.plusMinutes(
                    funcionExistente.getPelicula().getDuracionMinutos());

            if (inicioNueva.isBefore(finExistente) && finNueva.isAfter(inicioExistente)) {
                throw new ReglaNegocioException(
                        "Conflicto de horario en la sala: ya existe la función de '"
                                + funcionExistente.getPelicula().getNombre()
                                + "' de " + formatearRangoHorario(inicioExistente, finExistente)
                                + ". No se creó ninguna función del rango.");
            }
        }
    }

    private String formatearRangoHorario(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio.toLocalDate().equals(fin.toLocalDate())) {
            return inicio.toLocalDate() + " " + inicio.toLocalTime() + " a " + fin.toLocalTime();
        }
        return inicio.toLocalDate() + " " + inicio.toLocalTime()
                + " a " + fin.toLocalDate() + " " + fin.toLocalTime();
    }

    @Override
    @Transactional
    public FuncionResponseDTO actualizarHorario(Long id, ActualizarHorarioFuncionRequestDTO request) {
        Funcion funcion = obtenerFuncion(id);

        if (reservaRepository.existsByFuncionAndEstadoReservaIn(funcion, ESTADOS_RESERVA_ACTIVA)) {
            throw new ReglaNegocioException(
                    "No se puede modificar el horario de la función porque tiene reservas activas asociadas");
        }

        Pelicula pelicula = funcion.getPelicula();
        Sala sala = funcion.getSala();
        LocalDateTime inicioNuevo = request.getHorario();
        LocalDateTime finNuevo = inicioNuevo.plusMinutes(pelicula.getDuracionMinutos());

        validarFechaDentroDeCartelera(inicioNuevo.toLocalDate(), pelicula);
        if (!finNuevo.toLocalDate().equals(inicioNuevo.toLocalDate())) {
            validarFechaDentroDeCartelera(finNuevo.toLocalDate(), pelicula);
        }

        if (!inicioNuevo.isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("El horario debe ser futuro");
        }

        List<Funcion> funcionesMismaSala = obtenerFuncionesCandidatasSolapamiento(
                sala, inicioNuevo, finNuevo, id);
        validarSinSolapamiento(inicioNuevo, finNuevo, funcionesMismaSala);

        boolean horarioOcupado = funcionRepository.findByActivaTrueAndSala(sala).stream()
                .anyMatch(f -> !f.getId().equals(id) && f.getHorario().equals(inicioNuevo));
        if (horarioOcupado) {
            throw new ReglaNegocioException("Ya existe una función activa en esa sala a esa hora exacta");
        }

        funcion.setHorario(inicioNuevo);
        return funcionMapper.toResponse(funcionRepository.save(funcion));
    }

    // No borramos de la base, solo la marcamos inactiva
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

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerAsientosLibres(Long funcionId) {
        Funcion funcion = obtenerFuncion(funcionId);
        salaService.asegurarAsientosDeSala(funcion.getSala());

        Set<String> ocupados = new HashSet<>(obtenerAsientosOcupados(funcionId));

        return asientoService.obtenerPorSala(funcion.getSala()).stream()
                .map(Asiento::getEtiqueta)
                .filter(etiqueta -> !ocupados.contains(etiqueta))
                .toList();
    }
}
