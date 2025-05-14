package com.example.controller;

import com.example.model.JwtResponse;
import com.example.service.AuthService;
import com.example.service.EmpleadoService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor ingrese ambos campos.");
            errorLabel.setVisible(true);
            return;
        }

        Task<JwtResponse> task = authService.login(username, password);

        task.setOnSucceeded(event -> {
            JwtResponse response = task.getValue();
            if (response != null && response.getToken() != null) {
                // Almacenar el token JWT
                EmpleadoService.setJwtToken(response.getToken());

                // Abrir la nueva vista principal con los botones
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/HomeView.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                    errorLabel.setText("No se pudo cargar la vista principal.");
                    errorLabel.setVisible(true);
                }

            } else {
                errorLabel.setText("Credenciales incorrectas.");
                errorLabel.setVisible(true);
            }
        });

        task.setOnFailed(event -> {
            errorLabel.setText("Error de conexión.");
            errorLabel.setVisible(true);
        });

        new Thread(task).start(); // Ejecutar la tarea en un hilo separado
    }

    @FXML
    public void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }
}
