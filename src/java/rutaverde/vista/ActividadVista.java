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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import rutaverde.modelo.producto.Actividad;
import rutaverde.modelo.producto.NivelDificultad;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/** Pantalla de actividades: registro, suspender/activar y cupos disponibles por fecha. */
public class ActividadVista {

    private final VBox raiz = new VBox(12);
    private final TextField txtCodigo = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtMunicipio = new TextField();
    private final TextField txtDuracion = new TextField();
    private final TextField txtPrecio = new TextField();
    private final TextField txtCupo = new TextField();
    private final ComboBox<NivelDificultad> cmbDificultad = new ComboBox<>();
    private final Button btnRegistrar = new Button("Registrar actividad");
    private final Button btnSuspender = new Button("Suspender seleccionada");
    private final Button btnActivar = new Button("Activar seleccionada");
    private final DatePicker dpFechaCupos = new DatePicker(LocalDate.now());
    private final Label lblEstado = new Label();
    private final TableView<Actividad> tabla = new TableView<>();
    // Cómo obtener los cupos disponibles de cada actividad (lo define el controlador)
    private Function<Actividad, Integer> cuposDisponibles = a -> a.getCupoMaximoPorDia();

    public ActividadVista() {
        raiz.setPadding(new Insets(16));
        Label titulo = new Label("Actividades");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        cmbDificultad.getItems().addAll(NivelDificultad.values());
        cmbDificultad.setPromptText("Seleccione");
        btnRegistrar.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        agregar(form, 0, 0, "Código", txtCodigo);
        agregar(form, 1, 0, "Nombre", txtNombre);
        agregar(form, 2, 0, "Municipio", txtMunicipio);
        agregar(form, 0, 1, "Duración (horas)", txtDuracion);
        agregar(form, 1, 1, "Precio por persona ($)", txtPrecio);
        agregar(form, 2, 1, "Cupo máximo por día", txtCupo);
        agregar(form, 0, 2, "Dificultad", cmbDificultad);

        HBox acciones = new HBox(10, btnRegistrar, btnSuspender, btnActivar,
                new Label("   Ver cupos para la fecha:"), dpFechaCupos);

        tabla.getColumns().add(columna("Código", Actividad::getCodigo, 80));
        tabla.getColumns().add(columna("Nombre", Actividad::getNombre, 220));
        tabla.getColumns().add(columna("Municipio", Actividad::getMunicipio, 110));
        tabla.getColumns().add(columna("Horas", (Actividad a) -> String.valueOf(a.getDuracionHoras()), 60));
        tabla.getColumns().add(columna("Precio",
                (Actividad a) -> String.format(Locale.forLanguageTag("es-CO"), "$%,.0f", a.getPrecioPorPersona()), 100));
        tabla.getColumns().add(columna("Cupo máx.", (Actividad a) -> String.valueOf(a.getCupoMaximoPorDia()), 80));
        tabla.getColumns().add(columna("Dificultad", (Actividad a) -> a.getDificultad().name(), 90));
        tabla.getColumns().add(columna("Estado", (Actividad a) -> a.getEstado().name(), 100));
        tabla.getColumns().add(columna("Cupos disponibles",
                (Actividad a) -> String.valueOf(cuposDisponibles.apply(a)), 130));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        raiz.getChildren().addAll(titulo, form, acciones, lblEstado, tabla);
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
    public void alRegistrar(Runnable accion) { btnRegistrar.setOnAction(e -> accion.run()); }
    public void alSuspender(Runnable accion) { btnSuspender.setOnAction(e -> accion.run()); }
    public void alActivar(Runnable accion) { btnActivar.setOnAction(e -> accion.run()); }
    public void alCambiarFechaCupos(Runnable accion) { dpFechaCupos.setOnAction(e -> accion.run()); }

    // ---- Datos del formulario ----
    public String getCodigo() { return txtCodigo.getText(); }
    public String getNombre() { return txtNombre.getText(); }
    public String getMunicipio() { return txtMunicipio.getText(); }
    public String getDuracion() { return txtDuracion.getText(); }
    public String getPrecio() { return txtPrecio.getText(); }
    public String getCupo() { return txtCupo.getText(); }
    public NivelDificultad getDificultad() { return cmbDificultad.getValue(); }
    public LocalDate getFechaCupos() { return dpFechaCupos.getValue(); }
    public Actividad getActividadSeleccionada() { return tabla.getSelectionModel().getSelectedItem(); }

    // ---- Salida ----
    public void mostrarActividades(List<Actividad> actividades, Function<Actividad, Integer> cupos) {
        this.cuposDisponibles = cupos;
        Actividad seleccionada = tabla.getSelectionModel().getSelectedItem();
        tabla.setItems(FXCollections.observableArrayList(actividades));
        if (seleccionada != null) {
            tabla.getSelectionModel().select(seleccionada);
        }
        tabla.refresh();
    }

    public void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtMunicipio.clear();
        txtDuracion.clear();
        txtPrecio.clear();
        txtCupo.clear();
        cmbDificultad.setValue(null);
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
