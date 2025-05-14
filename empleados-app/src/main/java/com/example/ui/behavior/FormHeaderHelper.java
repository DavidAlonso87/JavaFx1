package com.example.ui.behavior;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Helper para generar un HBox con un Label centrado,
 * que servirá como encabezado en formularios.
 */
public class FormHeaderHelper {

    /**
     * Crea un encabezado centrado con el texto y la clase CSS indicada.
     *
     * @param text       El texto del encabezado (p.ej. "Crear Empleado")
     * @param styleClass La clase CSS a aplicar al Label (p.ej.
     *                   "create-form-header")
     * @return Un HBox centrado que contiene el Label estilizado
     */
    public static HBox createHeader(String text, String styleClass) {
        Label header = new Label(text);
        header.getStyleClass().add(styleClass);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        HBox container = new HBox(header);
        container.setAlignment(Pos.CENTER);
        return container;
    }
}
