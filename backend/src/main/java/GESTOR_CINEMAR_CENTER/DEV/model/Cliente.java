package GESTOR_CINEMAR_CENTER.DEV.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
public class Cliente extends  Usuario{
    private Integer puntosFidelidad = 0;

}
