package rutaverde.excepcion;

public class EntidadNoEncontrada extends RuntimeException {
    public EntidadNoEncontrada(String message) {
        super(message);
    }
}
