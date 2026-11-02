package GESTOR_CINEMAR_CENTER.DEV.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Inicializa el SDK de Mercado Pago con el access token configurado.
 *
 * Se usa el nombre completo {@code com.mercadopago.MercadoPagoConfig} para no
 * chocar con el nombre de esta clase.
 */
@Configuration
public class MercadoPagoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoConfiguration.class);

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.notification-url}")
    private String notificationUrl;

    @Value("${mercadopago.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @PostConstruct
    public void init() {
        if (accessToken != null && !accessToken.isBlank()) {
            com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
            log.info("MercadoPago SDK inicializado correctamente (modo {})",
                    isTestMode() ? "PRUEBA" : "PRODUCCION");
        } else {
            log.warn("mercadopago.access-token no configurado");
        }
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getFrontendUrl() {
        String url = frontendUrl == null ? "http://localhost:4200" : frontendUrl.trim();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /** Las credenciales de prueba empiezan con TEST-. */
    public boolean isTestMode() {
        return accessToken != null && accessToken.startsWith("TEST-");
    }
}
