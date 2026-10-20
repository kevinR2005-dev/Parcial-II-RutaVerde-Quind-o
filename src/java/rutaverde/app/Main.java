package rutaverde.app;

import javafx.application.Application;
import javafx.stage.Stage;
import rutaverde.controlador.ActividadController;
import rutaverde.controlador.ClienteController;
import rutaverde.controlador.GuiaController;
import rutaverde.controlador.PaqueteController;
import rutaverde.controlador.PrincipalController;
import rutaverde.controlador.ReporteController;
import rutaverde.controlador.ReservaController;
import rutaverde.modelo.Agencia;
import rutaverde.modelo.Cliente;
import rutaverde.modelo.Guia;
import rutaverde.modelo.Reserva;
import rutaverde.modelo.pago.PagoAndinoAPI;
import rutaverde.modelo.pago.PagoAndinoAdapter;
import rutaverde.modelo.pago.PasarelaPago;
import rutaverde.modelo.pago.ValidadorTarjeta;
import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.Paquete;
import rutaverde.modelo.voucher.GeneradorVoucher;
import rutaverde.modelo.voucher.VoucherAsesor;
import rutaverde.modelo.voucher.VoucherKiosco;
import rutaverde.repositorio.Repositorio;
import rutaverde.repositorio.RepositorioMemoria;
import rutaverde.servicio.CatalogoServicio;
import rutaverde.servicio.ConfirmacionReservaFacade;
import rutaverde.servicio.ConsecutivoReserva;
import rutaverde.servicio.ControlCupos;
import rutaverde.servicio.Notificador;
import rutaverde.servicio.NotificadorConsola;
import rutaverde.servicio.ReporteServicio;
import rutaverde.servicio.ReservaServicio;

/**
 * Punto de entrada y "composition root": aquí se crean todas las dependencias
 * y se inyectan por constructor. Ninguna otra clase usa "new" para sus colaboradores.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Repositorios (en memoria)
        Repositorio<Cliente, String> clientes = new RepositorioMemoria<>(Cliente::getDocumento);
        Repositorio<Guia, String> guias = new RepositorioMemoria<>(Guia::getIdentificacion);
        Repositorio<Actividad, String> actividades = new RepositorioMemoria<>(Actividad::getCodigo);
        Repositorio<Paquete, String> paquetes = new RepositorioMemoria<>(Paquete::getCodigo);
        Repositorio<Reserva, Integer> reservas = new RepositorioMemoria<>(Reserva::getNumero);

        // 2. Infraestructura: pasarela (Adapter), notificador, validador, cupos y consecutivo
        PasarelaPago pasarela = new PagoAndinoAdapter(new PagoAndinoAPI());
        Notificador notificador = new NotificadorConsola();
        ValidadorTarjeta validador = new ValidadorTarjeta();
        ControlCupos cupos = new ControlCupos();
        ConsecutivoReserva consecutivo = new ConsecutivoReserva();

        // 3. Servicios y Facade
        CatalogoServicio catalogo = new CatalogoServicio(clientes, guias, actividades, paquetes);
        ReservaServicio reservaServicio = new ReservaServicio(reservas);
        ReporteServicio reporteServicio = new ReporteServicio(reservaServicio);
        ConfirmacionReservaFacade facade = new ConfirmacionReservaFacade(
                cupos, validador, pasarela, consecutivo, reservaServicio, notificador);

        // 4. Vouchers por canal (Factory Method): asesor y kiosco
        Agencia agencia = DatosIniciales.crearAgencia();
        GeneradorVoucher voucherAsesor = new VoucherAsesor(agencia);
        GeneradorVoucher voucherKiosco = new VoucherKiosco(agencia);

        // 5. Datos de ejemplo
        new DatosIniciales(catalogo).cargar();

        // 6. Controladores (reciben servicios por constructor)
        ClienteController clienteCtrl = new ClienteController(catalogo);
        GuiaController guiaCtrl = new GuiaController(catalogo);
        ActividadController actividadCtrl = new ActividadController(catalogo, cupos);
        PaqueteController paqueteCtrl = new PaqueteController(catalogo);
        ReservaController reservaCtrl = new ReservaController(
                catalogo, reservaServicio, cupos, facade, voucherAsesor, voucherKiosco);
        ReporteController reporteCtrl = new ReporteController(reporteServicio);

        PrincipalController principal = new PrincipalController(agencia, clienteCtrl, guiaCtrl,
                actividadCtrl, paqueteCtrl, reservaCtrl, reporteCtrl);

        // 7. Abrir la ventana principal
        principal.iniciar(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}