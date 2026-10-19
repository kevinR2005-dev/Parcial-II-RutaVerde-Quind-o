package rutaverde.modelo.voucher;

import rutaverde.modelo.Agencia;
import rutaverde.modelo.Reserva;

public abstract class GeneradorVoucher {

    protected GeneradorVoucher(Agencia agencia) {
        this.agencia = agencia;
    }

    /** Estructura común a todos los canales; no se puede sobrescribir. */
    public final String emitir(Reserva r) {
        DetalleVoucher detalle = crearDetalle();      // <-- no hay "new" aquí
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
