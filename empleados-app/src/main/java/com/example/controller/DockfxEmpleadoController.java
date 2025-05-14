package com.example.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.entity.Empleado;
import com.example.service.EmpleadoService;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.dockfx.DockPane;
import org.dockfx.DockNode;

/**
 * Controlador para la vista DockFX de Empleados.
 */
public class DockfxEmpleadoController implements Initializable {

    @FXML
    private DockPane dockPane;

    // Crear Empleado
    @FXML
    private DockNode dnCrear;
    @FXML
    private TextField crearNombre;
    @FXML
    private TextField crearDepartamento;
    @FXML
    private Button btnGuardar;

    // Editar Empleado
    @FXML
    private DockNode dnEditar;
    @FXML
    private TableView<Empleado> editarTable;
    @FXML
    private TextField editarNombre;
    @FXML
    private TextField editarDepartamento;
    @FXML
    private Button btnActualizar;

    // Eliminar Empleado
    @FXML
    private DockNode dnEliminar;
    @FXML
    private TableView<Empleado> eliminarTable;
    @FXML
    private Button btnEliminar;

    // Tabla principal
    @FXML
    private DockNode dnEmpleados;
    @FXML
    private TableView<Empleado> empleadoTable;
    @FXML
    private TableColumn<Empleado, Long> colId;
    @FXML
    private TableColumn<Empleado, String> colNombre;
    @FXML
    private TableColumn<Empleado, String> colDepartamento;

    private final EmpleadoService empleadoService = new EmpleadoService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDepartamento.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        editarTable.getColumns().setAll(colId, colNombre, colDepartamento);
        eliminarTable.getColumns().setAll(colId, colNombre, colDepartamento);

        // Cargar datos
        cargarEmpleados(empleadoTable);
        cargarEmpleados(editarTable);
        cargarEmpleados(eliminarTable);

        // Selección en editarTable
        editarTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                editarNombre.setText(sel.getNombre());
                editarDepartamento.setText(sel.getDepartamento());
            }
        });

    }

    private void cargarEmpleados(TableView<Empleado> table) {
        Task<ObservableList<Empleado>> task = empleadoService.obtenerEmpleados();
        task.setOnSucceeded(evt -> table.setItems(task.getValue()));
        task.setOnFailed(evt -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    @FXML
    private void onGuardarEmpleado(ActionEvent event) {
        String nombre = crearNombre.getText();
        String depto = crearDepartamento.getText();
        if (nombre.isEmpty() || depto.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Completa todos los campos.").showAndWait();
            return;
        }
        Empleado emp = new Empleado();
        emp.setNombre(nombre);
        emp.setDepartamento(depto);
        Task<Void> task = empleadoService.crearEmpleado(emp);
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            crearNombre.clear();
            crearDepartamento.clear();
            recargarTodas();
        }));
        task.setOnFailed(e -> new Alert(Alert.AlertType.ERROR, "Error al crear: " + task.getException().getMessage())
                .showAndWait());
        new Thread(task).start();
    }

    @FXML
    private void onEditarEmpleado(ActionEvent event) {
        Empleado sel = editarTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un empleado a editar.").showAndWait();
            return;
        }
        sel.setNombre(editarNombre.getText());
        sel.setDepartamento(editarDepartamento.getText());
        Task<Void> task = empleadoService.actualizarEmpleado(sel);
        task.setOnSucceeded(e -> Platform.runLater(this::recargarTodas));
        task.setOnFailed(
                e -> new Alert(Alert.AlertType.ERROR, "Error al actualizar: " + task.getException().getMessage())
                        .showAndWait());
        new Thread(task).start();
    }

    @FXML
    private void onEliminarEmpleado(ActionEvent event) {
        Empleado sel = eliminarTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un empleado a eliminar.").showAndWait();
            return;
        }
        Task<Void> task = empleadoService.eliminarEmpleado(sel);
        task.setOnSucceeded(e -> Platform.runLater(this::recargarTodas));
        task.setOnFailed(e -> new Alert(Alert.AlertType.ERROR, "Error al eliminar: " + task.getException().getMessage())
                .showAndWait());
        new Thread(task).start();
    }

    /** Recarga datos en las tres tablas */
    private void recargarTodas() {
        cargarEmpleados(empleadoTable);
        cargarEmpleados(editarTable);
        cargarEmpleados(eliminarTable);
    }
}
