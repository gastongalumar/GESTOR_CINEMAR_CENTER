package GESTOR_CINEMAR_CENTER.DEV.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
public class Cliente extends  Usuario{
    private Integer puntosFidelidad = 0;

}
