package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.ActualizarMetodoPagoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.CrearReservaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ReservaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ValidacionTicketResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;

import java.util.List;

public interface ReservaService {
    ReservaResponseDTO crear(CrearReservaRequestDTO request);
    List<ReservaResponseDTO> listarPorCliente(Long clienteId);
    List<ReservaResponseDTO> listarReservasPorEmail(String email);
    Reserva findByTicketEntity(String numeroTicket);
    ReservaResponseDTO buscarReservaPorTicket(String numeroTicket);
    PagoResponseDTO buscarPagoPorTicket(String numeroTicket);
    ValidacionTicketResponseDTO validarTicket(String numeroTicket, boolean marcarUsado);
    ReservaResponseDTO actualizarMetodoPago(String numeroTicket, ActualizarMetodoPagoRequestDTO request);
    void cancelar(String numeroTicket);
    List<String> obtenerAsientosOcupados(Long funcionId);
}
