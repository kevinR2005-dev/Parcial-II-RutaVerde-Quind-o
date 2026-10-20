package rutaverde.servicio;

import rutaverde.excepcion.EntidadNoEncontradaException;
import rutaverde.modelo.Cliente;
import rutaverde.modelo.Guia;
import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.Paquete;
import rutaverde.modelo.producto.Producto;
import rutaverde.repositorio.Repositorio;

import java.util.List;

public class CatalogoServicio {

    private final Repositorio<Cliente, String> clientes;
    private final Repositorio<Guia, String> guias;
    private final Repositorio<Actividad, String> actividades;
    private final Repositorio<Paquete, String> paquetes;

    public CatalogoServicio(Repositorio<Cliente, String> clientes, Repositorio<Guia, String> guias,
                            Repositorio<Actividad, String> actividades, Repositorio<Paquete, String> paquetes) {
        this.clientes = clientes;
        this.guias = guias;
        this.actividades = actividades;
        this.paquetes = paquetes;
    }

    // ---------- Registro ----------

    public void registrarCliente(Cliente c) {
        if (c == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        clientes.guardar(c);
    }

    public void registrarGuia(Guia g) {
        if (g == null) {
            throw new IllegalArgumentException("El guía no puede ser nulo");
        }
        guias.guardar(g);
    }

    public void registrarActividad(Actividad a) {
        if (a == null) {
            throw new IllegalArgumentException("La actividad no puede ser nula");
        }
        actividades.guardar(a);
    }

    public void registrarPaquete(Paquete p) {
        if (p == null) {
            throw new IllegalArgumentException("El paquete no puede ser nulo");
        }
        paquetes.guardar(p);
    }

    // ---------- Búsqueda ----------

    public Producto buscarProducto(String codigo) {
        try {
            return actividades.buscarPorId(codigo);
        } catch (EntidadNoEncontradaException e) {
            // No es una actividad: se intenta con los paquetes
        }
        try {
            return paquetes.buscarPorId(codigo);
        } catch (EntidadNoEncontradaException e) {
            throw new EntidadNoEncontradaException("No existe un producto con el código " + codigo);
        }
    }

    public Cliente buscarCliente(String documento) {
        return clientes.buscarPorId(documento);
    }

    public Guia buscarGuia(String identificacion) {
        return guias.buscarPorId(identificacion);
    }

    // ---------- Listados (los usan las pantallas) ----------

    public List<Cliente> listarClientes() {
        return clientes.listar();
    }

    public List<Guia> listarGuias() {
        return guias.listar();
    }

    public List<Actividad> listarActividades() {
        return actividades.listar();
    }

    public List<Paquete> listarPaquetes() {
        return paquetes.listar();
    }
}