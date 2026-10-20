package rutaverde.controlador;

import rutaverde.modelo.voucher.GeneradorVoucher;
import rutaverde.servicio.CatalogoServicio;
import rutaverde.servicio.ConfirmacionReservaFacade;
import rutaverde.servicio.ControlCupos;
import rutaverde.servicio.ReservaServicio;

public class ReservaController {
    private final CatalogoServicio catalogo;
    private final ReservaServicio reservas;
    private final ControlCupos cupos;
    private final ConfirmacionReservaFacade facade;
    private final GeneradorVoucher voucherAsesor;
    private final GeneradorVoucher voucherKiosco;

    public ReservaController(CatalogoServicio catalogo, ReservaServicio reservas, ControlCupos cupos,
                             ConfirmacionReservaFacade facade, GeneradorVoucher voucherAsesor,
                             GeneradorVoucher voucherKiosco) {
        this.catalogo = catalogo;
        this.reservas = reservas;
        this.cupos = cupos;
        this.facade = facade;
        this.voucherAsesor = voucherAsesor;
        this.voucherKiosco = voucherKiosco;
    }
}