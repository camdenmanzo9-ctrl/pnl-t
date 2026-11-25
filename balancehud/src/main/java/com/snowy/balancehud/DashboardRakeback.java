package com.snowy.balancehud;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** Supplies display rows only; never modifies the dashboard's lifetime ledger. */
public final class DashboardRakeback {
    private DashboardRakeback() {}

    public static List<?> entries() {
        try {
            Class<?> type = Class.forName("dev.profit.dashboard.PaymentLedger$Entry");
            Constructor<?> constructor = type.getConstructor();
            Field name = type.getField("name"), paidYou = type.getField("paidYou");
            List<Object> rows = new ArrayList<>();
            for (var e : BalanceHudClient.NETS.entrySet()) {
                double loss = -e.getValue() - BalanceHudClient.RAKE_BASE.getOrDefault(e.getKey(), 0.0);
                if (e.getValue() >= 0 || loss <= 0) continue;
                Object row = constructor.newInstance();
                name.set(row, e.getKey());
                paidYou.setLong(row, Math.round(loss));
                rows.add(row);
            }
            return rows;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to read dashboard rakeback display rows", e);
        }
    }
}
