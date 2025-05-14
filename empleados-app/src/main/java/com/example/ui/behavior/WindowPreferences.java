package com.example.ui.behavior;

import java.util.prefs.Preferences;
import javafx.scene.layout.Region;

/**
 * Guarda y restaura posición, tamaño y estado de maximizado
 * de ventanas (Regions) usando java.util.prefs.Preferences.
 */
public class WindowPreferences {
    private static final Preferences prefs = Preferences.userNodeForPackage(WindowPreferences.class);

    /**
     * Clave base para un identificador de ventana (por ejemplo "empleados").
     */
    private final String key;

    public WindowPreferences(String key) {
        this.key = key;
    }

    public void save(Region node, boolean maximized) {
        prefs.putDouble(key + ".x", node.getLayoutX());
        prefs.putDouble(key + ".y", node.getLayoutY());
        prefs.putDouble(key + ".w", node.getWidth());
        prefs.putDouble(key + ".h", node.getHeight());
        prefs.putBoolean(key + ".max", maximized);
    }

    public void apply(Region node, Runnable maximizeCallback) {
        double x = prefs.getDouble(key + ".x", Double.NaN);
        if (!Double.isNaN(x)) {
            double y = prefs.getDouble(key + ".y", node.getLayoutY());
            double w = prefs.getDouble(key + ".w", node.getPrefWidth());
            double h = prefs.getDouble(key + ".h", node.getPrefHeight());
            boolean max = prefs.getBoolean(key + ".max", false);

            node.setLayoutX(x);
            node.setLayoutY(y);
            node.setPrefSize(w, h);
            if (max)
                maximizeCallback.run();
        }
    }
}
