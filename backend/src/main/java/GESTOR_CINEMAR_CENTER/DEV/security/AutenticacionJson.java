package GESTOR_CINEMAR_CENTER.DEV.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AutenticacionJson implements AuthenticationEntryPoint {

    private final ErrorSeguridadResponse escritorError;

    public AutenticacionJson(ErrorSeguridadResponse escritorError) {
        this.escritorError = escritorError;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        escritorError.write(response, request, HttpStatus.UNAUTHORIZED,
                "Debe autenticarse para acceder a este recurso.");
    }
}
