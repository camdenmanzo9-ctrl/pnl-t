/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Money;
import dev.profit.dashboard.PaymentLedger;
import dev.profit.dashboard.SettingsScreen;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongToIntFunction;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DashboardScreen
extends Screen {
    private static Tab tab = Tab.DASHBOARD;
    private static boolean sessionView = false;
    private static final int SIDE = 118;
    private static int BG;
    private static int SIDE_BG;
    private static int PANEL;
    private static int STRIPE;
    private static int LINE;
    private static int HOVER;
    private static int SELECT;
    private static int TEXT;
    private static int MUTED;
    private static int GOLD;
    private static int RED;
    private static int GREEN;
    private static int DARK;
    private static boolean xmas;
    private int scroll = 0;
    private long confirmUntil = 0L;
    private Button endButton;
    private Button closeButton;
    private Button sessionScope;
    private Button allScope;
    private Button themeButton;
    private final Map<Button, Tab> tabButtons = new LinkedHashMap<Button, Tab>();
    private final Map<Button, Integer> deleteButtons = new LinkedHashMap<Button, Integer>();
    private Button clearAllButton;
    private static String detailPlayer;
    private static long detailRange;
    private final Map<Button, String> rowButtons = new LinkedHashMap<Button, String>();
    private final Map<Button, Long> rangeButtons = new LinkedHashMap<Button, Long>();
    private Button backButton;
    private static long graphRange;
    private final Map<Button, Long> graphButtons = new LinkedHashMap<Button, Long>();
    private int pendingDelete = -2;
    private long pendingUntil = 0L;
    private static final DateTimeFormatter WHEN;

    private static void applyTheme(boolean christmas) {
        xmas = christmas;
        if (christmas) {
            BG = -15984108;
            SIDE_BG = -16116463;
            PANEL = -15719397;
            STRIPE = -15455199;
            LINE = -14597582;
            HOVER = -15256794;
            SELECT = -14927316;
            TEXT = -724504;
            MUTED = -7362406;
            GOLD = -670398;
            RED = -1750963;
            GREEN = -11870592;
            DARK = -15984108;
        } else {
            BG = -16052719;
            SIDE_BG = -15789289;
            PANEL = -15525860;
            STRIPE = -15262430;
            LINE = -14472655;
            HOVER = -15065050;
            SELECT = -14801620;
            TEXT = -1513240;
            MUTED = -8419699;
            GOLD = -868018;
            RED = -1554598;
            GREEN = -12730758;
            DARK = -16052719;
        }
    }

    public DashboardScreen() {
        super((Component)Component.literal((String)"Bettors Dashboard"));
    }

    public boolean isPauseScreen() {
        return false;
    }

    protected void init() {
        DashboardScreen.applyTheme(PaymentLedger.isChristmas());
        this.tabButtons.clear();
        this.addTab("Dashboard", Tab.DASHBOARD, 0);
        this.addTab("Graph", Tab.GRAPH, 1);
        this.addTab("Players", Tab.PLAYERS, 2);
        this.addTab("Paid You", Tab.PAID_YOU, 3);
        this.addTab("Sessions", Tab.SESSIONS, 4);
        this.sessionScope = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"Session"), b -> {
            sessionView = true;
            this.scroll = 0;
            this.rebuildWidgets();
        }).bounds(this.width - 136, 6, 64, 18).build());
        this.allScope = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"All Time"), b -> {
            sessionView = false;
            this.scroll = 0;
            this.rebuildWidgets();
        }).bounds(this.width - 72, 6, 64, 18).build());
        this.themeButton = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"Settings"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen((Screen)new SettingsScreen(this));
            }
        }).bounds(6, 170, 106, 20).build());
        this.endButton = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)DashboardScreen.sessionLabel()), b -> this.onEndSession()).bounds(6, this.height - 52, 106, 20).build());
        this.closeButton = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"Close"), b -> this.closeFully()).bounds(6, this.height - 28, 106, 20).build());
        this.deleteButtons.clear();
        this.clearAllButton = null;
        this.rowButtons.clear();
        this.rangeButtons.clear();
        this.graphButtons.clear();
        this.backButton = null;
        if (tab == Tab.GRAPH) {
            this.addGraphButtons();
        }
        if (tab == Tab.SESSIONS) {
            this.addSessionButtons();
        }
        if (tab == Tab.DASHBOARD) {
            this.addDashboardRowButtons();
        }
        if (tab == Tab.PLAYERS) {
            if (detailPlayer == null) {
                this.addLedgerRowButtons();
            } else {
                this.addDetailButtons();
            }
        }
    }

    private void openPlayer(String name) {
        tab = Tab.PLAYERS;
        detailPlayer = name;
        this.scroll = 0;
        this.rebuildWidgets();
    }

    private void clickArea(String name, int x, int y, int w, int h) {
        Button b = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)name), btn -> this.openPlayer(name)).bounds(x, y, w, h).build());
        this.rowButtons.put(b, name);
    }

    private void addDashboardRowButtons() {
        List<PaymentLedger.Entry> list = PaymentLedger.entries(sessionView);
        int cx = 134;
        int cw = this.width - cx - 14;
        int by = 104;
        int bh = this.height - 14 - by;
        if (list.isEmpty() || bh < 40) {
            return;
        }
        int lw = cw * 58 / 100;
        int rx = cx + lw + 10;
        int rw = cx + cw - rx;
        ArrayList<PaymentLedger.Entry> s = new ArrayList<PaymentLedger.Entry>(list);
        s.sort(Comparator.comparingLong(PaymentLedger.Entry::net));
        int rowsTop = by + 26;
        int rowH = 14;
        int visible = Math.max(1, (by + bh - 6 - rowsTop) / rowH);
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, s.size() - visible)));
        for (int i = 0; i < visible && this.scroll + i < s.size(); ++i) {
            this.clickArea(((PaymentLedger.Entry)s.get((int)(this.scroll + i))).name, cx + 4, rowsTop + i * rowH - 3, lw - 8, 13);
        }
        int top = bh / 2 + 2;
        ArrayList<PaymentLedger.Entry> won = new ArrayList<PaymentLedger.Entry>();
        ArrayList<PaymentLedger.Entry> lost = new ArrayList<PaymentLedger.Entry>();
        for (PaymentLedger.Entry e : list) {
            if (e.net() < 0L) {
                won.add(e);
                continue;
            }
            if (e.net() <= 0L) continue;
            lost.add(e);
        }
        won.sort(Comparator.comparingLong(PaymentLedger.Entry::net));
        lost.sort(Comparator.comparingLong(PaymentLedger.Entry::net).reversed());
        this.sectionClicks(won, rx, by, rw, top - 5);
        this.sectionClicks(lost, rx, by + top + 5, rw, bh - top - 5);
    }

    private void sectionClicks(List<PaymentLedger.Entry> list, int x, int y, int w, int h) {
        int rows = Math.max(0, Math.min(5, (h - 28) / 12));
        for (int i = 0; i < Math.min(rows, list.size()); ++i) {
            this.clickArea(list.get((int)i).name, x + 4, y + 24 + i * 12 - 2, w - 8, 12);
        }
    }

    private boolean hot(int mx, int my, int x1, int y1, int x2, int y2) {
        return mx >= x1 && mx < x2 && my >= y1 && my < y2;
    }

    private void drawNetGraph(GuiGraphicsExtractor g, List<PaymentLedger.Payment> pays, int x, int y, int w, int h) {
        int n = pays.size() + 1;
        long[] vals = new long[n];
        long run = 0L;
        long min = 0L;
        long max = 0L;
        for (int i = 0; i < pays.size(); ++i) {
            vals[i + 1] = run += pays.get((int)i).paidYou - pays.get((int)i).youPaid;
            min = Math.min(min, run);
            max = Math.max(max, run);
        }
        if (n < 2) {
            g.text(this.font, "No payments in this range yet", x, y + h / 2 - 4, MUTED, false);
            return;
        }
        if (max == min) {
            max = min + 1L;
        }
        long pad = Math.max(1L, (max - min) / 10L);
        long fMax = max + pad;
        long fMin = min - pad;
        int gw = Math.max(10, w);
        int endRoom = 70;
        int lineW = Math.max(10, gw - endRoom);
        int zy = y + (int)Math.round((double)fMax / (double)(fMax - fMin) * (double)(h - 1));
        g.fill(x, zy, x + lineW, zy + 1, LINE);
        g.text(this.font, "$0", x + lineW + 6, zy - 4, MUTED, false);
        int last = -1;
        for (int px = 0; px < lineW; ++px) {
            double t = (double)px * (double)(n - 1) / (double)Math.max(1, lineW - 1);
            int i0 = (int)Math.floor(t);
            int i1 = Math.min(n - 1, i0 + 1);
            double v = (double)vals[i0] + (double)(vals[i1] - vals[i0]) * (t - (double)i0);
            int yy = y + (int)Math.round(((double)fMax - v) / (double)(fMax - fMin) * (double)(h - 1));
            int col = v >= 0.0 ? GREEN : RED;
            g.fill(x + px, Math.min(yy, zy), x + px + 1, Math.max(yy, zy), 0x14000000 | col & 0xFFFFFF);
            int from = last < 0 ? yy : last;
            g.fill(x + px, Math.min(yy, from), x + px + 1, Math.max(yy, from) + 1, col);
            last = yy;
        }
        long now = vals[n - 1];
        int ec = now >= 0L ? GREEN : RED;
        g.fill(x + lineW - 2, last - 1, x + lineW + 1, last + 2, ec);
        int ly = Math.max(y, Math.min(y + h - 9, last - 4));
        if (Math.abs(ly - (zy - 4)) < 10) {
            ly = last < zy ? Math.max(y, zy - 16) : Math.min(y + h - 9, zy + 8);
        }
        g.text(this.font, DashboardScreen.signed(now), x + lineW + 6, ly, ec, false);
    }

    private List<PaymentLedger.Payment> dashGraphData() {
        long since = sessionView ? PaymentLedger.sessionStartTime() : 0L;
        return PaymentLedger.paymentsSince(since);
    }

    private int dashGraphH() {
        return Math.max(56, (this.height - 14 - 104) * 34 / 100);
    }

    private void moreHint(GuiGraphicsExtractor g, int rightX, int y) {
        this.rightText(g, "scroll for more", rightX, y, MUTED);
    }

    private void backToList() {
        detailPlayer = null;
        this.scroll = 0;
        this.rebuildWidgets();
    }

    public void onClose() {
        if (tab == Tab.PLAYERS && detailPlayer != null) {
            this.backToList();
            return;
        }
        super.onClose();
    }

    private void closeFully() {
        detailPlayer = null;
        super.onClose();
    }

    private List<PaymentLedger.Entry> ledgerOrder() {
        ArrayList<PaymentLedger.Entry> s = new ArrayList<PaymentLedger.Entry>(PaymentLedger.entries(sessionView));
        s.sort(Comparator.comparingLong(PaymentLedger.Entry::volume).reversed());
        return s;
    }

    private void addLedgerRowButtons() {
        int cx = 134;
        int cw = this.width - cx - 14;
        int py = 44;
        int ph = this.height - 14 - py;
        int hy = py + 22;
        int rowsTop = hy + 13;
        int rowH = 12;
        int visible = Math.max(1, (py + ph - 16 - rowsTop) / rowH);
        List<PaymentLedger.Entry> s = this.ledgerOrder();
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, s.size() - visible)));
        for (int i = 0; i < visible && this.scroll + i < s.size(); ++i) {
            String name = s.get((int)(this.scroll + i)).name;
            Button b = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)name), btn -> {
                detailPlayer = name;
                this.scroll = 0;
                this.rebuildWidgets();
            }).bounds(cx + 4, rowsTop + i * rowH - 2, cw - 8, 12).build());
            this.rowButtons.put(b, name);
        }
    }

    private void addGraphButtons() {
        int cx = 134;
        long day = 86400000L;
        String[] labels = new String[]{"This session", "24 hours", "7 days", "30 days"};
        long[] ranges = new long[]{-1L, day, 7L * day, 30L * day};
        int bx = cx;
        for (int i = 0; i < 4; ++i) {
            long r = ranges[i];
            int w = this.font.width(labels[i]) + 16;
            Button b = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)labels[i]), btn -> {
                graphRange = r;
            }).bounds(bx, 44, w, 16).build());
            this.graphButtons.put(b, r);
            bx += w + 4;
        }
    }

    private void drawGraphTab(GuiGraphicsExtractor g, int cx, int cw, int mouseX, int mouseY) {
        for (Map.Entry<Button, Long> e : this.graphButtons.entrySet()) {
            Button b = e.getKey();
            boolean on = e.getValue() == graphRange;
            int x = b.getX();
            int y = b.getY();
            int w = b.getWidth();
            int h = b.getHeight();
            g.fill(x, y, x + w, y + h, on ? SELECT : (DashboardScreen.over(b, mouseX, mouseY) ? HOVER : PANEL));
            this.outline(g, x, y, w, h, on ? GOLD : LINE);
            String t = b.getMessage().getString();
            g.text(this.font, t, x + (w - this.font.width(t)) / 2, y + (h - 8) / 2, on ? GOLD : MUTED, false);
        }
        long since = graphRange < 0L ? PaymentLedger.sessionStartTime() : System.currentTimeMillis() - graphRange;
        List<PaymentLedger.Payment> pays = PaymentLedger.paymentsSince(since);
        long run = 0L;
        long high = 0L;
        long low = 0L;
        for (PaymentLedger.Payment p : pays) {
            high = Math.max(high, run += p.paidYou - p.youPaid);
            low = Math.min(low, run);
        }
        int y = 70;
        int gap = 10;
        int cardW = (cw - gap * 3) / 4;
        this.card(g, cx, y, cardW, run >= 0L ? "YOU'RE UP" : "YOU'RE DOWN", DashboardScreen.signed(run), "right now", 0, DashboardScreen.yourColor(run));
        this.card(g, cx + (cardW + gap), y, cardW, "HIGHEST", DashboardScreen.signed(high), "best point", 0, DashboardScreen.yourColor(high));
        this.card(g, cx + (cardW + gap) * 2, y, cardW, "LOWEST", DashboardScreen.signed(low), "worst point", 0, DashboardScreen.yourColor(low));
        this.card(g, cx + (cardW + gap) * 3, y, cardW, "PAYMENTS", String.valueOf(pays.size()), "in this range", 0, TEXT);
        int gy = 126;
        int gh = this.height - 14 - gy;
        if (gh < 40) {
            return;
        }
        this.panel(g, cx, gy, cw, gh, 0);
        g.text(this.font, "UP / DOWN", cx + 10, gy + 10, TEXT, false);
        g.text(this.font, "your running net", cx + 16 + this.font.width("UP / DOWN"), gy + 10, MUTED, false);
        this.drawNetGraph(g, pays, cx + 12, gy + 30, cw - 24, gh - 44);
    }

    private void addDetailButtons() {
        int cx = 134;
        int cw = this.width - cx - 14;
        this.backButton = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"All players"), b -> this.backToList()).bounds(cx, 44, this.font.width("All players") + 28, 18).build());
        long day = 86400000L;
        String[] labels = new String[]{"24 hours", "7 days", "30 days", "All time"};
        long[] ranges = new long[]{day, 7L * day, 30L * day, 0L};
        int bx = cx;
        for (int i = 0; i < 4; ++i) {
            long r = ranges[i];
            int w = this.font.width(labels[i]) + 16;
            Button b2 = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)labels[i]), btn -> {
                detailRange = r;
                this.scroll = 0;
            }).bounds(bx, 92, w, 16).build());
            this.rangeButtons.put(b2, r);
            bx += w + 4;
        }
    }

    private void addSessionButtons() {
        int cx = 134;
        int cw = this.width - cx - 14;
        int py = 104;
        int ph = this.height - 14 - py;
        int hy = py + 22;
        int rowsTop = hy + 13;
        int rowH = 12;
        int visible = Math.max(1, (py + ph - 6 - rowsTop) / rowH);
        int size = PaymentLedger.history().size();
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, size - visible)));
        if (size > 0) {
            int bx = cx + 6 + this.font.width("PAST SESSIONS") + 10;
            this.clearAllButton = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"Clear all"), b -> this.confirmThen(-1, PaymentLedger::clearHistory)).bounds(bx, py + 4, 52, 13).build());
        }
        for (int i = 0; i < visible && this.scroll + i < size; ++i) {
            int idx = this.scroll + i;
            Button b2 = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"Del"), btn -> this.confirmThen(idx, () -> PaymentLedger.deleteSession(idx))).bounds(cx + cw - 38, rowsTop + i * rowH - 2, 32, 12).build());
            this.deleteButtons.put(b2, idx);
        }
    }

    private void confirmThen(int key, Runnable action) {
        long now = System.currentTimeMillis();
        if (this.pendingDelete == key && now < this.pendingUntil) {
            this.pendingDelete = -2;
            action.run();
            this.rebuildWidgets();
        } else {
            this.pendingDelete = key;
            this.pendingUntil = now + 3000L;
        }
    }

    private void addTab(String label, Tab t, int i) {
        Button b = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)label), btn -> {
            tab = t;
            this.scroll = 0;
            this.pendingDelete = -2;
            detailPlayer = null;
            this.rebuildWidgets();
        }).bounds(6, 44 + i * 24, 106, 20).build());
        this.tabButtons.put(b, t);
    }

    private static String sessionLabel() {
        return PaymentLedger.isSessionActive() ? "End Session" : "Start Session";
    }

    private void onEndSession() {
        if (!PaymentLedger.isSessionActive()) {
            PaymentLedger.startSession();
            this.endButton.setMessage((Component)Component.literal((String)DashboardScreen.sessionLabel()));
            return;
        }
        long now = System.currentTimeMillis();
        if (now > this.confirmUntil) {
            this.confirmUntil = now + 3000L;
            this.endButton.setMessage((Component)Component.literal((String)"Click to confirm"));
            return;
        }
        this.confirmUntil = 0L;
        PaymentLedger.endSession();
        this.endButton.setMessage((Component)Component.literal((String)DashboardScreen.sessionLabel()));
    }

    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        int top = xmas ? -15452122 : -15459546;
        int bottom = xmas ? -16379636 : -16381941;
        for (int y = 0; y < this.height; y += 2) {
            g.fill(118, y, this.width, Math.min(this.height, y + 2), DashboardScreen.lerpColor(top, bottom, (float)y / (float)Math.max(1, this.height)));
        }
        if (xmas) {
            this.drawSnow(g);
        }
        g.fill(0, 0, 118, this.height, SIDE_BG);
        g.fill(118, 0, 119, this.height, LINE);
    }

    private static int lerpColor(int a, int b, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        int ar = a >> 16 & 0xFF;
        int ag = a >> 8 & 0xFF;
        int ab = a & 0xFF;
        int br = b >> 16 & 0xFF;
        int bg = b >> 8 & 0xFF;
        int bb = b & 0xFF;
        int r = (int)((float)ar + (float)(br - ar) * t);
        int gg = (int)((float)ag + (float)(bg - ag) * t);
        int bl = (int)((float)ab + (float)(bb - ab) * t);
        return 0xFF000000 | r << 16 | gg << 8 | bl;
    }

    private void drawSnow(GuiGraphicsExtractor g) {
        double t = (double)(System.currentTimeMillis() % 1000000L) / 1000.0;
        int area = Math.max(1, this.width - 118);
        for (int i = 0; i < 80; ++i) {
            int h1 = i * 73856093 ^ 0x5BD1E995;
            int h2 = i * 19349663 ^ 0x27D4EB2D;
            double speed = 6 + Math.abs(h2 % 14);
            int size = Math.abs(h1) % 5 == 0 ? 2 : 1;
            int x = 118 + Math.abs(h1 % area) + (int)Math.round(Math.sin(t * 0.6 + (double)i) * 3.0);
            int y = (int)(((double)Math.abs(h2 % 1000) + t * speed) % (double)(this.height + 4)) - 2;
            g.fill(x, y, x + size, y + size, size == 2 ? -570425345 : -1711276033);
        }
    }

    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        if (this.pendingDelete != -2 && System.currentTimeMillis() > this.pendingUntil) {
            this.pendingDelete = -2;
        }
        if (this.confirmUntil != 0L && System.currentTimeMillis() > this.confirmUntil) {
            this.confirmUntil = 0L;
            this.endButton.setMessage((Component)Component.literal((String)DashboardScreen.sessionLabel()));
        }
        g.fill(0, 0, 118, this.height, SIDE_BG);
        g.text(this.font, "BETTORS", 12, 12, GOLD, false);
        g.text(this.font, xmas ? "Merry Christmas" : "Payments", 12, 23, xmas ? RED : MUTED, false);
        for (Map.Entry<Button, Tab> e : this.tabButtons.entrySet()) {
            this.navButton(g, e.getKey(), e.getValue() == tab, mouseX, mouseY);
        }
        this.navButton(g, this.themeButton, false, mouseX, mouseY);
        boolean active = PaymentLedger.isSessionActive();
        boolean confirming = this.confirmUntil != 0L;
        this.actionButton(g, this.endButton, active ? RED : GREEN, confirming, mouseX, mouseY);
        this.actionButton(g, this.closeButton, MUTED, false, mouseX, mouseY);
        int sy = this.height - 82;
        g.text(this.font, active ? "SESSION" : "SESSION ENDED", 12, sy, active ? MUTED : RED, false);
        g.text(this.font, PaymentLedger.formatDuration(PaymentLedger.sessionMillis()), 12, sy + 11, active ? TEXT : MUTED, false);
        if (PaymentLedger.lastSessionLength() >= 0L && sy - 28 > 194) {
            long p = PaymentLedger.lastSessionProfit();
            g.text(this.font, "LAST SESSION", 12, sy - 28, MUTED, false);
            g.text(this.font, PaymentLedger.formatDurationShort(PaymentLedger.lastSessionLength()) + "  " + (p >= 0L ? "+" : "") + Money.format(p), 12, sy - 17, p >= 0L ? GREEN : RED, false);
        }
        g.fill(118, 0, 119, this.height, LINE);
        int cx = 134;
        int cw = this.width - cx - 14;
        g.fill(119, 0, this.width, 30, 0x55000000);
        g.fill(cx, 12, cx + 4, 16, active ? GREEN : MUTED);
        String who = this.minecraft.getUser().getName().toUpperCase();
        g.text(this.font, who, cx + 9, 10, GOLD, false);
        int tx = cx + 9 + this.font.width(who) + 14;
        String sText = (active ? "Session " : "Session ended ") + PaymentLedger.formatDuration(PaymentLedger.sessionMillis());
        g.text(this.font, sText, tx, 10, active ? MUTED : RED, false);
        this.toggle(g, this.sessionScope, this.allScope, sessionView, mouseX, mouseY);
        if (xmas) {
            int sx;
            for (sx = 119; sx < this.width; sx += 6) {
                g.fill(sx, 30, Math.min(this.width, sx + 3), 32, RED);
            }
            for (sx = 122; sx < this.width; sx += 6) {
                g.fill(sx, 30, Math.min(this.width, sx + 3), 32, -724504);
            }
        } else {
            g.fill(119, 30, this.width, 31, LINE);
        }
        List<PaymentLedger.Entry> list = PaymentLedger.entries(sessionView);
        switch (tab) {
            case DASHBOARD: {
                this.drawDashboard(g, list, cx, cw, mouseX, mouseY);
                break;
            }
            case GRAPH: {
                this.drawGraphTab(g, cx, cw, mouseX, mouseY);
                break;
            }
            case PLAYERS: {
                if (detailPlayer != null) {
                    this.drawPlayerDetail(g, cx, cw, mouseX, mouseY);
                    break;
                }
                this.drawLedger(g, list, cx, cw, mouseX, mouseY);
                break;
            }
            case PAID_YOU: {
                this.drawPaidYou(g, list, cx, cw);
                break;
            }
            case SESSIONS: {
                this.drawSessions(g, cx, cw, mouseX, mouseY);
            }
        }
    }

    private static boolean over(Button b, int mx, int my) {
        return mx >= b.getX() && mx < b.getX() + b.getWidth() && my >= b.getY() && my < b.getY() + b.getHeight();
    }

    private void navButton(GuiGraphicsExtractor g, Button b, boolean selected, int mx, int my) {
        int x = b.getX();
        int y = b.getY();
        int w = b.getWidth();
        int h = b.getHeight();
        boolean hover = DashboardScreen.over(b, mx, my);
        g.fill(x, y, x + w, y + h, selected ? SELECT : (hover ? HOVER : SIDE_BG));
        if (selected) {
            g.fill(x, y, x + 2, y + h, GOLD);
        }
        g.text(this.font, b.getMessage().getString(), x + 10, y + (h - 8) / 2, selected || hover ? TEXT : MUTED, false);
    }

    private void themeToggle(GuiGraphicsExtractor g, Button b, int mx, int my) {
        int x = b.getX();
        int y = b.getY();
        int w = b.getWidth();
        int h = b.getHeight();
        g.fill(x, y, x + w, y + h, DashboardScreen.over(b, mx, my) ? HOVER : SIDE_BG);
        this.outline(g, x, y, w, h, LINE);
        g.text(this.font, "Theme", x + 6, y + (h - 8) / 2, MUTED, false);
        String v = xmas ? "Christmas" : "Classic";
        g.text(this.font, v, x + w - 6 - this.font.width(v), y + (h - 8) / 2, xmas ? RED : TEXT, false);
    }

    private void actionButton(GuiGraphicsExtractor g, Button b, int color, boolean filled, int mx, int my) {
        int x = b.getX();
        int y = b.getY();
        int w = b.getWidth();
        int h = b.getHeight();
        boolean hover = DashboardScreen.over(b, mx, my);
        g.fill(x, y, x + w, y + h, filled ? color : (hover ? HOVER : SIDE_BG));
        this.outline(g, x, y, w, h, color);
        String s = b.getMessage().getString();
        g.text(this.font, s, x + (w - this.font.width(s)) / 2, y + (h - 8) / 2, filled ? DARK : color, false);
    }

    private void toggle(GuiGraphicsExtractor g, Button left, Button right, boolean leftOn, int mx, int my) {
        int x = left.getX();
        int y = left.getY();
        int w = right.getX() + right.getWidth() - x;
        int h = left.getHeight();
        g.fill(x, y, x + w, y + h, PANEL);
        this.outline(g, x, y, w, h, LINE);
        for (Button b : new Button[]{left, right}) {
            boolean on = b == left == leftOn;
            int bx = b.getX();
            int bw = b.getWidth();
            if (on) {
                g.fill(bx + 1, y + 1, bx + bw - 1, y + h - 1, SELECT);
            } else if (DashboardScreen.over(b, mx, my)) {
                g.fill(bx + 1, y + 1, bx + bw - 1, y + h - 1, HOVER);
            }
            String s = b.getMessage().getString();
            g.text(this.font, s, bx + (bw - this.font.width(s)) / 2, y + (h - 8) / 2, on ? GOLD : MUTED, false);
        }
    }

    private void outline(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + 1, color);
        g.fill(x, y + h - 1, x + w, y + h, color);
        g.fill(x, y, x + 1, y + h, color);
        g.fill(x + w - 1, y, x + w, y + h, color);
    }

    private void panel(GuiGraphicsExtractor g, int x, int y, int w, int h, int accent) {
        g.fill(x, y, x + w, y + h, PANEL);
        this.outline(g, x, y, w, h, LINE);
        if (accent != 0) {
            g.fill(x, y, x + w, y + 2, accent);
        }
    }

    private void bigText(GuiGraphicsExtractor g, String s, int x, int y, int color, int maxW) {
        float sc = (float)this.font.width(s) * 1.5f <= (float)maxW ? 1.5f : 1.0f;
        g.pose().pushMatrix();
        g.pose().translate((float)x, (float)y);
        g.pose().scale(sc, sc);
        g.text(this.font, s, 0, 0, color, false);
        g.pose().popMatrix();
    }

    private void emptyState(GuiGraphicsExtractor g, int x, int y, int w, int h, String title, String line1, String line2) {
        this.panel(g, x, y, w, h, 0);
        int my = y + h / 2 - 16;
        g.text(this.font, title, x + (w - this.font.width(title)) / 2, my, TEXT, false);
        g.text(this.font, line1, x + (w - this.font.width(line1)) / 2, my + 14, MUTED, false);
        g.text(this.font, line2, x + (w - this.font.width(line2)) / 2, my + 26, MUTED, false);
    }

    private void drawDashboard(GuiGraphicsExtractor g, List<PaymentLedger.Entry> list, int cx, int cw, int mouseX, int mouseY) {
        long paid = 0L;
        long sent = 0L;
        long up = 0L;
        long down = 0L;
        int upCount = 0;
        int downCount = 0;
        int payCount = 0;
        for (PaymentLedger.Entry e : list) {
            payCount += e.payments;
            paid += e.paidYou;
            sent += e.youPaid;
            long yours = -e.net();
            if (yours > 0L) {
                up += yours;
                ++upCount;
                continue;
            }
            if (yours >= 0L) continue;
            down -= yours;
            ++downCount;
        }
        long net = up - down;
        int y = 44;
        int gap = 10;
        int cardW = (cw - gap * 4) / 5;
        this.card(g, cx, y, cardW, "PLAYERS", String.valueOf(list.size()), payCount + (payCount == 1 ? " payment" : " payments"), 0, TEXT);
        this.card(g, cx + (cardW + gap), y, cardW, "PAID YOU", Money.format(paid), "received", 0, TEXT);
        this.card(g, cx + (cardW + gap) * 2, y, cardW, "YOU PAID", Money.format(sent), "sent", 0, TEXT);
        this.card(g, cx + (cardW + gap) * 3, y, cardW, "YOU'RE UP", Money.format(up), "from " + upCount + (upCount == 1 ? " player" : " players"), 0, GREEN);
        this.card(g, cx + (cardW + gap) * 4, y, cardW, "YOU'RE DOWN", Money.format(down), "net " + DashboardScreen.signed(net), 0, RED);
        int by = 104;
        int bh = this.height - 14 - by;
        if (list.isEmpty()) {
            this.emptyState(g, cx, by, cw, bh, "No payments yet", "Payments show up here automatically when someone pays you or you pay them.", "Type /bettors debug to check they're being picked up.");
            return;
        }
        if (bh < 40) {
            return;
        }
        int lw = cw * 58 / 100;
        int rx = cx + lw + 10;
        int rw = cx + cw - rx;
        this.panel(g, cx, by, lw, bh, 0);
        g.text(this.font, "PLAYERS", cx + 8, by + 8, TEXT, false);
        g.text(this.font, "your net per player", cx + 14 + this.font.width("PLAYERS"), by + 8, MUTED, false);
        ArrayList<PaymentLedger.Entry> s = new ArrayList<PaymentLedger.Entry>(list);
        s.sort(Comparator.comparingLong(PaymentLedger.Entry::net));
        long maxAbs = 1L;
        for (PaymentLedger.Entry e : s) {
            maxAbs = Math.max(maxAbs, Math.abs(e.net()));
        }
        int rowsTop = by + 26;
        int rowH = 14;
        int visible = Math.max(1, (by + bh - 6 - rowsTop) / rowH);
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, s.size() - visible)));
        int nameW = lw * 30 / 100;
        int valW = 64;
        int barL = cx + 8 + nameW;
        int barR = cx + lw - 12 - valW;
        int mid = (barL + barR) / 2;
        int half = (barR - barL) / 2;
        int shown = Math.min(visible, s.size() - this.scroll);
        g.fill(mid, rowsTop - 3, mid + 1, rowsTop + shown * rowH - 4, LINE);
        for (int i = 0; i < shown; ++i) {
            PaymentLedger.Entry e = (PaymentLedger.Entry)s.get(this.scroll + i);
            int ry = rowsTop + i * rowH;
            long yours = -e.net();
            boolean h = this.hot(mouseX, mouseY, cx + 4, ry - 3, cx + lw - 4, ry + 10);
            if (h) {
                g.fill(cx + 4, ry - 3, cx + lw - 4, ry + 10, SELECT);
            }
            g.text(this.font, this.fit(e.name, nameW - 6), cx + 8, ry, h ? GOLD : TEXT, false);
            int len = (int)Math.max(yours == 0L ? 0.0 : 1.0, (double)half * (double)Math.abs(yours) / (double)maxAbs);
            if (yours > 0L) {
                g.fill(mid + 1, ry, mid + 1 + len, ry + 7, GREEN);
            } else if (yours < 0L) {
                g.fill(mid - len, ry, mid, ry + 7, RED);
            }
            this.rightText(g, DashboardScreen.signed(yours), cx + lw - 8, ry, DashboardScreen.yourColor(yours));
        }
        if (this.scroll + shown < s.size()) {
            this.moreHint(g, cx + lw - 8, by + 8);
        }
        int top = bh / 2 + 2;
        ArrayList<PaymentLedger.Entry> won = new ArrayList<PaymentLedger.Entry>();
        ArrayList<PaymentLedger.Entry> lost = new ArrayList<PaymentLedger.Entry>();
        for (PaymentLedger.Entry e : list) {
            if (e.net() < 0L) {
                won.add(e);
                continue;
            }
            if (e.net() <= 0L) continue;
            lost.add(e);
        }
        won.sort(Comparator.comparingLong(PaymentLedger.Entry::net));
        lost.sort(Comparator.comparingLong(PaymentLedger.Entry::net).reversed());
        this.listSection(g, "YOU WON MOST FROM", won, rx, by, rw, top - 5, GREEN, mouseX, mouseY);
        this.listSection(g, "YOU LOST MOST TO", lost, rx, by + top + 5, rw, bh - top - 5, RED, mouseX, mouseY);
    }

    private void listSection(GuiGraphicsExtractor g, String title, List<PaymentLedger.Entry> list, int x, int y, int w, int h, int color, int mouseX, int mouseY) {
        this.panel(g, x, y, w, h, 0);
        g.text(this.font, title, x + 8, y + 8, color, false);
        int rows = Math.max(0, Math.min(5, (h - 28) / 12));
        if (list.isEmpty()) {
            g.text(this.font, "Nobody yet", x + 8, y + 24, MUTED, false);
            return;
        }
        for (int i = 0; i < Math.min(rows, list.size()); ++i) {
            PaymentLedger.Entry e = list.get(i);
            int ry = y + 24 + i * 12;
            String amt = Money.format(Math.abs(e.net()));
            boolean hv = this.hot(mouseX, mouseY, x + 4, ry - 2, x + w - 4, ry + 10);
            if (hv) {
                g.fill(x + 4, ry - 2, x + w - 4, ry + 10, SELECT);
            }
            g.text(this.font, this.fit(e.name, w - 24 - this.font.width(amt)), x + 8, ry, hv ? GOLD : TEXT, false);
            this.rightText(g, amt, x + w - 8, ry, color);
        }
    }

    private void topList(GuiGraphicsExtractor g, String title, List<PaymentLedger.Entry> list, int x, int y, int w, int h, int color) {
        g.text(this.font, title, x, y, color, false);
        int rows = Math.max(0, (h - 22) / 11);
        for (int i = 0; i < Math.min(rows, list.size()); ++i) {
            PaymentLedger.Entry e = list.get(i);
            int ry = y + 16 + i * 11;
            String amt = Money.format(Math.abs(e.net()));
            g.text(this.font, this.fit(i + 1 + ". " + e.name, w - this.font.width(amt) - 6), x, ry, TEXT, false);
            this.rightText(g, amt, x + w, ry, color);
        }
    }

    private void card(GuiGraphicsExtractor g, int x, int y, int w, String label, String value, String sub, int accent, int valueColor) {
        this.panel(g, x, y, w, 46, 0);
        g.text(this.font, this.fit(label, w - 12), x + 6, y + 7, MUTED, false);
        this.bigText(g, this.fit(value, w - 12), x + 6, y + 19, valueColor, w - 12);
        g.text(this.font, this.fit(sub, w - 12), x + 6, y + 35, MUTED, false);
    }

    private void drawChart(GuiGraphicsExtractor g, List<PaymentLedger.Entry> list, int x, int y, int w, int h) {
        int prevY;
        if (list.isEmpty()) {
            g.text(this.font, "No payments yet", x, y + h / 2 - 4, MUTED, false);
            return;
        }
        ArrayList<PaymentLedger.Entry> s = new ArrayList<PaymentLedger.Entry>(list);
        s.sort(Comparator.comparingLong(PaymentLedger.Entry::net));
        long min = Math.min(0L, ((PaymentLedger.Entry)s.get(0)).net());
        long max = Math.max(0L, ((PaymentLedger.Entry)s.get(s.size() - 1)).net());
        if (min == max) {
            max = min + 1L;
        }
        String maxL = Money.format(max);
        String minL = Money.format(min);
        int labelW = Math.max(this.font.width(maxL), this.font.width(minL)) + 6;
        g.text(this.font, maxL, x, y, MUTED, false);
        g.text(this.font, minL, x, y + h - 8, MUTED, false);
        int lx = x + labelW;
        int pw = w - labelW;
        long fMin = min;
        long fMax = max;
        LongToIntFunction yOf = v -> y + (int)Math.round((double)(fMax - v) / (double)(fMax - fMin) * (double)(h - 1));
        int zy = yOf.applyAsInt(0L);
        g.fill(lx, zy, lx + pw, zy + 1, LINE);
        g.fill(lx, y, lx + 1, y + h, LINE);
        int n = s.size();
        if (n == 1) {
            int px = lx + pw / 2;
            int py = yOf.applyAsInt(((PaymentLedger.Entry)s.get(0)).net());
            g.fill(px - 1, py - 1, px + 2, py + 2, DashboardScreen.netColor(((PaymentLedger.Entry)s.get(0)).net()));
            return;
        }
        int prevX = lx;
        int lastY = prevY = yOf.applyAsInt(((PaymentLedger.Entry)s.get(0)).net());
        for (int i = 1; i < n; ++i) {
            int nx = lx + (int)((long)i * (long)(pw - 1) / (long)(n - 1));
            int ny = yOf.applyAsInt(((PaymentLedger.Entry)s.get(i)).net());
            long a = ((PaymentLedger.Entry)s.get(i - 1)).net();
            long b = ((PaymentLedger.Entry)s.get(i)).net();
            for (int xx = prevX; xx <= nx; ++xx) {
                double t = nx == prevX ? 1.0 : (double)(xx - prevX) / (double)(nx - prevX);
                int yy = (int)Math.round((double)prevY + (double)(ny - prevY) * t);
                double v2 = (double)a + (double)(b - a) * t;
                g.fill(xx, Math.min(yy, lastY), xx + 1, Math.max(yy, lastY) + 1, v2 > 0.0 ? GOLD : (v2 < 0.0 ? RED : MUTED));
                lastY = yy;
            }
            prevX = nx;
            prevY = ny;
        }
    }

    private void drawLedger(GuiGraphicsExtractor g, List<PaymentLedger.Entry> list, int cx, int cw, int mouseX, int mouseY) {
        int py = 44;
        int ph = this.height - 14 - py;
        this.panel(g, cx, py, cw, ph, GOLD);
        g.text(this.font, (sessionView ? "SESSION" : "ALL-TIME") + " PLAYER LEDGER", cx + 6, py + 7, GOLD, false);
        this.rightText(g, list.size() + " PLAYERS TRACKED", cx + cw - 6, py + 7, MUTED);
        int paidX = cx + cw * 50 / 100;
        int sentX = cx + cw * 68 / 100;
        int right = cx + cw - 6;
        int hy = py + 22;
        g.text(this.font, "PLAYER", cx + 6, hy, MUTED, false);
        g.text(this.font, "PAID YOU", paidX, hy, MUTED, false);
        g.text(this.font, "YOU PAID", sentX, hy, MUTED, false);
        this.rightText(g, "YOUR NET", right, hy, MUTED);
        ArrayList<PaymentLedger.Entry> s = new ArrayList<PaymentLedger.Entry>(list);
        s.sort(Comparator.comparingLong(PaymentLedger.Entry::volume).reversed());
        int rowsTop = hy + 13;
        int rowH = 12;
        int visible = Math.max(1, (py + ph - 16 - rowsTop) / rowH);
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, s.size() - visible)));
        for (int i = 0; i < visible && this.scroll + i < s.size(); ++i) {
            boolean hot;
            int idx = this.scroll + i;
            PaymentLedger.Entry e = (PaymentLedger.Entry)s.get(idx);
            int ry = rowsTop + i * rowH;
            boolean bl = hot = mouseX >= cx + 4 && mouseX < cx + cw - 4 && mouseY >= ry - 2 && mouseY < ry + 10;
            if (hot) {
                g.fill(cx + 4, ry - 2, cx + cw - 4, ry + 10, SELECT);
            } else if (i % 2 == 0) {
                g.fill(cx + 4, ry - 2, cx + cw - 4, ry + 10, STRIPE);
            }
            g.text(this.font, this.fit(idx + 1 + ". " + e.name, paidX - cx - 12), cx + 6, ry, hot ? GOLD : TEXT, false);
            g.text(this.font, Money.format(e.paidYou), paidX, ry, TEXT, false);
            g.text(this.font, Money.format(e.youPaid), sentX, ry, TEXT, false);
            long yours = -e.net();
            this.rightText(g, DashboardScreen.signed(yours), right, ry, DashboardScreen.yourColor(yours));
        }
        if (s.isEmpty()) {
            g.text(this.font, "No payments logged yet", cx + 6, rowsTop, MUTED, false);
        }
        if (this.scroll + visible < s.size()) {
            this.moreHint(g, cx + cw - 6, py + ph - 24);
        }
        String legend = "Click a player for details   \u00b7   + you won from them   \u00b7   - you lost to them";
        int legendW = Math.min(this.font.width(legend), cw - 12);
        g.text(this.font, this.fit(legend, cw - 12), cx + (cw - legendW) / 2, py + ph - 11, MUTED, false);
    }

    private void drawPaidYou(GuiGraphicsExtractor g, List<PaymentLedger.Entry> list, int cx, int cw) {
        int py = 44;
        int ph = this.height - 14 - py;
        this.panel(g, cx, py, cw, ph, GOLD);
        ArrayList<PaymentLedger.Entry> s = new ArrayList<PaymentLedger.Entry>();
        long total = 0L;
        long top = 1L;
        for (PaymentLedger.Entry e2 : list) {
            if (e2.paidYou <= 0L) continue;
            s.add(e2);
            total += e2.paidYou;
            top = Math.max(top, e2.paidYou);
        }
        s.sort(Comparator.comparingLong(e -> e.paidYou).reversed());
        g.text(this.font, "PAID YOU" + (sessionView ? " (SESSION)" : " (ALL TIME)"), cx + 6, py + 7, GOLD, false);
        this.rightText(g, "TOTAL " + Money.format(total), cx + cw - 6, py + 7, GOLD);
        int barX = cx + cw * 38 / 100;
        int barEnd = cx + cw * 78 / 100;
        int right = cx + cw - 6;
        int rowsTop = py + 24;
        int rowH = 13;
        int visible = Math.max(1, (py + ph - 6 - rowsTop) / rowH);
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, s.size() - visible)));
        for (int i = 0; i < visible && this.scroll + i < s.size(); ++i) {
            int idx = this.scroll + i;
            PaymentLedger.Entry e3 = (PaymentLedger.Entry)s.get(idx);
            int ry = rowsTop + i * rowH;
            g.text(this.font, this.fit(idx + 1 + ". " + e3.name, barX - cx - 12), cx + 6, ry, TEXT, false);
            g.fill(barX, ry, barEnd, ry + 8, STRIPE);
            int bw = (int)Math.max(1.0, (double)(barEnd - barX) * (double)e3.paidYou / (double)top);
            g.fill(barX, ry, barX + bw, ry + 8, GOLD);
            int pct = total > 0L ? (int)Math.round(100.0 * (double)e3.paidYou / (double)total) : 0;
            this.rightText(g, Money.format(e3.paidYou) + "  " + pct + "%", right, ry, GOLD);
        }
        if (s.isEmpty()) {
            g.text(this.font, "Nobody has paid you yet", cx + 6, rowsTop, MUTED, false);
        }
    }

    private static String ago(long ts) {
        long sec = Math.max(0L, (System.currentTimeMillis() - ts) / 1000L);
        if (sec < 60L) {
            return sec + "s ago";
        }
        long m = sec / 60L;
        if (m < 60L) {
            return m + "m ago";
        }
        long h = m / 60L;
        if (h < 24L) {
            return h + "h ago";
        }
        return h / 24L + "d ago";
    }

    private void backButtonStyle(GuiGraphicsExtractor g, Button b, int mx, int my) {
        int x = b.getX();
        int y = b.getY();
        int w = b.getWidth();
        int h = b.getHeight();
        boolean hover = DashboardScreen.over(b, mx, my);
        int c = hover ? GOLD : TEXT;
        g.fill(x, y, x + w, y + h, hover ? HOVER : PANEL);
        this.outline(g, x, y, w, h, hover ? GOLD : LINE);
        int ax = x + 8;
        int ay = y + h / 2;
        for (int i = 0; i < 4; ++i) {
            g.fill(ax + i, ay - i, ax + i + 1, ay - i + 1, c);
            g.fill(ax + i, ay + i, ax + i + 1, ay + i + 1, c);
        }
        g.fill(ax, ay, ax + 8, ay + 1, c);
        g.text(this.font, b.getMessage().getString(), x + 20, y + (h - 8) / 2, c, false);
    }

    private void drawPlayerDetail(GuiGraphicsExtractor g, int cx, int cw, int mouseX, int mouseY) {
        int count;
        String name = detailPlayer;
        if (this.backButton != null) {
            this.backButtonStyle(g, this.backButton, mouseX, mouseY);
        }
        this.bigText(g, this.fit(name, cw), cx, 70, TEXT, cw);
        for (Map.Entry<Button, Long> e : this.rangeButtons.entrySet()) {
            Button b = e.getKey();
            boolean on = e.getValue() == detailRange;
            int x = b.getX();
            int y = b.getY();
            int w = b.getWidth();
            int h = b.getHeight();
            g.fill(x, y, x + w, y + h, on ? SELECT : (DashboardScreen.over(b, mouseX, mouseY) ? HOVER : PANEL));
            this.outline(g, x, y, w, h, on ? GOLD : LINE);
            String t = b.getMessage().getString();
            g.text(this.font, t, x + (w - this.font.width(t)) / 2, y + (h - 8) / 2, on ? GOLD : MUTED, false);
        }
        long since = detailRange == 0L ? 0L : System.currentTimeMillis() - detailRange;
        List<PaymentLedger.Payment> pays = PaymentLedger.paymentsFor(name, since);
        long paid = 0L;
        long sent = 0L;
        if (detailRange == 0L) {
            PaymentLedger.Entry all = PaymentLedger.allTimeFor(name);
            if (all != null) {
                paid = all.paidYou;
                sent = all.youPaid;
            }
            count = all != null ? all.payments : pays.size();
        } else {
            for (PaymentLedger.Payment p : pays) {
                paid += p.paidYou;
                sent += p.youPaid;
            }
            count = pays.size();
        }
        long yours = paid - sent;
        String status = yours > 0L ? "They're down " + Money.format(yours) : (yours < 0L ? "They're up " + Money.format(-yours) : "Even");
        int y = 118;
        int gap = 10;
        int cardW = (cw - gap * 3) / 4;
        this.card(g, cx, y, cardW, "PAID YOU", Money.format(paid), "received", 0, TEXT);
        this.card(g, cx + (cardW + gap), y, cardW, "YOU PAID", Money.format(sent), "sent", 0, TEXT);
        this.card(g, cx + (cardW + gap) * 2, y, cardW, "YOUR NET", DashboardScreen.signed(yours), status.toLowerCase(), 0, DashboardScreen.yourColor(yours));
        this.card(g, cx + (cardW + gap) * 3, y, cardW, "PAYMENTS", String.valueOf(count), detailRange == 0L ? "all time" : "in this range", 0, TEXT);
        int gy = 174;
        int gh = Math.max(56, (this.height - 14 - gy) * 40 / 100);
        this.panel(g, cx, gy, cw, gh, 0);
        g.text(this.font, "UP / DOWN", cx + 8, gy + 8, TEXT, false);
        g.text(this.font, "your net with " + name, cx + 14 + this.font.width("UP / DOWN"), gy + 8, MUTED, false);
        ArrayList<PaymentLedger.Payment> oldestFirst = new ArrayList<PaymentLedger.Payment>(pays);
        Collections.reverse(oldestFirst);
        this.drawNetGraph(g, oldestFirst, cx + 8, gy + 22, cw - 16, gh - 30);
        int py = gy + gh + 10;
        int ph = this.height - 14 - py;
        if (ph < 30) {
            return;
        }
        this.panel(g, cx, py, cw, ph, 0);
        g.text(this.font, "PAYMENTS", cx + 8, py + 8, TEXT, false);
        int rowsTop = py + 24;
        int rowH = 12;
        int visible = Math.max(1, (py + ph - 18 - rowsTop) / rowH);
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, pays.size() - visible)));
        for (int i = 0; i < visible && this.scroll + i < pays.size(); ++i) {
            PaymentLedger.Payment p = pays.get(this.scroll + i);
            int ry = rowsTop + i * rowH;
            if (i % 2 == 0) {
                g.fill(cx + 4, ry - 2, cx + cw - 4, ry + 10, STRIPE);
            }
            boolean in = p.paidYou > 0L;
            g.text(this.font, in ? "paid you" : "you paid", cx + 8, ry, MUTED, false);
            String amt = (in ? "+" : "-") + Money.format(in ? p.paidYou : p.youPaid);
            g.text(this.font, amt, cx + 8 + this.font.width("you paid") + 16, ry, in ? GREEN : RED, false);
            String when = DashboardScreen.when(p.ts) + "   " + DashboardScreen.ago(p.ts);
            this.rightText(g, when, cx + cw - 8, ry, MUTED);
        }
        if (pays.isEmpty()) {
            g.text(this.font, "No payments in this time range", cx + 8, rowsTop, MUTED, false);
        }
        if (this.scroll + visible < pays.size()) {
            this.moreHint(g, cx + cw - 8, py + 8);
        }
        g.text(this.font, "History with times is kept from this update on, for about a month.", cx + 8, py + ph - 12, MUTED, false);
    }

    private static String when(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(WHEN);
    }

    private void drawSessions(GuiGraphicsExtractor g, int cx, int cw, int mouseX, int mouseY) {
        boolean active = PaymentLedger.isSessionActive();
        List<PaymentLedger.Entry> cur = PaymentLedger.entries(true);
        long in = 0L;
        long out = 0L;
        int payCount = 0;
        for (PaymentLedger.Entry e : cur) {
            in += e.paidYou;
            out += e.youPaid;
            payCount += e.payments;
        }
        long profit = in - out;
        int y = 44;
        int gap = 10;
        int cardW = (cw - gap * 4) / 5;
        this.card(g, cx, y, cardW, "STATUS", active ? "RUNNING" : "ENDED", "since " + DashboardScreen.when(PaymentLedger.sessionStartTime()), active ? GREEN : RED, active ? GREEN : RED);
        this.card(g, cx + (cardW + gap), y, cardW, "LENGTH", PaymentLedger.formatDuration(PaymentLedger.sessionMillis()), payCount + (payCount == 1 ? " payment" : " payments"), LINE, TEXT);
        this.card(g, cx + (cardW + gap) * 2, y, cardW, "RECEIVED", Money.format(in), cur.size() + (cur.size() == 1 ? " player" : " players"), 0, TEXT);
        this.card(g, cx + (cardW + gap) * 3, y, cardW, "SENT", Money.format(out), "paid out", 0, TEXT);
        this.card(g, cx + (cardW + gap) * 4, y, cardW, "YOUR PROFIT", (profit >= 0L ? "+" : "") + Money.format(profit), "this session", 0, profit >= 0L ? GREEN : RED);
        List<PaymentLedger.SessionRecord> hist = PaymentLedger.history();
        int py = 104;
        int ph = this.height - 14 - py;
        this.panel(g, cx, py, cw, ph, GOLD);
        g.text(this.font, "PAST SESSIONS", cx + 6, py + 7, GOLD, false);
        long total = 0L;
        long best = Long.MIN_VALUE;
        for (PaymentLedger.SessionRecord r : hist) {
            total += r.profit();
            best = Math.max(best, r.profit());
        }
        String summary = hist.isEmpty() ? "NONE YET" : hist.size() + " SESSIONS  \u00b7  TOTAL " + (total >= 0L ? "+" : "") + Money.format(total) + "  \u00b7  BEST +" + Money.format(Math.max(0L, best));
        int titleEnd = cx + 6 + this.font.width("PAST SESSIONS") + 10 + (hist.isEmpty() ? 0 : 62);
        this.rightText(g, this.fit(summary, cx + cw - 6 - titleEnd), cx + cw - 6, py + 7, MUTED);
        int lenX = cx + cw * 30 / 100;
        int inX = cx + cw * 46 / 100;
        int outX = cx + cw * 62 / 100;
        int plX = cx + cw * 76 / 100;
        int right = cx + cw - 44;
        int hy = py + 22;
        g.text(this.font, "STARTED", cx + 6, hy, MUTED, false);
        g.text(this.font, "LENGTH", lenX, hy, MUTED, false);
        g.text(this.font, "RECEIVED", inX, hy, MUTED, false);
        g.text(this.font, "SENT", outX, hy, MUTED, false);
        g.text(this.font, "PAYMENTS", plX, hy, MUTED, false);
        this.rightText(g, "PROFIT", right, hy, MUTED);
        int rowsTop = hy + 13;
        int rowH = 12;
        int visible = Math.max(1, (py + ph - 6 - rowsTop) / rowH);
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, hist.size() - visible)));
        for (int i = 0; i < visible && this.scroll + i < hist.size(); ++i) {
            PaymentLedger.SessionRecord r = hist.get(this.scroll + i);
            int ry = rowsTop + i * rowH;
            if (i % 2 == 0) {
                g.fill(cx + 4, ry - 2, cx + cw - 4, ry + 10, STRIPE);
            }
            g.text(this.font, this.fit(DashboardScreen.when(r.start), lenX - cx - 10), cx + 6, ry, TEXT, false);
            g.text(this.font, PaymentLedger.formatDuration(r.length()), lenX, ry, TEXT, false);
            g.text(this.font, Money.format(r.received), inX, ry, GOLD, false);
            g.text(this.font, Money.format(r.sent), outX, ry, RED, false);
            g.text(this.font, r.payments > 0 ? String.valueOf(r.payments) : "-", plX, ry, TEXT, false);
            long p = r.profit();
            this.rightText(g, (p >= 0L ? "+" : "") + Money.format(p), right, ry, p >= 0L ? GREEN : RED);
        }
        if (hist.isEmpty()) {
            g.text(this.font, "Ended sessions show up here", cx + 6, rowsTop, MUTED, false);
        }
        for (Map.Entry<Button, Integer> e : this.deleteButtons.entrySet()) {
            this.smallButton(g, e.getKey(), this.pendingDelete == e.getValue(), mouseX, mouseY);
        }
        if (this.clearAllButton != null) {
            this.smallButton(g, this.clearAllButton, this.pendingDelete == -1, mouseX, mouseY);
        }
    }

    private void smallButton(GuiGraphicsExtractor g, Button b, boolean armed, int mx, int my) {
        int x = b.getX();
        int y = b.getY();
        int w = b.getWidth();
        int h = b.getHeight();
        boolean hover = DashboardScreen.over(b, mx, my);
        g.fill(x, y, x + w, y + h, armed ? RED : (hover ? HOVER : PANEL));
        this.outline(g, x, y, w, h, armed || hover ? RED : LINE);
        String s = armed ? "Sure?" : b.getMessage().getString();
        g.text(this.font, s, x + (w - this.font.width(s)) / 2, y + (h - 8) / 2 + 1, armed ? DARK : (hover ? RED : MUTED), false);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scroll = Math.max(0, this.scroll - (int)Math.signum(scrollY) * 2);
        if (tab == Tab.SESSIONS || tab == Tab.DASHBOARD || tab == Tab.PLAYERS && detailPlayer == null) {
            this.rebuildWidgets();
        }
        return true;
    }

    private static int yourColor(long v) {
        return v > 0L ? GREEN : (v < 0L ? RED : MUTED);
    }

    private static String signed(long v) {
        return v == 0L ? "$0" : (v > 0L ? "+" : "-") + Money.format(Math.abs(v));
    }

    private static int netColor(long n) {
        return n > 0L ? GOLD : (n < 0L ? RED : MUTED);
    }

    private void rightText(GuiGraphicsExtractor g, String s, int rightX, int y, int color) {
        g.text(this.font, s, rightX - this.font.width(s), y, color, false);
    }

    private String fit(String s, int w) {
        if (w <= 0) {
            return "";
        }
        if (this.font.width(s) <= w) {
            return s;
        }
        return this.font.plainSubstrByWidth(s, Math.max(0, w - this.font.width("..."))) + "...";
    }

    static {
        DashboardScreen.applyTheme(false);
        detailPlayer = null;
        detailRange = 86400000L;
        graphRange = -1L;
        WHEN = DateTimeFormatter.ofPattern("MMM d  h:mm a", Locale.US);
    }

    private static enum Tab {
        DASHBOARD,
        GRAPH,
        PLAYERS,
        PAID_YOU,
        SESSIONS;

    }
}

