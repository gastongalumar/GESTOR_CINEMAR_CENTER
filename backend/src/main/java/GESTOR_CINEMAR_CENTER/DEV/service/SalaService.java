package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.sala.ActualizarSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.sala.CrearSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.asiento.AsientoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.sala.SalaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;

import java.util.List;

public interface SalaService {
    List<SalaResponseDTO> listarTodas();
    SalaResponseDTO buscarPorId(Long id);
    SalaResponseDTO buscarPorNombre(String nombre);
    SalaResponseDTO crear(CrearSalaRequestDTO request);
    SalaResponseDTO actualizar(Long id, ActualizarSalaRequestDTO request);
    void eliminar(Long id);
    List<AsientoResponseDTO> listarAsientosPorSala(Long salaId);
    void asegurarAsientosDeSala(Sala sala);
    Sala obtenerEntidad(Long id);
}
