package GESTOR_CINEMAR_CENTER.DEV.dto.request.customizacion;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidCssSize;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidHexColor;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidResourceUrl;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomizacionRequest {

    @Size(max = 500, message = "La URL del logo no puede superar 500 caracteres")
    @ValidResourceUrl
    private String logoUrl;

    @Size(max = 500, message = "La URL del fondo no puede superar 500 caracteres")
    @ValidResourceUrl
    private String fondoUrl;

    @ValidHexColor
    private String colorFondo;

    @ValidHexColor
    private String colorContenedor;

    @ValidHexColor
    private String colorFuente;

    @ValidHexColor
    private String colorSlider;

    @ValidCssSize
    private String tamanoFuenteTitulos;

    @ValidCssSize
    private String tamanoFuenteTexto;

    @ValidCssSize
    private String logoTamano;

    @Pattern(regexp = "^([0-9]|[1-9][0-9]|100)$", message = "La opacidad debe ser un valor entre 0 y 100")
    private String logoOpacity;

    @Pattern(regexp = "^([0-9]|[1-9][0-9]|100)$", message = "La opacidad debe ser un valor entre 0 y 100")
    private String globalOpacity;
}
