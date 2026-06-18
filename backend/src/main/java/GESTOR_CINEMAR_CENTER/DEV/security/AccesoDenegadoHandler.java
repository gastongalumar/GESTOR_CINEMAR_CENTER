package GESTOR_CINEMAR_CENTER.DEV.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccesoDenegadoHandler implements AccessDeniedHandler {

    private final ErrorSeguridadResponse escritorError;
    private final AccesoDenegadoMensaje mensajeAccesoDenegado;

    public AccesoDenegadoHandler(ErrorSeguridadResponse escritorError,
                                 AccesoDenegadoMensaje mensajeAccesoDenegado) {
        this.escritorError = escritorError;
        this.mensajeAccesoDenegado = mensajeAccesoDenegado;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mensaje = mensajeAccesoDenegado.resolver(request, authentication);
        escritorError.write(response, request, HttpStatus.FORBIDDEN, mensaje);
    }
}
