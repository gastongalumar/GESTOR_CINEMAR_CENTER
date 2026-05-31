package DEV.repository;


import DEV.model.Pago;
import DEV.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByReserva(Reserva reserva);
    List<Pago> findByEstado(String estado);
    Optional<Pago> findByTransaccionId(String transaccionId);
    List<Pago> findByMetodoPago(String metodoPago);
}