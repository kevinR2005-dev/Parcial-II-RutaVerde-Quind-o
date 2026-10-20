package rutaverde.modelo.voucher;

import rutaverde.modelo.Agencia;

public class VoucherAsesor extends GeneradorVoucher {

    public VoucherAsesor(Agencia agencia) {
        super(agencia);
    }

    @Override
    protected DetalleVoucher crearDetalle() {

        return new DetalleCompleto();
    }

}
