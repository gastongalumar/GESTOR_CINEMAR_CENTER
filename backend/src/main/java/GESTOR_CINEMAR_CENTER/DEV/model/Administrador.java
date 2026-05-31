package GESTOR_CINEMAR_CENTER.DEV.model;


import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Usuario {
    private String nivelAcceso = "AVANZADO";



    // Constructor con parámetros
    public Administrador(String nombre, String apellido, String email,
                         String password, String telefono) {
        super(nombre, apellido, email, password, telefono, TipoUsuario.ADMINISTRADOR);
    }


}