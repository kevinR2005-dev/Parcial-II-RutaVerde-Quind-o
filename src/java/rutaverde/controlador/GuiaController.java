package rutaverde.controlador;

import rutaverde.modelo.Guia;
import rutaverde.servicio.CatalogoServicio;
import rutaverde.vista.GuiaVista;

import java.util.LinkedHashSet;
import java.util.Set;

/** Conecta la pantalla de guías con el catálogo. No contiene reglas de negocio. */
public class GuiaController {

    private final CatalogoServicio catalogo;
    private final GuiaVista vista;

    public GuiaController(CatalogoServicio catalogo) {
        this.catalogo = catalogo;
        this.vista = new GuiaVista();
        this.vista.alRegistrar(this::registrar);
    }

    public GuiaVista getVista() {
        return vista;
    }

    public void refrescar() {
        vista.mostrarGuias(catalogo.listarGuias());
    }

    private void registrar() {
        try {
            Guia guia = new Guia(vista.getIdentificacion(), vista.getNombre(),
                    separarIdiomas(vista.getIdiomas()), vista.getTelefono(), leerTarifa());
            catalogo.registrarGuia(guia);
            vista.limpiarFormulario();
            refrescar();
            vista.mostrarMensaje("Guía registrado: " + guia.getNombre());
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Convierte "Español, Inglés" en un conjunto de idiomas
    private Set<String> separarIdiomas(String texto) {
        Set<String> idiomas = new LinkedHashSet<>();
        if (texto != null) {
            for (String parte : texto.split(",")) {
                if (!parte.isBlank()) {
                    idiomas.add(parte.trim());
                }
            }
        }
        return idiomas;
    }

    private double leerTarifa() {
        try {
            return Double.parseDouble(vista.getTarifa().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La tarifa por día debe ser un número");
        }
    }
}
