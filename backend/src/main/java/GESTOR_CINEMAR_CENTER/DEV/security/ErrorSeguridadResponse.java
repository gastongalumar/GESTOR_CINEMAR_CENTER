package GESTOR_CINEMAR_CENTER.DEV.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class ErrorSeguridadResponse {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public void write(HttpServletResponse response,
                      HttpServletRequest request,
                      HttpStatus status,
                      String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status.value());
        error.setError(obtenerEtiquetaEnEspanol(status));
        error.setMessage(message);
        error.setPath(request.getRequestURI());

        objectMapper.writeValue(response.getOutputStream(), error);
    }

    private String obtenerEtiquetaEnEspanol(HttpStatus status) {
        return switch (status) {
            case UNAUTHORIZED -> "No autorizado";
            case FORBIDDEN -> "Prohibido";
            default -> status.getReasonPhrase();
        };
    }
}
