package DEV.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RecursoNoEncontradoException(String recurso, String campo, Object valor) {
        super(String.format("%s no encontrado con %s: '%s'", recurso, campo, valor));
    }

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(String.format("%s no encontrado con id: %d", recurso, id));
    }
}