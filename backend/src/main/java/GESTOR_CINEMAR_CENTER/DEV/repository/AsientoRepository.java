package GESTOR_CINEMAR_CENTER.DEV.repository;


import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {
    List<Asiento> findBySala(Sala sala);
    Optional<Asiento> findBySalaAndEtiqueta(Sala sala, String etiqueta);
    boolean existsBySala(Sala sala);
    void deleteBySala(Sala sala);
}
