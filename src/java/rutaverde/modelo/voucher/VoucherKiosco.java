package rutaverde.modelo.voucher;

import rutaverde.modelo.Agencia;

public class VoucherKiosco extends GeneradorVoucher {

    public VoucherKiosco(Agencia agencia) {
        super(agencia);
    }

    @Override
    protected DetalleVoucher crearDetalle() {

        return new DetalleKiosco();
    }
}
