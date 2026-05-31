package DEV.model;

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
public class Funcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Sala sala;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Pelicula pelicula;

    @Column(nullable = false)
    private LocalDateTime horario;

    private Double precio = 5000.0;
}
