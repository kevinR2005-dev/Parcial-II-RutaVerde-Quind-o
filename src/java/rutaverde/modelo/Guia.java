package rutaverde.modelo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Guia {

    private static final double HORAS_POR_DIA = 8.0;

    private final String identificacion;
    private final String nombre;
    private final Set<String> idiomas;
    private final String telefono;
    private final double tarifaPorDia;

    public Guia(String identificacion, String nombre, Set<String> idiomas,
                String telefono, double tarifaPorDia) {
        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException("La identificación del guía es obligatoria");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del guía es obligatorio");
        }
        if (idiomas == null || idiomas.isEmpty()) {
            throw new IllegalArgumentException("El guía debe hablar al menos un idioma");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono del guía es obligatorio");
        }
        if (tarifaPorDia < 0) {
            throw new IllegalArgumentException("La tarifa por día no puede ser negativa");
        }
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.idiomas = new HashSet<>(idiomas); // copia defensiva
        this.telefono = telefono;
        this.tarifaPorDia = tarifaPorDia;
    }


    public boolean hablaIdioma(String idioma) {
        if (idioma == null) {
            return false;
        }
        for (String i : idiomas) {
            if (i.equalsIgnoreCase(idioma.trim())) {
                return true;
            }
        }
        return false;
    }


    public double calcularCosto(int horasProducto) {
        if (horasProducto < 0) {
            throw new IllegalArgumentException("Las horas no pueden ser negativas");
        }
        double dias = Math.ceil(horasProducto / HORAS_POR_DIA);
        return dias * tarifaPorDia;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public Set<String> getIdiomas() {
        return Collections.unmodifiableSet(idiomas);
    }

    public String getTelefono() {
        return telefono;
    }

    public double getTarifaPorDia() {
        return tarifaPorDia;
    }
}