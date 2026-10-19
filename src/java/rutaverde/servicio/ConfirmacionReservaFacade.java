package rutaverde.servicio;

import rutaverde.excepcion.ConfirmacionFallidaException;
import rutaverde.modelo.EstadoReserva;
import rutaverde.modelo.Reserva;
import rutaverde.modelo.pago.PasarelaPago;
import rutaverde.modelo.pago.ResultadoPago;
import rutaverde.modelo.pago.ValidadorTarjeta;
import rutaverde.modelo.voucher.GeneradorVoucher;

public class ConfirmacionReservaFacade {
    private final ControlCupos cupos;
    private final ValidadorTarjeta validador;
    private final PasarelaPago pasarela;
    private final ConsecutivoReserva consecutivo;
    private final ReservaServicio reservas;
    private final Notificador notificador;

    public ConfirmacionReservaFacade(ControlCupos cupos, ValidadorTarjeta validador,
                                     PasarelaPago pasarela, ConsecutivoReserva consecutivo,
                                     ReservaServicio reservas, Notificador notificador) {
        this.cupos = cupos;
        this.validador = validador;
        this.pasarela = pasarela;
        this.consecutivo = consecutivo;
        this.reservas = reservas;
        this.notificador = notificador;
    }

    /**
     * Confirma la reserva en el orden exacto del enunciado y devuelve el voucher.
     * Si falla un paso, envía un correo con el motivo, lanza
     * ConfirmacionFallidaException y no descuenta ningún cupo.
     */
    public String confirmar(Reserva r, String numeroTarjeta, GeneradorVoucher generador) {
        if (r.getEstado() == EstadoReserva.CONFIRMADA) {
            fallar(r, "La reserva ya fue confirmada");
        }

        // 1) Verificar cupo para todas las actividades del producto
        if (!cupos.hayCupo(r.getProducto(), r.getFechaViaje(), r.getViajeros())) {
            fallar(r, "No hay cupo suficiente para la fecha del viaje");
        }

        // 2) Validar la tarjeta (Luhn)
        if (!validador.esValida(numeroTarjeta)) {
            fallar(r, "Número de tarjeta inválido");
        }

        // 3) Cobrar en la pasarela
        String referencia = "RV-" + r.getCliente().getDocumento() + "-" + r.getFechaViaje();
        ResultadoPago pago = pasarela.cobrar(numeroTarjeta, r.calcularValor(), referencia);
        if (!pago.isAprobado()) {
            fallar(r, "Pago rechazado: " + pago.getMotivo());
        }

        // 4) Descontar los cupos (solo si todo lo anterior salió bien)
        cupos.descontar(r.getProducto(), r.getFechaViaje(), r.getViajeros());

        // 5) Registrar la reserva con su número consecutivo
        r.confirmar(consecutivo.siguiente());
        reservas.registrar(r);

        // 6) Emitir el voucher según el canal
        String voucher = generador.emitir(r);

        // 7) Enviarlo al cliente por correo
        notificador.enviar(r.getCliente().getCorreo(),
                "Confirmación de reserva No. " + r.getNumero(), voucher);
        return voucher;
    }

    private void fallar(Reserva r, String motivo) {
        notificador.enviar(r.getCliente().getCorreo(),
                "No se pudo confirmar tu reserva",
                "No fue posible confirmar tu reserva. Motivo: " + motivo);
        throw new ConfirmacionFallidaException(motivo);
    }
}
