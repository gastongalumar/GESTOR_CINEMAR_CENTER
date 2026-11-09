package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.candy.CandyComboRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.candy.CandyComboResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.model.CandyCombo;
import GESTOR_CINEMAR_CENTER.DEV.repository.CandyComboRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.CandyComboService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandyComboServiceImpl implements CandyComboService {

    private final CandyComboRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<CandyComboResponseDTO> listarTodos() {
        return repository.findAllByOrderByNombreAsc().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandyComboResponseDTO> listarActivos() {
        return repository.findByActivoTrueOrderByNombreAsc().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CandyComboResponseDTO obtener(Long id) {
        return toDto(find(id));
    }

    @Override
    @Transactional
    public CandyComboResponseDTO crear(CandyComboRequestDTO request) {
        CandyCombo combo = new CandyCombo();
        aplicar(combo, request);
        return toDto(repository.save(combo));
    }

    @Override
    @Transactional
    public CandyComboResponseDTO actualizar(Long id, CandyComboRequestDTO request) {
        CandyCombo combo = find(id);
        aplicar(combo, request);
        return toDto(repository.save(combo));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        CandyCombo combo = find(id);
        repository.delete(combo);
    }

    private CandyCombo find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Combo candy", id));
    }

    private void aplicar(CandyCombo combo, CandyComboRequestDTO request) {
        combo.setNombre(request.getNombre().trim());
        combo.setDescripcion(request.getDescripcion() == null ? null : request.getDescripcion().trim());
        combo.setPrecio(request.getPrecio());
        combo.setIcono(
                request.getIcono() == null || request.getIcono().isBlank()
                        ? "fastfood"
                        : request.getIcono().trim());
        combo.setActivo(request.getActivo() == null || request.getActivo());
    }

    private CandyComboResponseDTO toDto(CandyCombo c) {
        return new CandyComboResponseDTO(
                c.getId(), c.getNombre(), c.getDescripcion(), c.getPrecio(), c.getIcono(), c.getActivo());
    }
}
