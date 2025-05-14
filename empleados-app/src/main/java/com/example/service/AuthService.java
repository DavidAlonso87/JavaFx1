package com.example.service;

import com.example.model.JwtResponse;
import com.example.model.UsuarioLoginDTO;
import com.google.gson.Gson;
import javafx.concurrent.Task;
import okhttp3.*;

import java.io.IOException;

public class AuthService {

    private static final String BASE_URL = "http://localhost:8080/auth";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    public Task<JwtResponse> login(String username, String password) {
        Task<JwtResponse> task = new Task<JwtResponse>() {
            @Override
            protected JwtResponse call() throws Exception {
                UsuarioLoginDTO usuarioLoginDTO = new UsuarioLoginDTO(username, password);

                RequestBody body = RequestBody.create(
                        new Gson().toJson(usuarioLoginDTO), JSON);

                Request request = new Request.Builder()
                        .url(LOGIN_URL)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error: " + response);
                    }

                    String responseBody = response.body().string();
                    return new Gson().fromJson(responseBody, JwtResponse.class);
                } catch (IOException e) {
                    throw new RuntimeException("Login failed", e);
                }
            }
        };
        return task;
    }

    public boolean guardarAutenticacion(Long empleadoId, UsuarioLoginDTO usuarioLoginDTO) {
        String url = "http://localhost:8080/empleados/" + empleadoId + "/usuario";

        RequestBody body = RequestBody.create(
                new Gson().toJson(usuarioLoginDTO), JSON);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + EmpleadoService.getJwtToken())
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void handleLoginError(Exception e) {
        e.printStackTrace();
    }
}
