package GESTOR_CINEMAR_CENTER.DEV.repository;

import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import GESTOR_CINEMAR_CENTER.DEV.enums.GeneroPelicula;
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

    // Búsquedas activas por id y nombre
    Optional<Pelicula> findByIdAndActivaTrue(Long id);
    boolean existsByNombreAndActivaTrue(String nombre);
    boolean existsByNombreIgnoreCaseAndActivaTrue(String nombre);
    Optional<Pelicula> findByNombreAndActivaTrue(String nombre);

    // Listado de todas las activas
    List<Pelicula> findByActivaTrue();

    // Vigentes: activas + dentro del rango de cartelera
    @Query("SELECT p FROM Pelicula p WHERE p.activa = true AND p.fechaEstreno <= :fecha AND p.fechaSalida >= :fecha")
    List<Pelicula> findVigentesEnFecha(@Param("fecha") LocalDate fecha);

    // Paginado de vigentes activas
    @Query("SELECT p FROM Pelicula p WHERE p.activa = true AND p.fechaEstreno <= :fecha AND p.fechaSalida >= :fecha")
    Page<Pelicula> findVigentesEnFecha(@Param("fecha") LocalDate fecha, Pageable pageable);

    // Filtros activos por nombre y género
    @Query("SELECT p FROM Pelicula p WHERE p.activa = true AND p.nombre LIKE %:nombre%")
    List<Pelicula> findByNombreContainsIgnoreCase(@Param("nombre") String nombre);

    List<Pelicula> findByActivaTrueAndGenero(GeneroPelicula genero);

    @Query("SELECT p FROM Pelicula p WHERE p.activa = true AND p.nombre LIKE %:nombre% AND p.fechaEstreno <= :fecha AND p.fechaSalida >= :fecha")
    List<Pelicula> findVigentesPorNombre(@Param("nombre") String nombre, @Param("fecha") LocalDate fecha);

    @Query("SELECT p FROM Pelicula p WHERE p.activa = true AND p.genero = :genero AND p.fechaEstreno <= :fecha AND p.fechaSalida >= :fecha")
    List<Pelicula> findVigentesPorGenero(@Param("genero") GeneroPelicula genero, @Param("fecha") LocalDate fecha);

    @Query("""
            SELECT p FROM Pelicula p
            WHERE p.activa = true
              AND p.fechaEstreno > :hoy
              AND p.fechaEstreno <= :limite
            ORDER BY p.fechaEstreno ASC
            """)
    List<Pelicula> findProximamenteEnVentana(@Param("hoy") LocalDate hoy, @Param("limite") LocalDate limite);
}