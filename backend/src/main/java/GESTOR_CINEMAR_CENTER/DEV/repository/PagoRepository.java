package GESTOR_CINEMAR_CENTER.DEV.repository;

import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoPago;
import GESTOR_CINEMAR_CENTER.DEV.enums.MetodoPago;
import GESTOR_CINEMAR_CENTER.DEV.model.Pago;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
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
    Optional<Pago> findByTransaccionId(String transaccionId);
    List<Pago> findByMetodoPago(MetodoPago metodoPago);

    // ---------------------------
    // Métodos agregados para estadísticas
    // ---------------------------

    // Suma total de los montos de todos los pagos (null -> 0)
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p")
    Double sumTotalVentas();

    // Suma de montos para una fecha (solo fecha, ignora hora).
    // Usa FUNCTION('DATE', ...) para extraer la parte fecha (compatible con varios dialectos).
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE FUNCTION('DATE', p.fechaPago) = :fecha")
    Double sumVentasPorFecha(@Param("fecha") LocalDate fecha);
}