package com.example.ui.behavior;

import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * Permite redimensionar un Region arrastrando sus bordes o esquinas,
 * usando coordenadas del "scene" para calcular desplazamientos reales.
 */
public class ResizableHandler {
    private static final double RESIZE_MARGIN = 6;

    private final Node node;
    private final Region region;

    // Variables para estado previo a la operación
    private double clickX, clickY;
    private double nodeX, nodeY;
    private double nodeWidth, nodeHeight;

    private ResizableHandler(Node node) {
        this.node = node;
        this.region = (Region) node;
        node.setOnMouseMoved(this::onMouseMoved);
        node.setOnMousePressed(this::onMousePressed);
        node.setOnMouseDragged(this::onMouseDragged);
    }

    private void onMouseMoved(MouseEvent e) {
        Bounds bounds = node.getBoundsInLocal();
        double x = e.getX(), y = e.getY();
        boolean left = x < RESIZE_MARGIN;
        boolean right = x > bounds.getWidth() - RESIZE_MARGIN;
        boolean top = y < RESIZE_MARGIN;
        boolean bottom = y > bounds.getHeight() - RESIZE_MARGIN;

        Cursor cursor = Cursor.DEFAULT;
        if (left && top)
            cursor = Cursor.NW_RESIZE;
        else if (left && bottom)
            cursor = Cursor.SW_RESIZE;
        else if (right && top)
            cursor = Cursor.NE_RESIZE;
        else if (right && bottom)
            cursor = Cursor.SE_RESIZE;
        else if (left)
            cursor = Cursor.W_RESIZE;
        else if (right)
            cursor = Cursor.E_RESIZE;
        else if (top)
            cursor = Cursor.N_RESIZE;
        else if (bottom)
            cursor = Cursor.S_RESIZE;

        node.setCursor(cursor);
    }

    private void onMousePressed(MouseEvent e) {
        // Coordenadas globales al iniciar drag
        clickX = e.getSceneX();
        clickY = e.getSceneY();
        // Estado previo del nodo
        nodeX = node.getLayoutX();
        nodeY = node.getLayoutY();
        nodeWidth = region.getWidth();
        nodeHeight = region.getHeight();
    }

    private void onMouseDragged(MouseEvent e) {
        double dx = e.getSceneX() - clickX;
        double dy = e.getSceneY() - clickY;
        Cursor cursor = node.getCursor();

        // Este borde: ancho → width + dx
        if (cursor == Cursor.E_RESIZE || cursor == Cursor.SE_RESIZE || cursor == Cursor.NE_RESIZE) {
            region.setPrefWidth(Math.max(region.getMinWidth(), nodeWidth + dx));
        }
        // Inferior: alto → height + dy
        if (cursor == Cursor.S_RESIZE || cursor == Cursor.SE_RESIZE || cursor == Cursor.SW_RESIZE) {
            region.setPrefHeight(Math.max(region.getMinHeight(), nodeHeight + dy));
        }
        // Oeste: ancho → width - dx, mover X → nodeX + dx
        if (cursor == Cursor.W_RESIZE || cursor == Cursor.SW_RESIZE || cursor == Cursor.NW_RESIZE) {
            double newWidth = Math.max(region.getMinWidth(), nodeWidth - dx);
            region.setPrefWidth(newWidth);
            node.setLayoutX(nodeX + dx);
        }
        // Norte: alto → height - dy, mover Y → nodeY + dy
        if (cursor == Cursor.N_RESIZE || cursor == Cursor.NE_RESIZE || cursor == Cursor.NW_RESIZE) {
            double newHeight = Math.max(region.getMinHeight(), nodeHeight - dy);
            region.setPrefHeight(newHeight);
            node.setLayoutY(nodeY + dy);
        }
    }

    /** Punto de entrada estático */
    public static void makeResizable(Node node) {
        new ResizableHandler(node);
    }
}
