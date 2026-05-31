package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.ActualizarMetodoPagoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.CrearReservaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ReservaResponseDTO;
import DEV.model.*;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ValidacionTicketResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.enums.MetodoPago;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.PagoMapper;
import GESTOR_CINEMAR_CENTER.DEV.mapper.ReservaMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.*;
import GESTOR_CINEMAR_CENTER.DEV.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private static final List<EstadoReserva> ESTADOS_OCUPAN_ASIENTO = List.of(
            EstadoReserva.PENDIENTE,
            EstadoReserva.CONFIRMADA,
            EstadoReserva.VALIDADA
    );

    private final ReservaRepository reservaRepository;
    private final FuncionService funcionService;
   /* private final UsuarioRepository usuarioRepository;
    private final PagoRepository pagoRepository;*/
    private final SalaService salaService;
    private final AsientoService asientoService;
    private final ReservaMapper reservaMapper;
    private final PagoMapper pagoMapper;

    @Transactional
    public ReservaResponseDTO crear(CrearReservaRequestDTO request) {

        Usuario usuario = usuarioRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontrado("Usuario", request.getClienteId()));

        if (!(usuario instanceof Cliente cliente)) {
            throw new ReglaNegocioException("Solo los clientes pueden realizar reservas");
        }

        Funcion funcion = funcionService.obtenerFuncion(request.getFuncionId());

        String metodoPago = normalizarMetodoPago(request.getMetodoPago());

        List<String> etiquetasAsientos = request.getAsientosSeleccionados();

        if (etiquetasAsientos == null || etiquetasAsientos.isEmpty()) {
            throw new ReglaNegocioException("Debe seleccionar al menos un asiento");
        }

        salaService.asegurarAsientosDeSala(funcion.getSala());

        List<Long> idsOcupados = reservaRepository.findByFuncionAndEstadoReservaIn(funcion, ESTADOS_OCUPAN_ASIENTO)
                .stream()
                .flatMap(r -> r.getAsientos().stream())
                .map(Asiento::getId)
                .distinct()
                .toList();

        List<Asiento> asientosSeleccionados = asientoService.obtenerAsientosPorEtiquetas(funcion.getSala(), etiquetasAsientos);

        for (Asiento asiento : asientosSeleccionados) {
            if (idsOcupados.contains(asiento.getId())) {
                throw new ReglaNegocioException("El asiento " + asiento.getEtiqueta() + " ya no está disponible");
            }
        }

        Reserva reserva = reservaMapper.toEntity(request, cliente, funcion);

        reserva.setNumeroTicket("TK" + System.currentTimeMillis());
        reserva.setCodigoOR("OR-CMX-" + reserva.getNumeroTicket().substring(2));
        reserva.setFechaEmision(LocalDateTime.now());
        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        reserva.setMetodoPago(metodoPago);
        reserva.setAsientos(asientosSeleccionados);
        reserva.setMontoTotal(funcion.getPrecio() * asientosSeleccionados.size());

        reserva = reservaRepository.save(reserva);

        pagoRepository.save(new Pago(reserva, reserva.getMontoTotal(), metodoPago));

        return reservaMapper.toResponse(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorCliente(Long clienteId) {

        Usuario usuario = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontrado("Usuario", clienteId));

        if (!(usuario instanceof Cliente cliente)) {
            return List.of();
        }

        return reservaRepository.findByClienteOrderByFechaEmisionDesc(cliente)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Reserva findByTicketEntity(String numeroTicket) {
        return reservaRepository.findByNumeroTicket(numeroTicket)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Reserva con ticket " + numeroTicket + " no existe"));
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO buscarReservaPorTicket(String numeroTicket) {
        return reservaMapper.toResponse(findByTicketEntity(numeroTicket));
    }

    @Transactional(readOnly = true)
    public PagoResponseDTO buscarPagoPorTicket(String numeroTicket) {

        Reserva reserva = findByTicketEntity(numeroTicket);

        Pago pago = pagoRepository.findByReserva(reserva)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago", "ticket", numeroTicket));

        return pagoMapper.toResponse(pago);
    }

    @Transactional
    public ValidacionTicketResponseDTO validarTicket(String numeroTicket, boolean marcarUsado) {

        Reserva reserva = findByTicketEntity(numeroTicket);

        boolean marcarAhora = marcarUsado && reserva.getEstadoReserva() != EstadoReserva.VALIDADA;

        if (marcarAhora) {
            reserva.setEstadoReserva(EstadoReserva.VALIDADA);
            reserva.setFechaValidacion(LocalDateTime.now());
            reservaRepository.save(reserva);
        }

        return reservaMapper.toValidacionTicket(reserva, marcarAhora);
    }

    @Transactional
    public ReservaResponseDTO actualizarMetodoPago(String numeroTicket, ActualizarMetodoPagoRequestDTO request) {

        Reserva reserva = findByTicketEntity(numeroTicket);

        reserva.setMetodoPago(normalizarMetodoPago(request.getMetodoPago()));

        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    @Transactional
    public void cancelar(String numeroTicket) {

        Reserva reserva = findByTicketEntity(numeroTicket);

        reserva.setEstadoReserva(EstadoReserva.CANCELADA);

        reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<String> obtenerAsientosOcupados(Long funcionId) {

        Funcion funcion = funcionService.obtenerFuncion(funcionId);

        return reservaRepository.findByFuncionAndEstadoReservaIn(funcion, ESTADOS_OCUPAN_ASIENTO)
                .stream()
                .flatMap(reserva -> reserva.getAsientos().stream())
                .map(Asiento::getEtiqueta)
                .distinct()
                .toList();
    }

    public String normalizarMetodoPago(String metodoPago) {

        if (!MetodoPago.esValido(metodoPago)) {
            throw new ReglaNegocioException("Método de pago no permitido: " + metodoPago);
        }

        return MetodoPago.fromString(metodoPago).name();
    }
}
