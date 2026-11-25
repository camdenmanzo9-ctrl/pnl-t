/*
 * Decompiled with CFR 0.153-local.
 */
package dev.profit.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.OptionalLong;

final class TypedPayValues {
    TypedPayValues() {
    }

    static OptionalLong money(String string) {
        long l;
        if (string == null) {
            return OptionalLong.empty();
        }
        String string2 = string.trim().replace("$", "").replace(",", "").replace(" ", "").toUpperCase(Locale.ROOT);
        if (!string2.matches("(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)[KMBT]?")) {
            return OptionalLong.empty();
        }
        l = string2.endsWith("K") ? 1000L : (string2.endsWith("M") ? 1000000L : (string2.endsWith("B") ? 1000000000L : (string2.endsWith("T") ? 1000000000000L : 1L)));
        if (l != 1L) {
            string2 = string2.substring(0, string2.length() - 1);
        }
        try {
            long l3 = new BigDecimal(string2).multiply(BigDecimal.valueOf(l)).setScale(0, RoundingMode.HALF_UP).longValueExact();
            return l3 >= 1L ? OptionalLong.of(l3) : OptionalLong.empty();
        } catch (ArithmeticException | NumberFormatException runtimeException) {
            return OptionalLong.empty();
        }
    }

    static OptionalDouble decimal(String string, double d) {
        if (string == null) {
            return OptionalDouble.empty();
        }
        String string2 = string.trim().replaceAll("[%sS]$", "").trim();
        if (!string2.matches("(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)")) {
            return OptionalDouble.empty();
        }
        try {
            double d2 = Double.parseDouble(string2);
            return Double.isFinite(d2) && d2 >= 0.0 && d2 <= d ? OptionalDouble.of(d2) : OptionalDouble.empty();
        } catch (NumberFormatException numberFormatException) {
            return OptionalDouble.empty();
        }
    }
}

