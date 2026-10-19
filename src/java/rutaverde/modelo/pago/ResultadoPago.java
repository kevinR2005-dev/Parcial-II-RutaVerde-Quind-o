package rutaverde.modelo.pago;

public class ResultadoPago {

    private final boolean aprobado;
    private final String motivo;
    private final String autorizacion;

    private ResultadoPago(boolean aprobado, String motivo, String autorizacion) {
        this.aprobado = aprobado;
        this.motivo = motivo;
        this.autorizacion = autorizacion;
    }

    public static ResultadoPago aprobado(String autorizacion) {
        return new ResultadoPago(true, null, autorizacion);
    }

    public static ResultadoPago rechazado(String motivo) {
        return new ResultadoPago(false, motivo, null);
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getAutorizacion() {
        return autorizacion;
    }
}
