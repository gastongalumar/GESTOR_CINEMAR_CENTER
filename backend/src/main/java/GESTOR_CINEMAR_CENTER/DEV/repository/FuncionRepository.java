package GESTOR_CINEMAR_CENTER.DEV.repository;

import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    // Trae la funcion solo si sigue activa
    Optional<Funcion> findByIdAndActivaTrue(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Funcion f WHERE f.id = :id AND f.activa = true")
    Optional<Funcion> findByIdAndActivaTrueForUpdate(@Param("id") Long id);

    // Solo las que no estan dadas de baja
    List<Funcion> findByActivaTrueAndPelicula(Pelicula pelicula);
    List<Funcion> findByActivaTrueAndSala(Sala sala);
    List<Funcion> findByActivaTrue();

    // Horario futuro
    List<Funcion> findByActivaTrueAndHorarioAfter(LocalDateTime fecha);

    // Sirve para detectar solapamientos en la misma sala
    List<Funcion> findByActivaTrueAndSalaAndHorarioBetween(Sala sala, LocalDateTime inicio, LocalDateTime fin);

    // Antes de desactivar una sala
    boolean existsBySalaAndHorarioAfter(Sala sala, LocalDateTime horario);

    // Antes de desactivar una pelicula
    @Query("SELECT COUNT(f) > 0 FROM Funcion f WHERE f.pelicula = :pelicula AND f.activa = true AND f.horario > :ahora")
    boolean existsByPeliculaActivaFutura(@Param("pelicula") Pelicula pelicula, @Param("ahora") LocalDateTime ahora);

    // Misma sala, mismo horario exacto
    boolean existsBySalaAndHorarioAndActivaTrue(Sala sala, LocalDateTime horario);

    // Filtros del panel admin
    List<Funcion> findByHorarioBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT f FROM Funcion f WHERE f.pelicula.id = :peliculaId AND f.activa = true AND f.horario > :fecha")
    List<Funcion> findVigentesPorPelicula(@Param("peliculaId") Long peliculaId, @Param("fecha") LocalDateTime fecha);

    // Para el dashboard de estadisticas
    long countByActivaTrue();

    @Query("SELECT COUNT(f) FROM Funcion f WHERE f.activa = true AND f.horario > CURRENT_TIMESTAMP")
    Long countVigentes();

    // Lo usa SalaService al intentar cambiar el layout
    @Query("SELECT COUNT(f) > 0 FROM Funcion f WHERE f.sala = :sala AND f.activa = true AND f.horario > :horario")
    boolean existsActivaBySalaAndHorarioAfter(@Param("sala") Sala sala, @Param("horario") LocalDateTime horario);
}