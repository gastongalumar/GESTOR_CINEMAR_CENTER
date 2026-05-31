package DEV.model;


import DEV.enums.EstadoPago;
import DEV.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Reserva reserva;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private MetodoPago metodoPago;

    private LocalDateTime fechaPago;

    private EstadoPago estadoPago = EstadoPago.COMPLETADO;

    private String transaccionId;



}