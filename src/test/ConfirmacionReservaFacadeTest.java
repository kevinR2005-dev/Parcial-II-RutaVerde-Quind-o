import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import rutaverde.excepcion.ConfirmacionFallidaException;
import rutaverde.modelo.Agencia;
import rutaverde.modelo.Cliente;
import rutaverde.modelo.EstadoReserva;
import rutaverde.modelo.Reserva;
import rutaverde.modelo.pago.PasarelaPago;
import rutaverde.modelo.pago.ResultadoPago;
import rutaverde.modelo.pago.ValidadorTarjeta;
import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.NivelDificultad;
import rutaverde.modelo.producto.Paquete;
import rutaverde.modelo.producto.Producto;
import rutaverde.modelo.voucher.VoucherAsesor;
import rutaverde.repositorio.RepositorioMemoria;
import rutaverde.servicio.ConfirmacionReservaFacade;
import rutaverde.servicio.ConsecutivoReserva;
import rutaverde.servicio.ControlCupos;
import rutaverde.servicio.Notificador;
import rutaverde.servicio.ReservaServicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertThrows;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;


public class ConfirmacionReservaFacadeTest {

    private static final String TARJETA_VALIDA = "4539 1488 0343 6467";
    private static final String TARJETA_INVALIDA = "4539 1488 0343 6468";

    private final Agencia agencia = new Agencia("RutaVerde Quindío", "900123456-7", "RNT-12345",
            "Calle 1 # 2-3, Armenia", "6067000000", "info@rutaverde.co", "www.rutaverde.co");
    private final LocalDate fecha = LocalDate.now().plusDays(10);

    private ControlCupos cupos;
    private List<String> correos;
    private int cobros;
    private ResultadoPago respuestaPasarela;
    private ConfirmacionReservaFacade facade;
    private Cliente cliente;
    private Actividad aves;

    @BeforeMethod
    public void preparar() {
        cupos = new ControlCupos();
        ReservaServicio reservas = new ReservaServicio(new RepositorioMemoria<Reserva, Integer>(Reserva::getNumero));
        correos = new ArrayList<>();
        cobros = 0;
        respuestaPasarela = ResultadoPago.aprobado("PA-1001");

        PasarelaPago pasarelaFalsa = (tarjeta, valor, ref) -> {
            cobros++;
            return respuestaPasarela;
        };
        Notificador notificadorFalso = (destino, asunto, mensaje) -> correos.add(destino + "|" + asunto + "|" + mensaje);

        facade = new ConfirmacionReservaFacade(cupos, new ValidadorTarjeta(), pasarelaFalsa,
                new ConsecutivoReserva(), reservas, notificadorFalso);

        cliente = new Cliente("1094", "Ana Pérez", "3001112233", "ana@correo.com", "Colombia", "Español");
        aves = new Actividad("ACT1", "Avistamiento de aves", "Salento", 4, 50000, 10, NivelDificultad.BAJA);
    }

    private Reserva reservaDe(Producto producto, int viajeros) {
        return new Reserva.Builder().conCliente(cliente).conProducto(producto)
                .conFecha(fecha).conViajeros(viajeros).build();
    }

    @Test
    public void confirmacionExitosaEjecutaTodosLosPasos() {
        Reserva r = reservaDe(aves, 3);
        String voucher = facade.confirmar(r, TARJETA_VALIDA, new VoucherAsesor(agencia));

        assertEquals(EstadoReserva.CONFIRMADA, r.getEstado());
        assertEquals(1, r.getNumero());
        assertEquals(1, cobros);
        assertEquals(7, cupos.cuposDisponibles(aves, fecha)); // 10 - 3
        assertEquals(1, correos.size());
        assertTrue(correos.get(0).startsWith("ana@correo.com|Confirmación de reserva No. 1|"));
        assertTrue(voucher.contains("Avistamiento de aves"));
    }

    @Test
    public void tarjetaInvalidaNoCobraNiDescuentaCupos() {
        Reserva r = reservaDe(aves, 3);
        try {
            facade.confirmar(r, TARJETA_INVALIDA, new VoucherAsesor(agencia));
            org.testng.Assert.fail("Debió lanzar ConfirmacionFallidaException");
        } catch (ConfirmacionFallidaException e) {
            assertEquals("Número de tarjeta inválido", e.getMessage());
        }
        assertEquals(0, cobros);
        assertEquals(10, cupos.cuposDisponibles(aves, fecha));
        assertEquals(EstadoReserva.PENDIENTE, r.getEstado());
        assertTrue(correos.get(0).contains("Número de tarjeta inválido"));
    }

    @Test
    public void pagoRechazadoNoDescuentaCuposNiAsignaNumero() {
        respuestaPasarela = ResultadoPago.rechazado("Fondos insuficientes");
        Reserva r = reservaDe(aves, 3);
        assertThrows(ConfirmacionFallidaException.class,
                () -> facade.confirmar(r, TARJETA_VALIDA, new VoucherAsesor(agencia)));

        assertEquals(1, cobros);
        assertEquals(10, cupos.cuposDisponibles(aves, fecha));
        assertEquals(EstadoReserva.PENDIENTE, r.getEstado());
        assertEquals(1, correos.size());
        assertTrue(correos.get(0).contains("Fondos insuficientes"));
    }

    @Test
    public void sinCupoNoSeLlegaANiSiquieraACobrar() {
        Reserva r = reservaDe(aves, 11);
        assertThrows(ConfirmacionFallidaException.class,
                () -> facade.confirmar(r, TARJETA_VALIDA, new VoucherAsesor(agencia)));
        assertEquals(0, cobros);
    }

    @Test
    public void paqueteSinCupoEnUnaActividadNoDescuentaNingunCupo() {
        Actividad poco = new Actividad("ACT2", "Visita a finca", "Montenegro", 3, 40000, 2, NivelDificultad.MEDIA);
        Paquete paquete = new Paquete("PQ1", "Paquete Salento", 10);
        paquete.agregar(aves);
        paquete.agregar(poco);

        Reserva r = reservaDe(paquete, 3);
        assertThrows(ConfirmacionFallidaException.class,
                () -> facade.confirmar(r, TARJETA_VALIDA, new VoucherAsesor(agencia)));

        assertEquals(10, cupos.cuposDisponibles(aves, fecha)); // ninguna actividad se descontó
        assertEquals(2, cupos.cuposDisponibles(poco, fecha));
        assertEquals(0, cobros);
    }

    @Test
    public void losConsecutivosSonCrecientes() {
        Reserva r1 = reservaDe(aves, 1);
        Reserva r2 = reservaDe(aves, 1);
        facade.confirmar(r1, TARJETA_VALIDA, new VoucherAsesor(agencia));
        facade.confirmar(r2, TARJETA_VALIDA, new VoucherAsesor(agencia));

        assertEquals(1, r1.getNumero());
        assertEquals(2, r2.getNumero());
    }

    @Test
    public void noSePuedeConfirmarDosVecesLaMismaReserva() {
        Reserva r = reservaDe(aves, 1);
        facade.confirmar(r, TARJETA_VALIDA, new VoucherAsesor(agencia));
        assertThrows(ConfirmacionFallidaException.class,
                () -> facade.confirmar(r, TARJETA_VALIDA, new VoucherAsesor(agencia)));
        assertEquals(1, cobros);
    }
}
