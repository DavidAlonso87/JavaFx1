package com.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class HomeController {

    @FXML
    private void handleEmpleados(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/MdiEmpleadoViewCss.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tabla de empleados"); // Aqui fijas el nuevo titulo

            // --- Aquí fijamos tamaño relativo de ventana (por ejemplo 50% x 60% de
            // pantalla) ---
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setWidth(bounds.getWidth() * 0.5);
            stage.setHeight(bounds.getHeight() * 0.6);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUsuarios(ActionEvent event) {
        // A implementar más adelante
    }

    @FXML
    private void handleSalir(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
