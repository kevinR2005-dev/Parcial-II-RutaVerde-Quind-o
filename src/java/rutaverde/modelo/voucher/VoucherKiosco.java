package rutaverde.modelo.voucher;

public class VoucherKiosco extends GeneradorVoucher {

    @Override
    protected DetalleVoucher crearDetalle() {
        return new DetalleKiosco();
    }
}
