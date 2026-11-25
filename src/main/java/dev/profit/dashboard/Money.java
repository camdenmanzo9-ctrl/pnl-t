/*
 * Decompiled with CFR 0.153-local.
 */
package dev.profit.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {
    private Money() {
    }

    public static long parse(String raw) {
        long mult;
        String s = raw.replace("$", "").replace(",", "").replace(" ", "").trim();
        if (s.isEmpty()) {
            throw new NumberFormatException("empty amount");
        }
        mult = switch (Character.toUpperCase(s.charAt(s.length() - 1))) {
            case 'K' -> 1000L;
            case 'M' -> 1000000L;
            case 'B' -> 1000000000L;
            case 'T' -> 1000000000000L;
            default -> 1L;
        };
        if (mult != 1L) {
            s = s.substring(0, s.length() - 1);
        }
        return new BigDecimal(s).multiply(BigDecimal.valueOf(mult)).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    public static String format(long v) {
        long a = Math.abs(v);
        Object body = a >= 1000000000000L ? Money.scaled(a, 1000000000000L) + "T" : (a >= 1000000000L ? Money.scaled(a, 1000000000L) + "B" : (a >= 1000000L ? Money.scaled(a, 1000000L) + "M" : (a >= 1000L ? Money.scaled(a, 1000L) + "K" : Long.toString(a))));
        return (v < 0L ? "-$" : "$") + (String)body;
    }

    private static String scaled(long a, long unit) {
        return BigDecimal.valueOf(a).divide(BigDecimal.valueOf(unit), 2, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
    }
}

