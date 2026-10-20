package rutaverde.controlador;

import rutaverde.modelo.producto.Paquete;
import rutaverde.modelo.producto.Producto;
import rutaverde.servicio.CatalogoServicio;
import rutaverde.vista.PaqueteVista;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Conecta la pantalla de paquetes con el catálogo. Las reglas (ciclos, actividad repetida,
 * descuento) las valida el Composite en el modelo; aquí solo se muestran sus mensajes.
 */
public class PaqueteController {

    private final CatalogoServicio catalogo;
    private final PaqueteVista vista;

    public PaqueteController(CatalogoServicio catalogo) {
        this.catalogo = catalogo;
        this.vista = new PaqueteVista();
        this.vista.alCrear(this::crear);
        this.vista.alAgregar(this::agregar);
        this.vista.alSeleccionarPaquete(this::mostrarDetalle);
    }

    public PaqueteVista getVista() {
        return vista;
    }

    public void refrescar() {
        vista.mostrarPaquetes(catalogo.listarPaquetes());
        List<Producto> productos = new ArrayList<>(catalogo.listarActividades());
        productos.addAll(catalogo.listarPaquetes());
        vista.mostrarProductos(productos);
        mostrarDetalle();
    }

    private void crear() {
        try {
            double descuento;
            try {
                descuento = Double.parseDouble(vista.getDescuento().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El descuento debe ser un número entre 0 y 20");
            }
            Paquete paquete = new Paquete(vista.getCodigo(), vista.getNombre(), descuento);
            catalogo.registrarPaquete(paquete);
            vista.limpiarFormulario();
            refrescar();
            vista.mostrarMensaje("Paquete creado: " + paquete.getNombre()
                    + ". Selecciónelo y agréguele actividades o paquetes.");
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void agregar() {
        Paquete paquete = vista.getPaqueteSeleccionado();
        Producto producto = vista.getProductoAAgregar();
        if (paquete == null || producto == null) {
            vista.mostrarError("Seleccione un paquete de la tabla y un producto para agregar");
            return;
        }
        try {
            paquete.agregar(producto);
            refrescar();
            vista.mostrarMensaje(producto.getNombre() + " agregado a " + paquete.getNombre());
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Muestra el contenido del paquete seleccionado como un árbol con sangría
    private void mostrarDetalle() {
        Paquete paquete = vista.getPaqueteSeleccionado();
        if (paquete == null) {
            vista.mostrarDetalle("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(paquete.getNombre()).append(" (").append(paquete.getCodigo()).append(")\n");
        sb.append("Descuento: ").append(paquete.getDescuento()).append(" %\n");
        sb.append("Precio por persona: ").append(pesos(paquete.getPrecioPorPersona())).append("\n");
        sb.append("Duración total: ").append(paquete.getDuracionHoras()).append(" horas\n");
        sb.append("Actividades incluidas: ").append(paquete.getActividades().size()).append("\n\n");
        sb.append("Contenido:\n");
        agregarComponentes(paquete, 1, sb);
        vista.mostrarDetalle(sb.toString());
    }

    private void agregarComponentes(Paquete paquete, int nivel, StringBuilder sb) {
        for (Producto c : paquete.getComponentes()) {
            sb.append("  ".repeat(nivel)).append("- ").append(c.getNombre())
                    .append(" (").append(c.getCodigo()).append(", ")
                    .append(pesos(c.getPrecioPorPersona())).append(", ")
                    .append(c.getDuracionHoras()).append(" h)\n");
            if (c instanceof Paquete) {
                agregarComponentes((Paquete) c, nivel + 1, sb);
            }
        }
    }

    private String pesos(double valor) {
        return String.format(Locale.forLanguageTag("es-CO"), "$%,.0f", valor);
    }
}
