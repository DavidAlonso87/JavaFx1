package com.example.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.entity.Empleado;
import com.example.entity.Usuario;
import com.example.service.EmpleadoService;
import com.example.ui.InternalWindow;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class MdiEmpleadoFinalController implements Initializable {

    @FXML
    private AnchorPane mdiRoot;
    @FXML
    private TableView<Empleado> empleadoTable;
    @FXML
    private TableColumn<Empleado, Long> idColumn;
    @FXML
    private TableColumn<Empleado, String> nombreColumn;
    @FXML
    private TableColumn<Empleado, String> departamentoColumn;

    @FXML
    private AnchorPane empleadoWindow;

    // Offsets for dragging
    private double dragOffsetX;
    private double dragOffsetY;

    private final EmpleadoService empleadoService = new EmpleadoService();

    /** Guardamos la ventana en un campo para poder re-usarla */
    private InternalWindow tableWindow;

    private boolean maximized = false;
    private double prevX, prevY, prevW, prevH;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) Configuro las columnas y cargo datos
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        departamentoColumn.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        cargarEmpleados();

        // 2) Creo la ventana interna con TODO el pane (tabla + botones)
        tableWindow = new InternalWindow("Listado de Empleados", empleadoWindow);
        tableWindow.setPrefSize(600, 350);
        tableWindow.relocate(20, 20);

        // Después de crear tableWindow...
        empleadoWindow.prefWidthProperty().bind(tableWindow.prefWidthProperty());
        empleadoWindow.prefHeightProperty().bind(tableWindow.prefHeightProperty());

        // 3) -- AQUÍ DECLARAMOS closeBtn DENTRO DEL MÉTODO --
        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size: 14;" +
                        "-fx-text-fill: gray;");
        // Cuando pulso X, elimino la ventana del root
        closeBtn.setOnAction(e -> mdiRoot.getChildren().remove(tableWindow));

        // 4) Anclo la X en la esquina superior derecha del InternalWindow
        AnchorPane.setTopAnchor(closeBtn, 5.0);
        AnchorPane.setRightAnchor(closeBtn, 5.0);

        // 5) La añado como hijo de la ventana interna
        tableWindow.getChildren().add(closeBtn);
        // 3) Botón de Maximizar / Restaurar (▢ / ❐)
        Button maxBtn = new Button("▢");
        maxBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 12; -fx-text-fill: gray;");
        maxBtn.setOnAction(e -> toggleMaximize());
        AnchorPane.setTopAnchor(maxBtn, 5.0);
        // Lo ponemos justo a la izquierda del closeBtn (5px + ancho del botón)
        AnchorPane.setRightAnchor(maxBtn, 25.0);
        tableWindow.getChildren().add(maxBtn);

        // 4) Botón de Minimizar (—)
        Button minBtn = new Button("—");
        minBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 12; -fx-text-fill: gray;");
        minBtn.setOnAction(e -> mdiRoot.getChildren().remove(tableWindow));
        AnchorPane.setTopAnchor(minBtn, 5.0);
        AnchorPane.setRightAnchor(minBtn, 45.0);
        tableWindow.getChildren().add(minBtn);

        // 6) Finalmente, añado la ventana al root MDI
        mdiRoot.getChildren().add(tableWindow);

    }

    private void toggleMaximize() {
        if (!maximized) {
            // Guardar posición y tamaño actuales
            prevX = tableWindow.getLayoutX();
            prevY = tableWindow.getLayoutY();
            prevW = tableWindow.getWidth();
            prevH = tableWindow.getHeight();
            // Ajustar a pantalla completa del mdiRoot
            tableWindow.setLayoutX(0);
            tableWindow.setLayoutY(0);
            tableWindow.setPrefSize(mdiRoot.getWidth(), mdiRoot.getHeight());
            maximized = true;
            // Cambiar icono a “restaurar”
            ((Button) tableWindow.lookup("Button[text=\"▢\"]")).setText("❐");
        } else {
            // Restaurar posición y tamaño
            tableWindow.setLayoutX(prevX);
            tableWindow.setLayoutY(prevY);
            tableWindow.setPrefSize(prevW, prevH);
            maximized = false;
            // Cambiar icono a “maximizar”
            ((Button) tableWindow.lookup("Button[text=\"❐\"]")).setText("▢");
        }
    }

    private void cargarEmpleados() {
        Task<ObservableList<Empleado>> loadTask = empleadoService.obtenerEmpleados();
        loadTask.setOnSucceeded(evt -> empleadoTable.setItems(loadTask.getValue()));
        loadTask.setOnFailed(evt -> loadTask.getException().printStackTrace());
        new Thread(loadTask).start();
    }

    @FXML
    private void onShowTable(ActionEvent event) {
        if (!mdiRoot.getChildren().contains(tableWindow)) {
            mdiRoot.getChildren().add(tableWindow);
        }
        cargarEmpleados();
    }

    @FXML
    private void onNuevoEmpleado() {
        TextField nombreInput = new TextField();
        TextField deptoInput = new TextField();
        Button guardarBtn = new Button("Guardar");

        VBox form = new VBox(8,
                new Label("Nombre:"), nombreInput,
                new Label("Departamento:"), deptoInput,
                guardarBtn);
        form.setPrefSize(300, 180);

        InternalWindow win = new InternalWindow("Crear Empleado", form);
        win.relocate(60, 60);

        // 2) AÑADIR el botón “✕” de cierre
        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size: 14;" +
                        "-fx-text-fill: gray;");

        // Al hacer click, quita esta ventana del mdiRoot
        closeBtn.setOnAction(e -> mdiRoot.getChildren().remove(win));

        // Ancla la X en la esquina superior derecha de 'win'
        AnchorPane.setTopAnchor(closeBtn, 5.0);
        AnchorPane.setRightAnchor(closeBtn, 5.0);
        // Asegúrate de que InternalWindow extienda de AnchorPane o Pane
        win.getChildren().add(closeBtn);

        makeDraggable(win);
        mdiRoot.getChildren().add(win);

        guardarBtn.setOnAction(evt -> {
            String nombre = nombreInput.getText().trim();
            String departamento = deptoInput.getText().trim();
            if (nombre.isEmpty() || departamento.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Completa todos los campos.").showAndWait();
                return;
            }
            Empleado nuevo = new Empleado();
            nuevo.setNombre(nombre);
            nuevo.setDepartamento(departamento);
            Usuario u = new Usuario();
            u.setId(1L);
            nuevo.setUsuario(u);

            Task<Void> crearTask = empleadoService.crearEmpleado(nuevo);
            crearTask.setOnSucceeded(e -> {
                mdiRoot.getChildren().remove(win);
                cargarEmpleados();
            });
            crearTask.setOnFailed(e -> new Alert(Alert.AlertType.ERROR,
                    "Error al crear: " + crearTask.getException().getMessage())
                    .showAndWait());
            new Thread(crearTask).start();
        });
    }

    @FXML
    private void onEditarEmpleado() {
        Empleado seleccionado = empleadoTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un empleado para editar.").showAndWait();
            return;
        }

        TextField nombreInput = new TextField(seleccionado.getNombre());
        TextField deptoInput = new TextField(seleccionado.getDepartamento());
        Button actualizarBtn = new Button("Actualizar");

        VBox form = new VBox(8,
                new Label("Nombre:"), nombreInput,
                new Label("Departamento:"), deptoInput,
                actualizarBtn);
        form.setPrefSize(300, 180);

        InternalWindow win = new InternalWindow("Editar Empleado", form);
        win.relocate(80, 80);

        // 2) AÑADIR el botón “✕” de cierre
        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size: 14;" +
                        "-fx-text-fill: gray;");

        // Al hacer click, quita esta ventana del mdiRoot
        closeBtn.setOnAction(e -> mdiRoot.getChildren().remove(win));

        // Ancla la X en la esquina superior derecha de 'win'
        AnchorPane.setTopAnchor(closeBtn, 5.0);
        AnchorPane.setRightAnchor(closeBtn, 5.0);
        // Asegúrate de que InternalWindow extienda de AnchorPane o Pane
        win.getChildren().add(closeBtn);

        mdiRoot.getChildren().add(win);

        actualizarBtn.setOnAction(evt -> {
            String nombre = nombreInput.getText().trim();
            String departamento = deptoInput.getText().trim();
            if (nombre.isEmpty() || departamento.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Completa todos los campos.").showAndWait();
                return;
            }
            seleccionado.setNombre(nombre);
            seleccionado.setDepartamento(departamento);

            Task<Void> actualizarTask = empleadoService.actualizarEmpleado(seleccionado);
            actualizarTask.setOnSucceeded(e -> {
                mdiRoot.getChildren().remove(win);
                cargarEmpleados();
            });
            actualizarTask.setOnFailed(e -> new Alert(Alert.AlertType.ERROR,
                    "Error al actualizar: " + actualizarTask.getException().getMessage())
                    .showAndWait());
            new Thread(actualizarTask).start();
        });
    }

    @FXML
    private void onEliminarEmpleado() {
        Empleado seleccionado = empleadoTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un empleado para eliminar.").showAndWait();
            return;
        }

        Task<Void> eliminarTask = empleadoService.eliminarEmpleado(seleccionado);
        eliminarTask.setOnSucceeded(e -> cargarEmpleados());
        eliminarTask.setOnFailed(e -> new Alert(Alert.AlertType.ERROR,
                "Error al eliminar: " + eliminarTask.getException().getMessage())
                .showAndWait());
        new Thread(eliminarTask).start();
    }

    /**
     * Añade comportamiento draggable a cualquier nodo dentro de mdiRoot.
     */
    private void makeDraggable(Node node) {
        node.setOnMousePressed(evt -> {
            dragOffsetX = evt.getSceneX() - node.getLayoutX();
            dragOffsetY = evt.getSceneY() - node.getLayoutY();
            node.getScene().setCursor(Cursor.MOVE);
        });
        node.setOnMouseDragged(evt -> {
            double newX = evt.getSceneX() - dragOffsetX;
            double newY = evt.getSceneY() - dragOffsetY;
            // Opcional: limitar dentro de mdiRoot
            if (newX >= 0 && newX + node.getBoundsInLocal().getWidth() <= mdiRoot.getWidth()) {
                node.setLayoutX(newX);
            }
            if (newY >= 0 && newY + node.getBoundsInLocal().getHeight() <= mdiRoot.getHeight()) {
                node.setLayoutY(newY);
            }
        });
        node.setOnMouseReleased(evt -> node.getScene().setCursor(Cursor.DEFAULT));
    }
}
