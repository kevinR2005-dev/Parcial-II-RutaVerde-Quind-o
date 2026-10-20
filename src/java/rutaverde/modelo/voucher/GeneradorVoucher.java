package rutaverde.modelo.voucher;

import rutaverde.modelo.Agencia;
import rutaverde.modelo.Reserva;

public abstract class GeneradorVoucher {

    private final Agencia agencia;

    protected GeneradorVoucher(Agencia agencia) {

        this.agencia = agencia;
    }

    public final String emitir(Reserva r) {
        DetalleVoucher detalle = crearDetalle();
        return crearEncabezado() + "\n" + detalle.generar(r) + "\n" + crearPie();
    }
    protected abstract DetalleVoucher crearDetalle();

    private String crearEncabezado() {
        return agencia.getNombreComercial() + " - RNT " + agencia.getRnt();
    }

    private String crearPie() {
        return "Política de cancelación: sin costo hasta 48 horas antes del viaje.";
    }
}
