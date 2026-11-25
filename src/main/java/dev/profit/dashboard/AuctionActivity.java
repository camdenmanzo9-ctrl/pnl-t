/*
 * Decompiled with CFR 0.153-local.
 */
package dev.profit.dashboard;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class AuctionActivity {
    private static final Deque<Bid> recent = new ArrayDeque<Bid>();
    private static long total;

    AuctionActivity() {
    }

    static void reset() {
        recent.clear();
        total = 0L;
    }

    static void bid(String string, long l) {
        if (l <= 0L) {
            return;
        }
        total = l > Long.MAX_VALUE - total ? Long.MAX_VALUE : total + l;
        recent.addFirst(new Bid(string, l));
        while (recent.size() > 2) {
            recent.removeLast();
        }
    }

    static long total() {
        return total;
    }

    static List<Bid> recent() {
        return new ArrayList<Bid>(recent);
    }

    record Bid(String name, long amount) {
    }
}

