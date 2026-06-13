package GESTOR_CINEMAR_CENTER.DEV.dto.request.customizacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomizacionRequest {
    // URLs de recursos
    private String logoUrl;
    private String fondoUrl;
    // Colores
    private String colorFondo;
    private String colorContenedor;
    private String colorFuente;
    private String colorSlider;
    // Tamaños de fuente
    private String tamanoFuenteTitulos;
    private String tamanoFuenteTexto;
    // Configuración del logo
    private String logoTamano;
    private String logoOpacity;
    // Configuración global
    private String globalOpacity;
}
