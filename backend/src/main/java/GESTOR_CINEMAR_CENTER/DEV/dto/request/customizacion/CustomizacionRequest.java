package GESTOR_CINEMAR_CENTER.DEV.dto.request.customizacion;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomizacionRequest {



    private String logoUrl;


    private String fondoUrl;


    private String colorFondo;

    private String colorContenedor;

    private String colorFuente;

    private String colorSlider;

    private String tamanoFuenteTitulos;

    private String tamanoFuenteTexto;

    private String logoTamano;

    @Pattern(regexp = "^([0-9]|[1-9][0-9]|100)$", message = "La opacidad debe ser un valor entre 0 y 100")
    private String logoOpacity;

    @Pattern(regexp = "^([0-9]|[1-9][0-9]|100)$", message = "La opacidad debe ser un valor entre 0 y 100")
    private String globalOpacity;
}
