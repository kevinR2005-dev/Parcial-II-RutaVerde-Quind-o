import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.NivelDificultad;
import rutaverde.modelo.producto.Paquete;

import static org.testng.Assert.assertThrows;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class PaqueteTest {

    private Actividad cafe;     // 4 h, $100.000
    private Actividad cascada;  // 2 h, $50.000
    private Actividad parque;   // 3 h, $80.000
    private Actividad aves;     // 1 h, $20.000

    @BeforeMethod
    public void preparar() {
        cafe = new Actividad("A1", "Finca cafetera", "Salento", 4, 100000, 10, NivelDificultad.BAJA);
        cascada = new Actividad("A2", "Caminata a la cascada", "Salento", 2, 50000, 10, NivelDificultad.MEDIA);
        parque = new Actividad("A3", "Parque temático", "Montenegro", 3, 80000, 20, NivelDificultad.BAJA);
        aves = new Actividad("A4", "Avistamiento de aves", "Filandia", 1, 20000, 8, NivelDificultad.ALTA);
    }

    // ---------- Descuento ----------

    @Test
    public void descuentoMenorACeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Paquete("P1", "Paquete", -1));
    }

    @Test
    public void descuentoMayorAVeinteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Paquete("P1", "Paquete", 21));
    }

    @Test
    public void descuentosLimiteSonValidos() {
        assertEquals(0.0, new Paquete("P1", "Sin descuento", 0).getDescuento(), 0.001);
        assertEquals(20.0, new Paquete("P2", "Maximo descuento", 20).getDescuento(), 0.001);
    }

    // ---------- Ciclos ----------

    @Test
    public void paqueteNoPuedeContenerseASiMismo() {
        Paquete p = new Paquete("P1", "Paquete", 0);
        assertThrows(IllegalArgumentException.class, () -> p.agregar(p));
    }

    @Test
    public void cicloIndirectoLanzaExcepcion() {
        Paquete a = new Paquete("PA", "A", 0);
        Paquete b = new Paquete("PB", "B", 0);
        a.agregar(b);
        // B no puede contener a A porque A ya contiene a B
        assertThrows(IllegalArgumentException.class, () -> b.agregar(a));
    }

    // ---------- Actividad repetida ----------

    @Test
    public void actividadRepetidaLanzaExcepcion() {
        Paquete p = new Paquete("P1", "Paquete", 0);
        p.agregar(cafe);
        assertThrows(IllegalArgumentException.class, () -> p.agregar(cafe));
    }

    @Test
    public void actividadRepetidaEnSubpaqueteLanzaExcepcion() {
        Paquete interno = new Paquete("PI", "Interno", 0);
        interno.agregar(cafe);
        Paquete externo = new Paquete("PE", "Externo", 0);
        externo.agregar(interno);
        // cafe ya está en el árbol (dentro de interno)
        assertThrows(IllegalArgumentException.class, () -> externo.agregar(cafe));
    }

    @Test
    public void agregarNuloLanzaExcepcion() {
        Paquete p = new Paquete("P1", "Paquete", 0);
        assertThrows(IllegalArgumentException.class, () -> p.agregar(null));
    }

    // ---------- Precio, duración y actividades con 2 niveles ----------

    private Paquete armarQuindioTotal() {
        Paquete salento = new Paquete("PS", "Paquete Salento", 10);
        salento.agregar(cafe);
        salento.agregar(cascada);

        Paquete parques = new Paquete("PP", "Paquete Parques", 0);
        parques.agregar(parque);

        Paquete total = new Paquete("QT", "Quindío Total", 20);
        total.agregar(salento);
        total.agregar(parques);
        total.agregar(aves);
        return total;
    }

    @Test
    public void precioConDosNivelesDePaquetes() {
        // Salento = 150.000 * 0.9 = 135.000
        // Parques = 80.000
        // Total   = (135.000 + 80.000 + 20.000) * 0.8 = 188.000
        assertEquals(188000.0, armarQuindioTotal().getPrecioPorPersona(), 0.001);
    }

    @Test
    public void duracionConDosNivelesDePaquetes() {
        // 4 + 2 + 3 + 1
        assertEquals(10, armarQuindioTotal().getDuracionHoras());
    }

    @Test
    public void getActividadesDevuelveTodasLasDelArbol() {
        assertEquals(4, armarQuindioTotal().getActividades().size());
    }

    @Test
    public void contieneBuscaEnTodoElArbol() {
        Paquete total = armarQuindioTotal();
        assertTrue(total.contiene(cascada));   // está dentro de un subpaquete
        assertFalse(total.contiene(new Actividad("A9", "Otra", "Armenia", 1, 1000, 5, NivelDificultad.BAJA)));
    }

    @Test
    public void getComponentesNoSePuedeModificar() {
        Paquete p = new Paquete("P1", "Paquete", 0);
        p.agregar(cafe);
        assertThrows(UnsupportedOperationException.class, () -> p.getComponentes().add(aves));
    }

    // ---------- Actividad ----------

    @Test
    public void actividadNuevaEstaDisponibleYSePuedeSuspender() {
        assertTrue(cafe.estaDisponible());
        cafe.suspender();
        assertFalse(cafe.estaDisponible());
        cafe.activar();
        assertTrue(cafe.estaDisponible());
    }
}