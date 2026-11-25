/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Alerts;
import dev.profit.dashboard.AuctionActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;

public final class Auction {
    public static boolean switchOn = true;
    public static boolean running = false;
    public static long endAt = 0L;
    public static long durationMs = 15000L;
    public static ItemStack item = ItemStack.EMPTY;
    public static String itemName = "";
    public static final Map<String, Long> bids = new LinkedHashMap<String, Long>();
    public static String winner = null;
    public static long winAmount = 0L;
    public static boolean ended = false;
    public static int color = -7643914;
    public static long endedAt = 0L;
    public static String customName = "";
    public static int quantity = 1;
    public static final List<Past> history = new ArrayList<Past>();

    private Auction() {
    }

    public static String displayName() {
        String string = customName == null || customName.isBlank() ? itemName : customName;
        return quantity > 1 ? quantity + "x " + string : string;
    }

    public static String shortName() {
        return customName == null || customName.isBlank() ? itemName : customName;
    }

    public static boolean capturing() {
        return running && switchOn;
    }

    public static void start() {
        if (item.isEmpty()) {
            return;
        }
        bids.clear();
        AuctionActivity.reset();
        winner = null;
        winAmount = 0L;
        ended = false;
        running = true;
        endAt = System.currentTimeMillis() + durationMs;
    }

    public static void clear() {
        running = false;
        ended = false;
        bids.clear();
        winner = null;
        winAmount = 0L;
    }

    public static void clearItem() {
        running = false;
        ended = false;
        item = ItemStack.EMPTY;
        itemName = "";
        customName = "";
        quantity = 1;
        bids.clear();
        winner = null;
        winAmount = 0L;
    }

    public static void bid(String string, long l) {
        String string2 = string;
        for (String string3 : bids.keySet()) {
            if (!string3.equalsIgnoreCase(string)) continue;
            string2 = string3;
            break;
        }
        bids.merge(string2, l, Math::max);
        AuctionActivity.bid(string, l);
    }

    public static long left() {
        return Math.max(0L, endAt - System.currentTimeMillis());
    }

    public static void tick() {
        if (!running || System.currentTimeMillis() < endAt) {
            return;
        }
        running = false;
        ended = true;
        winner = null;
        winAmount = 0L;
        for (Map.Entry<String, Long> entry : bids.entrySet()) {
            if (entry.getValue() <= winAmount) continue;
            winner = entry.getKey();
            winAmount = entry.getValue();
        }
        endedAt = System.currentTimeMillis();
        history.add(0, new Past(Auction.shortName(), quantity, winner, winAmount));
        while (history.size() > 30) {
            history.remove(history.size() - 1);
        }
        if (winner != null) {
            try {
                Alerts.playSound(2, 1);
            } catch (Exception exception) {}
        }
    }

    public static List<Map.Entry<String, Long>> sortedBids() {
        ArrayList<Map.Entry<String, Long>> arrayList = new ArrayList<Map.Entry<String, Long>>(bids.entrySet());
        arrayList.sort((entry, entry2) -> Long.compare((Long)entry2.getValue(), (Long)entry.getValue()));
        return arrayList;
    }

    public record Past(String name, int qty, String winner, long amount) {
    }
}

