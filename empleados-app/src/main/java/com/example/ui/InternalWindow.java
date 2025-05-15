package com.example.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * InternalWindow: ventana flotante con barra de título,
 * soporte de arrastre, minimizar, maximizar y cerrar.
 */
public class InternalWindow extends AnchorPane {
    private double dragOffsetX, dragOffsetY;
    private boolean maximized = false;
    private double prevX, prevY, prevW, prevH;

    public InternalWindow(String title, Node content) {
        // Barra de título
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("window-title");
        HBox titleBar = new HBox(5, titleLabel);
        titleBar.getStyleClass().add("window-bar");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPrefHeight(30);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        // Botones de ventana
        Button btnMin = new Button("—");
        Button btnMax = new Button("?");
        Button btnClose = new Button("?");
        btnMin.getStyleClass().add("window-button");
        btnMax.getStyleClass().add("window-button");
        btnClose.getStyleClass().add("window-button");
        titleBar.getChildren().addAll(btnMin, btnMax, btnClose);

        // Contenido de la ventana
        VBox body = new VBox(content);
        body.getStyleClass().add("window-body");
        VBox.setVgrow(content, Priority.ALWAYS);

        // Container completo (barra + contenido)
        VBox container = new VBox(titleBar, body);
        AnchorPane.setTopAnchor(container, 0.0);
        AnchorPane.setBottomAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setRightAnchor(container, 0.0);
        getChildren().add(container);

        // Traer al frente al hacer click
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> toFront());

        // Arrastre por la barra de título
        titleBar.setOnMousePressed(this::onMousePressed);
        titleBar.setOnMouseDragged(this::onMouseDragged);
        titleBar.setCursor(Cursor.MOVE);

        // Minimizar: ocultar ventana
        btnMin.setOnAction(e -> setVisible(false));

        // Maximizar/restaurar
        btnMax.setOnAction(e -> {
            Region parent = (Region) getParent();
            if (!maximized) {
                prevX = getLayoutX();
                prevY = getLayoutY();
                prevW = getWidth();
                prevH = getHeight();
                setLayoutX(0);
                setLayoutY(0);
                setPrefSize(parent.getWidth(), parent.getHeight());
                maximized = true;
                btnMax.setText("?");
            } else {
                setLayoutX(prevX);
                setLayoutY(prevY);
                setPrefSize(prevW, prevH);
                maximized = false;
                btnMax.setText("?");
            }
        });

        btnClose.setOnAction(e -> {
            Node p = getParent();
            if (p instanceof Pane) {
                ((Pane) p).getChildren().remove(this);
            }
        });
        // Estilo de borde y fondo
        setStyle(
                "-fx-border-color: gray; " +
                        "-fx-border-width:1; " +
                        "-fx-background-color: white;");
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
