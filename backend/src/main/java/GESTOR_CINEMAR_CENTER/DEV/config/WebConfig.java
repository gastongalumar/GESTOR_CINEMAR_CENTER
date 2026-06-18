package GESTOR_CINEMAR_CENTER.DEV.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Los uploads los sirve FileController, no hace falta mapear recursos estaticos aca
}

