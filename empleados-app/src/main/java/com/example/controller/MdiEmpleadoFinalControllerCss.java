package com.example.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.entity.Empleado;
import com.example.entity.Usuario;
import com.example.service.EmpleadoService;
import com.example.ui.InternalWindow;
import com.example.ui.behavior.DraggableHandler;
import com.example.ui.behavior.ResizableHandler;
import com.example.ui.behavior.TransitionHelper;
import com.example.ui.behavior.FadeTransitionHelper;
import com.example.ui.behavior.FormHeaderHelper;
import com.example.ui.behavior.WindowPreferences;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * Controlador para la vista MDI de Empleados con cabecera,
 * estilos CSS, botones de ventana (minimizar, maximizar, cerrar),
 * arrastre, redimensionado manual y persistencia de posición/tamaño.
 */
public class MdiEmpleadoFinalControllerCss implements Initializable {

    @FXML
    private AnchorPane mdiRoot;
    @FXML
    private AnchorPane empleadoWindow;

    // Header
    @FXML
    private HBox windowHeader;
    @FXML
    private ImageView windowIcon;
    @FXML
    private Label windowTitle;
    @FXML
    private Button btnMinimize;
    @FXML
    private Button btnMaximize;
    @FXML
    private Button btnClose;

    // Tabla
    @FXML
    private TableView<Empleado> empleadoTable;
    @FXML
    private TableColumn<Empleado, Long> idColumn;
    @FXML
    private TableColumn<Empleado, String> nombreColumn;
    @FXML
    private TableColumn<Empleado, String> departamentoColumn;

    // Action bar
    @FXML
    private HBox actionBar;

    private final EmpleadoService empleadoService = new EmpleadoService();
    private final WindowPreferences tablePrefs = new WindowPreferences("empleadosTable");

    private InternalWindow tableWindow;

    private boolean maximized = false;
    private double prevX, prevY, prevW, prevH;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Cargar CSS
        mdiRoot.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(
                        getClass().getResource("/com/example/view/style.css")
                                .toExternalForm());
            }
        });

        // Columnas y datos
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        departamentoColumn.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        cargarEmpleados();

        // Crear ventana interna
        crearWindow();

        // Restaurar posición/tamaño/estado
        tablePrefs.apply(tableWindow, () -> {
            if (!maximized)
                toggleMaximize();
        });

        // Hacer draggable y resizable
        DraggableHandler.makeDraggable(windowHeader, tableWindow);
        ResizableHandler.makeResizable(tableWindow);

        // Botones de ventana
        btnClose.setOnAction(e -> mdiRoot.getChildren().remove(tableWindow));
        btnMinimize.setOnAction(e -> mdiRoot.getChildren().remove(tableWindow));
        btnMaximize.setOnAction(e -> toggleMaximize());

        // Añadir al root
        mdiRoot.getChildren().add(tableWindow);

        // Guardar tras cada cambio
        tableWindow.layoutXProperty().addListener((o, ov, nv) -> tablePrefs.save(tableWindow, maximized));
        tableWindow.layoutYProperty().addListener((o, ov, nv) -> tablePrefs.save(tableWindow, maximized));
        tableWindow.prefWidthProperty().addListener((o, ov, nv) -> tablePrefs.save(tableWindow, maximized));
        tableWindow.prefHeightProperty().addListener((o, ov, nv) -> tablePrefs.save(tableWindow, maximized));
    }

    private void crearWindow() {
        tableWindow = new InternalWindow("", empleadoWindow);
        tableWindow.setPrefSize(600, 400);
        tableWindow.relocate(10, 10);
    }

    private void cargarEmpleados() {
        Task<ObservableList<Empleado>> task = empleadoService.obtenerEmpleados();
        task.setOnSucceeded(evt -> empleadoTable.setItems(task.getValue()));
        task.setOnFailed(evt -> task.getException().printStackTrace());
        new Thread(task).start();
    }

    @FXML
    private void onShowTable(ActionEvent evt) {
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

        HBox headerBox = FormHeaderHelper.createHeader(
                "Crear Empleado", "create-form-content-header");

        VBox form = getForm(nombreInput, deptoInput, guardarBtn, headerBox);

        InternalWindow win = new InternalWindow("Crear Empleado", form);
        win.getStyleClass().add("create-form-window");
        win.relocate(60, 60);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size:14;" +
                        "-fx-text-fill:gray;");
        closeBtn.setOnAction(e -> mdiRoot.getChildren().remove(win));
        AnchorPane.setTopAnchor(closeBtn, 5.0);
        AnchorPane.setRightAnchor(closeBtn, 5.0);
        win.getChildren().add(closeBtn);

        DraggableHandler.makeDraggable(win, win);
        ResizableHandler.makeResizable(win);
        mdiRoot.getChildren().add(win);

        Platform.runLater(() -> {
            win.applyCss();
            win.layout();
            double fromX = -mdiRoot.getWidth();
            double baseX = win.getLayoutX();
            double extra = 600;
            double toX = baseX + extra;
            TransitionHelper.slideInFromLeft(win, fromX, toX, 1000);
        });

        guardarBtn.setOnAction(e -> {
            if (nombreInput.getText().isEmpty() || deptoInput.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Completa todos los campos.").showAndWait();
                return;
            }
            Empleado nuevo = new Empleado();
            nuevo.setNombre(nombreInput.getText());
            nuevo.setDepartamento(deptoInput.getText());
            Usuario u = new Usuario();
            u.setId(1L);
            nuevo.setUsuario(u);

            Task<Void> crear = empleadoService.crearEmpleado(nuevo);
            crear.setOnSucceeded(evt -> {
                mdiRoot.getChildren().remove(win);
                cargarEmpleados();
            });
            crear.setOnFailed(evt -> new Alert(
                    Alert.AlertType.ERROR,
                    "Error al crear: " + crear.getException().getMessage()).showAndWait());
            new Thread(crear).start();
        });
    }

    private VBox getForm(TextField nombreInput, TextField deptoInput, Button guardarBtn, HBox headerBox) {
        VBox form = new VBox(10,
                headerBox,
                new Label("Nombre:"), nombreInput,
                new Label("Departamento:"), deptoInput,
                guardarBtn);
        form.setPrefSize(300, 300);
        form.getStyleClass().add("create-form");
        return form;
    }

    @FXML
    private void onEditarEmpleado() {
        Empleado sel = empleadoTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un empleado.").showAndWait();
            return;
        }
        TextField nombreInput = new TextField(sel.getNombre());
        TextField deptoInput = new TextField(sel.getDepartamento());
        Button actualizarBtn = new Button("Actualizar");

        HBox headerBox = FormHeaderHelper.createHeader(
                "Editar Empleado", "edit-form-content-header");

        VBox form = new VBox(8,
                headerBox,
                new Label("Nombre:"), nombreInput,
                new Label("Departamento:"), deptoInput,
                actualizarBtn);
        form.setPrefSize(300, 300);
        form.getStyleClass().add("edit-form");

        InternalWindow win = new InternalWindow("Editar Empleado", form);
        win.getStyleClass().add("edit-form-window");
        win.relocate(600, 80);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size:14;" +
                        "-fx-text-fill:gray;");
        closeBtn.setOnAction(e -> mdiRoot.getChildren().remove(win));
        AnchorPane.setTopAnchor(closeBtn, 5.0);
        AnchorPane.setRightAnchor(closeBtn, 5.0);
        win.getChildren().add(closeBtn);

        DraggableHandler.makeDraggable(win, win);
        ResizableHandler.makeResizable(win);
        mdiRoot.getChildren().add(win);

        Platform.runLater(() -> {
            win.applyCss();
            win.layout();
            FadeTransitionHelper.fadeIn(win, 700);
        });

        actualizarBtn.setOnAction(e -> {
            sel.setNombre(nombreInput.getText());
            sel.setDepartamento(deptoInput.getText());
            Task<Void> upd = empleadoService.actualizarEmpleado(sel);
            upd.setOnSucceeded(e2 -> {
                mdiRoot.getChildren().remove(win);
                cargarEmpleados();
            });
            upd.setOnFailed(e2 -> new Alert(
                    Alert.AlertType.ERROR,
                    "Error al actualizar: " + upd.getException().getMessage()).showAndWait());
            new Thread(upd).start();
        });
    }

    @FXML
    private void onEliminarEmpleado() {
        Empleado sel = empleadoTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un empleado.").showAndWait();
            return;
        }
        Task<Void> del = empleadoService.eliminarEmpleado(sel);
        del.setOnSucceeded(e -> cargarEmpleados());
        del.setOnFailed(e2 -> new Alert(
                Alert.AlertType.ERROR,
                "Error al eliminar: " + del.getException().getMessage()).showAndWait());
        new Thread(del).start();
    }

    private void toggleMaximize() {
        if (!maximized) {
            prevX = tableWindow.getLayoutX();
            prevY = tableWindow.getLayoutY();
            prevW = tableWindow.getPrefWidth();
            prevH = tableWindow.getPrefHeight();
            tableWindow.setLayoutX(0);
            tableWindow.setLayoutY(0);
            tableWindow.setPrefSize(mdiRoot.getWidth(), mdiRoot.getHeight());
            maximized = true;
            btnMaximize.setText("❐");
        } else {
            tableWindow.setLayoutX(prevX);
            tableWindow.setLayoutY(prevY);
            tableWindow.setPrefSize(prevW, prevH);
            maximized = false;
            btnMaximize.setText("▢");
        }
        // Guardar estado maximizado/restaurado
        tablePrefs.save(tableWindow, maximized);
    }
}
