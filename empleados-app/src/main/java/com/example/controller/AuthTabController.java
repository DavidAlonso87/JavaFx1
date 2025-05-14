package com.example.controller;

import com.example.entity.Empleado;
import com.example.model.UsuarioLoginDTO;
import com.example.service.AuthService;
import com.example.service.EmpleadoService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthTabController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private Empleado empleado;

    private final AuthService authService = new AuthService();

    // Método para inicializar el Tab con los datos del empleado
    public void initData(Empleado empleado) {
        this.empleado = empleado;

        // Aquí podrías hacer una petición para cargar los datos del usuario del
        // empleado si lo necesitas
    }

    // Método que se ejecuta al presionar el botón "Guardar"
    @FXML
    public void handleSave() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor ingrese ambos campos.");
            errorLabel.setVisible(true);
            return;
        }

        UsuarioLoginDTO usuarioLoginDTO = new UsuarioLoginDTO(username, password);

        boolean success = authService.guardarAutenticacion(empleado.getId(), usuarioLoginDTO);

        if (success) {
            errorLabel.setText("Datos guardados exitosamente.");
        } else {
            errorLabel.setText("Hubo un error al guardar.");
        }
        errorLabel.setVisible(true);
    }
}
