package GESTOR_CINEMAR_CENTER.DEV.model;

import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@DiscriminatorValue("CLIENTE")
public class Cliente extends  Usuario{
    private Integer puntosFidelidad = 0;

    // Constructor con parámetros
    public Cliente(String nombre, String apellido, String email,
                   String password, String telefono) {
        super(nombre, apellido, email, password, telefono, TipoUsuario.CLIENTE);
    }

}