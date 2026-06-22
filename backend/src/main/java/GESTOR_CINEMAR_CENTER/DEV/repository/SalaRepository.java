package GESTOR_CINEMAR_CENTER.DEV.repository;


import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    Optional<Sala> findByNombre(String nombre);
    boolean existsByNombre(String nombre);


    List<Sala> findByActivaTrue();

    long countByActivaTrue();

    Optional<Sala> findByIdAndActivaTrue(Long id);

    Optional<Sala> findByNombreAndActivaTrue(String nombre);

    boolean existsByNombreAndActivaTrue(String nombre);
}