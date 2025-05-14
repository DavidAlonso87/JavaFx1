package com.example.ui.behavior;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Helper estático para animaciones comunes:
 * - fadeIn/fadeOut
 * - slideIn/slideOut
 */
public class TransitionHelper {

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
     * Desplaza el nodo en el eje X desde fromX hasta 0.
     *
     * @param node   nodo a animar
     * @param fromX  posición inicial en X (relativa)
     * @param millis duración en milisegundos
     */
    public static void slideInX(Node node, double fromX, double millis) {
        node.setTranslateX(fromX);
        TranslateTransition tt = new TranslateTransition(Duration.millis(millis), node);
        tt.setToX(0);
        tt.play();
    }

    /**
     * Combina un fadeIn y un slideInX simultáneos.
     */
    public static void reveal(Node node, double fromX, double millis) {
        fadeIn(node, millis);
        slideInX(node, fromX, millis);
    }

    /**
     * Desplaza el nodo desde fuera de la pantalla (fromX) hasta su posición de
     * layout (toX)
     * usando translateX, evitando que el layout manager “pise” la animación.
     *
     * @param node   nodo a animar (Region para poder usar layoutX)
     * @param fromX  posición X absoluta de partida (por ejemplo: -anchoVentana)
     * @param toX    posición X absoluta final (layoutX deseado)
     * @param millis duración en milisegundos
     */
    public static void slideInFromLeft(Region node, double fromX, double toX, double millis) {
        // 1) Sitúa el nodo en su posición de layout final
        node.setLayoutX(toX);
        // 2) Traslada visualmente hacia la izquierda (o donde toque)
        node.setTranslateX(fromX - toX);
        // 3) Inicialmente transparente
        node.setOpacity(0);

        // 4) Timeline que lleva translateX → 0 y opacity → 1
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.translateXProperty(), fromX - toX),
                        new KeyValue(node.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(millis),
                        new KeyValue(node.translateXProperty(), 0),
                        new KeyValue(node.opacityProperty(), 1)));
        tl.play();
    }

    /**
     * Método legacy para compatibilidad: redirige a slideInFromLeft.
     */
    @Deprecated
    public static void slideInLayoutX(Region node, double fromX, double toX, double millis) {
        slideInFromLeft(node, fromX, toX, millis);
    }
}
