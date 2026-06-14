package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.ActualizarMetodoPagoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.CrearReservaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ReservaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ValidacionTicketResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaService {
    ReservaResponseDTO crear(CrearReservaRequestDTO request, String emailAutenticado);
    List<ReservaResponseDTO> listarPorCliente(Long clienteId);
    List<ReservaResponseDTO> listarReservasPorEmail(String email);
    Reserva findByTicketEntity(String numeroTicket);
    ReservaResponseDTO buscarReservaPorTicket(String numeroTicket);
    ValidacionTicketResponseDTO validarTicket(String numeroTicket, boolean marcarUsado);
    void cancelar(String numeroTicket);
    
    // Métodos para filtrado de reservas (admin)
    List<ReservaResponseDTO> listarTodasReservas();
    List<ReservaResponseDTO> filtrarReservasPorFuncion(Long funcionId);
    List<ReservaResponseDTO> filtrarReservasPorSala(Long salaId);
    List<ReservaResponseDTO> filtrarReservasPorFecha(LocalDateTime inicio, LocalDateTime fin);
}
