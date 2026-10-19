package rutaverde.modelo.voucher;

import rutaverde.modelo.Reserva;

public class DetalleKiosco implements DetalleVoucher{

    @Override
    public String generar(Reserva r) {
        String fecha = r.getFechaViaje().toString().replace("-", "");
        String codigo = "RV-" + String.format("%05d", r.getNumero()) + "-" + fecha;
        return "Reserva " + r.getNumero() + " | " + r.getCliente().getNombreCompleto()
                + " | " + r.getProducto().getNombre() + " | " + r.getFechaViaje()
                + " | " + r.getViajeros() + " viajeros | Código: " + codigo;
    }
}
