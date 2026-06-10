package GESTOR_CINEMAR_CENTER.DEV.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
