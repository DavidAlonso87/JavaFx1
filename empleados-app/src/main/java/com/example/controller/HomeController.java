package com.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

public class HomeController {

    @FXML
    private void handleEmpleados(ActionEvent event) {
        try {
            // 1) Carga el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/DockfxEmpleadoView.fxml"));
            Parent root = loader.load();

            // 2) Calcula un tamaño razonable (por ejemplo 80% ancho x 70% alto)
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            double w = bounds.getWidth() * 0.8;
            double h = bounds.getHeight() * 0.7;

            // 3) Escena con ese tamaño
            Scene scene = new Scene(root, w, h);

            // 4) Nueva Stage para Empleados
            Stage empStage = new Stage();
            empStage.setTitle("Gestión de Empleados");
            empStage.setScene(scene);
            empStage.centerOnScreen();
            empStage.show();

            // 5) Cierra la ventana de login
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

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
