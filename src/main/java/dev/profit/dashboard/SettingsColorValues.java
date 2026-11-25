/*
 * Decompiled with CFR 0.153-local.
 */
package dev.profit.dashboard;

import java.util.OptionalInt;

final class SettingsColorValues {
    private SettingsColorValues() {
    }

    static OptionalInt parse(String string) {
        if (string == null) {
            return OptionalInt.empty();
        }
        String string2 = string.trim();
        if (string2.startsWith("#")) {
            string2 = string2.substring(1);
        }
        if (!string2.matches("[0-9a-fA-F]{6}")) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(Integer.parseInt(string2, 16));
    }
}

