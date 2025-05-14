package com.example.service;

import com.example.entity.Empleado;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmpleadoService {

    private static final String URL_API = "http://localhost:8080/empleados";
    private static String jwtToken;

    /** Permite almacenar el token JWT tras el login */
    public static void setJwtToken(String token) {
        jwtToken = token;
    }

    public static String getJwtToken() {
        return jwtToken;
    }

    /** GET /empleados **/
    public Task<ObservableList<Empleado>> obtenerEmpleados() {
        return new Task<>() {
            @Override
            protected ObservableList<Empleado> call() throws Exception {
                URL url = new URL(URL_API);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if (jwtToken != null && !jwtToken.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
                }
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new Exception("Error al obtener empleados: código " + conn.getResponseCode());
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null)
                    sb.append(line);
                in.close();
                Empleado[] arr = new Gson().fromJson(sb.toString(), Empleado[].class);
                return FXCollections.observableArrayList(arr);
            }
        };
    }

    /** POST /empleados **/
    public Task<Void> crearEmpleado(Empleado emp) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                String json = String.format(
                        "{\"nombre\":\"%s\",\"departamento\":\"%s\",\"usuario\":{\"id\":%d}}",
                        emp.getNombre(), emp.getDepartamento(), emp.getUsuario().getId());
                URL url = new URL(URL_API);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                if (jwtToken != null && !jwtToken.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
                }
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes("utf-8"));
                }
                if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                    throw new Exception("Error al crear empleado: código " + conn.getResponseCode());
                }
                return null;
            }

            @Override
            protected void succeeded() {
                mostrarAlerta("Éxito", "Empleado creado correctamente.");
            }

            @Override
            protected void failed() {
                mostrarAlerta("Error", getException().getMessage());
            }

            private void mostrarAlerta(String titulo, String mensaje) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle(titulo);
                a.setHeaderText(null);
                a.setContentText(mensaje);
                a.showAndWait();
            }
        };
    }

    /** PUT /empleados/{id} **/
    public Task<Void> actualizarEmpleado(Empleado emp) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                String json = new Gson().toJson(emp);
                URL url = new URL(URL_API);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                if (jwtToken != null && !jwtToken.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
                }
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes("utf-8"));
                }
                int code = conn.getResponseCode();
                // Tu backend responde CON 201 CREATED tanto para crear como para actualizar
                if (code != HttpURLConnection.HTTP_CREATED) {
                    throw new Exception("Error al actualizar empleado: código " + code);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                mostrarAlerta("Éxito", "Empleado actualizado correctamente.");
            }

            @Override
            protected void failed() {
                mostrarAlerta("Error", getException().getMessage());
            }

            private void mostrarAlerta(String titulo, String mensaje) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle(titulo);
                a.setHeaderText(null);
                a.setContentText(mensaje);
                a.showAndWait();
            }
        };
    }

    /** DELETE /empleados/{id} **/
    public Task<Void> eliminarEmpleado(Empleado emp) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                URL url = new URL(URL_API + "/" + emp.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                if (jwtToken != null && !jwtToken.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
                }
                int code = conn.getResponseCode();
                if (code != HttpURLConnection.HTTP_NO_CONTENT && code != HttpURLConnection.HTTP_OK) {
                    throw new Exception("Error al eliminar empleado: código " + code);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                mostrarAlerta("Éxito", "Empleado eliminado correctamente.");
            }

            @Override
            protected void failed() {
                mostrarAlerta("Error", getException().getMessage());
            }

            private void mostrarAlerta(String titulo, String mensaje) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle(titulo);
                a.setHeaderText(null);
                a.setContentText(mensaje);
                a.showAndWait();
            }
        };
    }
}
