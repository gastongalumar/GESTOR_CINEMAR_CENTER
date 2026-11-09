package GESTOR_CINEMAR_CENTER.DEV.repository;

import GESTOR_CINEMAR_CENTER.DEV.model.CandyCombo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandyComboRepository extends JpaRepository<CandyCombo, Long> {
    List<CandyCombo> findByActivoTrueOrderByNombreAsc();
    List<CandyCombo> findAllByOrderByNombreAsc();
}
