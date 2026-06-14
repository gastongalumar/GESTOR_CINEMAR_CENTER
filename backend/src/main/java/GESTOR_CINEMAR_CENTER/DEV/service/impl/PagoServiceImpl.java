package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.MetodoPago;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.PagoMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Pago;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.model.Usuario;
import GESTOR_CINEMAR_CENTER.DEV.repository.PagoRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.PagoService;
import GESTOR_CINEMAR_CENTER.DEV.service.ReservaService;
import GESTOR_CINEMAR_CENTER.DEV.service.UsuarioService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("pagoService")
@Transactional(readOnly = true)
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final PagoMapper pagoMapper;

    public PagoServiceImpl(PagoRepository pagoRepository,
                           @Lazy ReservaService reservaService,
                           UsuarioService usuarioService,
                           PagoMapper pagoMapper) {
        this.pagoRepository = pagoRepository;
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
        this.pagoMapper = pagoMapper;
    }

    @Override
    public List<PagoResponseDTO> listarTodos() {
        return pagoMapper.toDTOList(pagoRepository.findAll());
    }

    @Override
    public List<PagoResponseDTO> listarMisPagos(String email) {
        Usuario usuario = usuarioService.findByEmail(email.trim().toLowerCase());

        if (!(usuario instanceof Cliente cliente)) {
            return List.of();
        }

        return pagoMapper.toDTOList(pagoRepository.findByClienteOrderByFechaPagoDesc(cliente));
    }

    @Override
    public PagoResponseDTO buscarPorId(Long id) {
        return pagoMapper.toDTO(obtenerEntidad(id));
    }

    @Override
    public PagoResponseDTO buscarPorReserva(String numeroTicket) {
        Reserva reserva = reservaService.findByTicketEntity(numeroTicket);
        return pagoMapper.toDTO(buscarPagoPorReserva(reserva));
    }

    @Override
    public Pago buscarPagoPorReserva(Reserva reserva) {
        return pagoRepository.findByReserva(reserva)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago", "ticket", reserva.getNumeroTicket()));
    }

    private Pago obtenerEntidad(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago", id));
    }

    @Override
    @Transactional
    public Pago crearPago(Reserva reserva, double monto, String metodoPago) {
        if (monto <= 0) {
            throw new ReglaNegocioException("El monto del pago debe ser mayor a cero");
        }

        if (pagoRepository.findByReserva(reserva).isPresent()) {
            throw new ReglaNegocioException("Ya existe un pago asociado a esta reserva");
        }

        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMetodoPago(MetodoPago.valueOf(metodoPago));
        pago.setMonto(monto);
        pago.setFechaPago(LocalDateTime.now());
        return pagoRepository.save(pago);
    }

    @Override
    @Transactional
    public void actualizarMetodoPago(Reserva reserva, String metodoPago) {
        Pago pago = buscarPagoPorReserva(reserva);
        pago.setMetodoPago(MetodoPago.valueOf(metodoPago));
        pagoRepository.save(pago);
    }
}
