package rutaverde.controlador;

import rutaverde.modelo.producto.Actividad;
import rutaverde.servicio.CatalogoServicio;
import rutaverde.servicio.ControlCupos;
import rutaverde.vista.ActividadVista;

import java.time.LocalDate;

/** Conecta la pantalla de actividades con el catálogo y el control de cupos. */
public class ActividadController {

    private final CatalogoServicio catalogo;
    private final ControlCupos cupos;
    private final ActividadVista vista;

    public ActividadController(CatalogoServicio catalogo, ControlCupos cupos) {
        this.catalogo = catalogo;
        this.cupos = cupos;
        this.vista = new ActividadVista();
        this.vista.alRegistrar(this::registrar);
        this.vista.alSuspender(this::suspender);
        this.vista.alActivar(this::activar);
        this.vista.alCambiarFechaCupos(this::refrescar);
    }

    public ActividadVista getVista() {
        return vista;
    }

    public void refrescar() {
        LocalDate fecha = vista.getFechaCupos() != null ? vista.getFechaCupos() : LocalDate.now();
        vista.mostrarActividades(catalogo.listarActividades(), a -> cupos.cuposDisponibles(a, fecha));
    }

    private void registrar() {
        try {
            Actividad actividad = new Actividad(vista.getCodigo(), vista.getNombre(), vista.getMunicipio(),
                    leerEntero(vista.getDuracion(), "La duración"),
                    leerNumero(vista.getPrecio(), "El precio"),
                    leerEntero(vista.getCupo(), "El cupo máximo"),
                    vista.getDificultad());
            catalogo.registrarActividad(actividad);
            vista.limpiarFormulario();
            refrescar();
            vista.mostrarMensaje("Actividad registrada: " + actividad.getNombre());
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void suspender() {
        Actividad a = vista.getActividadSeleccionada();
        if (a == null) {
            vista.mostrarError("Seleccione una actividad de la tabla");
            return;
        }
        a.suspender();
        refrescar();
        vista.mostrarMensaje("Actividad suspendida: " + a.getNombre());
    }

    private void activar() {
        Actividad a = vista.getActividadSeleccionada();
        if (a == null) {
            vista.mostrarError("Seleccione una actividad de la tabla");
            return;
        }
        a.activar();
        refrescar();
        vista.mostrarMensaje("Actividad activada: " + a.getNombre());
    }

    private int leerEntero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número entero");
        }
    }

    private double leerNumero(String texto, String campo) {
        try {
            return Double.parseDouble(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número");
        }
    }
}
