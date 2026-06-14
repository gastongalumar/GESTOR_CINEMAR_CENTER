package GESTOR_CINEMAR_CENTER.DEV.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Los archivos estáticos de /uploads/** los sirve FileController con validación de rutas.
}

