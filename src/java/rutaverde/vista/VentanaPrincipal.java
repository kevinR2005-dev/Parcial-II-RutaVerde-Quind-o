package rutaverde.vista;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Ventana principal: encabezado de la agencia y una pestaña por módulo.
 * Solo arma la pantalla; no conoce servicios ni reglas de negocio.
 */
public class VentanaPrincipal {

    private final BorderPane raiz = new BorderPane();
    private final TabPane pestanas = new TabPane();

    public VentanaPrincipal(String titulo, String subtitulo) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label lblSubtitulo = new Label(subtitulo);
        lblSubtitulo.setStyle("-fx-text-fill: #c8e6c9; -fx-font-size: 12px;");
        VBox encabezado = new VBox(2, lblTitulo, lblSubtitulo);
        encabezado.setPadding(new Insets(12, 16, 12, 16));
        encabezado.setStyle("-fx-background-color: #2e7d32;");

        raiz.setTop(encabezado);
        raiz.setCenter(pestanas);
    }

    /** Agrega una pestaña; "alSeleccionar" se ejecuta cada vez que el usuario entra a ella. */
    public void agregarPestana(String titulo, Parent contenido, Runnable alSeleccionar) {
        Tab pestana = new Tab(titulo, contenido);
        pestana.setClosable(false);
        pestana.setOnSelectionChanged(e -> {
            if (pestana.isSelected() && alSeleccionar != null) {
                alSeleccionar.run();
            }
        });
        pestanas.getTabs().add(pestana);
    }

    public void mostrar(Stage stage) {
        stage.setTitle("RutaVerde Quindío - Gestión de reservas");
        stage.setScene(new Scene(raiz, 1150, 740));
        stage.show();
    }
}
