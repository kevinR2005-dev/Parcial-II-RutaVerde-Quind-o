package rutaverde.vista;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import rutaverde.modelo.Guia;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/** Pantalla de guías: formulario de registro y tabla. Solo muestra y captura eventos. */
public class GuiaVista {

    private final VBox raiz = new VBox(12);
    private final TextField txtIdentificacion = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtIdiomas = new TextField();
    private final TextField txtTelefono = new TextField();
    private final TextField txtTarifa = new TextField();
    private final Button btnRegistrar = new Button("Registrar guía");
    private final Label lblEstado = new Label();
    private final TableView<Guia> tabla = new TableView<>();

    public GuiaVista() {
        raiz.setPadding(new Insets(16));
        Label titulo = new Label("Guías");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        txtIdiomas.setPromptText("Español, Inglés");
        txtTarifa.setPromptText("Ej: 120000");
        btnRegistrar.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        agregar(form, 0, 0, "Identificación", txtIdentificacion);
        agregar(form, 1, 0, "Nombre", txtNombre);
        agregar(form, 2, 0, "Teléfono", txtTelefono);
        agregar(form, 0, 1, "Idiomas (separados por coma)", txtIdiomas);
        agregar(form, 1, 1, "Tarifa por día ($)", txtTarifa);

        tabla.getColumns().add(columna("Identificación", Guia::getIdentificacion, 120));
        tabla.getColumns().add(columna("Nombre", Guia::getNombre, 220));
        tabla.getColumns().add(columna("Idiomas", (Guia g) -> String.join(", ", g.getIdiomas()), 200));
        tabla.getColumns().add(columna("Teléfono", Guia::getTelefono, 130));
        tabla.getColumns().add(columna("Tarifa por día",
                (Guia g) -> String.format(Locale.forLanguageTag("es-CO"), "$%,.0f", g.getTarifaPorDia()), 130));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        raiz.getChildren().addAll(titulo, form, btnRegistrar, lblEstado, tabla);
    }

    private static void agregar(GridPane g, int fila, int col, String etiqueta, Node campo) {
        g.add(new Label(etiqueta), col * 2, fila);
        g.add(campo, col * 2 + 1, fila);
    }

    private static <T> TableColumn<T, String> columna(String titulo, Function<T, String> valor, double ancho) {
        TableColumn<T, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(cd -> new ReadOnlyStringWrapper(valor.apply(cd.getValue())));
        c.setPrefWidth(ancho);
        return c;
    }

    public Parent getRaiz() {
        return raiz;
    }

    public void alRegistrar(Runnable accion) {
        btnRegistrar.setOnAction(e -> accion.run());
    }

    public String getIdentificacion() { return txtIdentificacion.getText(); }
    public String getNombre() { return txtNombre.getText(); }
    public String getIdiomas() { return txtIdiomas.getText(); }
    public String getTelefono() { return txtTelefono.getText(); }
    public String getTarifa() { return txtTarifa.getText(); }

    public void mostrarGuias(List<Guia> guias) {
        tabla.setItems(FXCollections.observableArrayList(guias));
    }

    public void limpiarFormulario() {
        txtIdentificacion.clear();
        txtNombre.clear();
        txtIdiomas.clear();
        txtTelefono.clear();
        txtTarifa.clear();
    }

    public void mostrarMensaje(String mensaje) {
        lblEstado.setStyle("-fx-text-fill: #2e7d32;");
        lblEstado.setText(mensaje);
    }

    public void mostrarError(String mensaje) {
        lblEstado.setText("");
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
