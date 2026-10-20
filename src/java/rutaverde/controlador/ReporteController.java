package rutaverde.controlador;

import rutaverde.modelo.producto.Producto;
import rutaverde.servicio.ReporteServicio;
import rutaverde.vista.ReporteVista;

import java.time.LocalDate;
import java.util.Locale;

/** Conecta la pantalla de reportes con ReporteServicio. */
public class ReporteController {

    private final ReporteServicio reportes;
    private final ReporteVista vista;

    public ReporteController(ReporteServicio reportes) {
        this.reportes = reportes;
        this.vista = new ReporteVista();
        this.vista.alCalcularIngresos(this::calcularIngresos);
        this.vista.alBuscarProductoTop(this::buscarProductoTop);
    }

    public ReporteVista getVista() {
        return vista;
    }

    public void refrescar() {
        // Los reportes se calculan al pulsar los botones; no hay nada que recargar
    }

    private void calcularIngresos() {
        LocalDate inicio = vista.getInicio();
        LocalDate fin = vista.getFin();
        try {
            double total = reportes.ingresosPorPeriodo(inicio, fin);
            vista.mostrarIngresos(String.format(Locale.forLanguageTag("es-CO"), "$%,.0f", total));
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void buscarProductoTop() {
        Producto producto = reportes.productoConMasViajeros();
        if (producto == null) {
            vista.mostrarProductoTop("No hay reservas confirmadas");
        } else {
            vista.mostrarProductoTop(producto.getNombre() + " (" + producto.getCodigo() + ")");
        }
    }
}
