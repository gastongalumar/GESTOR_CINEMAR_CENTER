package GESTOR_CINEMAR_CENTER.DEV.dto.response.customizacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomizacionResponse {
    private Long id;
    private String logoUrl;
    private String fondoUrl;
}
