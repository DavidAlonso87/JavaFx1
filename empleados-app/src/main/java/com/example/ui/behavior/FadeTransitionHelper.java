package com.example.ui.behavior;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Helper estático para realizar transiciones de fundido (fade) en nodos.
 */
public class FadeTransitionHelper {

    /**
     * Aplica un fade in de 0→1 en el nodo.
     *
     * @param node   nodo a animar
     * @param millis duración en milisegundos
     */
    public static void fadeIn(Node node, double millis) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(millis), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    /**
     * Aplica un fade out de 1→0 en el nodo.
     *
     * @param node   nodo a animar
     * @param millis duración en milisegundos
     */
    public static void fadeOut(Node node, double millis) {
        node.setOpacity(1);
        FadeTransition ft = new FadeTransition(Duration.millis(millis), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }
}
