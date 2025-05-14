package com.example.controller;

import com.example.entity.Empleado;
import com.example.entity.Usuario;
import com.example.service.EmpleadoService;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Set;
import java.util.TreeSet;

public class EmpleadoController {

    @FXML
    private TableView<Empleado> empleadoTable;
    @FXML
    private TableColumn<Empleado, Long> idColumn;
    @FXML
    private TableColumn<Empleado, String> nombreColumn;
    @FXML
    private TableColumn<Empleado, String> departamentoColumn;

    @FXML
    private TextField nombreField;
    @FXML
    private TextField departamentoField;

    private final EmpleadoService empleadoService = new EmpleadoService();
    private Empleado empleadoSeleccionado;

    @FXML
    public void initialize() {
        // Configurar columnas
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        departamentoColumn.setCellValueFactory(new PropertyValueFactory<>("departamento"));

        // Cargar datos y configurar filtro en el header de la columna
        Task<ObservableList<Empleado>> task = empleadoService.obtenerEmpleados();
        task.setOnSucceeded(evt -> {
            ObservableList<Empleado> lista = task.getValue();
            empleadoTable.setItems(lista);

            // Extraer departamentos únicos
            Set<String> depts = new TreeSet<>();
            for (Empleado e : lista) {
                depts.add(e.getDepartamento());
            }

            // Crear MenuButton para filtrar
            MenuButton filterBtn = new MenuButton("Departamento");
            // “Todos” restaura la lista completa
            MenuItem all = new MenuItem("Todos");
            all.setOnAction(e -> empleadoTable.setItems(lista));
            filterBtn.getItems().add(all);

            // Una opción por cada departamento
            for (String d : depts) {
                MenuItem mi = new MenuItem(d);
                mi.setOnAction(e -> {
                    FilteredList<Empleado> filt = new FilteredList<>(lista);
                    filt.setPredicate(emp -> emp.getDepartamento().equals(d));
                    empleadoTable.setItems(filt);
                });
                filterBtn.getItems().add(mi);
            }

            // Sustituir el texto del header por el MenuButton
            departamentoColumn.setText(null);
            departamentoColumn.setGraphic(filterBtn);
        });
        task.setOnFailed(evt -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    @FXML
    private void handleCrearEmpleado() {
        String nombre = nombreField.getText().trim();
        String departamento = departamentoField.getText().trim();
        if (nombre.isEmpty() || departamento.isEmpty()) {
            mostrarAlerta("Error", "Debes completar todos los campos.");
            return;
        }
        Empleado nuevo = new Empleado();
        nuevo.setNombre(nombre);
        nuevo.setDepartamento(departamento);
        // Asignar usuario de ejemplo (luego usar el logueado)
        Usuario u = new Usuario();
        u.setId(1L);
        nuevo.setUsuario(u);

        Task<Void> t = empleadoService.crearEmpleado(nuevo);
        t.setOnSucceeded(e -> {
            mostrarAlerta("Éxito", "Empleado creado correctamente.");
            nombreField.clear();
            departamentoField.clear();
            initialize(); // recarga tabla y filtro
        });
        t.setOnFailed(e -> mostrarAlerta("Error", t.getException().getMessage()));
        new Thread(t).start();
    }

    @FXML
    private void handleEditarEmpleado() {
        empleadoSeleccionado = empleadoTable.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            nombreField.setText(empleadoSeleccionado.getNombre());
            departamentoField.setText(empleadoSeleccionado.getDepartamento());
        } else {
            mostrarAlerta("Error", "Selecciona un empleado para editar.");
        }
    }

    @FXML
    private void handleGuardarEmpleado() {
        if (empleadoSeleccionado == null) {
            mostrarAlerta("Error", "No hay ningún empleado seleccionado para actualizar.");
            return;
        }
        String nombre = nombreField.getText().trim();
        String departamento = departamentoField.getText().trim();
        if (nombre.isEmpty() || departamento.isEmpty()) {
            mostrarAlerta("Error", "Debes completar todos los campos.");
            return;
        }
        empleadoSeleccionado.setNombre(nombre);
        empleadoSeleccionado.setDepartamento(departamento);

        Task<Void> t = empleadoService.actualizarEmpleado(empleadoSeleccionado);
        t.setOnSucceeded(e -> {
            mostrarAlerta("Éxito", "Empleado actualizado correctamente.");
            nombreField.clear();
            departamentoField.clear();
            empleadoSeleccionado = null;
            initialize();
        });
        t.setOnFailed(e -> mostrarAlerta("Error", t.getException().getMessage()));
        new Thread(t).start();
    }

    @FXML
    private void handleEliminarEmpleado() {
        Empleado selected = empleadoTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarAlerta("Error", "Selecciona un empleado para eliminar.");
            return;
        }

        Task<Void> t = empleadoService.eliminarEmpleado(selected);
        t.setOnSucceeded(e -> {
            mostrarAlerta("Éxito", "Empleado eliminado correctamente.");
            nombreField.clear();
            departamentoField.clear();
            empleadoSeleccionado = null;
            initialize();
        });
        t.setOnFailed(e -> mostrarAlerta("Error", t.getException().getMessage()));
        new Thread(t).start();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
