package GESTOR_CINEMAR_CENTER.DEV.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Duration;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pelicula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String rutaImagen;

    @Column(nullable = false)
    private LocalDate fechaEstreno;

    @Column(nullable = false)
    private LocalDate fechaSalida;

    @Column(nullable = false)
    private Integer duracionMinutos;
}
