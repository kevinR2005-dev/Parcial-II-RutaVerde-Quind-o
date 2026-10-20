package rutaverde.controlador;

import rutaverde.modelo.Cliente;
import rutaverde.servicio.CatalogoServicio;
import rutaverde.vista.ClienteVista;

/** Conecta la pantalla de clientes con el catálogo. No contiene reglas de negocio. */
public class ClienteController {

    private final CatalogoServicio catalogo;
    private final ClienteVista vista;

    public ClienteController(CatalogoServicio catalogo) {
        this.catalogo = catalogo;
        this.vista = new ClienteVista();
        this.vista.alRegistrar(this::registrar);
    }

    public ClienteVista getVista() {
        return vista;
    }

    public void refrescar() {
        vista.mostrarClientes(catalogo.listarClientes());
    }

    private void registrar() {
        try {
            // Las validaciones las hace el modelo (constructor de Cliente)
            Cliente cliente = new Cliente(vista.getDocumento(), vista.getNombre(), vista.getTelefono(),
                    vista.getCorreo(), vista.getPais(), vista.getIdioma());
            catalogo.registrarCliente(cliente);
            vista.limpiarFormulario();
            refrescar();
            vista.mostrarMensaje("Cliente registrado: " + cliente.getNombreCompleto());
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }
}
