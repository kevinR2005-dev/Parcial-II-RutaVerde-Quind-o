package rutaverde.vista;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
import rutaverde.modelo.Cliente;
import rutaverde.modelo.Guia;
import rutaverde.modelo.Reserva;
import rutaverde.modelo.producto.Producto;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Pantalla de reservas: captura los datos, permite consultar cupos, calcular el valor y confirmar.
 * Solo muestra y captura eventos; no aplica reglas de negocio.
 */
public class ReservaVista {

    private final VBox raiz = new VBox(10);
    private final ComboBox<Cliente> cmbCliente = new ComboBox<>();
    private final ComboBox<Producto> cmbProducto = new ComboBox<>();
    private final DatePicker dpFecha = new DatePicker(LocalDate.now().plusDays(7));
    private final TextField txtViajeros = new TextField("1");
    private final CheckBox chkGuia = new CheckBox("Incluir guía");
    private final ComboBox<Guia> cmbGuia = new ComboBox<>();
    private final CheckBox chkTransporte = new CheckBox("Transporte desde el hotel ($35.000 por viajero)");
    private final CheckBox chkSeguro = new CheckBox("Seguro de viaje ($15.000 por viajero)");
    private final TextArea txtObservaciones = new TextArea();
    private final TextField txtTarjeta = new TextField();
    private final ComboBox<String> cmbCanal = new ComboBox<>();
    private final Button btnCupos = new Button("Consultar cupos");
    private final Button btnCalcular = new Button("Calcular valor");
    private final Button btnConfirmar = new Button("Confirmar reserva");
    private final TextArea txtResultado = new TextArea();
    private final Label lblEstado = new Label();
    private final TableView<Reserva> tabla = new TableView<>();

    public ReservaVista() {
        raiz.setPadding(new Insets(16));
        Label titulo = new Label("Reservas");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        cmbCliente.setPromptText("Seleccione el cliente");
        cmbCliente.setPrefWidth(300);
        cmbCliente.setConverter(convertidor((Cliente c) -> c.getDocumento() + " - " + c.getNombreCompleto()));
        cmbProducto.setPromptText("Actividad o paquete");
        cmbProducto.setPrefWidth(300);
        cmbProducto.setConverter(convertidor((Producto p) -> p.getCodigo() + " - " + p.getNombre()));
        cmbGuia.setPromptText("Seleccione el guía");
        cmbGuia.setPrefWidth(300);
        cmbGuia.setDisable(true);
        cmbGuia.setConverter(convertidor((Guia g) -> g.getNombre() + " (" + String.join(", ", g.getIdiomas()) + ")"));
        chkGuia.setOnAction(e -> cmbGuia.setDisable(!chkGuia.isSelected()));

        txtViajeros.setPrefWidth(80);
        txtObservaciones.setPromptText("Alergias, movilidad reducida, alimentación...");
        txtObservaciones.setPrefRowCount(2);
        txtTarjeta.setPromptText("4539 1488 0343 6467");
        cmbCanal.getItems().addAll("Asesor (voucher detallado)", "Kiosco (tiquete corto)");
        cmbCanal.getSelectionModel().selectFirst();

        btnConfirmar.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        agregar(form, 0, "Cliente", cmbCliente);
        agregar(form, 1, "Producto", cmbProducto);
        agregar(form, 2, "Fecha del viaje", dpFecha);
        agregar(form, 3, "Viajeros (1 a 15)", txtViajeros);
        form.add(chkGuia, 0, 4);
        form.add(cmbGuia, 1, 4);
        form.add(chkTransporte, 1, 5);
        form.add(chkSeguro, 1, 6);
        agregar(form, 7, "Observaciones", txtObservaciones);
        agregar(form, 8, "Tarjeta", txtTarjeta);
        agregar(form, 9, "Canal / voucher", cmbCanal);

        HBox botones = new HBox(10, btnCupos, btnCalcular, btnConfirmar);

        txtResultado.setEditable(false);
        txtResultado.setStyle("-fx-font-family: 'Consolas', 'Monospaced';");
        txtResultado.setPromptText("Aquí aparecen los cupos, el valor y el voucher.");
        VBox.setVgrow(txtResultado, Priority.ALWAYS);
        VBox panelIzquierdo = new VBox(10, form, botones);
        VBox panelDerecho = new VBox(6, new Label("Resultado"), txtResultado);
        HBox.setHgrow(panelDerecho, Priority.ALWAYS);
        HBox superior = new HBox(18, panelIzquierdo, panelDerecho);
        VBox.setVgrow(superior, Priority.ALWAYS);

        tabla.getColumns().add(columna("N°", (Reserva r) -> String.valueOf(r.getNumero()), 50));
        tabla.getColumns().add(columna("Cliente", (Reserva r) -> r.getCliente().getNombreCompleto(), 200));
        tabla.getColumns().add(columna("Producto", (Reserva r) -> r.getProducto().getNombre(), 200));
        tabla.getColumns().add(columna("Fecha", (Reserva r) -> r.getFechaViaje().toString(), 100));
        tabla.getColumns().add(columna("Viajeros", (Reserva r) -> String.valueOf(r.getViajeros()), 80));
        tabla.getColumns().add(columna("Guía",
                (Reserva r) -> r.getGuia() == null ? "-" : r.getGuia().getNombre(), 150));
        tabla.getColumns().add(columna("Valor",
                (Reserva r) -> String.format(Locale.forLanguageTag("es-CO"), "$%,.0f", r.calcularValor()), 120));
        tabla.setPrefHeight(170);
        tabla.setMinHeight(130);

        raiz.getChildren().addAll(titulo, superior, lblEstado, new Label("Reservas confirmadas"), tabla);
    }

    private static void agregar(GridPane g, int fila, String etiqueta, Node campo) {
        g.add(new Label(etiqueta), 0, fila);
        g.add(campo, 1, fila);
    }

    private static <T> StringConverter<T> convertidor(Function<T, String> texto) {
        return new StringConverter<T>() {
            @Override
            public String toString(T objeto) {
                return objeto == null ? "" : texto.apply(objeto);
            }

            @Override
            public T fromString(String s) {
                return null;
            }
        };
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
    public void alConsultarCupos(Runnable accion) { btnCupos.setOnAction(e -> accion.run()); }
    public void alCalcular(Runnable accion) { btnCalcular.setOnAction(e -> accion.run()); }
    public void alConfirmar(Runnable accion) { btnConfirmar.setOnAction(e -> accion.run()); }

    // ---- Datos del formulario ----
    public Cliente getCliente() { return cmbCliente.getValue(); }
    public Producto getProducto() { return cmbProducto.getValue(); }
    public LocalDate getFecha() { return dpFecha.getValue(); }
    public String getViajeros() { return txtViajeros.getText(); }
    public boolean isGuiaSolicitado() { return chkGuia.isSelected(); }
    public Guia getGuia() { return chkGuia.isSelected() ? cmbGuia.getValue() : null; }
    public boolean isTransporte() { return chkTransporte.isSelected(); }
    public boolean isSeguro() { return chkSeguro.isSelected(); }
    public String getObservaciones() { return txtObservaciones.getText(); }
    public String getTarjeta() { return txtTarjeta.getText(); }
    public boolean isCanalKiosco() { return cmbCanal.getSelectionModel().getSelectedIndex() == 1; }

    // ---- Salida ----
    public void mostrarClientes(List<Cliente> clientes) {
        Cliente seleccionado = cmbCliente.getValue();
        cmbCliente.getItems().setAll(clientes);
        if (seleccionado != null && clientes.contains(seleccionado)) {
            cmbCliente.setValue(seleccionado);
        }
    }

    public void mostrarProductos(List<Producto> productos) {
        Producto seleccionado = cmbProducto.getValue();
        cmbProducto.getItems().setAll(productos);
        if (seleccionado != null && productos.contains(seleccionado)) {
            cmbProducto.setValue(seleccionado);
        }
    }

    public void mostrarGuias(List<Guia> guias) {
        Guia seleccionado = cmbGuia.getValue();
        cmbGuia.getItems().setAll(guias);
        if (seleccionado != null && guias.contains(seleccionado)) {
            cmbGuia.setValue(seleccionado);
        }
    }

    public void mostrarReservas(List<Reserva> reservas) {
        tabla.setItems(FXCollections.observableArrayList(reservas));
    }

    public void mostrarResultado(String texto) {
        txtResultado.setText(texto);
    }

    public void limpiarTarjeta() {
        txtTarjeta.clear();
    }

    public void mostrarMensaje(String mensaje) {
        lblEstado.setStyle("-fx-text-fill: #2e7d32;");
        lblEstado.setText(mensaje);
    }

    public void mostrarError(String mensaje) {
        lblEstado.setStyle("-fx-text-fill: #c62828;");
        lblEstado.setText(mensaje);
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
