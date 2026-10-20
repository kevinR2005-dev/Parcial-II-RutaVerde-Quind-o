package rutaverde.controlador;

import rutaverde.servicio.CatalogoServicio;
import rutaverde.servicio.ControlCupos;

public class ActividadController {
    private final CatalogoServicio catalogo;
    private final ControlCupos cupos;

    public ActividadController(CatalogoServicio catalogo, ControlCupos cupos) {
        this.catalogo = catalogo;
        this.cupos = cupos;
    }
}