package rutaverde.servicio;

import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.Producto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ControlCupos {

    private final Map<String, Integer> reservados = new HashMap<>();

    public boolean hayCupo(Producto p, LocalDate fecha, int viajeros) {
        for (Actividad a : p.getActividades()) {
            if (cuposDisponibles(a, fecha) < viajeros) {
                return false;
            }
        }
        return true;
    }


    public void descontar(Producto p, LocalDate fecha, int viajeros) {
        for (Actividad a : p.getActividades()) {
            reservados.merge(clave(a, fecha), viajeros, Integer::sum);
        }
    }


    public int cuposDisponibles(Actividad a, LocalDate fecha) {
        return a.getCupoMaximoPorDia() - reservados.getOrDefault(clave(a, fecha), 0);
    }

    private String clave(Actividad a, LocalDate fecha) {
        return a.getCodigo() + "|" + fecha;
    }
}
