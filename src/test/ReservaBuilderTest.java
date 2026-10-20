import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import rutaverde.modelo.Cliente;
import rutaverde.modelo.EstadoReserva;
import rutaverde.modelo.Guia;
import rutaverde.modelo.Reserva;
import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.NivelDificultad;
import rutaverde.modelo.producto.Paquete;

import java.time.LocalDate;
import java.util.Set;

import static org.testng.Assert.assertThrows;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

public class ReservaBuilderTest {

    private Cliente cliente;
    private Guia guiaEspanol;
    private Guia guiaIngles;
    private Actividad cafe; // 4 h, $100.000 por persona
    private LocalDate fecha;

    @BeforeMethod
    public void preparar() {
        cliente = new Cliente("1001", "Ana Gómez", "3001234567", "ana@correo.com", "Colombia", "Español");
        guiaEspanol = new Guia("G1", "Carlos", Set.of("español", "inglés"), "3109876543", 120000);
        guiaIngles = new Guia("G2", "John", Set.of("inglés"), "3115556677", 150000);
        cafe = new Actividad("A1", "Finca cafetera", "Salento", 4, 100000, 10, NivelDificultad.BAJA);
        fecha = LocalDate.now().plusDays(7);
    }

    /** Builder con los datos obligatorios correctos; cada prueba cambia lo que necesita. */
    private Reserva.Builder builderValido() {
        return new Reserva.Builder()
                .conCliente(cliente)
                .conProducto(cafe)
                .conFecha(fecha)
                .conViajeros(2);
    }

    // ---------- Reserva válida ----------

    @Test
    public void reservaValidaNaceConEstadoPendienteYSinNumero() {
        Reserva r = builderValido().build();
        assertEquals(EstadoReserva.PENDIENTE, r.getEstado());
        assertEquals(0, r.getNumero());
        assertNull(r.getGuia());
        assertEquals("", r.getObservaciones());
    }

    // ---------- Datos obligatorios ----------

    @Test
    public void sinClienteLanzaExcepcion() {
        Reserva.Builder b = new Reserva.Builder().conProducto(cafe).conFecha(fecha).conViajeros(2);
        assertThrows(IllegalStateException.class, b::build);
    }

    @Test
    public void sinProductoLanzaExcepcion() {
        Reserva.Builder b = new Reserva.Builder().conCliente(cliente).conFecha(fecha).conViajeros(2);
        assertThrows(IllegalStateException.class, b::build);
    }

    @Test
    public void sinFechaLanzaExcepcion() {
        Reserva.Builder b = new Reserva.Builder().conCliente(cliente).conProducto(cafe).conViajeros(2);
        assertThrows(IllegalStateException.class, b::build);
    }

    @Test
    public void sinViajerosLanzaExcepcion() {
        Reserva.Builder b = new Reserva.Builder().conCliente(cliente).conProducto(cafe).conFecha(fecha);
        assertThrows(IllegalStateException.class, b::build);
    }

    // ---------- Viajeros entre 1 y 15 ----------

    @Test
    public void ceroViajerosLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> builderValido().conViajeros(0).build());
    }

    @Test
    public void dieciseisViajerosLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> builderValido().conViajeros(16).build());
    }

    @Test
    public void viajerosLimiteSonValidos() {
        assertEquals(1, builderValido().conViajeros(1).build().getViajeros());
        assertEquals(15, builderValido().conViajeros(15).build().getViajeros());
    }

    // ---------- Fecha ----------

    @Test
    public void fechaEnElPasadoLanzaExcepcion() {
        assertThrows(IllegalStateException.class,
                () -> builderValido().conFecha(LocalDate.now().minusDays(1)).build());
    }

    @Test
    public void fechaDeHoyEsValida() {
        Reserva r = builderValido().conFecha(LocalDate.now()).build();
        assertEquals(LocalDate.now(), r.getFechaViaje());
    }

    // ---------- Guía ----------

    @Test
    public void guiaQueNoHablaElIdiomaDelClienteLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> builderValido().conGuia(guiaIngles).build());
    }

    @Test
    public void guiaQueHablaElIdiomaSinDistinguirMayusculasEsValido() {
        // El cliente prefiere "Español" y el guía registró "español"
        Reserva r = builderValido().conGuia(guiaEspanol).build();
        assertEquals(guiaEspanol, r.getGuia());
    }

    // ---------- Actividades suspendidas ----------

    @Test
    public void actividadSuspendidaLanzaExcepcion() {
        cafe.suspender();
        assertThrows(IllegalStateException.class, () -> builderValido().build());
    }

    @Test
    public void paqueteConActividadSuspendidaLanzaExcepcion() {
        Actividad cascada = new Actividad("A2", "Cascada", "Salento", 2, 50000, 10, NivelDificultad.MEDIA);
        Paquete salento = new Paquete("PS", "Paquete Salento", 0);
        salento.agregar(cafe);
        salento.agregar(cascada);
        cascada.suspender();
        assertThrows(IllegalStateException.class, () -> builderValido().conProducto(salento).build());
    }

    // ---------- Cálculo del valor ----------

    @Test
    public void valorSinOpcionalesEsPrecioPorViajeros() {
        Reserva r = builderValido().conViajeros(3).build();
        assertEquals(300000.0, r.calcularValor(), 0.001);
    }

    @Test
    public void valorConGuiaTransporteYSeguro() {
        // 100.000 x 3 = 300.000
        // guía: 4 h -> 1 día x 120.000 = 120.000
        // transporte: 35.000 x 3 = 105.000
        // seguro: 15.000 x 3 = 45.000
        Reserva r = builderValido()
                .conViajeros(3)
                .conGuia(guiaEspanol)
                .conTransporte(true)
                .conSeguro(true)
                .build();
        assertEquals(570000.0, r.calcularValor(), 0.001);
    }

    @Test
    public void costoDelGuiaUsaTechoDeDiasYNoSeMultiplicaPorViajeros() {
        // Actividad de 9 h -> ⌈9/8⌉ = 2 días x 120.000 = 240.000 (una sola vez)
        Actividad larga = new Actividad("A5", "Travesía", "Génova", 9, 10000, 10, NivelDificultad.ALTA);
        Reserva r = builderValido()
                .conProducto(larga)
                .conViajeros(5)
                .conGuia(guiaEspanol)
                .build();
        // 10.000 x 5 = 50.000 + 240.000 = 290.000
        assertEquals(290000.0, r.calcularValor(), 0.001);
    }

    @Test
    public void valorConPaqueteUsaElPrecioConDescuento() {
        Paquete p = new Paquete("P1", "Paquete", 10);
        p.agregar(cafe); // 100.000 * 0.9 = 90.000 por persona
        Reserva r = builderValido().conProducto(p).conViajeros(2).build();
        assertEquals(180000.0, r.calcularValor(), 0.001);
    }

    // ---------- Confirmación ----------

    @Test
    public void confirmarAsignaNumeroYEstadoConfirmada() {
        Reserva r = builderValido().build();
        r.confirmar(7);
        assertEquals(7, r.getNumero());
        assertEquals(EstadoReserva.CONFIRMADA, r.getEstado());
    }

    @Test
    public void confirmarConNumeroInvalidoLanzaExcepcion() {
        Reserva r = builderValido().build();
        assertThrows(IllegalArgumentException.class, () -> r.confirmar(0));
    }

    @Test
    public void confirmarDosVecesLanzaExcepcion() {
        Reserva r = builderValido().build();
        r.confirmar(1);
        assertThrows(IllegalStateException.class, () -> r.confirmar(2));
    }

    @Test
    public void observacionesYServiciosOpcionalesSeGuardan() {
        Reserva r = builderValido()
                .conObservaciones("Alergia a los frutos secos")
                .conTransporte(true)
                .conSeguro(true)
                .build();
        assertEquals("Alergia a los frutos secos", r.getObservaciones());
        assertTrue(r.isTransporte());
        assertTrue(r.isSeguro());
    }
}