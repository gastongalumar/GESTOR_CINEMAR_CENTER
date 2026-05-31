package GESTOR_CINEMAR_CENTER.DEV.repository;

import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @EntityGraph(attributePaths = {"asientos"})
    List<Reserva> findByClienteOrderByFechaEmisionDesc(Cliente cliente);

    @EntityGraph(attributePaths = {"asientos"})
    Optional<Reserva> findByNumeroTicket(String numeroTicket);

    @EntityGraph(attributePaths = {"asientos"})
    List<Reserva> findByFuncionAndEstadoReservaIn(Funcion funcion, List<EstadoReserva> estados);

    @EntityGraph(attributePaths = {"asientos", "funcion"})
    List<Reserva> findAll();

    List<Reserva> findByCliente(Cliente cliente);
    List<Reserva> findByFuncion(Funcion funcion);

    @Query("SELECT r FROM Reserva r WHERE r.cliente.id = :clienteId AND r.fechaEmision BETWEEN :inicio AND :fin")
    List<Reserva> findReservasPorClienteYFecha(@Param("clienteId") Long clienteId,
                                               @Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);
}