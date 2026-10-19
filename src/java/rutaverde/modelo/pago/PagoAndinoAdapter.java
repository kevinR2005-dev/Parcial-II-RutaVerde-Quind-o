package rutaverde.modelo.pago;

public class PagoAndinoAdapter implements PasarelaPago{

    private final PagoAndinoAPI api;

    public PagoAndinoAdapter(PagoAndinoAPI api) {
        this.api = api;
    }

    @Override
    public ResultadoPago cobrar(String numeroTarjeta, double valorPesos, String referencia) {
        long valorCentavos = Math.round(valorPesos * 100);
        String tarjeta = numeroTarjeta == null ? "" : numeroTarjeta.replace(" ", "");
        int codigo = api.procesarCobro(tarjeta, valorCentavos, referencia);
        return traducir(codigo);
    }

    private ResultadoPago traducir(int codigo) {
        switch (codigo) {
            case 0:
                return ResultadoPago.aprobado(api.ultimaAutorizacion());
            case 51:
                return ResultadoPago.rechazado("Fondos insuficientes");
            case 14:
                return ResultadoPago.rechazado("Tarjeta inválida");
            case 91:
                return ResultadoPago.rechazado("Pasarela no disponible");
            default:
                return ResultadoPago.rechazado("Error desconocido (código " + codigo + ")");
        }
    }
}
