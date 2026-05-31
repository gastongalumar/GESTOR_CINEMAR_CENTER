package GESTOR_CINEMAR_CENTER.DEV.service;


import GESTOR_CINEMAR_CENTER.DEV.dto.response.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.mapper.PagoMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Pago;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
import GESTOR_CINEMAR_CENTER.DEV.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final PagoMapper pagoMapper;

    public PagoService(PagoRepository pagoRepository,
                       ReservaRepository reservaRepository,
                       PagoMapper pagoMapper) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
        this.pagoMapper = pagoMapper;
    }

    public List<PagoResponseDTO> listarTodos() {
        return pagoMapper.toDTOList(pagoRepository.findAll());
    }

    public PagoResponseDTO buscarPorId(Long id) {
        return pagoMapper.toDTO(obtenerEntidad(id));
    }

    public PagoResponseDTO buscarPorReserva(String numeroTicket) {
        Reserva reserva = reservaRepository.findByNumeroTicket(numeroTicket)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "ticket", numeroTicket));
        Pago pago = pagoRepository.findByReserva(reserva)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "ticket", numeroTicket));
        return pagoMapper.toDTO(pago);
    }

    private Pago obtenerEntidad(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
    }
}
