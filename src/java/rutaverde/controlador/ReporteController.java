package rutaverde.controlador;

import rutaverde.servicio.ReporteServicio;

public class ReporteController {
    private final ReporteServicio reportes;

    public ReporteController(ReporteServicio reportes) {
        this.reportes = reportes;
    }
}