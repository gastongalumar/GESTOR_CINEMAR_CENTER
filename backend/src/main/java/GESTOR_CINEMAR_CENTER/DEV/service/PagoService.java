package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.MetodoPago;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.PagoMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Pago;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
import GESTOR_CINEMAR_CENTER.DEV.repository.PagoRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaService reservaService;
    private final PagoMapper pagoMapper;

    public PagoService(PagoRepository pagoRepository,
                       @Lazy ReservaService reservaService,
                       PagoMapper pagoMapper) {
        this.pagoRepository = pagoRepository;
        this.reservaService = reservaService;
        this.pagoMapper = pagoMapper;
    }

    public List<PagoResponseDTO> listarTodos() {
        return pagoMapper.toDTOList(pagoRepository.findAll());
    }

    public PagoResponseDTO buscarPorId(Long id) {
        return pagoMapper.toDTO(obtenerEntidad(id));
    }

    public PagoResponseDTO buscarPorReserva(String numeroTicket) {
        Reserva reserva = reservaService.findByTicketEntity(numeroTicket);
        return pagoMapper.toDTO(buscarPagoPorReserva(reserva));
    }

    public Pago buscarPagoPorReserva(Reserva reserva) {
        return pagoRepository.findByReserva(reserva)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago", "ticket", reserva.getNumeroTicket()));
    }

    private Pago obtenerEntidad(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago", id));
    }

    @Transactional
    public Pago crearPago(Reserva reserva, double monto, String metodoPago) {
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMetodoPago(MetodoPago.fromString(metodoPago));
        pago.setMonto(monto);
        return pagoRepository.save(pago);
    }
}
