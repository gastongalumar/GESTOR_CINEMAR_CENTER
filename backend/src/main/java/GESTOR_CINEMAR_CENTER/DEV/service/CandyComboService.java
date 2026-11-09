package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.candy.CandyComboRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.candy.CandyComboResponseDTO;

import java.util.List;

public interface CandyComboService {
    List<CandyComboResponseDTO> listarTodos();
    List<CandyComboResponseDTO> listarActivos();
    CandyComboResponseDTO obtener(Long id);
    CandyComboResponseDTO crear(CandyComboRequestDTO request);
    CandyComboResponseDTO actualizar(Long id, CandyComboRequestDTO request);
    void eliminar(Long id);
}
