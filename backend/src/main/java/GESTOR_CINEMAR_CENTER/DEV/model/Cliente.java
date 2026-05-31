package GESTOR_CINEMAR_CENTER.DEV.model;

import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cliente extends  Usuario{
    private Integer puntosFidelidad = 0;

    // Constructor con parámetros
    public Cliente(String nombre, String apellido, String email,
                   String password, String telefono) {
        super(nombre, apellido, email, password, telefono, TipoUsuario.CLIENTE);
    }

}
