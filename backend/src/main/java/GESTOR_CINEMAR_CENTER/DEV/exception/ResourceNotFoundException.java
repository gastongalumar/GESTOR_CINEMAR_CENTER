package GESTOR_CINEMAR_CENTER.DEV.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }

    public ResourceNotFoundException(String recurso, String campo, Object valor) {
        super(String.format("%s no encontrado con %s: '%s'", recurso, campo, valor));
    }

    public ResourceNotFoundException(String recurso, Long id) {
        super(String.format("%s no encontrado con id: %d", recurso, id));
    }
}