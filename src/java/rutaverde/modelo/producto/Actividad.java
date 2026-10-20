package rutaverde.modelo.producto;

import java.util.List;
import java.util.Objects;


public class Actividad implements Producto {

    private final String codigo;
    private final String nombre;
    private final String municipio;
    private final int duracionHoras;
    private final double precioPorPersona;
    private final int cupoMaximoPorDia;
    private final NivelDificultad dificultad;
    private EstadoActividad estado;

    public Actividad(String codigo, String nombre, String municipio, int duracionHoras,
                     double precioPorPersona, int cupoMaximoPorDia, NivelDificultad dificultad) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la actividad es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la actividad es obligatorio");
        }
        if (municipio == null || municipio.isBlank()) {
            throw new IllegalArgumentException("El municipio de la actividad es obligatorio");
        }
        if (duracionHoras <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 horas");
        }
        if (precioPorPersona < 0) {
            throw new IllegalArgumentException("El precio por persona no puede ser negativo");
        }
        if (cupoMaximoPorDia <= 0) {
            throw new IllegalArgumentException("El cupo máximo por día debe ser mayor a 0");
        }
        if (dificultad == null) {
            throw new IllegalArgumentException("El nivel de dificultad es obligatorio");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.municipio = municipio;
        this.duracionHoras = duracionHoras;
        this.precioPorPersona = precioPorPersona;
        this.cupoMaximoPorDia = cupoMaximoPorDia;
        this.dificultad = dificultad;
        this.estado = EstadoActividad.DISPONIBLE; // estado inicial
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
        return precioPorPersona;
    }

    @Override
    public int getDuracionHoras() {
        return duracionHoras;
    }


    @Override
    public List<Actividad> getActividades() {
        return List.of(this);
    }

    public String getMunicipio() {
        return municipio;
    }

    public int getCupoMaximoPorDia() {
        return cupoMaximoPorDia;
    }

    public NivelDificultad getDificultad() {
        return dificultad;
    }

    public EstadoActividad getEstado() {
        return estado;
    }

    public boolean estaDisponible() {
        return estado == EstadoActividad.DISPONIBLE;
    }

    public void suspender() {
        estado = EstadoActividad.SUSPENDIDA;
    }

    public void activar() {
        estado = EstadoActividad.DISPONIBLE;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Actividad)) return false;
        return codigo.equals(((Actividad) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}