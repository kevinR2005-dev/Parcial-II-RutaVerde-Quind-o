package rutaverde.modelo.voucher;

public class VoucherAsesor extends GeneradorVoucher {

    @Override
    protected DetalleVoucher crearDetalle() {
        return new DetalleCompleto();
    }

}
