package GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class FieldErrorResponse {

    private String field;
    private String message;

}
