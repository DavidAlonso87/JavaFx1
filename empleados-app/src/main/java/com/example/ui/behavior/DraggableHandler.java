package com.example.ui.behavior;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Permite hacer cualquier nodo arrastrable
 * a partir de una zona de “dragArea”.
 */
public class DraggableHandler {
    private double offsetX, offsetY;

    private DraggableHandler(Node dragArea, Node target) {
        dragArea.setOnMousePressed(this::initDrag);
        dragArea.setOnMouseDragged(e -> doDrag(e, target));
        dragArea.setOnMouseReleased(e -> dragArea.getScene().setCursor(Cursor.DEFAULT));
    }

    private void initDrag(MouseEvent e) {
        offsetX = e.getSceneX() - ((Node) e.getSource()).getLayoutX();
        offsetY = e.getSceneY() - ((Node) e.getSource()).getLayoutY();
        ((Node) e.getSource()).getScene().setCursor(Cursor.MOVE);
    }

    private void doDrag(MouseEvent e, Node target) {
        target.setLayoutX(e.getSceneX() - offsetX);
        target.setLayoutY(e.getSceneY() - offsetY);
    }

    /**
     * Punto de entrada: llama a
     * new DraggableHandler(dragArea, target);
     */
    public static void makeDraggable(Node dragArea, Node target) {
        new DraggableHandler(dragArea, target);
    }
}
