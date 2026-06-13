package GESTOR_CINEMAR_CENTER.DEV.repository;

import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
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

    @Query("SELECT p FROM Pelicula p WHERE p.nombre LIKE %:nombre%")
    List<Pelicula> findByNombreContainsIgnoreCase(@Param("nombre") String nombre);

    @Query("SELECT p FROM Pelicula p WHERE p.genero LIKE %:genero%")
    List<Pelicula> findByGeneroContainsIgnoreCase(@Param("genero") String genero);

    @Query("SELECT p FROM Pelicula p WHERE p.nombre LIKE %:nombre% AND p.fechaEstreno <= :fecha AND p.fechaSalida >= :fecha")
    List<Pelicula> findVigentesPorNombre(@Param("nombre") String nombre, @Param("fecha") LocalDate fecha);

    @Query("SELECT p FROM Pelicula p WHERE p.genero LIKE %:genero% AND p.fechaEstreno <= :fecha AND p.fechaSalida >= :fecha")
    List<Pelicula> findVigentesPorGenero(@Param("genero") String genero, @Param("fecha") LocalDate fecha);
}