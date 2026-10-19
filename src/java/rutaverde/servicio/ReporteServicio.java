package rutaverde.servicio;

import rutaverde.modelo.Reserva;
import rutaverde.modelo.producto.Producto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;




public class ReporteServicio {

    private final ReservaServicio reservas;

    public ReporteServicio(ReservaServicio reservas) {
        if (reservas == null) {
            throw new IllegalArgumentException("El servicio de reservas es obligatorio");
        }
        this.reservas = reservas;
    }


    public double ingresosPorPeriodo(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Las fechas inicial y final son obligatorias");
        }
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial");
        }
        double total = 0;
        for (Reserva r : reservas.listarConfirmadas()) {
            LocalDate fecha = r.getFechaViaje();
            // "no antes del inicio y no después del fin" = extremos incluidos
            if (!fecha.isBefore(inicio) && !fecha.isAfter(fin)) {
                total += r.calcularValor();
            }
        }
        return total;
    }


    public Producto productoConMasViajeros() {

        Map<String, Integer> viajerosPorCodigo = new LinkedHashMap<>();
        Map<String, Producto> productos = new LinkedHashMap<>();
        for (Reserva r : reservas.listarConfirmadas()) {
            String codigo = r.getProducto().getCodigo();
            viajerosPorCodigo.merge(codigo, r.getViajeros(), Integer::sum);
            productos.putIfAbsent(codigo, r.getProducto());
        }
        Producto mejor = null;
        int maximo = 0;
        for (Map.Entry<String, Integer> e : viajerosPorCodigo.entrySet()) {
            if (e.getValue() > maximo) {
                maximo = e.getValue();
                mejor = productos.get(e.getKey());
            }
        }
        return mejor;
    }
}
