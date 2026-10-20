package rutaverde.vista;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

/** Pantalla de reportes: ingresos por periodo y producto con más viajeros. */
public class ReporteVista {

    private final VBox raiz = new VBox(14);
    private final DatePicker dpInicio = new DatePicker(LocalDate.now());
    private final DatePicker dpFin = new DatePicker(LocalDate.now().plusMonths(1));
    private final Button btnIngresos = new Button("Calcular ingresos");
    private final Button btnProductoTop = new Button("Producto con más viajeros");
    private final Label lblIngresos = new Label("-");
    private final Label lblProductoTop = new Label("-");

    public ReporteVista() {
        raiz.setPadding(new Insets(16));
        Label titulo = new Label("Reportes");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        btnIngresos.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        btnProductoTop.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        lblIngresos.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        lblProductoTop.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label subIngresos = new Label("Ingresos de reservas confirmadas con viaje en el periodo (fechas incluidas)");
        subIngresos.setStyle("-fx-font-weight: bold;");
        HBox filaPeriodo = new HBox(10, new Label("Desde"), dpInicio, new Label("Hasta"), dpFin, btnIngresos);

        Label subTop = new Label("Producto que más viajeros ha movido");
        subTop.setStyle("-fx-font-weight: bold;");

        raiz.getChildren().addAll(titulo, subIngresos, filaPeriodo, lblIngresos, subTop, btnProductoTop, lblProductoTop);
    }

    public Parent getRaiz() {
        return raiz;
    }

    public void alCalcularIngresos(Runnable accion) { btnIngresos.setOnAction(e -> accion.run()); }
    public void alBuscarProductoTop(Runnable accion) { btnProductoTop.setOnAction(e -> accion.run()); }

    public LocalDate getInicio() { return dpInicio.getValue(); }
    public LocalDate getFin() { return dpFin.getValue(); }

    public void mostrarIngresos(String texto) { lblIngresos.setText(texto); }
    public void mostrarProductoTop(String texto) { lblProductoTop.setText(texto); }

    public void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
