package DEV.repository;

import DEV.model.Funcion;
import DEV.model.Pelicula;
import DEV.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    List<Funcion> findByPelicula(Pelicula pelicula);
    List<Funcion> findBySala(Sala sala);
    List<Funcion> findBySalaAndHorarioBetween(Sala sala, LocalDateTime inicio, LocalDateTime fin);
    List<Funcion> findByHorarioAfter(LocalDateTime fecha);
    List<Funcion> findByHorarioBetween(LocalDateTime inicio, LocalDateTime fin);
    boolean existsBySalaAndHorario(Sala sala, LocalDateTime horario);

    @Query("SELECT f FROM Funcion f WHERE f.pelicula.id = :peliculaId AND f.horario > :fecha")
    List<Funcion> findVigentesPorPelicula(@Param("peliculaId") Long peliculaId, @Param("fecha") LocalDateTime fecha);
}