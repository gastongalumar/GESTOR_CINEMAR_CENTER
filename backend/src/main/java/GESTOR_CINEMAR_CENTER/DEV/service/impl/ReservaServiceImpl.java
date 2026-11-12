package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.ActualizarMetodoPagoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.ComboSeleccionadoDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.CrearReservaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ReservaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ValidacionTicketResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.enums.MetodoPagoHelper;
import GESTOR_CINEMAR_CENTER.DEV.exception.ConflictoRecursoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.PagoMapper;
import GESTOR_CINEMAR_CENTER.DEV.mapper.ReservaMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.*;
import GESTOR_CINEMAR_CENTER.DEV.repository.CandyComboRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.FuncionRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.ReservaRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("reservaService")
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final List<EstadoReserva> ESTADOS_OCUPAN_ASIENTO = List.of(
            EstadoReserva.PENDIENTE,
            EstadoReserva.CONFIRMADA,
            EstadoReserva.VALIDADA
    );

    private final ReservaRepository reservaRepository;
    private final FuncionRepository funcionRepository;
    private final FuncionService funcionService;
    private final PagoService pagoService;
    private final UsuarioService usuarioService;
    private final SalaService salaService;
    private final AsientoService asientoService;
    private final ReservaMapper reservaMapper;
    private final PagoMapper pagoMapper;
    private final HoldService holdService;
    private final CandyComboRepository candyComboRepository;
    private final CodigoDescuentoService codigoDescuentoService;

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
        validarRangoFechas(inicio, fin);
        return reservaRepository.findByFechaEmisionBetween(inicio, fin)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReservaResponseDTO crear(CrearReservaRequestDTO request, String emailAutenticado) {
        Usuario usuario = usuarioService.findByEmail(emailAutenticado.trim().toLowerCase());

        if (!(usuario instanceof Cliente cliente)) {
            throw new ReglaNegocioException("Solo los clientes pueden realizar reservas");
        }

        Funcion funcion = funcionRepository.findByIdAndActivaTrueForUpdate(request.getFuncionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Función", request.getFuncionId()));

        if (!funcion.getHorario().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("No se pueden reservar funciones que ya comenzaron o finalizaron");
        }

        String metodoPago = normalizarMetodoPago(request.getMetodoPago());
        List<String> etiquetasAsientos = request.getAsientosSeleccionados();

        if (etiquetasAsientos == null || etiquetasAsientos.isEmpty()) {
            throw new ReglaNegocioException("Debe seleccionar al menos un asiento");
        }

        validarAsientosSinDuplicados(etiquetasAsientos);

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
                throw new ConflictoRecursoException("El asiento " + asiento.getEtiqueta() + " ya no está disponible");
            }
        }

        holdService.validarSinHoldsAjenos(funcion, asientosSeleccionados, cliente.getId());

        Reserva reserva = reservaMapper.toEntity(request, cliente, funcion);

        reserva.setNumeroTicket("TK" + System.currentTimeMillis());
        reserva.setCodigoOR("OR-CMX-" + reserva.getNumeroTicket().substring(2));
        reserva.setFechaEmision(LocalDateTime.now());
        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        reserva.setMetodoPago(metodoPago);
        reserva.setAsientos(asientosSeleccionados);
        reserva.setPagada(false);

        double subtotalEntradas = funcion.getPrecio() * asientosSeleccionados.size();
        List<ReservaComboItem> itemsCombo = armarCombos(request.getCombos(), reserva);
        double subtotalCandy = itemsCombo.stream()
                .mapToDouble(i -> i.getPrecioUnitario() * i.getCantidad())
                .sum();
        double subtotal = subtotalEntradas + subtotalCandy;

        CodigoDescuento codigoAplicado = null;
        double descuento = 0;
        if (request.getCodigoDescuento() != null && !request.getCodigoDescuento().isBlank()) {
            codigoAplicado = codigoDescuentoService.consumirSiValido(request.getCodigoDescuento(), subtotal);
            descuento = codigoDescuentoService.calcularDescuento(codigoAplicado, subtotal);
        }

        double total = Math.max(0, subtotal - descuento);
        reserva.setSubtotalEntradas(subtotalEntradas);
        reserva.setSubtotalCandy(subtotalCandy);
        reserva.setDescuentoMonto(descuento);
        reserva.setCodigoDescuentoAplicado(codigoAplicado == null ? null : codigoAplicado.getCodigo());
        reserva.setMontoTotal(total);
        reserva.getCombos().clear();
        reserva.getCombos().addAll(itemsCombo);

        try {
            reserva = reservaRepository.save(reserva);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ConflictoRecursoException("Uno o más asientos ya no están disponibles");
        }

        holdService.eliminarHoldsDeAsientos(funcion, asientosSeleccionados);
        pagoService.crearPago(reserva, reserva.getMontoTotal(), reserva.getMetodoPago());

        return reservaMapper.toResponse(reserva);
    }

    private List<ReservaComboItem> armarCombos(List<ComboSeleccionadoDTO> seleccionados, Reserva reserva) {
        if (seleccionados == null || seleccionados.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Integer> cantidades = new HashMap<>();
        for (ComboSeleccionadoDTO sel : seleccionados) {
            if (sel.getComboId() == null || sel.getCantidad() == null || sel.getCantidad() < 1) {
                throw new ReglaNegocioException("Combo inválido en la reserva");
            }
            cantidades.merge(sel.getComboId(), sel.getCantidad(), Integer::sum);
        }

        List<ReservaComboItem> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : cantidades.entrySet()) {
            CandyCombo combo = candyComboRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Combo candy", entry.getKey()));
            if (!Boolean.TRUE.equals(combo.getActivo())) {
                throw new ReglaNegocioException("El combo \"" + combo.getNombre() + "\" no está disponible");
            }
            ReservaComboItem item = new ReservaComboItem();
            item.setReserva(reserva);
            item.setCombo(combo);
            item.setCantidad(entry.getValue());
            item.setPrecioUnitario(combo.getPrecio());
            items.add(item);
        }
        return items;
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
        Usuario usuario = usuarioService.findByEmail(email.trim().toLowerCase());

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
    public Reserva obtenerTicketSiAutorizado(String numeroTicket, String emailAutenticado, boolean esStaff) {
        Reserva reserva = findByTicketEntity(numeroTicket);
        asegurarDuenoOStaff(reserva, emailAutenticado, esStaff);
        return reserva;
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO buscarReservaPorTicket(String numeroTicket, String emailAutenticado, boolean esStaff) {
        return reservaMapper.toResponse(obtenerTicketSiAutorizado(numeroTicket, emailAutenticado, esStaff));
    }

    @Override
    @Transactional
    public ValidacionTicketResponseDTO validarTicket(String numeroTicket, boolean marcarUsado) {
        Reserva reserva = findByTicketEntity(numeroTicket);

        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("No se puede validar un ticket de una reserva cancelada");
        }

        if (reserva.getEstadoReserva() == EstadoReserva.VALIDADA && marcarUsado) {
            return reservaMapper.toValidacionTicket(reserva, false);
        }

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
    public void cancelar(String numeroTicket, String emailAutenticado, boolean esStaff) {
        Reserva reserva = obtenerTicketSiAutorizado(numeroTicket, emailAutenticado, esStaff);

        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("La reserva ya está cancelada");
        }

        if (reserva.getEstadoReserva() == EstadoReserva.VALIDADA) {
            throw new ReglaNegocioException("No se puede cancelar una reserva cuyo ticket ya fue validado");
        }

        if (!reserva.getFuncion().getHorario().isAfter(LocalDateTime.now().plusDays(1))) {
            throw new ReglaNegocioException(
                    "No se puede cancelar una reserva cuando faltan menos de 24 horas para el inicio de la función");
        }

        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    /**
     * CLIENTE solo ve/cancela su ticket. Staff pasa. Ajeno → 404 (no revelar existencia).
     */
    private void asegurarDuenoOStaff(Reserva reserva, String emailAutenticado, boolean esStaff) {
        if (esStaff) {
            return;
        }
        String emailCliente = reserva.getCliente() != null ? reserva.getCliente().getEmail() : null;
        if (emailAutenticado == null
                || emailCliente == null
                || !emailCliente.equalsIgnoreCase(emailAutenticado.trim())) {
            throw new RecursoNoEncontradoException("No encontramos ese ticket.");
        }
    }

    @Override
    @Transactional
    public ReservaResponseDTO marcarComoPagada(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", reservaId));
        reserva.setPagada(true);
        if (reserva.getEstadoReserva() == EstadoReserva.PENDIENTE) {
            reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        }
        reserva = reservaRepository.save(reserva);
        return reservaMapper.toResponse(reserva);
    }

    private void validarRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new ReglaNegocioException("Las fechas desde y hasta aree obligatorias");
        }
        if (inicio.isAfter(fin)) {
            throw new ReglaNegocioException("La fecha inicial no puede be posterior a la fecha final");
        }
    }

    private void validarReservaModificable(Reserva reserva) {
        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("No se puede modificar a reserva cancelada");
        }
        if (reserva.getEstadoReserva() == EstadoReserva.VALIDADA) {
            throw new ReglaNegocioException("No se puede modificar a reserva cuyo ticket ya fue validado");
        }
    }

    private void validarAsientosSinDuplicados(List<String> etiquetasAsientos) {
        Set<String> vistos = new HashSet<>();
        for (String etiqueta : etiquetasAsientos) {
            String normalizada = etiqueta.trim().toUpperCase();
            if (!vistos.add(normalizada)) {
                throw new ReglaNegocioException("No se pueden seleccionar asientos duplicados: " + etiqueta);
            }
        }
    }

    private String normalizarMetodoPago(String metodoPago) {
        if (!MetodoPagoHelper.esValido(metodoPago)) {
            throw new ReglaNegocioException("Método de pago no permitido: " + metodoPago);
        }

        return MetodoPagoHelper.fromString(metodoPago).name();
    }
}
