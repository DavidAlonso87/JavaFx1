package com.example.service;

import com.example.entity.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class UsuarioService {

    private static final String URL_API = "http://localhost:8080/api/usuarios"; // Cambia a la URL de tu API backend

    // Método para obtener todos los usuarios
    public Task<ObservableList<Usuario>> obtenerUsuarios() {
        return new Task<ObservableList<Usuario>>() {
            @Override
            protected ObservableList<Usuario> call() throws Exception {
                // Aquí puedes realizar la petición GET para obtener los usuarios desde la API
                URL url = new URL(URL_API);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Aquí procesarías la respuesta JSON y la convertirías en una lista de usuarios
                    // Este es un ejemplo ficticio
                    ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
                    // Lógica de parseo JSON aquí

                    return usuarios;
                } else {
                    throw new Exception("Error al obtener los usuarios");
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                // Aquí podrías actualizar la interfaz con los usuarios obtenidos
            }

            @Override
            protected void failed() {
                super.failed();
                mostrarAlerta("Error", "No se pudieron obtener los usuarios.");
            }

            private void mostrarAlerta(String titulo, String mensaje) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(titulo);
                alert.setHeaderText(null);
                alert.setContentText(mensaje);
                alert.showAndWait();
            }
        };
    }

    // Método para guardar un nuevo usuario
    public Task<Void> guardarUsuario(Usuario usuario) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Convertir el usuario a JSON
                String jsonInputString = String.format("{\"username\":\"%s\", \"password\":\"%s\", \"rol\":\"%s\"}",
                        usuario.getUsername(), usuario.getPassword(), usuario.getRol());

                // Realizar la petición HTTP
                URL url = new URL(URL_API);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    updateMessage("Usuario guardado correctamente.");
                } else {
                    throw new Exception("Error al guardar el usuario");
                }

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                mostrarAlerta("Éxito", "Usuario guardado correctamente.");
            }

            @Override
            protected void failed() {
                super.failed();
                mostrarAlerta("Error", "No se pudo guardar el usuario.");
            }

            private void mostrarAlerta(String titulo, String mensaje) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(titulo);
                alert.setHeaderText(null);
                alert.setContentText(mensaje);
                alert.showAndWait();
            }
        };
    }
}
