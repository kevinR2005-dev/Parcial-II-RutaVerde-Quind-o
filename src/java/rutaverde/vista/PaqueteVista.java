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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import rutaverde.modelo.producto.Paquete;
import rutaverde.modelo.producto.Producto;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/** Pantalla de paquetes: crear, agregar componentes y ver el contenido en forma de árbol. */
public class PaqueteVista {

    private final VBox raiz = new VBox(12);
    private final TextField txtCodigo = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtDescuento = new TextField();
    private final Button btnCrear = new Button("Crear paquete");
    private final ComboBox<Producto> cmbProducto = new ComboBox<>();
    private final Button btnAgregar = new Button("Agregar al paquete seleccionado");
    private final TextArea txtDetalle = new TextArea();
    private final Label lblEstado = new Label();
    private final TableView<Paquete> tabla = new TableView<>();

    public PaqueteVista() {
        raiz.setPadding(new Insets(16));
        Label titulo = new Label("Paquetes turísticos");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        txtDescuento.setPromptText("0 a 20");
        btnCrear.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        agregar(form, 0, 0, "Código", txtCodigo);
        agregar(form, 1, 0, "Nombre", txtNombre);
        agregar(form, 2, 0, "Descuento (%)", txtDescuento);
        HBox filaCrear = new HBox(10, form, btnCrear);

        tabla.getColumns().add(columna("Código", Paquete::getCodigo, 70));
        tabla.getColumns().add(columna("Nombre", Paquete::getNombre, 170));
        tabla.getColumns().add(columna("Desc. %", (Paquete p) -> String.valueOf(p.getDescuento()), 70));
        tabla.getColumns().add(columna("Precio/persona",
                (Paquete p) -> String.format(Locale.forLanguageTag("es-CO"), "$%,.0f", p.getPrecioPorPersona()), 120));
        tabla.getColumns().add(columna("Horas", (Paquete p) -> String.valueOf(p.getDuracionHoras()), 60));
        tabla.setPrefWidth(520);

        cmbProducto.setPromptText("Producto a agregar");
        cmbProducto.setPrefWidth(300);
        cmbProducto.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto p) {
                return p == null ? "" : p.getCodigo() + " - " + p.getNombre();
            }

            @Override
            public Producto fromString(String texto) {
                return null;
            }
        });

        txtDetalle.setEditable(false);
        txtDetalle.setStyle("-fx-font-family: 'Consolas', 'Monospaced';");
        VBox.setVgrow(txtDetalle, Priority.ALWAYS);
        Label lblDetalle = new Label("Contenido del paquete seleccionado");
        VBox panelDerecho = new VBox(8, lblDetalle, txtDetalle, new HBox(10, cmbProducto, btnAgregar));
        HBox.setHgrow(panelDerecho, Priority.ALWAYS);

        HBox centro = new HBox(14, tabla, panelDerecho);
        VBox.setVgrow(centro, Priority.ALWAYS);

        raiz.getChildren().addAll(titulo, filaCrear, lblEstado, centro);
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
    public void alCrear(Runnable accion) { btnCrear.setOnAction(e -> accion.run()); }
    public void alAgregar(Runnable accion) { btnAgregar.setOnAction(e -> accion.run()); }

    public void alSeleccionarPaquete(Runnable accion) {
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> accion.run());
    }

    // ---- Datos del formulario ----
    public String getCodigo() { return txtCodigo.getText(); }
    public String getNombre() { return txtNombre.getText(); }
    public String getDescuento() { return txtDescuento.getText(); }
    public Paquete getPaqueteSeleccionado() { return tabla.getSelectionModel().getSelectedItem(); }
    public Producto getProductoAAgregar() { return cmbProducto.getValue(); }

    // ---- Salida ----
    public void mostrarPaquetes(List<Paquete> paquetes) {
        Paquete seleccionado = tabla.getSelectionModel().getSelectedItem();
        tabla.setItems(FXCollections.observableArrayList(paquetes));
        if (seleccionado != null) {
            tabla.getSelectionModel().select(seleccionado);
        }
        tabla.refresh();
    }

    public void mostrarProductos(List<Producto> productos) {
        cmbProducto.getItems().setAll(productos);
    }

    public void mostrarDetalle(String texto) {
        txtDetalle.setText(texto);
    }

    public void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtDescuento.clear();
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
