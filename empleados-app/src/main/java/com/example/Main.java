package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML de la vista de login (ruta correcta)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/login-view.fxml"));
        Scene scene = new Scene(loader.load());

        // Establecer el título de la ventana
        primaryStage.setTitle("Login de Usuarios");

        // Establecer la escena con el archivo FXML cargado
        primaryStage.setScene(scene);

        // Mostrar la ventana
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}