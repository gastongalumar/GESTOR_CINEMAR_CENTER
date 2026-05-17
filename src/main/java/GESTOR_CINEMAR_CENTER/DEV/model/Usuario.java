package GESTOR_CINEMAR_CENTER.DEV.model;

import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@ToString
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaUltimoAcceso;
    private Integer intentosFallidos = 0;


    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        fechaUltimoAcceso = LocalDateTime.now();
    }


}
