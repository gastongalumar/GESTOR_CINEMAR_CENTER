package GESTOR_CINEMAR_CENTER.DEV.model;

import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "disc_tipo", discriminatorType = DiscriminatorType.STRING)
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

    // El tipo de usuario va en otra columna, no confundir con el discriminator de JPA
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaUltimoAcceso;
    private Integer intentosFallidos = 0;

    public Usuario(String nombre, String apellido, String email, String password, String telefono, TipoUsuario tipo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.tipo = tipo;
    }

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        fechaUltimoAcceso = LocalDateTime.now();
    }
}