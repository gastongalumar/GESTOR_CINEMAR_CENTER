package GESTOR_CINEMAR_CENTER.DEV.model;

import GESTOR_CINEMAR_CENTER.DEV.enums.GeneroPelicula;
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

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fechaEstreno;

    @Column(nullable = false)
    private LocalDate fechaSalida;

    @Column(nullable = false)
    private Integer duracionMinutos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GeneroPelicula genero;

    @Column
    private String rutaImagen;

    @Column(nullable = false)
    private boolean activa = true;
}
