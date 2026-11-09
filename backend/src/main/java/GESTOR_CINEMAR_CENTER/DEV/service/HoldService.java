package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.asiento.EstadoAsientosResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.hold.HoldResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;

import java.util.List;

public interface HoldService {
    HoldResponseDTO crearHold(Long funcionId, String etiqueta, String email);
    void liberarHold(Long funcionId, String etiqueta, String email);
    void liberarTodosDelUsuario(Long funcionId, String email);
    void renovarHoldsDelUsuario(Long funcionId, String email);
    EstadoAsientosResponseDTO obtenerEstadoAsientos(Long funcionId, String email);
    void validarSinHoldsAjenos(Funcion funcion, List<Asiento> asientos, Long clienteId);
    void eliminarHoldsDeAsientos(Funcion funcion, List<Asiento> asientos);
    void limpiarExpirados();
}
