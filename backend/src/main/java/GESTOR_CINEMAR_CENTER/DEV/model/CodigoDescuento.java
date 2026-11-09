package GESTOR_CINEMAR_CENTER.DEV.model;

import GESTOR_CINEMAR_CENTER.DEV.enums.TipoDescuento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "codigo_descuento")
public class CodigoDescuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDescuento tipo = TipoDescuento.PORCENTAJE;

    /** Porcentaje (ej. 10) o monto fijo en pesos. */
    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    /** null = ilimitado */
    private Integer usosMaximos;

    @Column(nullable = false)
    private Integer usosActuales = 0;

    private LocalDateTime vigenteDesde;
    private LocalDateTime vigenteHasta;

    @Column(length = 255)
    private String descripcion;
}
