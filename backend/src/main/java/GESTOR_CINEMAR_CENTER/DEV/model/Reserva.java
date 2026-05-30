package GESTOR_CINEMAR_CENTER.DEV.model;

import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String numeroTicket;

    private String codigoOR;

    @ManyToOne
    @JoinColumn(nullable = false)
   // @JsonIgnoreProperties({"reservas", "password", "intentosFallidos", "fechaUltimoAcceso"})
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties({"reservas"})
    private Funcion funcion;

    private Double montoTotal;
    private String metodoPago;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaValidacion;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estadoReserva = EstadoReserva.CONFIRMADA;

    @ManyToMany
    @JoinTable(
            name = "reserva_asiento",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "asiento_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"reserva_id", "asiento_id"})
    )
    private List<Asiento> asientos = new ArrayList<>();



    //tendria que mover esta logica al service
    /*
    @PrePersist
    protected void onCreate() {
        fechaEmision = LocalDateTime.now();
        if (numeroTicket == null) {
            numeroTicket = "TK" + System.currentTimeMillis();
            codigoOR = "OR-CMX-" + numeroTicket.substring(2);
        }
        if (!this.usado) this.usado = false;
    }*/

}
