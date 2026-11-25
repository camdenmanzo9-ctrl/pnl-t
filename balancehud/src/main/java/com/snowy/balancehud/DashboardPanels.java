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

    static boolean enabled(int panel) {
        if (SWITCHES == null) return true;
        try {
            return SWITCHES[panel].getBoolean(null);
        } catch (IllegalAccessException | LinkageError e) {
            return true;
        }
    }
}
