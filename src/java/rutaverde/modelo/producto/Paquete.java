package rutaverde.modelo.producto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Paquete implements Producto {

    private static final double DESCUENTO_MINIMO = 0;
    private static final double DESCUENTO_MAXIMO = 20;

    private final String codigo;
    private final String nombre;
    private final double descuento; // porcentaje entre 0 y 20
    private final List<Producto> componentes = new ArrayList<>();

    public Paquete(String codigo, String nombre, double descuento) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del paquete es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del paquete es obligatorio");
        }
        if (descuento < DESCUENTO_MINIMO || descuento > DESCUENTO_MAXIMO) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 20");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.descuento = descuento;
    }


    public void agregar(Producto p) {
        if (p == null) {
            throw new IllegalArgumentException("No se puede agregar un producto nulo");
        }
        // Ciclo directo (a sí mismo) o indirecto (p ya contiene a este paquete)
        if (this.equals(p) || (p instanceof Paquete && ((Paquete) p).contiene(this))) {
            throw new IllegalArgumentException("Un paquete no puede contenerse a sí mismo, ni directa ni indirectamente");
        }
        // El mismo producto ya está en algún nivel del árbol
        if (contiene(p)) {
            throw new IllegalArgumentException("El paquete ya contiene el producto " + p.getCodigo());
        }
        // Alguna actividad de p ya está en el árbol
        List<Actividad> existentes = getActividades();
        for (Actividad a : p.getActividades()) {
            if (existentes.contains(a)) {
                throw new IllegalArgumentException("La actividad " + a.getCodigo() + " ya está incluida en el paquete");
            }
        }
        componentes.add(p);
    }


    public boolean contiene(Producto p) {
        for (Producto c : componentes) {
            if (c.equals(p)) {
                return true;
            }
            if (c instanceof Paquete && ((Paquete) c).contiene(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }


    @Override
    public double getPrecioPorPersona() {
        double suma = 0;
        for (Producto c : componentes) {
            suma += c.getPrecioPorPersona();
        }
        return suma * (1 - descuento / 100.0);
    }


    @Override
    public int getDuracionHoras() {
        int total = 0;
        for (Producto c : componentes) {
            total += c.getDuracionHoras();
        }
        return total;
    }


    @Override
    public List<Actividad> getActividades() {
        List<Actividad> todas = new ArrayList<>();
        for (Producto c : componentes) {
            todas.addAll(c.getActividades());
        }
        return todas;
    }

    public double getDescuento() {
        return descuento;
    }


    public List<Producto> getComponentes() {
        return Collections.unmodifiableList(componentes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paquete)) return false;
        return codigo.equals(((Paquete) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
