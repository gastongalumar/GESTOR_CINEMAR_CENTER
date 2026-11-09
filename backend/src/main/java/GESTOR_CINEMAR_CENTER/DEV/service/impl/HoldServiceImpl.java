package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.asiento.EstadoAsientosResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.hold.HoldResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.exception.ConflictoRecursoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.model.*;
import GESTOR_CINEMAR_CENTER.DEV.repository.AsientoHoldRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.FuncionRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.ReservaRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.AsientoService;
import GESTOR_CINEMAR_CENTER.DEV.service.FuncionService;
import GESTOR_CINEMAR_CENTER.DEV.service.HoldService;
import GESTOR_CINEMAR_CENTER.DEV.service.SalaService;
import GESTOR_CINEMAR_CENTER.DEV.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service("holdService")
@RequiredArgsConstructor
public class HoldServiceImpl implements HoldService {

    private static final int TTL_MINUTOS = 3;
    private static final List<EstadoReserva> ESTADOS_OCUPAN_ASIENTO = List.of(
            EstadoReserva.PENDIENTE,
            EstadoReserva.CONFIRMADA,
            EstadoReserva.VALIDADA
    );

    private final AsientoHoldRepository asientoHoldRepository;
    private final FuncionRepository funcionRepository;
    private final ReservaRepository reservaRepository;
    private final FuncionService funcionService;
    private final AsientoService asientoService;
    private final SalaService salaService;
    private final UsuarioService usuarioService;

    @Override
    @Transactional
    public HoldResponseDTO crearHold(Long funcionId, String etiqueta, String email) {
        Cliente cliente = obtenerCliente(email);
        Funcion funcion = funcionRepository.findByIdAndActivaTrueForUpdate(funcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Función", funcionId));

        if (!funcion.getHorario().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("No se pueden seleccionar asientos de funciones que ya comenzaron");
        }

        salaService.asegurarAsientosDeSala(funcion.getSala());
        String etiquetaNormalizada = normalizarEtiqueta(etiqueta);
        Asiento asiento = asientoService.obtenerAsientosPorEtiquetas(
                funcion.getSala(),
                List.of(etiquetaNormalizada)
        ).getFirst();

        boolean reservado = reservaRepository
                .findByFuncionAndEstadoReservaIn(funcion, ESTADOS_OCUPAN_ASIENTO)
                .stream()
                .flatMap(reserva -> reserva.getAsientos().stream())
                .anyMatch(asientoReservado -> asientoReservado.getId().equals(asiento.getId()));
        if (reservado) {
            throw new ConflictoRecursoException("El asiento " + etiquetaNormalizada + " ya está reservado");
        }

        LocalDateTime ahora = LocalDateTime.now();
        AsientoHold hold = asientoHoldRepository.findByFuncionAndAsiento(funcion, asiento).orElse(null);
        if (hold != null && hold.getExpiresAt().isAfter(ahora) && !hold.getCliente().getId().equals(cliente.getId())) {
            throw new ConflictoRecursoException(
                    "El asiento " + etiquetaNormalizada + " está siendo seleccionado por otro usuario"
            );
        }

        if (hold == null) {
            hold = new AsientoHold(null, funcion, asiento, cliente, ahora.plusMinutes(TTL_MINUTOS));
        } else {
            hold.setCliente(cliente);
            hold.setExpiresAt(ahora.plusMinutes(TTL_MINUTOS));
        }

        try {
            hold = asientoHoldRepository.saveAndFlush(hold);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictoRecursoException("El asiento " + etiquetaNormalizada + " ya no está disponible");
        }

        return new HoldResponseDTO(hold.getId(), funcionId, etiquetaNormalizada, hold.getExpiresAt());
    }

    @Override
    @Transactional
    public void liberarHold(Long funcionId, String etiqueta, String email) {
        Cliente cliente = obtenerCliente(email);
        Funcion funcion = funcionService.obtenerFuncion(funcionId);
        Asiento asiento = asientoService.obtenerAsientosPorEtiquetas(
                funcion.getSala(),
                List.of(normalizarEtiqueta(etiqueta))
        ).getFirst();
        asientoHoldRepository.deleteByFuncionAndAsientoAndCliente(funcion, asiento, cliente);
    }

    @Override
    @Transactional
    public void liberarTodosDelUsuario(Long funcionId, String email) {
        Cliente cliente = obtenerCliente(email);
        Funcion funcion = funcionService.obtenerFuncion(funcionId);
        asientoHoldRepository.deleteByFuncionAndCliente(funcion, cliente);
    }

    @Override
    @Transactional
    public void renovarHoldsDelUsuario(Long funcionId, String email) {
        Usuario usuario = usuarioService.findByEmail(email.trim().toLowerCase(Locale.ROOT));
        if (!(usuario instanceof Cliente cliente)) return;

        Funcion funcion = funcionService.obtenerFuncion(funcionId);
        LocalDateTime ahora = LocalDateTime.now();
        List<AsientoHold> holds = asientoHoldRepository
                .findByFuncionAndClienteAndExpiresAtAfter(funcion, cliente, ahora);
        LocalDateTime vencimiento = ahora.plusMinutes(TTL_MINUTOS);
        holds.forEach(hold -> hold.setExpiresAt(vencimiento));
        asientoHoldRepository.saveAll(holds);
    }

    @Override
    @Transactional
    public EstadoAsientosResponseDTO obtenerEstadoAsientos(Long funcionId, String email) {
        Funcion funcion = funcionService.obtenerFuncion(funcionId);
        renovarHoldsDelUsuario(funcionId, email);

        LocalDateTime ahora = LocalDateTime.now();
        Set<String> ocupados = Set.copyOf(funcionService.obtenerAsientosOcupados(funcionId));
        Usuario usuario = usuarioService.findByEmail(email.trim().toLowerCase(Locale.ROOT));
        Long clienteId = usuario instanceof Cliente cliente ? cliente.getId() : null;

        List<AsientoHold> vigentes = asientoHoldRepository.findVigentesByFuncion(funcion, ahora);
        List<String> misBloqueados = vigentes.stream()
                .filter(hold -> clienteId != null && hold.getCliente().getId().equals(clienteId))
                .map(hold -> hold.getAsiento().getEtiqueta())
                .distinct()
                .sorted()
                .toList();
        List<String> bloqueados = vigentes.stream()
                .filter(hold -> clienteId == null || !hold.getCliente().getId().equals(clienteId))
                .map(hold -> hold.getAsiento().getEtiqueta())
                .filter(etiqueta -> !ocupados.contains(etiqueta))
                .distinct()
                .sorted()
                .toList();

        return new EstadoAsientosResponseDTO(ocupados.stream().sorted().toList(), bloqueados, misBloqueados);
    }

    @Override
    @Transactional(readOnly = true)
    public void validarSinHoldsAjenos(Funcion funcion, List<Asiento> asientos, Long clienteId) {
        Set<Long> asientoIds = asientos.stream().map(Asiento::getId).collect(java.util.stream.Collectors.toSet());
        LocalDateTime ahora = LocalDateTime.now();
        asientoHoldRepository.findVigentesByFuncion(funcion, ahora).stream()
                .filter(hold -> asientoIds.contains(hold.getAsiento().getId()))
                .filter(hold -> !hold.getCliente().getId().equals(clienteId))
                .findFirst()
                .ifPresent(hold -> {
                    throw new ConflictoRecursoException(
                            "El asiento " + hold.getAsiento().getEtiqueta()
                                    + " está siendo seleccionado por otro usuario"
                    );
                });
    }

    @Override
    @Transactional
    public void eliminarHoldsDeAsientos(Funcion funcion, List<Asiento> asientos) {
        if (!asientos.isEmpty()) asientoHoldRepository.deleteByFuncionAndAsientoIn(funcion, asientos);
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 30_000)
    public void limpiarExpirados() {
        asientoHoldRepository.deleteExpirados(LocalDateTime.now());
    }

    private Cliente obtenerCliente(String email) {
        Usuario usuario = usuarioService.findByEmail(email.trim().toLowerCase(Locale.ROOT));
        if (!(usuario instanceof Cliente cliente)) {
            throw new ReglaNegocioException("Solo los clientes pueden bloquear asientos temporalmente");
        }
        return cliente;
    }

    private String normalizarEtiqueta(String etiqueta) {
        return etiqueta.trim().toUpperCase(Locale.ROOT);
    }
}
