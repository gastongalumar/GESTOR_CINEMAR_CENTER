package GESTOR_CINEMAR_CENTER.DEV.repository;

import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoPago;
import GESTOR_CINEMAR_CENTER.DEV.enums.MetodoPago;
import GESTOR_CINEMAR_CENTER.DEV.model.Pago;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByReserva(Reserva reserva);
    List<Pago> findByEstadoPago(EstadoPago estadoPago);
    List<Pago> findByMetodoPago(MetodoPago metodoPago);

    @Query("SELECT p FROM Pago p WHERE p.reserva.cliente = :cliente ORDER BY p.fechaPago DESC")
    List<Pago> findByClienteOrderByFechaPagoDesc(@Param("cliente") Cliente cliente);

    // Queries para el dashboard (solo pagos completados de reservas no canceladas/expiradas/reembolsadas)
    @Query("""
            SELECT COALESCE(SUM(p.monto), 0) FROM Pago p
            WHERE p.estadoPago = GESTOR_CINEMAR_CENTER.DEV.enums.EstadoPago.COMPLETADO
              AND p.reserva.estadoReserva NOT IN (
                  GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva.CANCELADA,
                  GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva.EXPIRADA,
                  GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva.REEMBOLSADA
              )
            """)
    Double sumTotalVentas();

    @Query("""
            SELECT COALESCE(SUM(p.monto), 0) FROM Pago p
            WHERE p.estadoPago = GESTOR_CINEMAR_CENTER.DEV.enums.EstadoPago.COMPLETADO
              AND p.reserva.estadoReserva NOT IN (
                  GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva.CANCELADA,
                  GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva.EXPIRADA,
                  GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva.REEMBOLSADA
              )
              AND FUNCTION('DATE', p.fechaPago) = :fecha
            """)
    Double sumVentasPorFecha(@Param("fecha") LocalDate fecha);
}