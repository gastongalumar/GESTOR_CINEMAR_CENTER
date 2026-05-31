package GESTOR_CINEMAR_CENTER.DEV.repository.repository;

import com.cinebackendspring.model.Customizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomizacionRepository extends JpaRepository<Customizacion, Long> {
    default Customizacion findFirst() {
        return findAll().stream().findFirst().orElse(null);
    }
}