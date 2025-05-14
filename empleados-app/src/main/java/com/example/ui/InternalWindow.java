package com.example.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

/**
 * Un InternalWindow que ahora:
 * - Ancla el container a sus cuatro lados.
 * - Permite que el body y el content crezcan con PRIORITY.ALWAYS.
 */
public class InternalWindow extends AnchorPane {
    private double dragOffsetX, dragOffsetY;

    public InternalWindow(String title, Node content) {
        // 1) Barra de título
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("window-title");
        HBox titleBar = new HBox(titleLabel);
        titleBar.getStyleClass().add("window-bar");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPrefHeight(30);

        // 2) Cuerpo
        VBox body = new VBox(content);
        body.getStyleClass().add("window-body");
        // permitir que body crezca dentro de container:
        VBox.setVgrow(body, Priority.ALWAYS);

        // 3) Si el content es un Region (tu AnchorPane FXML), permitirle crecer:
        if (content instanceof Region) {
            Region r = (Region) content;
            // para que no se “pegue” a su tamaño mínimo
            r.setMinSize(0, 0);
            VBox.setVgrow(r, Priority.ALWAYS);
        }

        // 4) Container que agrupa barra + cuerpo
        VBox container = new VBox(titleBar, body);

        // 5) Anclar el container a los 4 bordes de este AnchorPane:
        AnchorPane.setTopAnchor(container, 0.0);
        AnchorPane.setBottomAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setRightAnchor(container, 0.0);

        getChildren().add(container);

        // 6) Arrastar por la ventana
        titleBar.setOnMousePressed(this::onMousePressed);
        titleBar.setOnMouseDragged(this::onMouseDragged);
        titleBar.setCursor(Cursor.MOVE);

        // 5) Traer al frente al hacer click en cualquier parte de la ventana
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> this.toFront());

        // 7) Estilo de borde y fondo
        setStyle("-fx-border-color: gray; -fx-border-width:1; -fx-background-color: white;");
    }

    private void onMousePressed(MouseEvent ev) {
        dragOffsetX = ev.getSceneX() - getLayoutX();
        dragOffsetY = ev.getSceneY() - getLayoutY();
    }

    private void onMouseDragged(MouseEvent ev) {
        double nx = ev.getSceneX() - dragOffsetX;
        double ny = ev.getSceneY() - dragOffsetY;
        Region parent = (Region) getParent();
        nx = Math.max(0, Math.min(nx, parent.getWidth() - getWidth()));
        ny = Math.max(0, Math.min(ny, parent.getHeight() - getHeight()));
        relocate(nx, ny);
    }
}
