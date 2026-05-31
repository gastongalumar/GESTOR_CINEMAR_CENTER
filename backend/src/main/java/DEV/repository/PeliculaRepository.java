package DEV.repository;

import DEV.model.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByFechaSalidaAfter(LocalDate fecha);
    List<Pelicula> findByFechaEstrenoBeforeAndFechaSalidaAfter(LocalDate fecha1, LocalDate fecha2);
    boolean existsByNombre(String nombre);
    Optional<Pelicula> findByNombre(String nombre);

    @Query("SELECT p FROM Pelicula p WHERE p.fechaEstreno <= :fecha AND p.fechaSalida >= :fecha")
    List<Pelicula> findVigentesEnFecha(@Param("fecha") LocalDate fecha);

    // Paginado
    @Query("SELECT p FROM Pelicula p WHERE p.fechaEstreno <= :fecha AND p.fechaSalida >= :fecha")
    Page<Pelicula> findVigentesEnFecha(@Param("fecha") LocalDate fecha, Pageable pageable);
}