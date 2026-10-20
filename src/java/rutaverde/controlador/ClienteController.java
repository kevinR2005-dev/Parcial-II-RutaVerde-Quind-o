package rutaverde.controlador;

import rutaverde.servicio.CatalogoServicio;

public class ClienteController {
    private final CatalogoServicio catalogo;

    public ClienteController(CatalogoServicio catalogo) {
        this.catalogo = catalogo;
    }
}