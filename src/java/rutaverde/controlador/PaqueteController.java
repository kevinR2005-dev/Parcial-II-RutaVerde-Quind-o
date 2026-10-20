package rutaverde.controlador;

import rutaverde.servicio.CatalogoServicio;

public class PaqueteController {
    private final CatalogoServicio catalogo;

    public PaqueteController(CatalogoServicio catalogo) {
        this.catalogo = catalogo;
    }
}