package rutaverde.app;

import rutaverde.modelo.Agencia;
import rutaverde.modelo.Cliente;
import rutaverde.modelo.Guia;
import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.NivelDificultad;
import rutaverde.modelo.producto.Paquete;
import rutaverde.servicio.CatalogoServicio;

import java.util.Set;

/**
 * Carga los datos de ejemplo de RutaVerde Quindío para poder usar y demostrar la aplicación.
 * Solo usa CatalogoServicio: no conoce repositorios ni JavaFX.
 */
public class DatosIniciales {

    private final CatalogoServicio catalogo;

    public DatosIniciales(CatalogoServicio catalogo) {
        if (catalogo == null) {
            throw new IllegalArgumentException("El catálogo es obligatorio");
        }
        this.catalogo = catalogo;
    }

    /** Datos básicos de la agencia (los usa el encabezado del voucher). */
    public static Agencia crearAgencia() {
        return new Agencia("RutaVerde Quindío", "901.234.567-8", "123456",
                "Calle 20 # 14-35, Armenia, Quindío", "606 7410000",
                "reservas@rutaverde.co", "www.rutaverde.co");
    }

    public void cargar() {
        cargarClientes();
        cargarGuias();
        cargarActividadesYPaquetes();
    }

    private void cargarClientes() {
        catalogo.registrarCliente(new Cliente("1094887654", "Ana María Gómez", "3001234567",
                "ana.gomez@correo.com", "Colombia", "Español"));
        catalogo.registrarCliente(new Cliente("US-4839201", "John Smith", "+1 305 555 0142",
                "john.smith@mail.com", "Estados Unidos", "Inglés"));
        catalogo.registrarCliente(new Cliente("FR-7788123", "Camille Dubois", "+33 6 12 34 56 78",
                "camille.dubois@mail.fr", "Francia", "Francés"));
    }

    private void cargarGuias() {
        catalogo.registrarGuia(new Guia("G-001", "Carlos Ramírez",
                Set.of("Español", "Inglés"), "3105550101", 120000));
        catalogo.registrarGuia(new Guia("G-002", "Laura Martínez",
                Set.of("Español", "Francés"), "3105550102", 130000));
        catalogo.registrarGuia(new Guia("G-003", "Pedro Londoño",
                Set.of("Español"), "3105550103", 100000));
    }

    private void cargarActividadesYPaquetes() {
        // --- Actividades ---
        Actividad cocora = new Actividad("A-01", "Caminata Valle del Cocora", "Salento",
                5, 60000, 15, NivelDificultad.MEDIA);
        Actividad fincaCafe = new Actividad("A-02", "Recorrido finca cafetera", "Salento",
                3, 45000, 12, NivelDificultad.BAJA);
        Actividad mirador = new Actividad("A-03", "Mirador Alto de la Cruz", "Salento",
                1, 15000, 20, NivelDificultad.BAJA);
        Actividad parqueCafe = new Actividad("A-04", "Parque del Café", "Montenegro",
                8, 90000, 25, NivelDificultad.BAJA);
        Actividad panaca = new Actividad("A-05", "PANACA", "Quimbaya",
                8, 85000, 25, NivelDificultad.BAJA);
        Actividad aves = new Actividad("A-06", "Avistamiento de aves", "Filandia",
                4, 55000, 8, NivelDificultad.ALTA);
        Actividad botanico = new Actividad("A-07", "Jardín Botánico del Quindío", "Calarcá",
                3, 30000, 20, NivelDificultad.BAJA);
        Actividad canopy = new Actividad("A-08", "Canopy en Filandia", "Filandia",
                2, 70000, 10, NivelDificultad.ALTA);
        canopy.suspender(); // ejemplo de actividad suspendida: no se puede reservar

        for (Actividad a : new Actividad[]{cocora, fincaCafe, mirador, parqueCafe,
                panaca, aves, botanico, canopy}) {
            catalogo.registrarActividad(a);
        }

        // --- Paquetes (Composite: un paquete contiene actividades y otros paquetes) ---
        Paquete salento = new Paquete("P-01", "Paquete Salento", 10);
        salento.agregar(cocora);
        salento.agregar(fincaCafe);
        salento.agregar(mirador);

        Paquete parques = new Paquete("P-02", "Paquete Parques", 5);
        parques.agregar(parqueCafe);
        parques.agregar(panaca);

        Paquete quindioTotal = new Paquete("P-03", "Quindío Total", 15);
        quindioTotal.agregar(salento);
        quindioTotal.agregar(parques);
        quindioTotal.agregar(aves);

        catalogo.registrarPaquete(salento);
        catalogo.registrarPaquete(parques);
        catalogo.registrarPaquete(quindioTotal);
    }
}
