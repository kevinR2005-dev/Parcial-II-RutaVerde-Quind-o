package rutaverde.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rutaverde.modelo.Cliente;
import rutaverde.modelo.Reserva;
import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.NivelDificultad;
import rutaverde.modelo.producto.Producto;
import rutaverde.repositorio.RepositorioMemoria;
import rutaverde.servicio.ReporteServicio;
import rutaverde.servicio.ReservaServicio;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReporteServicioTest {

    private ReservaServicio reservaServicio;
    private ReporteServicio reporte;
    private Cliente cliente;
    private Actividad cascada;   // $100.000 por persona
    private Actividad cafetal;   // $50.000 por persona
    private final LocalDate hoy = LocalDate.now();

    @BeforeEach
    void preparar() {
        reservaServicio = new ReservaServicio(new RepositorioMemoria<Reserva, Integer>(Reserva::getNumero));
        reporte = new ReporteServicio(reservaServicio);
        cliente = new Cliente("1", "Ana Gómez", "3000000000", "ana@correo.com", "Colombia", "Español");
        cascada = new Actividad("A1", "Cascada", "Salento", 4, 100000, 20, NivelDificultad.MEDIA);
        cafetal = new Actividad("A2", "Cafetal", "Armenia", 4, 50000, 20, NivelDificultad.BAJA);
    }

    private Reserva confirmada(int numero, Producto p, LocalDate fecha, int viajeros) {
        Reserva r = new Reserva.Builder().conCliente(cliente).conProducto(p)
                .conFecha(fecha).conViajeros(viajeros).build();
        r.confirmar(numero);
        reservaServicio.registrar(r);
        return r;
    }

    @Test
    void ingresosIncluyenFechaInicialYFinalDelPeriodo() {
        confirmada(1, cascada, hoy.plusDays(1), 2);   // 200.000 (fecha inicial)
        confirmada(2, cascada, hoy.plusDays(3), 1);   // 100.000 (fecha final)
        confirmada(3, cascada, hoy.plusDays(5), 3);   // fuera del periodo
        assertEquals(300000, reporte.ingresosPorPeriodo(hoy.plusDays(1), hoy.plusDays(3)), 0.01);
    }

    @Test
    void ingresosSinReservasSonCero() {
        assertEquals(0, reporte.ingresosPorPeriodo(hoy, hoy.plusDays(10)), 0.01);
    }

    @Test
    void ingresosIgnoranReservasPendientes() {
        confirmada(1, cascada, hoy.plusDays(1), 1);
        Reserva pendiente = new Reserva.Builder().conCliente(cliente).conProducto(cascada)
                .conFecha(hoy.plusDays(1)).conViajeros(4).build();
        reservaServicio.registrar(pendiente);
        assertEquals(100000, reporte.ingresosPorPeriodo(hoy, hoy.plusDays(2)), 0.01);
    }

    @Test
    void periodoInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> reporte.ingresosPorPeriodo(hoy.plusDays(3), hoy));
        assertThrows(IllegalArgumentException.class, () -> reporte.ingresosPorPeriodo(null, hoy));
    }

    @Test
    void productoConMasViajerosSumaReservasDelMismoProducto() {
        confirmada(1, cascada, hoy.plusDays(1), 2);
        confirmada(2, cascada, hoy.plusDays(2), 3);   // cascada: 5
        confirmada(3, cafetal, hoy.plusDays(1), 4);   // cafetal: 4
        assertEquals("A1", reporte.productoConMasViajeros().getCodigo());
    }

    @Test
    void productoConMasViajerosEsNuloSinReservasConfirmadas() {
        assertNull(reporte.productoConMasViajeros());
    }
}