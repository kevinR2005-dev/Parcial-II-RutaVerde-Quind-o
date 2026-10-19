import org.testng.annotations.Test;
import rutaverde.modelo.pago.PagoAndinoAPI;
import rutaverde.modelo.pago.PagoAndinoAdapter;
import rutaverde.modelo.pago.ResultadoPago;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class PagoAndinoAdapterTest {

    private static class ApiFalsa extends PagoAndinoAPI {
        long centavosRecibidos;
        final int codigo;

        ApiFalsa(int codigo) {
            this.codigo = codigo;
        }

        @Override
        public int procesarCobro(String numeroTarjeta, long valorCentavos, String referencia) {
            centavosRecibidos = valorCentavos;
            return codigo;
        }
    }

    @Test
    public void conviertePesosACentavos() {
        ApiFalsa api = new ApiFalsa(0);
        new PagoAndinoAdapter(api).cobrar("4539148803436467", 1_000_000, "REF-1");
        assertEquals(100_000_000L, api.centavosRecibidos);
    }

    @Test
    public void codigoCeroEsAprobadoConAutorizacion() {
        ResultadoPago r = new PagoAndinoAdapter(new PagoAndinoAPI()).cobrar("4539148803436467", 500_000, "REF-2");
        assertTrue(r.isAprobado());
        assertEquals("PA-1001", r.getAutorizacion());
    }

    @Test
    public void codigo51EsFondosInsuficientes() {
        ResultadoPago r = new PagoAndinoAdapter(new ApiFalsa(51)).cobrar("4539148803436467", 1000, "R");
        assertFalse(r.isAprobado());
        assertEquals("Fondos insuficientes", r.getMotivo());
    }

    @Test
    public void codigo14EsTarjetaInvalida() {
        ResultadoPago r = new PagoAndinoAdapter(new ApiFalsa(14)).cobrar("4539148803436467", 1000, "R");
        assertEquals("Tarjeta inválida", r.getMotivo());
    }

    @Test
    public void codigo91EsPasarelaNoDisponible() {
        ResultadoPago r = new PagoAndinoAdapter(new ApiFalsa(91)).cobrar("4539148803436467", 1000, "R");
        assertEquals("Pasarela no disponible", r.getMotivo());
    }

    @Test
    public void codigoDesconocidoSeReporta() {
        ResultadoPago r = new PagoAndinoAdapter(new ApiFalsa(77)).cobrar("4539148803436467", 1000, "R");
        assertFalse(r.isAprobado());
        assertEquals("Error desconocido (código 77)", r.getMotivo());
    }

    @Test
    public void montoMayorATresMillonesEsRechazadoPorLaApiReal() {
        ResultadoPago r = new PagoAndinoAdapter(new PagoAndinoAPI()).cobrar("4539148803436467", 3_000_001, "R");
        assertFalse(r.isAprobado());
        assertEquals("Fondos insuficientes", r.getMotivo());
    }
}
