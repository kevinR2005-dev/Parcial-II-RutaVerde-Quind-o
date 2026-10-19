package rutaverde.modelo.voucher;

import rutaverde.modelo.Reserva;
import rutaverde.modelo.producto.Actividad;

import java.util.Locale;

public class DetalleCompleto implements DetalleVoucher{

    @Override
    public String generar(Reserva r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Reserva No. ").append(r.getNumero()).append("\n");
        sb.append("Cliente: ").append(r.getCliente().getNombreCompleto())
                .append(" (").append(r.getCliente().getDocumento()).append(")\n");
        sb.append("Producto: ").append(r.getProducto().getNombre()).append("\n");
        sb.append("Actividades:\n");
        for (Actividad a : r.getProducto().getActividades()) {
            sb.append("  - ").append(a.getNombre())
                    .append(" (").append(a.getMunicipio()).append(", ")
                    .append(a.getDuracionHoras()).append(" h)\n");
        }
        sb.append("Fecha del viaje: ").append(r.getFechaViaje()).append("\n");
        sb.append("Viajeros: ").append(r.getViajeros()).append("\n");
        sb.append("Guía: ").append(r.getGuia() == null ? "No incluido" : r.getGuia().getNombre()).append("\n");
        sb.append("Transporte desde el hotel: ").append(r.isTransporte() ? "Sí" : "No").append("\n");
        sb.append("Seguro de viaje: ").append(r.isSeguro() ? "Sí" : "No").append("\n");
        String obs = r.getObservaciones();
        sb.append("Observaciones: ").append(obs == null || obs.isBlank() ? "Ninguna" : obs).append("\n");
        sb.append("Valor total: ").append(String.format(Locale.forLanguageTag("es-CO"), "$%,.0f", r.calcularValor()));
        return sb.toString();
    }
}
