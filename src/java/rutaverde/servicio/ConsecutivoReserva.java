package rutaverde.servicio;

public class ConsecutivoReserva {

    private int ultimo = 0;

    public int siguiente() {
        ultimo++;
        return ultimo;
    }
}
