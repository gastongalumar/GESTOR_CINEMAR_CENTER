package GESTOR_CINEMAR_CENTER.DEV.repository;

import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    // Buscar función activa por id
    Optional<Funcion> findByIdAndActivaTrue(Long id);

    // Listados filtrados por activa
    List<Funcion> findByActivaTrueAndPelicula(Pelicula pelicula);
    List<Funcion> findByActivaTrueAndSala(Sala sala);
    List<Funcion> findByActivaTrue();

    // Vigentes: activas con horario futuro
    List<Funcion> findByActivaTrueAndHorarioAfter(LocalDateTime fecha);

    // Búsqueda por rango de horario (solo activas) — para solapamiento
    List<Funcion> findByActivaTrueAndSalaAndHorarioBetween(Sala sala, LocalDateTime inicio, LocalDateTime fin);

    // Para verificar funciones futuras de una sala (borrado lógico de sala)
    boolean existsBySalaAndHorarioAfter(Sala sala, LocalDateTime horario);

    // Solo activas con horario futuro para sala (al eliminar pelicula)
    @Query("SELECT COUNT(f) > 0 FROM Funcion f WHERE f.pelicula = :pelicula AND f.activa = true AND f.horario > :ahora")
    boolean existsByPeliculaActivaFutura(@Param("pelicula") Pelicula pelicula, @Param("ahora") LocalDateTime ahora);

    // Colisión exacta de horario (solo activas)
    boolean existsBySalaAndHorarioAndActivaTrue(Sala sala, LocalDateTime horario);

    // Para filtros admin
    List<Funcion> findByHorarioBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT f FROM Funcion f WHERE f.pelicula.id = :peliculaId AND f.activa = true AND f.horario > :fecha")
    List<Funcion> findVigentesPorPelicula(@Param("peliculaId") Long peliculaId, @Param("fecha") LocalDateTime fecha);

    // ---------------------------
    // Método agregado para estadísticas
    // ---------------------------

    @Query("SELECT COUNT(f) FROM Funcion f WHERE f.horario > CURRENT_TIMESTAMP")
    Long countVigentes();

    // Compatibilidad con SalaServiceImpl (chequeo funciones futuras)
    @Query("SELECT COUNT(f) > 0 FROM Funcion f WHERE f.sala = :sala AND f.activa = true AND f.horario > :horario")
    boolean existsActivaBySalaAndHorarioAfter(@Param("sala") Sala sala, @Param("horario") LocalDateTime horario);
}