package com.snowy.balancehud;

import java.util.ArrayDeque;

/** Display-only animation; the real balance is saved immediately. */
final class BalanceCounter {
    static final long DURATION_NS = 650_000_000L;
    private final ArrayDeque<Double> queued = new ArrayDeque<>();
    private double shown = -1, from, target;
    private long started;
    private boolean active;

    void snap(double amount) {
        queued.clear();
        shown = from = target = amount;
        active = false;
    }

    void payment(double before, double after, long now) {
        if (shown < 0) snap(before);
        value(now);
        if (active) queued.addLast(after);
        else begin(after, now);
    }

    private void begin(double amount, long now) {
        from = shown;
        target = amount;
        started = now;
        active = true;
    }

    double value(long now) {
        while (active && now - started >= DURATION_NS) {
            shown = target;
            long nextStart = started + DURATION_NS;
            if (queued.isEmpty()) active = false;
            else begin(queued.removeFirst(), nextStart);
        }
        if (active) {
            double t = Math.max(0, (now - started) / (double) DURATION_NS);
            double ease = t * t * (3 - 2 * t);
            shown = from + (target - from) * ease;
        }
        return shown;
    }
}
