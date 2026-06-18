package GESTOR_CINEMAR_CENTER.DEV.exception;


import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.ErrorResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.FieldErrorResponse;
import GESTOR_CINEMAR_CENTER.DEV.security.AccesoDenegadoMensaje;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final AccesoDenegadoMensaje mensajeAccesoDenegado;

    public GlobalExceptionHandler(AccesoDenegadoMensaje mensajeAccesoDenegado) {
        this.mensajeAccesoDenegado = mensajeAccesoDenegado;
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            ReglaNegocioException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request));
    }

    @ExceptionHandler(ConflictoRecursoException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictoRecursoException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            RecursoNoEncontradoException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request));
    }

    @ExceptionHandler(GuardadoImagenException.class)
    public ResponseEntity<ErrorResponse> handleImageSave(
            GuardadoImagenException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorResponse error = buildError(HttpStatus.BAD_REQUEST, "Validation Failed",
                "Error de validación", request);
        List<FieldErrorResponse> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .toList();
        error.setFieldErrors(fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        ErrorResponse error = buildError(HttpStatus.BAD_REQUEST, "Validation Failed",
                "Error de validación", request);
        List<FieldErrorResponse> fieldErrors = ex.getConstraintViolations().stream()
                .map(this::toFieldError)
                .toList();
        error.setFieldErrors(fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "El parámetro '" + ex.getName() + "' tiene un formato inválido";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, "Bad Request", message, request));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        String message = "Falta el parámetro obligatorio: " + ex.getParameterName();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, "Bad Request", message, request));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, "Conflict",
                        "La operación viola una restricción de integridad de datos", request));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, "No autorizado",
                        "Email o contraseña incorrectos", request));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(
            DisabledException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(HttpStatus.FORBIDDEN, "Prohibido",
                        ex.getMessage(), request));
    }

    @ExceptionHandler({InsufficientAuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleInsufficientAuth(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, "No autorizado", ex.getMessage(), request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        String mensaje = mensajeAccesoDenegado.resolver(
                request, SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(HttpStatus.FORBIDDEN, "Prohibido", mensaje, request));
    }

    private ErrorResponse buildError(HttpStatus status, String error, String message,
                                     HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(LocalDateTime.now());
        body.setStatus(status.value());
        body.setError(error);
        body.setMessage(message);
        body.setPath(request.getRequestURI());
        return body;
    }

    private FieldErrorResponse toFieldError(FieldError fieldError) {
        return new FieldErrorResponse(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private FieldErrorResponse toFieldError(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath().toString();
        int dotIndex = field.lastIndexOf('.');
        if (dotIndex >= 0) {
            field = field.substring(dotIndex + 1);
        }
        return new FieldErrorResponse(field, violation.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Formato JSON inválido. Verifique la estructura de su request.";
        if (ex.getCause() instanceof com.fasterxml.jackson.databind.JsonMappingException) {
            message = "El tipo de dato es incorrecto. Verifique los tipos de sus campos.";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, "Bad Request", message, request));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(
            IOException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, "Bad Request",
                        "Error al procesar el archivo: " + ex.getMessage(), request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, "Bad Request",
                        "Argumento inválido: " + ex.getMessage(), request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                        "Ha ocurrido un error inesperado", request));
    }
}
