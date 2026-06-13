package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.ActualizarMetodoPagoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.CrearReservaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ReservaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ValidacionTicketResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.enums.MetodoPagoHelper;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.PagoMapper;
import GESTOR_CINEMAR_CENTER.DEV.mapper.ReservaMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.*;
import GESTOR_CINEMAR_CENTER.DEV.repository.ReservaRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("reservaService")
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final List<EstadoReserva> ESTADOS_OCUPAN_ASIENTO = List.of(
            EstadoReserva.PENDIENTE,
            EstadoReserva.CONFIRMADA,
            EstadoReserva.VALIDADA
    );

    private final ReservaRepository reservaRepository;
    private final FuncionService funcionService;
    private final PagoService pagoService;
    private final UsuarioService usuarioService;
    private final SalaService salaService;
    private final AsientoService asientoService;
    private final ReservaMapper reservaMapper;
    private final PagoMapper pagoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarTodasReservas() {
        return reservaRepository.findAll()
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> filtrarReservasPorFuncion(Long funcionId) {
        Funcion funcion = funcionService.obtenerFuncion(funcionId);
        return reservaRepository.findByFuncion(funcion)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> filtrarReservasPorSala(Long salaId) {
        return reservaRepository.findBySalaId(salaId)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> filtrarReservasPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return reservaRepository.findByFechaEmisionBetween(inicio, fin)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReservaResponseDTO crear(CrearReservaRequestDTO request) {
        Usuario usuario = usuarioService.findById(request.getClienteId());

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
        pagoService.crearPago(reserva, reserva.getMontoTotal(), reserva.getMetodoPago());

        return reservaMapper.toResponse(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorCliente(Long clienteId) {
        Usuario usuario = usuarioService.findById(clienteId);

        if (!(usuario instanceof Cliente cliente)) {
            return List.of();
        }

        return reservaRepository.findByClienteOrderByFechaEmisionDesc(cliente)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarReservasPorEmail(String email) {
        Usuario usuario = usuarioService.findByEmail(email);

        if (!(usuario instanceof Cliente cliente)) {
            return List.of();
        }

        return reservaRepository.findByClienteOrderByFechaEmisionDesc(cliente)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva findByTicketEntity(String numeroTicket) {
        return reservaRepository.findByNumeroTicket(numeroTicket)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Reserva con ticket " + numeroTicket + " no existe"));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO buscarReservaPorTicket(String numeroTicket) {
        return reservaMapper.toResponse(findByTicketEntity(numeroTicket));
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO buscarPagoPorTicket(String numeroTicket) {
        Reserva reserva = findByTicketEntity(numeroTicket);
        Pago pago = pagoService.buscarPagoPorReserva(reserva);
        return pagoMapper.toDTO(pago);
    }

    @Override
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

    @Override
    @Transactional
    public ReservaResponseDTO actualizarMetodoPago(String numeroTicket, ActualizarMetodoPagoRequestDTO request) {
        Reserva reserva = findByTicketEntity(numeroTicket);
        reserva.setMetodoPago(normalizarMetodoPago(request.getMetodoPago()));
        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    @Override
    @Transactional
    public void cancelar(String numeroTicket) {
        Reserva reserva = findByTicketEntity(numeroTicket);
        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Override
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



    private String normalizarMetodoPago(String metodoPago) {
        if (!MetodoPagoHelper.esValido(metodoPago)) {
            throw new ReglaNegocioException("Método de pago no permitido: " + metodoPago);
        }

        return MetodoPagoHelper.fromString(metodoPago).name();
    }
}
