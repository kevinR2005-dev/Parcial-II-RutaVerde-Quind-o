package rutaverde.controlador;

import rutaverde.excepcion.ConfirmacionFallidaException;
import rutaverde.modelo.Reserva;
import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.Producto;
import rutaverde.modelo.voucher.GeneradorVoucher;
import rutaverde.servicio.CatalogoServicio;
import rutaverde.servicio.ConfirmacionReservaFacade;
import rutaverde.servicio.ControlCupos;
import rutaverde.servicio.ReservaServicio;
import rutaverde.vista.ReservaVista;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Conecta la pantalla de reservas con los servicios. Para confirmar solo llama al Facade:
 * no repite los 7 pasos. El voucher depende del canal elegido (asesor o kiosco).
 */
public class ReservaController {

    private final CatalogoServicio catalogo;
    private final ReservaServicio reservas;
    private final ControlCupos cupos;
    private final ConfirmacionReservaFacade facade;
    private final GeneradorVoucher voucherAsesor;
    private final GeneradorVoucher voucherKiosco;
    private final ReservaVista vista;

    public ReservaController(CatalogoServicio catalogo, ReservaServicio reservas, ControlCupos cupos,
                             ConfirmacionReservaFacade facade, GeneradorVoucher voucherAsesor,
                             GeneradorVoucher voucherKiosco) {
        this.catalogo = catalogo;
        this.reservas = reservas;
        this.cupos = cupos;
        this.facade = facade;
        this.voucherAsesor = voucherAsesor;
        this.voucherKiosco = voucherKiosco;
        this.vista = new ReservaVista();
        this.vista.alConsultarCupos(this::consultarCupos);
        this.vista.alCalcular(this::calcular);
        this.vista.alConfirmar(this::confirmar);
    }

    public ReservaVista getVista() {
        return vista;
    }

    public void refrescar() {
        vista.mostrarClientes(catalogo.listarClientes());
        List<Producto> productos = new ArrayList<>(catalogo.listarActividades());
        productos.addAll(catalogo.listarPaquetes());
        vista.mostrarProductos(productos);
        vista.mostrarGuias(catalogo.listarGuias());
        vista.mostrarReservas(reservas.listarConfirmadas());
    }

    // Muestra los cupos disponibles de cada actividad del producto en la fecha elegida
    private void consultarCupos() {
        Producto producto = vista.getProducto();
        LocalDate fecha = vista.getFecha();
        if (producto == null || fecha == null) {
            vista.mostrarError("Seleccione un producto y una fecha");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cupos disponibles el ").append(fecha).append(" para ").append(producto.getNombre()).append(":\n\n");
        for (Actividad a : producto.getActividades()) {
            sb.append("- ").append(a.getNombre()).append(": ")
                    .append(cupos.cuposDisponibles(a, fecha)).append(" de ")
                    .append(a.getCupoMaximoPorDia()).append("\n");
        }
        vista.mostrarResultado(sb.toString());
    }

    private void calcular() {
        try {
            Reserva r = construirReserva();
            vista.mostrarResultado("Valor de la reserva: " + pesos(r.calcularValor())
                    + "\n(precio x viajeros + guía + transporte + seguro)");
            vista.mostrarMensaje("Valor calculado. Falta confirmar la reserva.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void confirmar() {
        try {
            Reserva r = construirReserva();
            GeneradorVoucher generador = vista.isCanalKiosco() ? voucherKiosco : voucherAsesor;
            String voucher = facade.confirmar(r, vista.getTarjeta(), generador);
            vista.mostrarResultado(voucher);
            vista.limpiarTarjeta();
            refrescar();
            vista.mostrarMensaje("Reserva No. " + r.getNumero() + " confirmada. Valor: " + pesos(r.calcularValor()));
        } catch (ConfirmacionFallidaException e) {
            vista.mostrarResultado("No se pudo confirmar la reserva.\nMotivo: " + e.getMessage()
                    + "\n\nSe envió un correo al cliente con el motivo. No se descontaron cupos.");
            vista.mostrarError("No se pudo confirmar: " + e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Arma la reserva con el Builder; sus validaciones lanzan IllegalStateException
    private Reserva construirReserva() {
        if (vista.isGuiaSolicitado() && vista.getGuia() == null) {
            throw new IllegalArgumentException("Seleccione el guía o desmarque 'Incluir guía'");
        }
        return new Reserva.Builder()
                .conCliente(vista.getCliente())
                .conProducto(vista.getProducto())
                .conFecha(vista.getFecha())
                .conViajeros(leerViajeros())
                .conGuia(vista.getGuia())
                .conTransporte(vista.isTransporte())
                .conSeguro(vista.isSeguro())
                .conObservaciones(vista.getObservaciones())
                .build();
    }

    private int leerViajeros() {
        try {
            return Integer.parseInt(vista.getViajeros().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El número de viajeros debe ser un número entero");
        }
    }

    private String pesos(double valor) {
        return String.format(Locale.forLanguageTag("es-CO"), "$%,.0f", valor);
    }
}
