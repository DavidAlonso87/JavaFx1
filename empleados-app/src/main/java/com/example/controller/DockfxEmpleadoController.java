package com.example.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.entity.Empleado;
import com.example.entity.Usuario;
import com.example.service.EmpleadoService;
import com.example.ui.InternalWindow;
import com.example.ui.behavior.FormHeaderHelper;
import com.example.ui.behavior.ResizableHandler;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.dockfx.DockPane;
import org.dockfx.DockNode;
import org.dockfx.DockPos;

/**
 * Controlador para la vista DockFX de Empleados con:
 * - navegación en columna izquierda
 * - tabla en centro
 * - acciones en columna derecha
 * - formularios en InternalWindow
 */
public class DockfxEmpleadoController implements Initializable {

    // === Paneles principales ===
    @FXML
    private DockPane dockPane;
    @FXML
    private VBox navContainer;
    @FXML
    private VBox tableContainer;
    @FXML
    private VBox actionsContainer;

    // === Botones de navegación ===
    @FXML
    private Button btnNavEmpleados;
    @FXML
    private Button btnNavUsuarios;
    @FXML
    private Button btnNavProductos;
    @FXML
    private Button btnNavSalir;

    // === Botones de acción ===
    @FXML
    private Button btnCrear;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;

    // === Tabla principal ===
    @FXML
    private TableView<Empleado> empleadoTable;

    // Servicio para cargar datos
    private final EmpleadoService empleadoService = new EmpleadoService();

    // DockNodes guardados como campos para poder redockearlos
    private DockNode dnNavNode;
    private DockNode dnTableNode;
    private DockNode dnActionsNode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) Crear los DockNode y guardarlos en campos
        dnTableNode = new DockNode(tableContainer, "Empleados");
        dnNavNode = new DockNode(navContainer, "Navegación");
        dnActionsNode = new DockNode(actionsContainer, "Acciones");

        // 2) Dockearlos: tabla primero en CENTER
        dnTableNode.dock(dockPane, DockPos.CENTER);
        // después nav y actions relativos a la tabla
        dnNavNode.dock(dockPane, DockPos.LEFT, dnTableNode);
        dnActionsNode.dock(dockPane, DockPos.RIGHT, dnTableNode);

        // 3) Ajustar divisores del SplitPane interno para 30/50/20 %
        Platform.runLater(() -> {
            if (!dockPane.getChildren().isEmpty()
                    && dockPane.getChildren().get(0) instanceof SplitPane) {
                SplitPane sp = (SplitPane) dockPane.getChildren().get(0);
                // primer divisor al 30%, segundo al 80% (30 + 50)
                sp.setDividerPositions(0.2, 0.9);
            }
        });

        // 4) Configurar columnas y cargar datos en la tabla
        setupTable(empleadoTable);
        cargarEmpleados(empleadoTable);
    }

    // === Handlers de navegación ===

    /** Muestra (o vuelve a dockear) la tabla de empleados y la recarga */
    @FXML
    private void onNavEmpleados(ActionEvent event) {
        dnTableNode.dock(dockPane, DockPos.CENTER);
        recargarTabla();
    }

    @FXML
    private void onNavUsuarios(ActionEvent event) {
        // TODO: implementar vista "Usuarios"
    }

    @FXML
    private void onNavProductos(ActionEvent event) {
        // TODO: implementar vista "Productos"
    }

    @FXML
    private void onNavSalir(ActionEvent event) {
        // TODO: volver al menú anterior
    }

    // === Botones de acción (formularios) ===

    @FXML
    private void onCrearEmpleado(ActionEvent event) {
        Node form = buildCrearForm();
        InternalWindow win = new InternalWindow("Crear Empleado", form);
        ResizableHandler.makeResizable(win);
        dockPane.getChildren().add(win);
    }

    @FXML
    private void onEditarEmpleado(ActionEvent event) {
        Empleado sel = empleadoTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un empleado.").showAndWait();
            return;
        }
        Node form = buildEditarForm(sel);
        InternalWindow win = new InternalWindow("Editar Empleado", form);
        ResizableHandler.makeResizable(win);
        dockPane.getChildren().add(win);
    }

    @FXML
    private void onEliminarEmpleado(ActionEvent event) {
        Empleado sel = empleadoTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un empleado a eliminar.").showAndWait();
            return;
        }
        Task<Void> task = empleadoService.eliminarEmpleado(sel);
        task.setOnSucceeded(e -> recargarTabla());
        task.setOnFailed(e -> new Alert(
                Alert.AlertType.ERROR,
                "Error al eliminar: " + task.getException().getMessage())
                .showAndWait());
        new Thread(task).start();
    }

    // === Métodos auxiliares ===

    /** Recarga la tabla principal de empleados */
    private void recargarTabla() {
        cargarEmpleados(empleadoTable);
    }

    /** Configura columnas ID, Nombre y Departamento */
    private void setupTable(TableView<Empleado> table) {
        TableColumn<Empleado, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<Empleado, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(150);

        TableColumn<Empleado, String> colDepto = new TableColumn<>("Departamento");
        colDepto.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        colDepto.setPrefWidth(150);

        table.getColumns().setAll(colId, colNombre, colDepto);
    }

    /** Carga empleados en el TableView especificado */
    private void cargarEmpleados(TableView<Empleado> table) {
        Task<ObservableList<Empleado>> task = empleadoService.obtenerEmpleados();
        task.setOnSucceeded(evt -> table.setItems(task.getValue()));
        task.setOnFailed(evt -> evt.getSource().getException().printStackTrace());
        new Thread(task).start();
    }

    /** Construye y devuelve el formulario “Crear Empleado” */
    private Node buildCrearForm() {
        TextField nombreInput = new TextField();
        TextField deptoInput = new TextField();
        Button guardarBtn = new Button("Guardar");

        HBox header = FormHeaderHelper.createHeader(
                "Crear Empleado", "create-form-content-header");

        VBox form = new VBox(10,
                header,
                new Label("Nombre:"), nombreInput,
                new Label("Departamento:"), deptoInput,
                guardarBtn);
        form.setPrefSize(300, 200);
        form.getStyleClass().add("create-form");

        guardarBtn.setOnAction(e -> {
            String n = nombreInput.getText().trim();
            String d = deptoInput.getText().trim();
            if (n.isEmpty() || d.isEmpty()) {
                new Alert(Alert.AlertType.WARNING,
                        "Completa todos los campos.").showAndWait();
                return;
            }
            Empleado emp = new Empleado();
            emp.setNombre(n);
            emp.setDepartamento(d);
            Usuario u = new Usuario();
            u.setId(1L);
            emp.setUsuario(u);

            Task<Void> task = empleadoService.crearEmpleado(emp);
            task.setOnSucceeded(ev -> recargarTabla());
            task.setOnFailed(ev -> new Alert(
                    Alert.AlertType.ERROR,
                    "Error al crear: " + task.getException().getMessage())
                    .showAndWait());
            new Thread(task).start();
        });

        return form;
    }

    /** Construye y devuelve el formulario “Editar Empleado” */
    private Node buildEditarForm(Empleado sel) {
        TextField nombreInput = new TextField(sel.getNombre());
        TextField deptoInput = new TextField(sel.getDepartamento());
        Button actualizarBtn = new Button("Actualizar");

        HBox header = FormHeaderHelper.createHeader(
                "Editar Empleado", "edit-form-content-header");

        VBox form = new VBox(10,
                header,
                new Label("Nombre:"), nombreInput,
                new Label("Departamento:"), deptoInput,
                actualizarBtn);
        form.setPrefSize(300, 180);
        form.getStyleClass().add("edit-form");

        actualizarBtn.setOnAction(e -> {
            sel.setNombre(nombreInput.getText().trim());
            sel.setDepartamento(deptoInput.getText().trim());
            Task<Void> task = empleadoService.actualizarEmpleado(sel);
            task.setOnSucceeded(ev -> recargarTabla());
            task.setOnFailed(ev -> new Alert(
                    Alert.AlertType.ERROR,
                    "Error al actualizar: " + task.getException().getMessage())
                    .showAndWait());
            new Thread(task).start();
        });

        return form;
    }

}
