package com.snowy.balancehud;

import java.lang.reflect.Field;

/** Reads the existing dashboard switches without changing dashboard state. */
final class DashboardPanels {
    private static final Field[] SWITCHES = findSwitches();

    private static Field[] findSwitches() {
        try {
            Class<?> panels = Class.forName("dev.profit.dashboard.Panels");
            return new Field[] { panels.getField("balance"), panels.getField("profit"),
                    panels.getField("rakeback") };
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }

    // TEST: outline color picked in the dashboard's Streamer tab (0 = use default)
    private static final java.lang.reflect.Method BORDER = findBorder();

    private static java.lang.reflect.Method findBorder() {
        try {
            return Class.forName("dev.profit.dashboard.Panels").getMethod("balBorderColor");
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }

    static int balanceBorder() {
        if (BORDER == null) return 0;
        try {
            return (int) BORDER.invoke(null);
        } catch (ReflectiveOperationException | LinkageError e) {
            return 0;
        }
    }

    static boolean enabled(int panel) {
        if (SWITCHES == null) return true;
        try {
            return SWITCHES[panel].getBoolean(null);
        } catch (IllegalAccessException | LinkageError e) {
            return true;
        }
    }
}
