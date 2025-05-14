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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class MdiEmpleadoController implements Initializable {

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

    private final EmpleadoService empleadoService = new EmpleadoService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas de la tabla
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        departamentoColumn.setCellValueFactory(new PropertyValueFactory<>("departamento"));

        // Cargar datos iniciales
        cargarEmpleados();

        // 3) Envuelve la TableView en una ventana interna MDI
        InternalWindow tableWindow = new InternalWindow("Listado de Empleados", empleadoTable);

        // 4) Ajusta tamaño y posición por defecto
        tableWindow.setPrefSize(600, 300);
        tableWindow.relocate(50, 50);

        // 5) Añádelo al root MDI para que pueda moverse libremente
        mdiRoot.getChildren().add(tableWindow);
    }

    private void cargarEmpleados() {
        Task<ObservableList<Empleado>> loadTask = empleadoService.obtenerEmpleados();
        loadTask.setOnSucceeded(evt -> empleadoTable.setItems(loadTask.getValue()));
        loadTask.setOnFailed(evt -> loadTask.getException().printStackTrace());
        new Thread(loadTask).start();
    }

    /**
     * Handler del botón “Mostrar tabla” (onShowTable).
     * Simplemente recarga los datos de la tabla.
     */
    @FXML
    private void onShowTable(ActionEvent event) {
        cargarEmpleados();
    }

    /** Abre un InternalWindow para crear un nuevo empleado */
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

    /** Abre un InternalWindow para editar el empleado seleccionado */
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

    /** Elimina el empleado seleccionado tras confirmación */
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
}
