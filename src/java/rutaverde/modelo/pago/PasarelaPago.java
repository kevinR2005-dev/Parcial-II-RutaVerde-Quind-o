package rutaverde.modelo.pago;

public interface PasarelaPago {
    ResultadoPago cobrar(String numeroTarjeta, double valorPesos, String referencia);
}
