package rutaverde.modelo;

import rutaverde.modelo.producto.Producto;

import java.time.LocalDate;


public class Reserva {
    public static final double TARIFA_TRANSPORTE = 35000;
    public static final double TARIFA_SEGURO = 15000;

    private int numero;
    private EstadoReserva estado = EstadoReserva.PENDIENTE;
    private final Cliente cliente; private final Producto producto; private final LocalDate fechaViaje;
    private final int viajeros; private final Guia guia;
    private final boolean transporte, seguro; private final String observaciones;

    private Reserva(Builder b) {
        cliente = b.cliente; producto = b.producto; fechaViaje = b.fecha; viajeros = b.viajeros;
        guia = b.guia; transporte = b.transporte; seguro = b.seguro; observaciones = b.observaciones;
    }

    public double calcularValor() {
        double v = producto.getPrecioPorPersona() * viajeros;
        if (guia != null) v += guia.calcularCosto(producto.getDuracionHoras());
        if (transporte) v += TARIFA_TRANSPORTE * viajeros;
        if (seguro) v += TARIFA_SEGURO * viajeros;
        return v;
    }
    public void confirmar(int numero) { this.numero = numero; this.estado = EstadoReserva.CONFIRMADA; }
    public int getNumero() { return numero; }
    public Cliente getCliente() { return cliente; }
    public Producto getProducto() { return producto; }
    public LocalDate getFechaViaje() { return fechaViaje; }
    public int getViajeros() { return viajeros; }
    public Guia getGuia() { return guia; }
    public boolean isTransporte() { return transporte; }
    public boolean isSeguro() { return seguro; }
    public String getObservaciones() { return observaciones; }
    public EstadoReserva getEstado() { return estado; }

    public static class Builder {
        private Cliente cliente; private Producto producto; private LocalDate fecha;
        private int viajeros; private Guia guia; private boolean transporte, seguro; private String observaciones;
        public Builder conCliente(Cliente c) { cliente = c; return this; }
        public Builder conProducto(Producto p) { producto = p; return this; }
        public Builder conFecha(LocalDate f) { fecha = f; return this; }
        public Builder conViajeros(int n) { viajeros = n; return this; }
        public Builder conGuia(Guia g) { guia = g; return this; }
        public Builder conTransporte(boolean t) { transporte = t; return this; }
        public Builder conSeguro(boolean s) { seguro = s; return this; }
        public Builder conObservaciones(String o) { observaciones = o; return this; }
        public Reserva build() {
            if (cliente == null || producto == null || fecha == null || viajeros == 0)
                throw new IllegalStateException("Faltan datos obligatorios de la reserva");
            if (fecha.isBefore(LocalDate.now())) throw new IllegalStateException("La fecha no puede estar en el pasado");
            if (viajeros < 1 || viajeros > 15) throw new IllegalStateException("Los viajeros deben estar entre 1 y 15");
            return new Reserva(this);
        }
    }
}
