package rutaverde.vista;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import rutaverde.modelo.Cliente;

import java.util.List;
import java.util.function.Function;

/** Pantalla de clientes: formulario de registro y tabla. Solo muestra y captura eventos. */
public class ClienteVista {

    private final VBox raiz = new VBox(12);
    private final TextField txtDocumento = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtTelefono = new TextField();
    private final TextField txtCorreo = new TextField();
    private final TextField txtPais = new TextField();
    private final ComboBox<String> cmbIdioma = new ComboBox<>();
    private final Button btnRegistrar = new Button("Registrar cliente");
    private final Label lblEstado = new Label();
    private final TableView<Cliente> tabla = new TableView<>();

    public ClienteVista() {
        raiz.setPadding(new Insets(16));
        Label titulo = new Label("Clientes");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        txtDocumento.setPromptText("Cédula o pasaporte");
        txtCorreo.setPromptText("nombre@correo.com");
        cmbIdioma.getItems().addAll("Español", "Inglés", "Francés", "Alemán", "Portugués", "Italiano");
        cmbIdioma.setPromptText("Seleccione");
        btnRegistrar.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        agregar(form, 0, 0, "Documento", txtDocumento);
        agregar(form, 1, 0, "Nombre completo", txtNombre);
        agregar(form, 2, 0, "Teléfono", txtTelefono);
        agregar(form, 0, 1, "Correo", txtCorreo);
        agregar(form, 1, 1, "País de origen", txtPais);
        agregar(form, 2, 1, "Idioma preferido", cmbIdioma);

        tabla.getColumns().add(columna("Documento", Cliente::getDocumento, 120));
        tabla.getColumns().add(columna("Nombre", Cliente::getNombreCompleto, 220));
        tabla.getColumns().add(columna("Teléfono", Cliente::getTelefono, 130));
        tabla.getColumns().add(columna("Correo", Cliente::getCorreo, 220));
        tabla.getColumns().add(columna("País", Cliente::getPaisOrigen, 130));
        tabla.getColumns().add(columna("Idioma", Cliente::getIdiomaPreferido, 100));
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

    // ---- Eventos ----
    public void alRegistrar(Runnable accion) {
        btnRegistrar.setOnAction(e -> accion.run());
    }

    // ---- Datos del formulario ----
    public String getDocumento() { return txtDocumento.getText(); }
    public String getNombre() { return txtNombre.getText(); }
    public String getTelefono() { return txtTelefono.getText(); }
    public String getCorreo() { return txtCorreo.getText(); }
    public String getPais() { return txtPais.getText(); }
    public String getIdioma() { return cmbIdioma.getValue(); }

    // ---- Salida ----
    public void mostrarClientes(List<Cliente> clientes) {
        tabla.setItems(FXCollections.observableArrayList(clientes));
    }

    public void limpiarFormulario() {
        txtDocumento.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtPais.clear();
        cmbIdioma.setValue(null);
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
