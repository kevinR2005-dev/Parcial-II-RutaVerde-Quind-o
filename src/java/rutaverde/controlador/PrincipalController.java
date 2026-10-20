package rutaverde.controlador;

import javafx.stage.Stage;
import rutaverde.modelo.Agencia;
import rutaverde.vista.VentanaPrincipal;

/** Arma la ventana principal con una pestaña por módulo y la abre. */
public class PrincipalController {

    private final Agencia agencia;
    private final ClienteController clientes;
    private final GuiaController guias;
    private final ActividadController actividades;
    private final PaqueteController paquetes;
    private final ReservaController reservas;
    private final ReporteController reportes;

    public PrincipalController(Agencia agencia, ClienteController clientes, GuiaController guias,
                               ActividadController actividades, PaqueteController paquetes,
                               ReservaController reservas, ReporteController reportes) {
        this.agencia = agencia;
        this.clientes = clientes;
        this.guias = guias;
        this.actividades = actividades;
        this.paquetes = paquetes;
        this.reservas = reservas;
        this.reportes = reportes;
    }

    public void iniciar(Stage stage) {
        VentanaPrincipal ventana = new VentanaPrincipal(agencia.getNombreComercial(),
                "Gestión de clientes, guías, actividades, paquetes y reservas  |  " + "RNT " + agencia.getRnt());

        // Cada pestaña recarga sus datos al entrar, para ver siempre la información actual
        ventana.agregarPestana("Clientes", clientes.getVista().getRaiz(), clientes::refrescar);
        ventana.agregarPestana("Guías", guias.getVista().getRaiz(), guias::refrescar);
        ventana.agregarPestana("Actividades", actividades.getVista().getRaiz(), actividades::refrescar);
        ventana.agregarPestana("Paquetes", paquetes.getVista().getRaiz(), paquetes::refrescar);
        ventana.agregarPestana("Reservas", reservas.getVista().getRaiz(), reservas::refrescar);
        ventana.agregarPestana("Reportes", reportes.getVista().getRaiz(), reportes::refrescar);

        // Carga inicial de todas las pantallas
        clientes.refrescar();
        guias.refrescar();
        actividades.refrescar();
        paquetes.refrescar();
        reservas.refrescar();

        ventana.mostrar(stage);
    }
}
