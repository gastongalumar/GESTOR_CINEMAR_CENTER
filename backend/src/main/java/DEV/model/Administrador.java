package DEV.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor

public class Administrador extends Usuario {
//    private String nivelAcceso = "AVANZADO";

}
