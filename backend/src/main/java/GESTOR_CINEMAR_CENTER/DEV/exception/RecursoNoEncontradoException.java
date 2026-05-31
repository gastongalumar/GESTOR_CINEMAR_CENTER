package GESTOR_CINEMAR_CENTER.DEV.exception;


public class RecursoNoEncontrado extends RuntimeException {

    public RecursoNoEncontrado(String mensaje) {
        super(mensaje);
    }

    public RecursoNoEncontrado(String recurso, String campo, Object valor) {
        super(String.format("%s no encontrado con %s: '%s'", recurso, campo, valor));
    }

    public RecursoNoEncontrado(String recurso, Long id) {
        super(String.format("%s no encontrado con id: %d", recurso, id));
    }
}