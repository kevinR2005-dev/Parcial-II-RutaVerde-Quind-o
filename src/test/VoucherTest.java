
import org.testng.annotations.Test;
import rutaverde.modelo.Agencia;
import rutaverde.modelo.Cliente;
import rutaverde.modelo.Guia;
import rutaverde.modelo.Reserva;
import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.NivelDificultad;
import rutaverde.modelo.voucher.VoucherAsesor;
import rutaverde.modelo.voucher.VoucherKiosco;

import java.time.LocalDate;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
public class VoucherTest {

    private final Agencia agencia = new Agencia("RutaVerde Quindío", "900123456-7", "RNT-12345",
            "Calle 1 # 2-3, Armenia", "6067000000", "info@rutaverde.co", "www.rutaverde.co");

    private Reserva crearReservaConfirmada() {
        Cliente cliente = new Cliente("1094", "Ana Pérez", "3001112233", "ana@correo.com", "Colombia", "Español");
        Guia guia = new Guia("G1", "Luis Gómez", Set.of("Español"), "3009998877", 120000);
        Actividad aves = new Actividad("ACT1", "Avistamiento de aves", "Salento", 4, 50000, 10, NivelDificultad.BAJA);
        Reserva r = new Reserva.Builder()
                .conCliente(cliente).conProducto(aves)
                .conFecha(LocalDate.now().plusDays(10)).conViajeros(2)
                .conGuia(guia).conTransporte(true).conSeguro(true)
                .build();
        r.confirmar(1);
        return r;
    }

    @Test
    public void ambosCanalesTienenElMismoEncabezadoYPie() {
        Reserva r = crearReservaConfirmada();
        String asesor = new VoucherAsesor(agencia).emitir(r);
        String kiosco = new VoucherKiosco(agencia).emitir(r);

        String encabezado = "RutaVerde Quindío - RNT RNT-12345";
        assertTrue(asesor.startsWith(encabezado));
        assertTrue(kiosco.startsWith(encabezado));

        String pie = "sin costo hasta 48 horas antes del viaje.";
        assertTrue(asesor.endsWith(pie));
        assertTrue(kiosco.endsWith(pie));
    }

    @Test
    public void voucherDeAsesorEsDetalladoConActividadGuiaYServicios() {
        String asesor = new VoucherAsesor(agencia).emitir(crearReservaConfirmada());
        assertTrue(asesor.contains("Avistamiento de aves"));
        assertTrue(asesor.contains("Luis Gómez"));
        assertTrue(asesor.contains("Transporte desde el hotel: Sí"));
        assertTrue(asesor.contains("Seguro de viaje: Sí"));
    }

    @Test
    public void voucherDeKioscoTieneDetalleDeUnaSolaLineaConCodigo() {
        String kiosco = new VoucherKiosco(agencia).emitir(crearReservaConfirmada());
        String[] lineas = kiosco.split("\n");
        assertEquals(3, lineas.length); // encabezado + una línea de detalle + pie
        assertTrue(lineas[1].contains("Código: RV-00001-"));
    }
}
