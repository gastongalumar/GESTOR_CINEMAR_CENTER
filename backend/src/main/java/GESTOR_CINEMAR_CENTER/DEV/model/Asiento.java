package GESTOR_CINEMAR_CENTER.DEV.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sala_id", "fila", "columna"}),
        @UniqueConstraint(columnNames = {"sala_id", "etiqueta"})
})
public class Asiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    @JsonIgnore
    private Sala sala;

    @Column(nullable = false)
    private Integer fila;

    @Column(nullable = false)
    private Integer columna;

    @Column(nullable = false, length = 10)
    private String etiqueta;


}