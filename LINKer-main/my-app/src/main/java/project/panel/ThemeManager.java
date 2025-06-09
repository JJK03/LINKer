package project.panel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static Color backgroundColor = new Color(0, 0, 0);  // 기본 검정색
    private static final List<ThemeChangeListener> listeners = new ArrayList<>();

    public static void setBackgroundColor(Color color) {
        backgroundColor = color;
        notifyListeners();
    }

    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    public static void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    public static void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChanged(backgroundColor);
        }
    }

    public interface ThemeChangeListener {
        void onThemeChanged(Color newColor);
    }
}