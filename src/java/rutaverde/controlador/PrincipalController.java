package rutaverde.controlador;

import javafx.stage.Stage;
import rutaverde.modelo.Agencia;

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

    /** Abre la ventana principal. Pendiente: se completa cuando existan las vistas. */
    public void iniciar(Stage stage) {
        stage.setTitle(agencia.getNombreComercial());
        stage.show();
    }
}