/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.fabricmc.loader.api.FabricLoader
 */
package dev.profit.dashboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;

public final class PaymentLedger {
    private static final long KEEP_MS = 3024000000L;
    private static final Gson GSON = new GsonBuilder().create();   // compact: smaller file, faster to write
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("bettors-dashboard.json");
    private static Data data = new Data();
    private static long lastSave = 0L;

    private PaymentLedger() {
    }

    public static void load() {
        try {
            Data d;
            if (Files.exists(FILE, new LinkOption[0]) && (d = (Data)GSON.fromJson(Files.readString(FILE), Data.class)) != null) {
                data = d;
            }
        } catch (Exception e) {
            System.err.println("[Bettors] Could not read " + String.valueOf(FILE) + ": " + String.valueOf(e));
        }
        if (PaymentLedger.data.allTime == null) {
            PaymentLedger.data.allTime = new LinkedHashMap<String, Entry>();
        }
        if (PaymentLedger.data.session == null) {
            PaymentLedger.data.session = new LinkedHashMap<String, Entry>();
        }
        if (PaymentLedger.data.history == null) {
            PaymentLedger.data.history = new ArrayList<SessionRecord>();
        }
        if (PaymentLedger.data.log == null) {
            PaymentLedger.data.log = new ArrayList<Payment>();
        }
        if (PaymentLedger.data.balHist == null) {
            PaymentLedger.data.balHist = new ArrayList<long[]>();
        }
        if (PaymentLedger.data.playByDay == null) {
            PaymentLedger.data.playByDay = new LinkedHashMap<String, Long>();
        }
        if (PaymentLedger.data.sessionStart <= 0L) {
            PaymentLedger.data.sessionStart = System.currentTimeMillis();
        }
    }

    // Busy betting: write the file at most once every 3 seconds instead of after every payment.
    private static boolean dirty = false;
    private static long dirtySince = 0L;

    public static void saveSoon() {
        if (!dirty) {
            dirty = true;
            dirtySince = System.currentTimeMillis();
        }
    }

    /** Called every tick. */
    public static void tickSave() {
        if (dirty && System.currentTimeMillis() - dirtySince >= 3000L) PaymentLedger.save();
    }

    /** Called when you leave a server or close the game, so nothing is lost. */
    public static void flush() {
        if (dirty) PaymentLedger.save();
    }

    public static void save() {
        dirty = false;
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            Files.writeString(FILE, (CharSequence)GSON.toJson((Object)data), new OpenOption[0]);
        } catch (Exception e) {
            System.err.println("[Bettors] Could not save " + String.valueOf(FILE) + ": " + String.valueOf(e));
        }
    }

    public static void record(String player, long paidYou, long youPaid) {
        PaymentLedger.add(PaymentLedger.data.allTime, player, paidYou, youPaid, true);
        if (PaymentLedger.data.sessionActive) {
            PaymentLedger.add(PaymentLedger.data.session, player, paidYou, youPaid, true);
        }
        Payment pay = new Payment();
        pay.name = player;
        pay.paidYou = paidYou;
        pay.youPaid = youPaid;
        pay.ts = System.currentTimeMillis();
        PaymentLedger.data.log.add(pay);
        long cutoff = pay.ts - 3024000000L;
        PaymentLedger.data.log.removeIf(x -> x.ts < cutoff);
        while (PaymentLedger.data.log.size() > 20000) {
            PaymentLedger.data.log.remove(0);
        }
        PaymentLedger.saveSoon();
    }

    /** Payments from the last 5 weeks (read only). */
    public static java.util.List<Payment> recentLog() {
        return java.util.Collections.unmodifiableList(PaymentLedger.data.log);
    }

    public static long balance() {
        return PaymentLedger.data.balance;
    }

    public static void setBalance(long v) {
        PaymentLedger.data.balance = v;
        PaymentLedger.data.balHist.add(new long[]{System.currentTimeMillis(), v});
        while (PaymentLedger.data.balHist.size() > 2000) {
            PaymentLedger.data.balHist.remove(0);
        }
        PaymentLedger.saveSoon();
    }

    public static void adjustBalance(long delta) {
        if (PaymentLedger.data.balance >= 0L) {
            PaymentLedger.setBalance(PaymentLedger.data.balance + delta);
        }
    }

    public static List<long[]> balanceSince(long since) {
        ArrayList<long[]> out = new ArrayList<long[]>();
        for (long[] b : PaymentLedger.data.balHist) {
            if (b[0] < since) continue;
            out.add(b);
        }
        return out;
    }

    public static void addPlaytime(long ms) {
        String day = LocalDate.now().toString();
        PaymentLedger.data.playByDay.merge(day, ms, Long::sum);
        PaymentLedger.data.playTotal += ms;
        long now = System.currentTimeMillis();
        if (now - lastSave > 30000L) {
            lastSave = now;
            PaymentLedger.save();
        }
    }

    public static long playTotal() {
        return PaymentLedger.data.playTotal;
    }

    public static long[] playLastDays(int n) {
        long[] out = new long[n];
        LocalDate today = LocalDate.now();
        for (int i = 0; i < n; ++i) {
            out[n - 1 - i] = PaymentLedger.data.playByDay.getOrDefault(today.minusDays(i).toString(), 0L);
        }
        return out;
    }

    public static List<Payment> paymentsSince(long since) {
        ArrayList<Payment> out = new ArrayList<Payment>();
        for (Payment x : PaymentLedger.data.log) {
            if (x.ts < since) continue;
            out.add(x);
        }
        return out;
    }

    public static List<Payment> paymentsFor(String player, long since) {
        ArrayList<Payment> out = new ArrayList<Payment>();
        for (int i = PaymentLedger.data.log.size() - 1; i >= 0; --i) {
            Payment x = PaymentLedger.data.log.get(i);
            if (x.ts < since || !x.name.equalsIgnoreCase(player)) continue;
            out.add(x);
        }
        return out;
    }

    public static Entry allTimeFor(String player) {
        return PaymentLedger.data.allTime.get(player.toLowerCase(Locale.ROOT));
    }

    public static void importAllTime(String player, long paidYou, long youPaid) {
        PaymentLedger.add(PaymentLedger.data.allTime, player, paidYou, youPaid, false);
        PaymentLedger.save();
    }

    private static void add(Map<String, Entry> map, String player, long paidYou, long youPaid, boolean isPayment) {
        Entry e = map.computeIfAbsent(player.toLowerCase(Locale.ROOT), k -> new Entry());
        e.name = player;
        e.paidYou += paidYou;
        e.youPaid += youPaid;
        if (isPayment) {
            ++e.payments;
        }
    }

    public static List<Entry> entries(boolean session) {
        return new ArrayList<Entry>((session ? PaymentLedger.data.session : PaymentLedger.data.allTime).values());
    }

    public static boolean isSessionActive() {
        return PaymentLedger.data.sessionActive;
    }

    public static long sessionStartTime() {
        return PaymentLedger.data.sessionStart;
    }

    public static boolean isChristmas() {
        return "christmas".equals(PaymentLedger.data.theme);
    }

    public static void setChristmas(boolean on) {
        PaymentLedger.data.theme = on ? "christmas" : "classic";
        PaymentLedger.save();
    }

    public static List<SessionRecord> history() {
        return new ArrayList<SessionRecord>(PaymentLedger.data.history);
    }

    public static void deleteSession(int index) {
        if (index >= 0 && index < PaymentLedger.data.history.size()) {
            PaymentLedger.data.history.remove(index);
            PaymentLedger.save();
        }
    }

    public static void clearHistory() {
        PaymentLedger.data.history.clear();
        PaymentLedger.data.lastSessionLength = -1L;
        PaymentLedger.data.lastSessionProfit = 0L;
        PaymentLedger.save();
    }

    public static long sessionMillis() {
        return (PaymentLedger.data.sessionActive ? System.currentTimeMillis() : PaymentLedger.data.sessionEnd) - PaymentLedger.data.sessionStart;
    }

    public static long lastSessionLength() {
        return PaymentLedger.data.lastSessionLength;
    }

    public static long lastSessionProfit() {
        return PaymentLedger.data.lastSessionProfit;
    }

    public static SessionSummary endSession() {
        long in = 0L;
        long out = 0L;
        for (Entry e : PaymentLedger.data.session.values()) {
            in += e.paidYou;
            out += e.youPaid;
        }
        PaymentLedger.data.sessionEnd = System.currentTimeMillis();
        PaymentLedger.data.sessionActive = false;
        SessionSummary s = new SessionSummary(PaymentLedger.sessionMillis(), in, out, PaymentLedger.data.session.size());
        PaymentLedger.data.lastSessionLength = s.lengthMs();
        PaymentLedger.data.lastSessionProfit = s.profit();
        SessionRecord r = new SessionRecord();
        r.start = PaymentLedger.data.sessionStart;
        r.end = PaymentLedger.data.sessionEnd;
        r.received = in;
        r.sent = out;
        r.players = s.players();
        int count = 0;
        for (Entry e : PaymentLedger.data.session.values()) {
            count += e.payments;
        }
        r.payments = count;
        PaymentLedger.data.history.add(0, r);
        while (PaymentLedger.data.history.size() > 100) {
            PaymentLedger.data.history.remove(PaymentLedger.data.history.size() - 1);
        }
        PaymentLedger.save();
        return s;
    }

    public static void startSession() {
        PaymentLedger.data.session = new LinkedHashMap<String, Entry>();
        PaymentLedger.data.sessionStart = System.currentTimeMillis();
        PaymentLedger.data.sessionEnd = 0L;
        PaymentLedger.data.sessionActive = true;
        PaymentLedger.save();
    }

    public static String formatDuration(long ms) {
        long sec = Math.max(0L, ms / 1000L);
        long h = sec / 3600L;
        long m = sec % 3600L / 60L;
        long s = sec % 60L;
        return h > 0L ? String.format("%dh %02dm %02ds", h, m, s) : String.format("%dm %02ds", m, s);
    }

    public static String formatDurationShort(long ms) {
        long min = Math.max(0L, ms / 60000L);
        return min >= 60L ? min / 60L + "h " + min % 60L + "m" : min + "m";
    }

    private static final class Data {
        Map<String, Entry> allTime = new LinkedHashMap<String, Entry>();
        Map<String, Entry> session = new LinkedHashMap<String, Entry>();
        long sessionStart = System.currentTimeMillis();
        long sessionEnd = 0L;
        boolean sessionActive = true;
        long lastSessionLength = -1L;
        long lastSessionProfit = 0L;
        List<SessionRecord> history = new ArrayList<SessionRecord>();
        String theme = "classic";
        List<Payment> log = new ArrayList<Payment>();
        long balance = -1L;
        List<long[]> balHist = new ArrayList<long[]>();
        Map<String, Long> playByDay = new LinkedHashMap<String, Long>();
        long playTotal = 0L;

        private Data() {
        }
    }

    public static final class Payment {
        public String name;
        public long paidYou;
        public long youPaid;
        public long ts;
    }

    public static final class Entry {
        public String name;
        public long paidYou;
        public long youPaid;
        public int payments;

        public long net() {
            return this.youPaid - this.paidYou;
        }

        public long volume() {
            return this.paidYou + this.youPaid;
        }
    }

    public record SessionSummary(long lengthMs, long received, long sent, int players) {
        public long profit() {
            return this.received - this.sent;
        }
    }

    public static final class SessionRecord {
        public long start;
        public long end;
        public long received;
        public long sent;
        public int players;
        public int payments;

        public long profit() {
            return this.received - this.sent;
        }

        public long length() {
            return this.end - this.start;
        }
    }
}

