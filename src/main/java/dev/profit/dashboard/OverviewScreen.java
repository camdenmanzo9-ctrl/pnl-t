/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.PlayerFaceExtractor
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.client.input.CharacterEvent
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FontDescription
 *  net.minecraft.network.chat.FontDescription$Resource
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.PlayerSkin
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package dev.profit.dashboard;

import com.mojang.blaze3d.platform.InputConstants;
import dev.profit.dashboard.Auction;
import dev.profit.dashboard.BalanceTimeline;
import dev.profit.dashboard.DashboardClient;
import dev.profit.dashboard.Fonts;
import dev.profit.dashboard.HistorySkinCache;
import dev.profit.dashboard.Money;
import dev.profit.dashboard.OverlayEditorScreen;
import dev.profit.dashboard.Panels;
import dev.profit.dashboard.PaymentLedger;
import dev.profit.dashboard.PreviewPlayer;
import dev.profit.dashboard.Scenes;
import dev.profit.dashboard.Settings;
import dev.profit.dashboard.SettingsScreen;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class OverviewScreen
extends Screen {
    private final Map<String, Long> counterStarts = new LinkedHashMap<String, Long>();
    private final Map<String, int[]> counterBounds = new LinkedHashMap<String, int[]>();
    private final Map<String, Integer> counterWidths = new LinkedHashMap<String, Integer>();
    private static final int BG = -15921644;
    private static final int FRAME = -15526625;
    private static final int PANEL = -15526625;
    private static final int BORDER = -14934485;
    private static final int SIDEBAR = -15526625;
    private static int PURPLE = -9675545;
    private static int PURPLE_LIGHT = -7570714;
    private static final int TEXT = -1;
    private static final int MUTED = -7696482;
    private static final int DIM = -9538685;
    private static final int GOLD = -932849;
    private static final int BLUE = -8680193;
    private static final int RED = -41385;
    private static final int GREEN = -13710223;
    private static final int AMBER = -2056144;
    private static final long DAY = 86400000L;
    private static final String[] RANGE_LABELS = new String[]{"SESSION", "24H", "7D", "30D", "ALL"};
    private static final long[] RANGES;
    private static final String[] TAB_LABELS;
    private static int tab;
    private static int range;
    private static int filter;
    private static int payFilter;
    private static int playersView;
    private static int payShown;
    private static boolean paused;
    private static long frozenAt;
    private static long revealAt;
    private final List<Button> rangeButtons = new ArrayList<Button>();
    private final List<Button> tabButtons = new ArrayList<Button>();
    private final List<Button> filterButtons = new ArrayList<Button>();
    private final List<Button> viewButtons = new ArrayList<Button>();
    private Button sessionButton;
    private Button hideAllOverlays;
    private Button headerSettings;
    private SettingsScreen embeddedSettings;
    private Button loadMoreBtn;
    private EditBox searchBox;
    private Object pendingFocus;
    private static String search;
    private static String paySearch;
    private long confirmUntil = 0L;
    private static int scroll;
    private int ox;
    private int ow;
    private int side;
    private int fx;
    private int fy;
    private int fw;
    private int fh;
    private static final Style INTER;
    private static final Style INTER_BOLD;
    private static final float TEXT_SIZE = 1.25f;
    private static PreviewPlayer previewPlayer;
    private static Object previewLevel;
    private static String wlPlayer;
    private Button wlBack;
    private final Map<Button, String> wlRows = new LinkedHashMap<Button, String>();
    private static final int WL_ROW_H = 21;
    private int lastMouseX;
    private int lastMouseY;
    private static final int REVEAL_MS = 520;
    private static final boolean GLOWS = false;
    private static final int PINK = -757066;
    private static final int PAY_ROW = 20;
    private static final int K_TOGGLE = 0;
    private static final int K_KEY = 1;
    private static final int K_ACCENT = 2;
    private static final int K_SCENE = 3;
    private static final int S_ROW_H = 36;
    private static final int S_GAP = 6;
    private final List<Button[]> streamButtons = new ArrayList<Button[]>();
    private EditBox hexBox;
    private boolean bindingKey = false;
    private EditBox itemSearch;
    private static String itemQuery;
    private final Map<Button, Runnable> auctionButtons = new LinkedHashMap<Button, Runnable>();
    private Button startBtn;
    private Button clearBtn;
    private Button switchBtn;
    private Button gridBtn;
    private final List<Button> durationBtns = new ArrayList<Button>();
    private final List<Button> colorBtns = new ArrayList<Button>();
    private static final long[] DURATIONS;
    private static final String[] DURATION_LABELS;
    private static final int[] TIMER_COLORS;
    private static List<ItemStack> allItems;
    private static List<String> allNames;
    private static String cachedQuery;
    private static List<Integer> cachedItems;
    private EditBox nameBox;
    private EditBox qtyBox;
    private EditBox secBox;
    private Button nameClick;
    private Button qtyClick;
    private Button secClick;
    private Button svBtn;
    private Button hueBtn;
    private static float hue;
    private static float sat;
    private static float val;
    private static final int CELL = 26;
    private static final int BID_H = 92;
    private Button overlayEditorButton;
    private Button smoothHigh;
    private Button smoothLight;
    private Button backgroundPrev;
    private Button backgroundNext;
    private Button slowSlideOn;
    private Button slowSlideOff;

    static {
        long[] lArray = new long[5];
        lArray[0] = -1L;
        lArray[1] = 86400000L;
        lArray[2] = 604800000L;
        lArray[3] = 2592000000L;
        RANGES = lArray;
        TAB_LABELS = new String[]{"Dashboard", "Players", "Payments", "Auctions", "Streamer", "General", "Overlay"};
        tab = 0;
        range = 2;
        filter = 0;
        payFilter = 0;
        playersView = 0;
        payShown = 12;
        paused = false;
        frozenAt = 0L;
        revealAt = 0L;
        search = "";
        paySearch = "";
        scroll = 0;
        INTER = Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)"inter")));
        INTER_BOLD = Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)"inter_bold")));
        wlPlayer = null;
        itemQuery = "";
        DURATIONS = new long[]{15000L, 30000L, 45000L, 60000L, 120000L, 300000L};
        DURATION_LABELS = new String[]{"15s", "30s", "45s", "1m", "2m", "5m"};
        TIMER_COLORS = new int[]{-932849, PURPLE, -13710223, -41385, -10777345, -757066, -12918817, -855814};
        cachedQuery = null;
        cachedItems = null;
        hue = 300.0f;
        sat = 0.55f;
        val = 0.72f;
    }

    private long countValue(String string, long l) {
        Long l2 = this.counterStarts.get(string);
        if (l2 == null) {
            return l;
        }
        double d = Math.max(0.0, Math.min(1.0, (double)(System.nanoTime() - l2) / 3.5E8));
        if (d >= 1.0) {
            this.counterStarts.remove(string);
            return l;
        }
        return Math.round((double)l * (1.0 - Math.pow(1.0 - d, 3.0)));
    }

    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (tab == 7 && this.embeddedSettings != null && this.embeddedSettings.contains(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            return this.embeddedSettings.mouseClicked(mouseButtonEvent, bl);
        }
        if (mouseButtonEvent.button() == 0) {
            for (Map.Entry<String, int[]> entry : this.counterBounds.entrySet()) {
                int[] nArray = entry.getValue();
                if (!(mouseButtonEvent.x() >= (double)nArray[0]) || !(mouseButtonEvent.x() < (double)(nArray[0] + nArray[2])) || !(mouseButtonEvent.y() >= (double)nArray[1]) || !(mouseButtonEvent.y() < (double)(nArray[1] + nArray[3]))) continue;
                this.counterStarts.put(entry.getKey(), System.nanoTime());
                return true;
            }
        }
        return super.mouseClicked(mouseButtonEvent, bl);
    }

    private static void applyPalette() {
        PURPLE = -9675545;
        PURPLE_LIGHT = -7570714;
    }

    private static int onAccent() {
        int n = PURPLE >> 16 & 0xFF;
        int n2 = PURPLE >> 8 & 0xFF;
        int n3 = PURPLE & 0xFF;
        return (n * 299 + n2 * 587 + n3 * 114) / 1000 > 170 ? -15658735 : -1;
    }

    public OverviewScreen() {
        super((Component)Component.literal((String)"DonutGambleTracker"));
    }

    public static void openTab(int n) {
        tab = Math.max(0, Math.min(TAB_LABELS.length - 1, n));
        scroll = 0;
        wlPlayer = null;
        OverviewScreen.reveal();
    }

    public boolean isPauseScreen() {
        return false;
    }

    private long now() {
        return paused ? frozenAt : System.currentTimeMillis();
    }

    private long since() {
        long l = RANGES[range];
        if (l < 0L) {
            return PaymentLedger.isSessionActive() ? PaymentLedger.sessionStartTime() : this.now() + 1L;
        }
        return l == 0L ? 0L : this.now() - l;
    }

    private static Component c(String string, boolean bl) {
        return Component.literal((String)string).withStyle(bl ? INTER_BOLD : INTER);
    }

    private void text(GuiGraphicsExtractor guiGraphicsExtractor, String string, int n, int n2, int n3, float f, boolean bl) {
        Fonts.draw(guiGraphicsExtractor, this.font, string, n, n2, n3, f * 1.25f, bl);
    }

    private int w(String string, float f) {
        return Fonts.width(this.font, string, f * 1.25f, false);
    }

    private int wb(String string, float f) {
        return Fonts.width(this.font, string, f * 1.25f, true);
    }

    private int caps(GuiGraphicsExtractor guiGraphicsExtractor, String string, int n, int n2, int n3) {
        int n4 = n;
        char[] cArray = string.toCharArray();
        int n5 = cArray.length;
        int n6 = 0;
        while (n6 < n5) {
            char c = cArray[n6];
            String string2 = String.valueOf(c);
            this.text(guiGraphicsExtractor, string2, n4, n2, n3, 0.76f, true);
            n4 += this.wb(string2, 0.76f);
            ++n6;
        }
        return n4 - n;
    }

    private int capsW(String string) {
        int n = 0;
        char[] cArray = string.toCharArray();
        int n2 = cArray.length;
        int n3 = 0;
        while (n3 < n2) {
            char c = cArray[n3];
            n += this.wb(String.valueOf(c), 0.76f);
            ++n3;
        }
        return n;
    }

    private void togglePause() {
        boolean bl = paused = !paused;
        if (paused) {
            frozenAt = System.currentTimeMillis();
        }
    }

    private static void reveal() {
        revealAt = System.currentTimeMillis();
    }

    void refreshDashboardLayout() {
        this.rebuildWidgets();
    }

    void closeEmbeddedSettings() {
        this.go(5);
    }

    private void go(int n) {
        this.embeddedSettings = null;
        tab = n;
        scroll = 0;
        wlPlayer = null;
        this.bindingKey = false;
        OverviewScreen.reveal();
        this.rebuildWidgets();
    }

    public boolean charTyped(CharacterEvent characterEvent) {
        if (tab == 7 && this.embeddedSettings != null) {
            return this.embeddedSettings.charTyped(characterEvent);
        }
        return super.charTyped(characterEvent);
    }

    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double d2) {
        if (tab == 7 && this.embeddedSettings != null) {
            return this.embeddedSettings.mouseDragged(mouseButtonEvent, d, d2);
        }
        return super.mouseDragged(mouseButtonEvent, d, d2);
    }

    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        if (tab == 7 && this.embeddedSettings != null) {
            return this.embeddedSettings.mouseReleased(mouseButtonEvent);
        }
        return super.mouseReleased(mouseButtonEvent);
    }

    public boolean keyPressed(KeyEvent keyEvent) {
        if (tab == 7 && this.embeddedSettings != null) {
            if (keyEvent.key() == 256) {
                this.go(5);
                return true;
            }
            return this.embeddedSettings.keyPressed(keyEvent);
        }
        int n = keyEvent.key();
        if (this.bindingKey) {
            KeyMapping keyMapping = DashboardClient.streamerKey;
            if (keyMapping != null && n != 256) {
                boolean bl = n == 259 || n == 261;
                keyMapping.setKey(bl ? InputConstants.UNKNOWN : InputConstants.Type.KEYSYM.getOrCreate(n));
                KeyMapping.resetMapping();
                if (this.minecraft != null) {
                    this.minecraft.options.save();
                }
            }
            this.bindingKey = false;
            return true;
        }
        EditBox[] editBoxArray = new EditBox[]{this.searchBox, this.itemSearch, this.nameBox, this.qtyBox, this.secBox, this.hexBox};
        int n2 = editBoxArray.length;
        int n3 = 0;
        while (n3 < n2) {
            EditBox editBox = editBoxArray[n3];
            if (editBox != null && editBox.isFocused() && n != 256) {
                return super.keyPressed(keyEvent);
            }
            ++n3;
        }
        if (n >= 49 && n <= 53) {
            range = n - 49;
            return true;
        }
        if (n == 32) {
            this.togglePause();
            return true;
        }
        if (n == 258) {
            this.go((tab + 1) % TAB_LABELS.length);
            return true;
        }
        return super.keyPressed(keyEvent);
    }

    public boolean mouseScrolled(double d, double d2, double d3, double d4) {
        scroll = Math.max(0, scroll - (int)Math.signum(d4) * 3);
        if (tab == 1 && wlPlayer == null && playersView == 0 || tab == 4) {
            this.rebuildWidgets();
        }
        return true;
    }

    private int small(String string) {
        return this.w(string, 0.75f) + 12;
    }

    private Button btn(String string, int n, int n2, int n3, Runnable runnable) {
        return (Button)this.addWidget(Button.builder((Component)Component.literal((String)string), button -> runnable.run()).bounds(n, n2, n3, 16).build());
    }

    private int contentY() {
        return this.fy + 38;
    }

    private int contentBottom() {
        return this.fy + this.fh - 22;
    }

    protected void init() {
        int n;
        int n2;
        if (Settings.S.slowSlideDown) {
            OverviewScreen.reveal();
        }
        OverviewScreen.applyPalette();
        int n3 = Math.max(6, this.width / 70);
        int n4 = Math.max(5, this.height / 60);
        this.ow = this.width - n3 * 2;
        this.fh = this.height - n4 * 2;
        this.ox = n3;
        this.fy = n4;
        this.side = Math.max(84, Math.min(124, this.ow / 7));
        this.fx = this.ox + this.side;
        this.fw = this.ow - this.side;
        this.rangeButtons.clear();
        this.tabButtons.clear();
        this.filterButtons.clear();
        this.viewButtons.clear();
        this.streamButtons.clear();
        this.sessionButton = null;
        this.loadMoreBtn = null;
        this.searchBox = null;
        this.wlRows.clear();
        this.wlBack = null;
        this.itemSearch = null;
        this.nameBox = null;
        this.qtyBox = null;
        this.secBox = null;
        this.hexBox = null;
        this.auctionButtons.clear();
        int n5 = Math.min(22, Math.max(16, (this.fh - 140) / 5));
        int n6 = 0;
        while (n6 < TAB_LABELS.length) {
            int n7 = n6;
            Button button = this.btn("", this.ox + 6, this.fy + 36 + n6 * n5, this.side - 12, () -> this.go(n7));
            if (n6 == 5) {
                button.setY(this.fy + this.fh - 74);
            }
            if (n6 == 7) {
                button.visible = false;
            }
            if (n6 == 6) {
                button.setY(this.fy + this.fh - 96);
            }
            button.setHeight(18);
            this.tabButtons.add(button);
            ++n6;
        }
        this.headerSettings = this.btn("", this.ox + 6, this.fy + this.fh - 52, this.side - 12, () -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen((Screen)new SettingsScreen(this));
            }
        });
        this.headerSettings.setHeight(18);
        n6 = this.wb("RESET SESSION", 0.7f) + 26;
        this.sessionButton = this.btn("", this.fx + this.fw - 10 - n6, this.fy + 5, n6, this::onSession);
        int n8 = this.sessionButton.getX() - 8;
        int n9 = RANGE_LABELS.length - 1;
        while (n9 >= 0) {
            n2 = n9;
            n = this.wb(RANGE_LABELS[n9], 0.7f) + 12;
            final int pick = n9;
            this.rangeButtons.add(0, this.btn(RANGE_LABELS[n9], n8 - n, this.fy + 5, n, () -> {
                range = pick;
            }));
            n8 -= n + 1;
            --n9;
        }
        if (tab == 0) {
            int[] nArray = this.feedBox();
            int n10 = nArray[0] + nArray[2] - 8;
            String[] stringArray = new String[]{"OUT", "IN", "ALL"};
            n = 0;
            while (n < 3) {
                int n11 = 2 - n;
                int n12 = this.small(stringArray[n]);
                this.filterButtons.add(0, this.btn(stringArray[n], n10 - n12, nArray[1] + 6, n12, () -> {
                    filter = n11;
                }));
                n10 -= n12 + 3;
                ++n;
            }
        } else if (tab == 1 && wlPlayer != null) {
            n9 = this.w("All players", 0.8f) + 26;
            this.wlBack = this.btn("All players", this.fx + 10, this.contentY() + 4, n9, () -> {
                wlPlayer = null;
                scroll = 0;
                OverviewScreen.reveal();
                this.rebuildWidgets();
            });
            this.wlBack.setHeight(18);
        } else if (tab == 1) {
            int n13;
            String[] stringArray = new String[]{"RAKEBACK", "ALL PLAYERS"};
            int n14 = this.fx + this.fw - 10;
            n2 = 0;
            while (n2 < 2) {
                n = 1 - n2;
                n13 = this.wb(stringArray[n2], 0.7f) + 16;
                final int view = n;
                this.viewButtons.add(0, this.btn(stringArray[n2], n14 - n13, this.contentY() + 6, n13, () -> {
                    if (playersView != view) {
                        playersView = view;
                        scroll = 0;
                        OverviewScreen.reveal();
                        this.rebuildWidgets();
                    }
                }));
                n14 -= n13 + 1;
                ++n2;
            }
            n2 = Math.min(170, this.fw / 4);
            n = this.fx + this.fw - 10 - n2;
            n13 = this.contentY() + 30 + 8;
            this.searchBox = new EditBox(this.font, -4000, -4000, 200, 12, (Component)Component.literal((String)"Search"));
            this.searchBox.setMaxLength(32);
            this.searchBox.setValue(search);
            this.searchBox.setResponder(string -> {
                search = string;
                scroll = 0;
            });
            this.addWidget(this.searchBox);
            Button button = this.btn("", n, n13, n2, () -> {
                this.pendingFocus = this.searchBox;
            });
            button.setHeight(20);
            if (playersView == 0) {
                this.addWinLossRowButtons();
            }
        } else if (tab == 2) {
            this.initPayments();
        } else if (tab == 3) {
            this.initAuctions();
        } else if (tab == 4) {
            this.initStreamer();
        } else if (tab == 5) {
            this.initGeneral();
        } else if (tab == 7) {
            this.embeddedSettings = new SettingsScreen(this);
            n9 = Math.min(520, this.fw - 20);
            n2 = Math.min(300, this.fh - 46);
            if (Settings.S.dashboardFullscreen) {
                this.embeddedSettings.mount(this.width, this.height, 0, 0, this.width, this.height);
            } else {
                this.embeddedSettings.mount(this.width, this.height, this.fx + (this.fw - n9) / 2, this.fy + 30 + (this.fh - 46 - n2) / 2, n9, n2);
            }
        } else if (tab == 6) {
            this.overlayEditorButton = this.btn("", this.fx + 22, this.contentY() + 72, 170, () -> this.minecraft.setScreen((Screen)new OverlayEditorScreen(this)));
            this.overlayEditorButton.setHeight(23);
        }
    }

    private void drawOverlay(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        this.pageTitle(guiGraphicsExtractor, "Overlay", "Move and resize your HUD panels in one place.");
        int n3 = this.contentY() + 44;
        this.box(guiGraphicsExtractor, this.fx + 10, n3, this.fw - 20, 100, -15526625, -14934485);
        this.caps(guiGraphicsExtractor, "HUD LAYOUT EDITOR", this.fx + 22, n3 + 10, -1);
        this.segment(guiGraphicsExtractor, this.overlayEditorButton, "OPEN EDITOR", true, n, n2);
        this.text(guiGraphicsExtractor, this.fitText("Drag a panel to move it. Drag its corner to resize.", this.fw - 44, 0.8f, false), this.fx + 22, n3 + 62, -7696482, 0.8f, false);
        this.text(guiGraphicsExtractor, this.fitText("Size buttons, optional snapping, and reset for each panel.", this.fw - 44, 0.75f, false), this.fx + 22, n3 + 78, -9538685, 0.75f, false);
        if (n3 + 129 <= this.contentBottom()) {
            this.text(guiGraphicsExtractor, this.fitText("Dashboard: Profit HUD, Auction, Win streak, Payment popup", this.fw - 44, 0.8f, true), this.fx + 22, n3 + 120, -1, 0.8f, true);
        }
        if (n3 + 149 <= this.contentBottom()) {
            this.text(guiGraphicsExtractor, this.fitText("Balance HUD: Balance, Profit tracker, Rakeback, Auction log", this.fw - 44, 0.8f, true), this.fx + 22, n3 + 140, -1, 0.8f, true);
        }
        if (n3 + 171 <= this.contentBottom()) {
            this.text(guiGraphicsExtractor, this.fitText("Empty panels have placeholders. Tracking data stays unchanged.", this.fw - 44, 0.75f, false), this.fx + 22, n3 + 162, -7696482, 0.75f, false);
        }
    }

    // TEST: arrow picker row in the Streamer tab = Balance HUD outline color.
    private static int backgroundIndex() { return Math.floorMod(Panels.balBorder, Panels.BORDER_NAMES.length); }

    private static void setBackground(int i) {
        Panels.balBorder = Math.floorMod(i, Panels.BORDER_NAMES.length);
        Panels.save();
    }

    private static String backgroundName(int i) { return Panels.BORDER_NAMES[i]; }

    private void initGeneral() {
        int n = this.contentY() + 48;
        this.smoothHigh = this.btn("", this.fx + this.fw - 124, n + 9, 50, () -> {
            Settings.S.highSmoothness = true;
            Settings.save();
        });
        this.smoothLight = this.btn("", this.fx + this.fw - 70, n + 9, 50, () -> {
            Settings.S.highSmoothness = false;
            Settings.save();
        });
        this.smoothHigh.setHeight(20);
        this.smoothLight.setHeight(20);
        this.slowSlideOn = this.btn("", this.fx + this.fw - 124, n + 57, 50, () -> {
            Settings.S.slowSlideDown = true;
            Settings.save();
            OverviewScreen.reveal();
        });
        this.slowSlideOff = this.btn("", this.fx + this.fw - 70, n + 57, 50, () -> {
            Settings.S.slowSlideDown = false;
            Settings.save();
        });
        this.slowSlideOn.setHeight(20);
        this.slowSlideOff.setHeight(20);
    }

    private void drawGeneral(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        this.pageTitle(guiGraphicsExtractor, "General", "Dashboard appearance and performance");
        int n3 = this.contentY() + 48;
        this.box(guiGraphicsExtractor, this.fx + 10, n3, this.fw - 20, 40, -15526625, -14934485);
        this.caps(guiGraphicsExtractor, "SMOOTHNESS", this.fx + 22, n3 + 8, -1);
        String string = Settings.S.highSmoothness ? "High: animated scenery and decorative effects." : "Light: still scenery, decorative animation off.";
        this.text(guiGraphicsExtractor, this.fitText(string, Math.max(40, this.fw - 180), 0.65f, false), this.fx + 22, n3 + 25, -7696482, 0.65f, false);
        this.segment(guiGraphicsExtractor, this.smoothHigh, "HIGH", Settings.S.highSmoothness, n, n2);
        this.segment(guiGraphicsExtractor, this.smoothLight, "LIGHT", !Settings.S.highSmoothness, n, n2);
        int n6 = n3 + 48;   // background picker moved to the Streamer tab
        this.box(guiGraphicsExtractor, this.fx + 10, n6, this.fw - 20, 40, -15526625, -14934485);
        this.caps(guiGraphicsExtractor, "SLOW SLIDE-DOWN", this.fx + 22, n6 + 8, -1);
        this.text(guiGraphicsExtractor, this.fitText("Gently lower content when opening or switching tabs.", Math.max(40, this.fw - 180), 0.65f, false), this.fx + 22, n6 + 25, -7696482, 0.65f, false);
        this.segment(guiGraphicsExtractor, this.slowSlideOn, "ON", Settings.S.slowSlideDown, n, n2);
        this.segment(guiGraphicsExtractor, this.slowSlideOff, "OFF", !Settings.S.slowSlideDown, n, n2);
    }

    private void onSession() {
        long l = System.currentTimeMillis();
        if (PaymentLedger.isSessionActive()) {
            if (l > this.confirmUntil) {
                this.confirmUntil = l + 3000L;
                return;
            }
            this.confirmUntil = 0L;
            PaymentLedger.endSession();
        }
        PaymentLedger.startSession();
        OverviewScreen.reveal();
        this.rebuildWidgets();
    }

    private PreviewPlayer preview() {
        if (previewPlayer == null || previewLevel != this.minecraft.level) {
            previewPlayer = new PreviewPlayer(this.minecraft.level, this.minecraft.player.getGameProfile());
            previewLevel = this.minecraft.level;
        }
        previewPlayer.refreshParts();
        return previewPlayer;
    }

    private List<PaymentLedger.Entry> winLossList() {
        ArrayList<PaymentLedger.Entry> arrayList = new ArrayList<PaymentLedger.Entry>();
        String string = search.trim().toLowerCase(Locale.US);
        for (PaymentLedger.Entry entry : PaymentLedger.entries(false)) {
            if (!string.isEmpty() && !entry.name.toLowerCase(Locale.US).contains(string)) continue;
            arrayList.add(entry);
        }
        arrayList.sort(Comparator.comparingLong(PaymentLedger.Entry::net).reversed());
        return arrayList;
    }

    private int wlTableY() {
        return this.contentY() + 30 + 44;
    }

    private int wlRowsY() {
        return this.wlTableY() + 8 + 19;
    }

    private int wlVisible() {
        return Math.max(1, (this.contentBottom() - 4 - this.wlRowsY()) / 21);
    }

    private void addWinLossRowButtons() {
        this.wlRows.clear();
        List<PaymentLedger.Entry> list = this.winLossList();
        int n = this.fx + 10;
        int n2 = this.fw - 20;
        scroll = Math.max(0, Math.min(scroll, Math.max(0, list.size() - this.wlVisible())));
        int n3 = 0;
        while (n3 < this.wlVisible() && scroll + n3 < list.size()) {
            String string = list.get((int)(OverviewScreen.scroll + n3)).name;
            Button button = this.btn("", n + 4, this.wlRowsY() + n3 * 21 - 3, n2 - 12, () -> {
                wlPlayer = string;
                scroll = 0;
                OverviewScreen.reveal();
                this.rebuildWidgets();
            });
            button.setHeight(21);
            this.wlRows.put(button, string);
            ++n3;
        }
    }

    public void onClose() {
        if (tab == 1 && wlPlayer != null) {
            wlPlayer = null;
            scroll = 0;
            OverviewScreen.reveal();
            this.rebuildWidgets();
            return;
        }
        super.onClose();
    }

    private boolean shortScreen() {
        return this.contentBottom() - this.contentY() < 380;
    }

    private int bottomH() {
        return this.shortScreen() ? 80 : 98;
    }

    private int welcomeY() {
        return this.contentY();
    }

    private int cardsY() {
        return this.welcomeY() + (this.shortScreen() ? 42 : 50);
    }

    private int cardsH() {
        return this.shortScreen() ? 74 : 88;
    }

    private int bottomY() {
        return this.contentBottom() - this.bottomH();
    }

    private int[] feedBox() {
        int n = this.cardsY() + this.cardsH() + 8;
        int n2 = Math.max(70, this.bottomY() - 8 - n);
        int n3 = (this.fw - 28) * 38 / 100;
        return new int[]{this.fx + 10, n, n3, n2};
    }

    private int[] chartBox() {
        int[] nArray = this.feedBox();
        int n = nArray[0] + nArray[2] + 8;
        return new int[]{n, nArray[1], this.fx + this.fw - 10 - n, nArray[3]};
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f) {
        guiGraphicsExtractor.fill(0, 0, this.width, this.height, -15921644);
        OverviewScreen.round(guiGraphicsExtractor, this.ox, this.fy, this.ow, this.fh, 6, OverviewScreen.alpha(-14934485, 192));
        Scenes.draw(guiGraphicsExtractor, this.fx, this.fy + 27, this.fw - 1, this.fh - 28, 0, 0L);
        guiGraphicsExtractor.fill(this.fx, this.fy + 1, this.fx + this.fw - 1, this.fy + 26, OverviewScreen.alpha(-15526625, 244));
        guiGraphicsExtractor.fill(this.fx, this.fy + 26, this.fx + this.fw - 1, this.fy + 27, -14934485);
        guiGraphicsExtractor.fill(this.fx, this.fy + this.fh - 16, this.fx + this.fw - 1, this.fy + this.fh - 1, OverviewScreen.alpha(-15526625, 216));
        OverviewScreen.round(guiGraphicsExtractor, this.ox + 1, this.fy + 1, this.side, this.fh - 2, 5, -15526625);
        guiGraphicsExtractor.fill(this.ox + this.side, this.fy + 1, this.ox + this.side + 1, this.fy + this.fh - 1, -14934485);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f) {
        this.counterBounds.clear();
        OverviewScreen.applyPalette();
        this.lastMouseX = n;
        this.lastMouseY = n2;
        Object object = this.pendingFocus;
        if (object instanceof EditBox var6_6) {
            this.setFocused((GuiEventListener)var6_6);
            this.pendingFocus = null;
        }
        super.extractRenderState(guiGraphicsExtractor, n, n2, f);
        this.drawSidebar(guiGraphicsExtractor, n, n2);
        this.drawHeader(guiGraphicsExtractor, n, n2);
        if (tab == 7 && this.embeddedSettings != null) {
            this.embeddedSettings.extractRenderState(guiGraphicsExtractor, n, n2, f);
            return;
        }
        float f2 = OverviewScreen.ease(Math.min(1.0f, (float)(System.currentTimeMillis() - revealAt) / (Settings.S.slowSlideDown ? 350.0f : 260.0f)));
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate(0.0f, (1.0f - f2) * (Settings.S.slowSlideDown ? -12.0f : 6.0f));
        switch (tab) {
            case 0: {
                this.drawOverview(guiGraphicsExtractor, n, n2);
                break;
            }
            case 1: {
                if (wlPlayer != null) {
                    this.drawPlayerPage(guiGraphicsExtractor, n, n2);
                    break;
                }
                if (playersView == 0) {
                    this.drawWinLoss(guiGraphicsExtractor, n, n2);
                } else {
                    this.drawRakeback(guiGraphicsExtractor);
                }
                this.drawViewSwitch(guiGraphicsExtractor, n, n2);
                break;
            }
            case 2: {
                this.drawPayments(guiGraphicsExtractor, n, n2);
                break;
            }
            case 3: {
                this.drawAuctions(guiGraphicsExtractor, n, n2);
                break;
            }
            case 4: {
                this.drawStreamer(guiGraphicsExtractor, n, n2);
                break;
            }
            case 5: {
                this.drawGeneral(guiGraphicsExtractor, n, n2);
                break;
            }
            case 7: {
                if (this.embeddedSettings == null) break;
                this.embeddedSettings.extractRenderState(guiGraphicsExtractor, n, n2, f);
                break;
            }
            case 6: {
                this.drawOverlay(guiGraphicsExtractor, n, n2);
            }
        }
        guiGraphicsExtractor.pose().popMatrix();
        if (Settings.S.highSmoothness) {
            Scenes.stringLights(guiGraphicsExtractor, this.fx + 1, this.fy + 26, this.fw - 3, 0.0);   // still lights
        }
        this.revealCurtain(guiGraphicsExtractor);
        this.text(guiGraphicsExtractor, "Display only. Data is read from your chat, nothing is sent to the server.", this.fx + 10, this.fy + this.fh - 11, -9538685, 0.7f, false);
    }

    private static float ease(float f) {
        f = Math.max(0.0f, Math.min(1.0f, f));
        float f2 = 1.0f - f;
        return 1.0f - f2 * f2 * f2;
    }

    private void revealCurtain(GuiGraphicsExtractor guiGraphicsExtractor) {
        int n;
        if (Settings.S.slowSlideDown) {
            return;
        }
        long l = System.currentTimeMillis() - revealAt;
        int n2 = this.contentY();
        int n3 = this.contentBottom();
        int n4 = Math.max(1, (n3 - n2 + 20 - 1) / (n = 20));
        float f = Math.min(18.0f, 120.0f / (float)n4);
        if ((float)l > 260.0f + f * (float)n4) {
            return;
        }
        int n5 = 0;
        while (n5 < n4) {
            float f2 = OverviewScreen.ease(((float)l - (float)n5 * f) / 260.0f);
            if (!(f2 >= 1.0f)) {
                int n6 = n2 + n5 * n;
                int n7 = Math.min(n3, n6 + n);
                guiGraphicsExtractor.fill(this.fx, n6, this.fx + this.fw - 1, n7, OverviewScreen.alpha(-15526625, (int)((1.0f - f2) * 235.0f)));
            }
            ++n5;
        }
    }

    private void drawSidebar(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        int n3 = this.ox + 10;
        OverviewScreen.donutRing(guiGraphicsExtractor, n3 + 6, this.fy + 16);
        this.text(guiGraphicsExtractor, "Snowy's", n3 + 18, this.fy + 9, -1, 0.85f, true);
        this.text(guiGraphicsExtractor, "Dashboard", n3 + 18, this.fy + 19, -9538685, 0.62f, false);
        int n4 = 0;
        while (n4 < this.tabButtons.size()) {
            if (this.tabButtons.get((int)n4).visible) {
                this.sideItem(guiGraphicsExtractor, this.tabButtons.get(n4), TAB_LABELS[n4], n4, n4 == tab, n, n2);
            }
            ++n4;
        }
        if (this.headerSettings != null) {
            this.sideItem(guiGraphicsExtractor, this.headerSettings, "Settings", 7, tab == 7, n, n2);
        }
        String string = this.minecraft != null ? this.minecraft.getUser().getName() : "you";
        String string2 = Settings.S.displayName == null || Settings.S.displayName.isBlank() ? string : Settings.S.displayName;
        int n5 = this.fy + this.fh - 26;
        guiGraphicsExtractor.fill(this.ox + 6, n5 - 4, this.ox + this.side - 6, n5 - 3, -14934485);
        this.face(guiGraphicsExtractor, string, this.ox + 10, n5 + 2, 14);
        Object object = string2;
        while (((String)object).length() > 3 && this.wb((String)object, 0.8f) > this.side - 38) {
            object = ((String)object).substring(0, ((String)object).length() - 2) + ".";
        }
        this.text(guiGraphicsExtractor, (String)object, this.ox + 30, n5 + 5, -1, 0.8f, true);
    }

    private void sideItem(GuiGraphicsExtractor guiGraphicsExtractor, Button button, String string, int n, boolean bl, int n2, int n3) {
        int n4 = button.getX();
        int n5 = button.getY();
        int n6 = button.getWidth();
        int n7 = button.getHeight();
        boolean bl2 = OverviewScreen.over(button, n2, n3);
        if (bl) {
            OverviewScreen.round(guiGraphicsExtractor, n4, n5, n6, n7, 3, OverviewScreen.mixColor(-15526625, PURPLE, 0.22f));
            guiGraphicsExtractor.fill(n4, n5 + 2, n4 + 2, n5 + n7 - 2, PURPLE);
        } else if (bl2) {
            OverviewScreen.round(guiGraphicsExtractor, n4, n5, n6, n7, 3, OverviewScreen.mixColor(-15526625, PURPLE, 0.1f));
        }
        int n8 = bl ? -1 : (bl2 ? -1 : -7696482);
        OverviewScreen.sideIcon(guiGraphicsExtractor, n, n4 + 8, n5 + n7 / 2, bl ? PURPLE_LIGHT : n8);
        this.text(guiGraphicsExtractor, string, n4 + 22, n5 + (n7 - 7) / 2, n8, 0.8f, bl);
    }

    private static void sideIcon(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4) {
        int n5 = n3 - 4;
        switch (n) {
            case 0: {
                guiGraphicsExtractor.fill(n2, n5, n2 + 3, n5 + 3, n4);
                guiGraphicsExtractor.fill(n2 + 5, n5, n2 + 8, n5 + 3, n4);
                guiGraphicsExtractor.fill(n2, n5 + 5, n2 + 3, n5 + 8, n4);
                guiGraphicsExtractor.fill(n2 + 5, n5 + 5, n2 + 8, n5 + 8, n4);
                break;
            }
            case 1: {
                guiGraphicsExtractor.fill(n2 + 2, n5, n2 + 6, n5 + 4, n4);
                guiGraphicsExtractor.fill(n2, n5 + 5, n2 + 8, n5 + 8, n4);
                break;
            }
            case 2: {
                guiGraphicsExtractor.fill(n2, n5 + 1, n2 + 8, n5 + 2, n4);
                guiGraphicsExtractor.fill(n2 + 6, n5, n2 + 7, n5 + 3, n4);
                guiGraphicsExtractor.fill(n2, n5 + 6, n2 + 8, n5 + 7, n4);
                guiGraphicsExtractor.fill(n2 + 1, n5 + 5, n2 + 2, n5 + 8, n4);
                break;
            }
            case 3: {
                guiGraphicsExtractor.fill(n2 + 3, n5, n2 + 8, n5 + 3, n4);
                guiGraphicsExtractor.fill(n2 + 1, n5 + 3, n2 + 4, n5 + 6, n4);
                guiGraphicsExtractor.fill(n2, n5 + 7, n2 + 8, n5 + 8, n4);
                break;
            }
            case 4: {
                guiGraphicsExtractor.fill(n2, n5, n2 + 8, n5 + 1, n4);
                guiGraphicsExtractor.fill(n2, n5 + 5, n2 + 8, n5 + 6, n4);
                guiGraphicsExtractor.fill(n2, n5, n2 + 1, n5 + 6, n4);
                guiGraphicsExtractor.fill(n2 + 7, n5, n2 + 8, n5 + 6, n4);
                guiGraphicsExtractor.fill(n2 + 3, n5 + 6, n2 + 5, n5 + 8, n4);
                break;
            }
            default: {
                guiGraphicsExtractor.fill(n2 + 2, n5 + 2, n2 + 6, n5 + 6, n4);
                guiGraphicsExtractor.fill(n2 + 3, n5, n2 + 5, n5 + 8, n4);
                guiGraphicsExtractor.fill(n2, n5 + 3, n2 + 8, n5 + 5, n4);
                guiGraphicsExtractor.fill(n2 + 3, n5 + 3, n2 + 5, n5 + 5, -15526625);
            }
        }
    }

    private void drawHeader(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        boolean bl;
        int n3;
        int n4 = this.fx + 12;
        this.caps(guiGraphicsExtractor, TAB_LABELS[tab].toUpperCase(Locale.US), n4, this.fy + 10, -1);
        if (paused) {
            n3 = n4 + this.capsW(TAB_LABELS[tab].toUpperCase(Locale.US)) + 10;
            guiGraphicsExtractor.fill(n3, this.fy + 11, n3 + 4, this.fy + 15, -2056144);
            this.caps(guiGraphicsExtractor, "PAUSED", n3 + 8, this.fy + 10, -2056144);
        }
        this.groupBar(guiGraphicsExtractor, this.rangeButtons);
        n3 = 0;
        while (n3 < this.rangeButtons.size()) {
            Button button = this.rangeButtons.get(n3);
            bl = n3 == range;
            this.cover(guiGraphicsExtractor, button, -15526625);
            OverviewScreen.round(guiGraphicsExtractor, button.getX(), button.getY(), button.getWidth(), button.getHeight(), 2, bl ? PURPLE : (OverviewScreen.over(button, n, n2) ? OverviewScreen.mixColor(-15526625, PURPLE, 0.35f) : -15526625));
            String string = button.getMessage().getString();
            this.text(guiGraphicsExtractor, string, button.getX() + (button.getWidth() - this.wb(string, 0.7f)) / 2, button.getY() + (button.getHeight() - 7) / 2, bl ? OverviewScreen.onAccent() : -7696482, 0.7f, true);
            ++n3;
        }
        if (this.sessionButton != null) {
            Button button = this.sessionButton;
            boolean bl2 = PaymentLedger.isSessionActive();
            bl = this.confirmUntil > System.currentTimeMillis();
            boolean bl3 = OverviewScreen.over(button, n, n2);
            int n5 = button.getX();
            int n6 = button.getY();
            int n7 = button.getWidth();
            int n8 = button.getHeight();
            this.box(guiGraphicsExtractor, n5, n6, n7, n8, bl3 ? -14020842 : -15070190, bl ? -41385 : -8769990);
            String string = bl ? "CLICK AGAIN" : (bl2 ? "RESET SESSION" : "START SESSION");
            OverviewScreen.refreshIcon(guiGraphicsExtractor, n5 + 8, n6 + n8 / 2, bl ? -41385 : -30054);
            this.text(guiGraphicsExtractor, string, n5 + 17, n6 + (n8 - 7) / 2, bl ? -41385 : -30054, 0.7f, true);
        }
    }

    private static void refreshIcon(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3) {
        int n4 = 40;
        while (n4 < 330) {
            double d = Math.toRadians(n4);
            int n5 = n + (int)Math.round(Math.sin(d) * 3.0);
            int n6 = n2 - (int)Math.round(Math.cos(d) * 3.0);
            guiGraphicsExtractor.fill(n5, n6, n5 + 1, n6 + 1, n3);
            n4 += 18;
        }
        guiGraphicsExtractor.fill(n - 1, n2 - 4, n + 2, n2 - 3, n3);
        guiGraphicsExtractor.fill(n + 1, n2 - 5, n + 2, n2 - 2, n3);
    }

    private static long daysToChristmas() {
        LocalDate localDate;
        LocalDate localDate2 = LocalDate.now();
        if (localDate2.isAfter(localDate = LocalDate.of(localDate2.getYear(), 12, 25))) {
            localDate = localDate.plusYears(1L);
        }
        return ChronoUnit.DAYS.between(localDate2, localDate);
    }

    private void drawOverview(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        boolean bl;
        int n42;
        int n52;
        String string = this.minecraft != null ? this.minecraft.getUser().getName() : "you";
        int n62 = this.welcomeY();
        this.box(guiGraphicsExtractor, this.fx + 10, n62 - 8, this.fw - 20, this.cardsY() + 6 - (n62 - 8), -15526625, -14934485);
        OverviewScreen.topGlow(guiGraphicsExtractor, this.fx + 10, n62 - 8, this.fw - 20, PURPLE);
        if (this.minecraft != null && this.minecraft.player != null && Settings.S.skinFaces) {
            n52 = this.fx + 14;
            int n72 = n62 - 6;
            int n8 = this.fx + 98;
            int n9 = this.cardsY() + (this.shortScreen() ? 44 : 52);
            float f = (float)(n52 + n8) / 2.0f;
            float f2 = (float)(n72 + n9) / 2.0f;
            double d = (double)System.currentTimeMillis() / 1000.0;
            double d2 = 5.0;
            double d3 = d % d2;
            n42 = (long)(d / d2) % 2L == 0L ? 1 : -1;
            double d4 = d3 - 3.0;
            double d5 = d4 < 0.0 ? 0.0 : (d4 < 0.3 ? d4 / 0.3 : (d4 < 1.7 ? 1.0 : Math.max(0.0, (2.0 - d4) / 0.3)));
            d5 = d5 * d5 * (3.0 - 2.0 * d5);
            float f3 = f + (float)((double)(n42 * 32) * d5);
            InventoryScreen.extractEntityInInventoryFollowsMouse((GuiGraphicsExtractor)guiGraphicsExtractor, (int)n52, (int)n72, (int)n8, (int)n9, (int)(this.shortScreen() ? 44 : 52), (float)0.0625f, (float)f3, (float)f2, (LivingEntity)this.preview());
        } else {
            this.face(guiGraphicsExtractor, string, this.fx + 16, n62 + 4, 30);
        }
        n52 = this.fx + 104;
        this.text(guiGraphicsExtractor, "Welcome, ", n52, n62 + 8, -1, 1.6f, true);
        String string2 = Settings.S.displayName == null || Settings.S.displayName.isBlank() ? string : Settings.S.displayName;
        this.glowText(guiGraphicsExtractor, string2, n52 + this.wb("Welcome, ", 1.6f), n62 + 8, PURPLE_LIGHT, 1.6f);
        this.text(guiGraphicsExtractor, "Have a great time on DonutSMP!", n52, n62 + 30, -7696482, 0.75f, false);
        List<PaymentLedger.Payment> list = this.rangePayments();
        long l = 0L;
        long[] lArray2 = new long[list.size() + 1];
        int n10 = 0;
        while (n10 < list.size()) {
            lArray2[n10 + 1] = l += list.get((int)n10).paidYou - list.get((int)n10).youPaid;
            ++n10;
        }
        n10 = this.cardsY();
        int n11 = this.cardsH();
        int n12 = 8;
        int n13 = (this.fw - 20 - n12 * 3) / 4;
        int n14 = this.fx + 10;
        int n15 = n14 + n13 + n12;
        n42 = n15 + n13 + n12;
        int n16 = n42 + n13 + n12;
        List<long[]> list2 = BalanceTimeline.window(PaymentLedger.balanceSince(0L), this.since(), System.currentTimeMillis());
        long[] lArray3 = new long[list2.size()];
        int n17 = 0;
        while (n17 < list2.size()) {
            lArray3[n17] = list2.get(n17)[1];
            ++n17;
        }
        long l2 = PaymentLedger.balance();
        boolean bl2 = bl = lArray3.length > 1 && lArray3[lArray3.length - 1] < lArray3[0];
        String string3 = l2 < 0L ? "type /bal to load" : (lArray3.length > 1 ? (bl ? "\u2193 " : "\u2191 ") + OverviewScreen.signedMoney(lArray3[lArray3.length - 1] - lArray3[0]) + " " + this.rangeWord() : "from /bal and the Balance HUD");
        this.card(guiGraphicsExtractor, n14, n10, n13, n11, "BALANCE (EST.)", l2 < 0L ? "$---" : Money.format(l2), string3, -932849, -1, bl ? -41385 : -13710223, lArray3);
        int n18 = switch (range) {
            case 0, 1 -> 1;
            case 2 -> 7;
            default -> 30;
        };
        long[] lArray4 = PaymentLedger.playLastDays(Math.max(7, n18));
        long l3 = 0L;
        int n19 = Math.max(0, lArray4.length - n18);
        while (n19 < lArray4.length) {
            l3 += lArray4[n19];
            ++n19;
        }
        String string4 = "+" + l3 / 3600000L + "h " + l3 % 3600000L / 60000L + "m " + this.rangeWord();
        this.card(guiGraphicsExtractor, n15, n10, n13, n11, "PLAYTIME", PaymentLedger.playTotal() / 3600000L + "h", string4, -8680193, -1, -7696482, lArray4);
        String string5 = list.isEmpty() ? "no payments yet" : "since " + OverviewScreen.dayTime(list.get((int)0).ts);
        int n20 = l >= 0L ? -13710223 : -41385;
        this.card(guiGraphicsExtractor, n42, n10, n13, n11, RANGE_LABELS[range] + " PROFIT", OverviewScreen.signedMoney(l), string5, n20, n20, -7696482, lArray2);
        long l4 = 0L;
        for (PaymentLedger.Entry object52 : PaymentLedger.entries(false)) {
            l4 += object52.paidYou;
        }
        this.card(guiGraphicsExtractor, n16, n10, n13, n11, "VOLUME", Money.format(l4), "all time paid to you", -13773633, -1, -7696482, null);
        int[] nArray = this.feedBox();
        this.box(guiGraphicsExtractor, nArray[0], nArray[1], nArray[2], nArray[3], -15526625, -14934485);
        OverviewScreen.topGlow(guiGraphicsExtractor, nArray[0], nArray[1], nArray[2], PURPLE);
        this.caps(guiGraphicsExtractor, "PAYMENT FEED", nArray[0] + 10, nArray[1] + 10, -1);
        this.groupBar(guiGraphicsExtractor, this.filterButtons);
        int n21 = 0;
        while (n21 < this.filterButtons.size()) {
            this.pill(guiGraphicsExtractor, this.filterButtons.get(n21), n21 == filter, n, n2);
            ++n21;
        }
        ArrayList<PaymentLedger.Payment> arrayList = new ArrayList<PaymentLedger.Payment>();
        int n8 = list.size() - 1;
        while (n8 >= 0) {
            PaymentLedger.Payment n23 = list.get(n8);
            int n9 = n7 = n23.paidYou > 0L ? 1 : 0;
            if (filter == 0 || filter == 1 && n7 != 0 || filter == 2 && n7 == 0) {
                arrayList.add(n23);
            }
            --n8;
        }
        n8 = 25;
        n7 = nArray[1] + 28;
        int n22 = Math.max(0, (nArray[1] + nArray[3] - 4 - n7) / n8);
        scroll = Math.max(0, Math.min(scroll, Math.max(0, arrayList.size() - n22)));
        this.scrollbar(guiGraphicsExtractor, nArray[0] + nArray[2] - 5, n7 - 2, nArray[1] + nArray[3] - 4 - n7, n22, arrayList.size());
        int nArray2 = 0;
        while (nArray2 < Math.min(n22, arrayList.size() - scroll)) {
            PaymentLedger.Payment n25 = (PaymentLedger.Payment)arrayList.get(scroll + nArray2);
            boolean string7 = n25.paidYou > 0L;
            int string8 = n7 + nArray2 * n8;
            if (nArray2 > 0) {
                guiGraphicsExtractor.fill(nArray[0] + 8, string8 - 3, nArray[0] + nArray[2] - 10, string8 - 2, OverviewScreen.alpha(-14934485, 200));
            }
            this.text(guiGraphicsExtractor, OverviewScreen.clock(n25.ts), nArray[0] + 10, string8 + 6, -9538685, 0.66f, false);
            n6 = nArray[0] + 10 + this.w("00:00 pm", 0.66f) + 8;
            this.face(guiGraphicsExtractor, n25.name, n6, string8 + 3, 12);
            String n26 = OverviewScreen.signedMoney(string7 ? n25.paidYou : -n25.youPaid);
            n5 = this.wb(n26, 0.8f);
            n4 = nArray[0] + nArray[2] - 12 - n5;
            n3 = n6 + 17;
            int lArray5 = n4 - 8 - n3;
            this.text(guiGraphicsExtractor, this.fitText(n25.name, lArray5, 0.8f, true), n3, string8 + 1, -1, 0.8f, true);
            this.text(guiGraphicsExtractor, this.fitText(string7 ? "paid you" : "you paid", lArray5, 0.66f, false), n3, string8 + 12, -9538685, 0.66f, false);
            this.text(guiGraphicsExtractor, n26, n4, string8 + 6, string7 ? -13710223 : -41385, 0.8f, true);
            ++nArray2;
        }
        if (arrayList.isEmpty()) {
            this.text(guiGraphicsExtractor, "No payments in this range", nArray[0] + 10, n7 + 2, -9538685, 0.85f, false);
        }
        int[] nArray3 = this.chartBox();
        this.box(guiGraphicsExtractor, nArray3[0], nArray3[1], nArray3[2], nArray3[3], -15526625, -14934485);
        OverviewScreen.topGlow(guiGraphicsExtractor, nArray3[0], nArray3[1], nArray3[2], PURPLE);
        int n23 = this.caps(guiGraphicsExtractor, "BALANCE", nArray3[0] + 12, nArray3[1] + 10, -1);
        this.text(guiGraphicsExtractor, "hover graph to inspect", nArray3[0] + 12, nArray3[1] + 23, -9538685, 0.6f, false);
        String string6 = switch (range) {
            case 0 -> "this session";
            case 1 -> "last 24 hours";
            case 2 -> "last 7 days";
            case 3 -> "last 30 days";
            default -> "all time";
        };
        String string7 = l2 < 0L ? "$---" : Money.format(l2);
        n6 = nArray3[0] + nArray3[2] - 12 - this.wb(string7, 1.4f);
        if (nArray3[0] + 20 + n23 + this.w(string6, 0.66f) + 12 < n6) {
            this.text(guiGraphicsExtractor, string6, nArray3[0] + 20 + n23, nArray3[1] + 11, -9538685, 0.66f, false);
        }
        this.text(guiGraphicsExtractor, string7, n6, nArray3[1] + 8, -1, 1.4f, true);
        int n24 = nArray3[0] + 12;
        n5 = nArray3[1] + 34;
        n4 = nArray3[2] - 24;
        n3 = nArray3[3] - 46;
        if (lArray3.length < 2) {
            this.text(guiGraphicsExtractor, l2 < 0L ? "Type /bal (or run the Balance HUD) to start the chart" : "The chart fills in as your balance changes", n24, n5 + n3 / 2 - 4, -9538685, 0.8f, false);
        } else {
            long l5;
            long[] lArray5 = list2.stream().mapToLong(lArray -> lArray[0]).toArray();
            long l6 = Arrays.stream(lArray3).min().orElse(0L);
            if (l6 == (l5 = Arrays.stream(lArray3).max().orElse(0L))) {
                --l6;
                ++l5;
            }
            this.balanceChart(guiGraphicsExtractor, lArray3, lArray5, n24, n5, n4, n3, l6, l5, bl ? -41385 : PURPLE);
            this.chartHover(guiGraphicsExtractor, lArray3, lArray5, n24, n5, n4, n3, l6, l5, 4, bl ? -41385 : PURPLE, true);
        }
        this.drawWinnersLosers(guiGraphicsExtractor, list);
    }

    private void drawWinnersLosers(GuiGraphicsExtractor guiGraphicsExtractor, List<PaymentLedger.Payment> list) {
        int n = this.fx + 10;
        int n2 = this.bottomY();
        int n3 = this.fw - 20;
        int n4 = this.bottomH();
        this.box(guiGraphicsExtractor, n, n2, n3, n4, -15526625, -14934485);
        OverviewScreen.topGlow(guiGraphicsExtractor, n, n2, n3, PURPLE);
        int n5 = this.caps(guiGraphicsExtractor, "TOP WINNERS / LOSERS", n + 12, n2 + 9, -1);
        this.text(guiGraphicsExtractor, this.rangeWord(), n + 20 + n5, n2 + 10, -9538685, 0.66f, false);
        Map<String, long[]> map = this.perPlayer(list);
        ArrayList<Map.Entry<String, long[]>> arrayList = new ArrayList<Map.Entry<String, long[]>>();
        ArrayList<Map.Entry<String, long[]>> arrayList2 = new ArrayList<Map.Entry<String, long[]>>();
        for (Map.Entry<String, long[]> entry3 : map.entrySet()) {
            if (entry3.getValue()[0] < 0L) {
                arrayList.add(entry3);
                continue;
            }
            if (entry3.getValue()[0] <= 0L) continue;
            arrayList2.add(entry3);
        }
        arrayList.sort((entry, entry2) -> Long.compare(((long[])entry.getValue())[0], ((long[])entry2.getValue())[0]));
        arrayList2.sort((entry, entry2) -> Long.compare(((long[])entry2.getValue())[0], ((long[])entry.getValue())[0]));
        int n6 = (n3 - 36) / 2;
        int n7 = n + 12;
        int n8 = n + 12 + n6 + 12;
        this.caps(guiGraphicsExtractor, "TOP WINNERS", n7, n2 + 22, -13710223);
        this.caps(guiGraphicsExtractor, "TOP LOSERS", n8, n2 + 22, -41385);
        int n9 = 18;
        int n10 = n2 + 36;
        int n11 = Math.max(1, (n2 + n4 - 2 - n10) / n9);
        long l = arrayList.isEmpty() ? 1L : Math.max(1L, -((long[])((Map.Entry)arrayList.get(0)).getValue())[0]);
        long l2 = arrayList2.isEmpty() ? 1L : Math.max(1L, ((long[])((Map.Entry)arrayList2.get(0)).getValue())[0]);
        int n12 = 0;
        while (n12 < n11) {
            if (n12 < arrayList.size()) {
                this.wlRow(guiGraphicsExtractor, n7, n10 + n12 * n9, n6, n12 + 1, (String)((Map.Entry)arrayList.get(n12)).getKey(), -((long[])((Map.Entry)arrayList.get(n12)).getValue())[0], l, -13710223);
            }
            if (n12 < arrayList2.size()) {
                this.wlRow(guiGraphicsExtractor, n8, n10 + n12 * n9, n6, n12 + 1, (String)((Map.Entry)arrayList2.get(n12)).getKey(), -((long[])((Map.Entry)arrayList2.get(n12)).getValue())[0], l2, -41385);
            }
            ++n12;
        }
        if (arrayList.isEmpty()) {
            this.text(guiGraphicsExtractor, "Nobody is up on you", n7, n10 + 2, -9538685, 0.75f, false);
        }
        if (arrayList2.isEmpty()) {
            this.text(guiGraphicsExtractor, "Nobody is down to you", n8, n10 + 2, -9538685, 0.75f, false);
        }
    }

    private void wlRow(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, String string, long l, long l2, int n5) {
        this.text(guiGraphicsExtractor, String.valueOf(n4), n, n2 + 3, -9538685, 0.72f, false);
        this.face(guiGraphicsExtractor, string, n + 12, n2, 12);
        String string2 = OverviewScreen.signedMoney(l);
        int n6 = this.wb(string2, 0.8f);
        Object object = string;
        while (((String)object).length() > 3 && this.wb((String)object, 0.8f) > n3 - 38 - n6) {
            object = ((String)object).substring(0, ((String)object).length() - 2) + ".";
        }
        this.text(guiGraphicsExtractor, (String)object, n + 30, n2 + 2, -1, 0.8f, true);
        this.text(guiGraphicsExtractor, string2, n + n3 - n6, n2 + 2, n5, 0.8f, true);
        int n7 = (int)((double)(n3 - 12) * Math.min(1.0, (double)Math.abs(l) / (double)l2));
        guiGraphicsExtractor.fill(n + 12, n2 + 15, n + n3, n2 + 16, OverviewScreen.alpha(-14934485, 200));
        guiGraphicsExtractor.fill(n + 12, n2 + 15, n + 12 + Math.max(2, n7), n2 + 16, n5);
    }

    private static String ago(long l) {
        long l2 = Math.max(0L, (System.currentTimeMillis() - l) / 60000L);
        if (l2 < 1L) {
            return "just now";
        }
        if (l2 < 60L) {
            return l2 + "m ago";
        }
        long l3 = l2 / 60L;
        if (l3 < 24L) {
            return l3 + "h ago";
        }
        return l3 / 24L + "d ago";
    }

    private void drawPlayerPage(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        String string = wlPlayer;
        PaymentLedger.Entry entry = PaymentLedger.allTimeFor(string);
        List<PaymentLedger.Payment> list = PaymentLedger.paymentsFor(string, 0L);
        ArrayList<PaymentLedger.Payment> arrayList = new ArrayList<PaymentLedger.Payment>(list);
        Collections.reverse(arrayList);
        Button button = this.wlBack;
        boolean bl = OverviewScreen.over(button, n, n2);
        this.box(guiGraphicsExtractor, button.getX(), button.getY(), button.getWidth(), button.getHeight(), bl ? -14804434 : -15526625, bl ? PURPLE : -14934485);
        int n3 = button.getX() + 8;
        int n4 = button.getY() + button.getHeight() / 2;
        int n5 = 0;
        while (n5 < 4) {
            guiGraphicsExtractor.fill(n3 + n5, n4 - n5, n3 + n5 + 1, n4 - n5 + 1, bl ? -1 : -7696482);
            guiGraphicsExtractor.fill(n3 + n5, n4 + n5, n3 + n5 + 1, n4 + n5 + 1, bl ? -1 : -7696482);
            ++n5;
        }
        this.text(guiGraphicsExtractor, "All players", n3 + 9, button.getY() + (button.getHeight() - 7) / 2, bl ? -1 : -7696482, 0.8f, false);
        n5 = this.fx + 10;
        int n6 = this.contentY() + 30;
        this.face(guiGraphicsExtractor, string, n5, n6, 26);
        this.text(guiGraphicsExtractor, string, n5 + 34, n6 + 2, -1, 1.4f, true);
        String string2 = list.isEmpty() ? "No dated payments yet" : "first bet " + OverviewScreen.day(arrayList.get((int)0).ts) + "   -   last bet " + OverviewScreen.ago(list.get((int)0).ts);
        this.text(guiGraphicsExtractor, string2, n5 + 34, n6 + 18, -9538685, 0.72f, false);
        long l = entry == null ? 0L : entry.net();
        long l2 = entry == null ? 0L : entry.paidYou;
        long l3 = entry == null ? 0L : entry.youPaid;
        int n7 = entry == null ? list.size() : entry.payments;
        long l4 = 0L;
        for (PaymentLedger.Payment payment : list) {
            l4 = Math.max(l4, payment.youPaid);
        }
        int n8 = n6 + 34;
        int n9 = 8;
        int n10 = (this.fw - 20 - n9 * 4) / 5;
        int[] nArray = new int[5];
        nArray[0] = this.fx + 10;
        int[] nArray2 = nArray;
        int n11 = 1;
        while (n11 < 5) {
            nArray2[n11] = nArray2[n11 - 1] + n10 + n9;
            ++n11;
        }
        this.statBox(guiGraphicsExtractor, nArray2[0], n8, n10, "THEIR NET", OverviewScreen.signedMoney(l), l > 0L ? -13710223 : (l < 0L ? -41385 : -1), l > 0L ? "up on you" : (l < 0L ? "down to you" : "even"));
        this.statBox(guiGraphicsExtractor, nArray2[1], n8, n10, "PAID YOU", Money.format(l2), -1, "their bets");
        this.statBox(guiGraphicsExtractor, nArray2[2], n8, n10, "YOU PAID", Money.format(l3), -1, "their wins");
        this.statBox(guiGraphicsExtractor, nArray2[3], n8, n10, "BETS", String.valueOf(n7), -1, "payments");
        this.statBox(guiGraphicsExtractor, nArray2[4], n8, n10, "BIGGEST WIN", Money.format(l4), l4 > 0L ? -13710223 : -1, "one payout");
        n11 = n8 + 58;
        int n12 = this.contentBottom() - n11;
        int n13 = (this.fw - 28) * 58 / 100;
        int n14 = this.fx + 10 + n13 + 8;
        int n15 = this.fx + this.fw - 10 - n14;
        this.box(guiGraphicsExtractor, this.fx + 10, n11, n13, n12, -15526625, -14934485);
        this.caps(guiGraphicsExtractor, "THEIR NET OVER TIME", this.fx + 20, n11 + 10, -1);
        long[] lArray = new long[arrayList.size() + 1];
        long l5 = 0L;
        int n16 = 0;
        while (n16 < arrayList.size()) {
            lArray[n16 + 1] = l5 += arrayList.get((int)n16).youPaid - arrayList.get((int)n16).paidYou;
            ++n16;
        }
        String string3 = OverviewScreen.signedMoney(l5);
        this.text(guiGraphicsExtractor, string3, this.fx + 10 + n13 - 12 - this.wb(string3, 1.1f), n11 + 8, l5 > 0L ? -13710223 : (l5 < 0L ? -41385 : -7696482), 1.1f, true);
        this.chart(guiGraphicsExtractor, lArray, this.fx + 20, n11 + 28, n13 - 30, n12 - 38);
        if (!arrayList.isEmpty()) {
            long[] lArray2 = new long[lArray.length];
            lArray2[0] = arrayList.get((int)0).ts;
            int n17 = 0;
            while (n17 < arrayList.size()) {
                lArray2[n17 + 1] = arrayList.get((int)n17).ts;
                ++n17;
            }
            long l6 = Math.min(0L, Arrays.stream(lArray).min().orElse(0L));
            long l7 = Math.max(0L, Arrays.stream(lArray).max().orElse(0L));
            long l8 = Math.max(1L, (l7 - l6) / 12L);
            this.chartHover(guiGraphicsExtractor, lArray, lArray2, this.fx + 20, n11 + 28, n13 - 30, n12 - 38, l6 - l8, l7 + l8, 0, PURPLE);
        }
        this.box(guiGraphicsExtractor, n14, n11, n15, n12, -15526625, -14934485);
        this.caps(guiGraphicsExtractor, "PAYMENTS", n14 + 10, n11 + 10, -1);
        if (!list.isEmpty()) {
            this.text(guiGraphicsExtractor, String.valueOf(list.size()), n14 + 16 + this.capsW("PAYMENTS"), n11 + 10, -9538685, 0.72f, false);
        }
        int n18 = 18;
        int n19 = n11 + 28;
        int n20 = Math.max(0, (n11 + n12 - 6 - n19) / n18);
        scroll = Math.max(0, Math.min(scroll, Math.max(0, list.size() - n20)));
        int n21 = 0;
        while (n21 < n20 && scroll + n21 < list.size()) {
            PaymentLedger.Payment payment = list.get(scroll + n21);
            boolean bl2 = payment.paidYou > 0L;
            int n22 = n19 + n21 * n18;
            if (n21 % 2 == 0) {
                guiGraphicsExtractor.fill(n14 + 6, n22 - 3, n14 + n15 - 6, n22 + n18 - 3, -15396321);
            }
            this.text(guiGraphicsExtractor, OverviewScreen.day(payment.ts) + "  " + OverviewScreen.clock(payment.ts), n14 + 12, n22 + 2, -9538685, 0.74f, false);
            this.text(guiGraphicsExtractor, bl2 ? "bet" : "won", n14 + n15 * 52 / 100, n22 + 2, -7696482, 0.74f, false);
            String string4 = bl2 ? "+" + Money.format(payment.paidYou) : "-" + Money.format(payment.youPaid);
            this.text(guiGraphicsExtractor, string4, n14 + n15 - 12 - this.wb(string4, 0.8f), n22 + 1, bl2 ? -13710223 : -41385, 0.8f, true);
            ++n21;
        }
        if (list.isEmpty()) {
            this.text(guiGraphicsExtractor, "No dated payments with " + string + " yet", n14 + 12, n19 + 2, -9538685, 0.8f, false);
        }
        this.scrollbar(guiGraphicsExtractor, n14 + n15 - 4, n19 - 3, n11 + n12 - 6 - n19, n20, list.size());
    }

    private void statBox(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, String string, String string2, int n4, String string3) {
        if (string.equals("RECEIVED") || string.equals("SENT")) {
            this.counterBounds.put(string, new int[]{n, n2, n3, 50});
        }
        this.box(guiGraphicsExtractor, n, n2, n3, 50, -15526625, -14934485);
        this.caps(guiGraphicsExtractor, string, n + 10, n2 + 8, -7696482);
        this.text(guiGraphicsExtractor, string2, n + 10, n2 + 21, n4, 1.15f, true);
        this.text(guiGraphicsExtractor, string3, n + 10, n2 + 38, -9538685, 0.66f, false);
    }

    private void box(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, int n5, int n6) {
        if (n5 == -15526625) {
            n5 = -434957025;
        }
        int n7 = Math.max(1, Math.min(4, Math.min(n3, n4) / 5));
        OverviewScreen.round(guiGraphicsExtractor, n, n2, n3, n4, n7, n6);
        OverviewScreen.round(guiGraphicsExtractor, n + 1, n2 + 1, n3 - 2, n4 - 2, Math.max(0, n7 - 1), n5);
    }

    static void round(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, int n5, int n6) {
        if (n3 <= 0 || n4 <= 0) {
            return;
        }
        if ((n5 = Math.min(n5, Math.min(n3, n4) / 2)) <= 0) {
            guiGraphicsExtractor.fill(n, n2, n + n3, n2 + n4, n6);
            return;
        }
        guiGraphicsExtractor.fill(n, n2 + n5, n + n3, n2 + n4 - n5, n6);
        int n7 = 0;
        while (n7 < n5) {
            double d = (double)(n5 - n7) - 0.5;
            int n8 = (int)Math.round((double)n5 - Math.sqrt(Math.max(0.0, (double)(n5 * n5) - d * d)));
            guiGraphicsExtractor.fill(n + n8, n2 + n7, n + n3 - n8, n2 + n7 + 1, n6);
            guiGraphicsExtractor.fill(n + n8, n2 + n4 - 1 - n7, n + n3 - n8, n2 + n4 - n7, n6);
            ++n7;
        }
    }

    private static int alpha(int n, int n2) {
        return n2 << 24 | n & 0xFFFFFF;
    }

    private void glow(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, int n5, int n6) {
    }

    private static boolean over(Button button, int n, int n2) {
        return button != null && n >= button.getX() && n < button.getX() + button.getWidth() && n2 >= button.getY() && n2 < button.getY() + button.getHeight();
    }

    private void pill(GuiGraphicsExtractor guiGraphicsExtractor, Button button, boolean bl, int n, int n2) {
        this.pill(guiGraphicsExtractor, button, bl, n, n2, 0.76f);
    }

    private void pill(GuiGraphicsExtractor guiGraphicsExtractor, Button button, boolean bl, int n, int n2, float f) {
        int n3 = button.getX();
        int n4 = button.getY();
        int n5 = button.getWidth();
        int n6 = button.getHeight();
        boolean bl2 = OverviewScreen.over(button, n, n2);
        this.cover(guiGraphicsExtractor, button, -15526625);
        OverviewScreen.round(guiGraphicsExtractor, n3, n4, n5, n6, 3, bl ? PURPLE : (bl2 ? OverviewScreen.mixColor(-15526625, PURPLE, 0.45f) : OverviewScreen.mixColor(-15526625, PURPLE, 0.25f)));
        String string = button.getMessage().getString();
        this.text(guiGraphicsExtractor, string, n3 + (n5 - this.wb(string, f)) / 2, n4 + (n6 - Math.round(9.0f * f)) / 2, bl ? OverviewScreen.onAccent() : (bl2 ? -1 : -7696482), f, true);
    }

    private void smoothLine(GuiGraphicsExtractor guiGraphicsExtractor, long[] lArray, int n, int n2, int n3, int n4, int n5) {
        if (lArray.length < 2) {
            guiGraphicsExtractor.fill(n, n2 + n4 - 2, n + n3, n2 + n4, OverviewScreen.alpha(n5, 90));
            return;
        }
        long l = Long.MAX_VALUE;
        long l2 = Long.MIN_VALUE;
        long[] lArray2 = lArray;
        int n6 = lArray.length;
        int n7 = 0;
        while (n7 < n6) {
            long l3 = lArray2[n7];
            l = Math.min(l, l3);
            l2 = Math.max(l2, l3);
            ++n7;
        }
        if (l2 == l) {
            ++l2;
            --l;
        }
        int n8 = Integer.MIN_VALUE;
        int n9 = 0;
        while (n9 < n3) {
            double d = (double)n9 * (double)(lArray.length - 1) / (double)Math.max(1, n3 - 1);
            int n10 = (int)d;
            int n11 = Math.min(lArray.length - 1, n10 + 1);
            double d2 = d - (double)n10;
            double d3 = (1.0 - Math.cos(Math.PI * d2)) / 2.0;
            double d4 = (double)lArray[n10] + (double)(lArray[n11] - lArray[n10]) * d3;
            int n12 = n2 + (int)Math.round(((double)l2 - d4) / (double)(l2 - l) * (double)(n4 - 2));
            int n13 = n8 == Integer.MIN_VALUE ? n12 : n8;
            guiGraphicsExtractor.fill(n + n9, n12 + 2, n + n9 + 1, n2 + n4 + 2, OverviewScreen.alpha(n5, 30));
            guiGraphicsExtractor.fill(n + n9, Math.min(n13, n12), n + n9 + 1, Math.max(n13, n12) + 2, n5);
            n8 = n12;
            ++n9;
        }
    }

    private void bars(GuiGraphicsExtractor guiGraphicsExtractor, long[] lArray, int n, int n2, int n3, int n4, int n5) {
        int n6 = lArray.length;
        long l = 1L;
        long[] lArray2 = lArray;
        int n7 = lArray.length;
        int n8 = 0;
        while (n8 < n7) {
            long l2 = lArray2[n8];
            l = Math.max(l, l2);
            ++n8;
        }
        int n9 = n6 > 14 ? 1 : 3;
        float f = (float)(n3 - n9 * (n6 - 1)) / (float)n6;
        n8 = 0;
        while (n8 < n6) {
            n7 = n + Math.round((float)n8 * (f + (float)n9));
            int n10 = n + Math.round((float)n8 * (f + (float)n9) + f);
            int n11 = lArray[n8] <= 0L ? 1 : Math.max(2, Math.round((float)((long)n4 * lArray[n8]) / (float)l));
            boolean bl = n8 == n6 - 1;
            guiGraphicsExtractor.fill(n7, n2 + n4 - n11, Math.max(n7 + 1, n10), n2 + n4, bl ? n5 : OverviewScreen.alpha(n5, lArray[n8] <= 0L ? 50 : 130));
            ++n8;
        }
    }

    private static int mixColor(int n, int n2, float f) {
        f = Math.max(0.0f, Math.min(1.0f, f));
        int n3 = (int)((float)(n >> 16 & 0xFF) + (float)((n2 >> 16 & 0xFF) - (n >> 16 & 0xFF)) * f);
        int n4 = (int)((float)(n >> 8 & 0xFF) + (float)((n2 >> 8 & 0xFF) - (n >> 8 & 0xFF)) * f);
        int n5 = (int)((float)(n & 0xFF) + (float)((n2 & 0xFF) - (n & 0xFF)) * f);
        return 0xFF000000 | n3 << 16 | n4 << 8 | n5;
    }

    private int spacedW(String string, float f) {
        int n = 0;
        char[] cArray = string.toCharArray();
        int n2 = cArray.length;
        int n3 = 0;
        while (n3 < n2) {
            char c = cArray[n3];
            n += this.wb(String.valueOf(c), f) + 1;
            ++n3;
        }
        return Math.max(0, n - 1);
    }

    private void spacedText(GuiGraphicsExtractor guiGraphicsExtractor, String string, int n, int n2, int n3, float f) {
        char[] cArray = string.toCharArray();
        int n4 = cArray.length;
        int n5 = 0;
        while (n5 < n4) {
            char c = cArray[n5];
            String string2 = String.valueOf(c);
            this.text(guiGraphicsExtractor, string2, n, n2, n3, f, true);
            n += this.wb(string2, f) + 1;
            ++n5;
        }
    }

    private void cover(GuiGraphicsExtractor guiGraphicsExtractor, Button button) {
        this.cover(guiGraphicsExtractor, button, -15526625);
    }

    private void cover(GuiGraphicsExtractor guiGraphicsExtractor, Button button, int n) {
        guiGraphicsExtractor.fill(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), n);
    }

    private void groupBar(GuiGraphicsExtractor guiGraphicsExtractor, List<Button> list) {
        if (list.isEmpty()) {
            return;
        }
        Button button = list.get(0);
        Button button2 = list.get(list.size() - 1);
        this.box(guiGraphicsExtractor, button.getX() - 2, button.getY() - 2, button2.getX() + button2.getWidth() - button.getX() + 4, button.getHeight() + 4, -15526625, -14934485);
    }

    private void spark(GuiGraphicsExtractor guiGraphicsExtractor, long[] lArray, int n, int n2, int n3, int n4, int n5) {
        if (lArray.length < 2) {
            guiGraphicsExtractor.fill(n, n2 + n4 - 1, n + n3, n2 + n4, OverviewScreen.alpha(n5, 60));
            return;
        }
        long l = Long.MAX_VALUE;
        long l2 = Long.MIN_VALUE;
        long[] lArray2 = lArray;
        int n6 = lArray.length;
        int n7 = 0;
        while (n7 < n6) {
            long l3 = lArray2[n7];
            l = Math.min(l, l3);
            l2 = Math.max(l2, l3);
            ++n7;
        }
        if (l2 == l) {
            ++l2;
            --l;
        }
        int n8 = -1;
        int n9 = 0;
        while (n9 < n3) {
            double d = (double)n9 * (double)(lArray.length - 1) / (double)Math.max(1, n3 - 1);
            int n10 = (int)d;
            int n11 = Math.min(lArray.length - 1, n10 + 1);
            double d2 = (double)lArray[n10] + (double)(lArray[n11] - lArray[n10]) * (d - (double)n10);
            int n12 = n2 + (int)Math.round(((double)l2 - d2) / (double)(l2 - l) * (double)(n4 - 2)) + 1;
            guiGraphicsExtractor.fill(n + n9, n12, n + n9 + 2, n2 + n4, OverviewScreen.alpha(n5, 34));
            int n13 = n8 < 0 ? n12 : n8;
            guiGraphicsExtractor.fill(n + n9, Math.min(n12, n13), n + n9 + 2, Math.max(n12, n13) + 1, n5);
            n8 = n12;
            n9 += 2;
        }
    }

    private static String clock(long l) {
        return Instant.ofEpochMilli(l).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("h:mm a", Locale.US)).toLowerCase(Locale.US);
    }

    private static String day(long l) {
        return Instant.ofEpochMilli(l).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMM d", Locale.US));
    }

    private static String dayTime(long l) {
        return Instant.ofEpochMilli(l).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.US));
    }

    private static String signedMoney(long l) {
        return (l > 0L ? "+" : (l < 0L ? "-" : "")) + Money.format(Math.abs(l));
    }

    private String rangeWord() {
        return switch (range) {
            case 0 -> "this session";
            case 1 -> "today";
            case 2 -> "this week";
            case 3 -> "this month";
            default -> "all time";
        };
    }

    private void face(GuiGraphicsExtractor guiGraphicsExtractor, String string, int n, int n2, int n3) {
        if (Settings.S.skinFaces) {
            try {
                PlayerInfo playerInfo;
                ClientPacketListener clientPacketListener = this.minecraft == null ? null : this.minecraft.getConnection();
                PlayerInfo playerInfo2 = playerInfo = clientPacketListener == null ? null : clientPacketListener.getPlayerInfo(string);
                if (playerInfo != null) {
                    PlayerFaceExtractor.extractRenderState((GuiGraphicsExtractor)guiGraphicsExtractor, (PlayerSkin)playerInfo.getSkin(), (int)n, (int)n2, (int)n3);
                    return;
                }
                PlayerFaceExtractor.extractRenderState((GuiGraphicsExtractor)guiGraphicsExtractor, (PlayerSkin)HistorySkinCache.get(this.minecraft, string), (int)n, (int)n2, (int)n3);
                return;
            } catch (Exception exception) {}
        }
        guiGraphicsExtractor.fill(n + 1, n2, n + n3 - 1, n2 + n3, PURPLE);
        guiGraphicsExtractor.fill(n, n2 + 1, n + n3, n2 + n3 - 1, PURPLE);
        String string2 = string.isEmpty() ? "?" : string.substring(0, 1).toUpperCase(Locale.US);
        float f = (float)n3 / 12.0f;
        int n4 = n2 + Math.round(((float)n3 - Fonts.capHeight(f)) / 2.0f - Fonts.capTop(f));
        this.text(guiGraphicsExtractor, string2, n + (n3 - this.wb(string2, f)) / 2, n4, -1, f, true);
    }

    private List<PaymentLedger.Payment> rangePayments() {
        long l = this.since();
        long l2 = this.now();
        ArrayList<PaymentLedger.Payment> arrayList = new ArrayList<PaymentLedger.Payment>();
        for (PaymentLedger.Payment payment : PaymentLedger.paymentsSince(l)) {
            if (payment.ts > l2) continue;
            arrayList.add(payment);
        }
        return arrayList;
    }

    private Map<String, long[]> perPlayer(List<PaymentLedger.Payment> list) {
        LinkedHashMap<String, long[]> linkedHashMap = new LinkedHashMap<String, long[]>();
        for (PaymentLedger.Payment payment : list) {
            long[] lArray = linkedHashMap.computeIfAbsent(payment.name, string -> new long[4]);
            lArray[0] = lArray[0] + (payment.paidYou - payment.youPaid);
            lArray[1] = lArray[1] + payment.paidYou;
            lArray[2] = lArray[2] + payment.youPaid;
            lArray[3] = lArray[3] + 1L;
        }
        return linkedHashMap;
    }

    private static void donut(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        double d = 5.6;
        double d2 = 1.9;
        int n3 = -6;
        while (n3 <= 6) {
            int n4 = -6;
            while (n4 <= 6) {
                double d3 = Math.sqrt(((double)n4 + 0.5) * ((double)n4 + 0.5) + ((double)n3 + 0.5) * ((double)n3 + 0.5));
                if (!(d3 > d) && !(d3 < d2)) {
                    boolean bl;
                    boolean bl2 = bl = d3 < d - 1.2 && (n3 < 1 || n3 == 1 && (n4 + 7) % 3 != 0);
                    int n5 = bl ? (n4 < -1 && n3 < -1 ? -415532 : -757066) : (n3 > 2 ? -5212614 : -2516402);
                    guiGraphicsExtractor.fill(n + n4, n2 + n3, n + n4 + 1, n2 + n3 + 1, n5);
                }
                ++n4;
            }
            ++n3;
        }
        guiGraphicsExtractor.fill(n - 3, n2 - 4, n - 2, n2 - 3, -1);
        guiGraphicsExtractor.fill(n + 2, n2 - 4, n + 3, n2 - 3, -139193);
        guiGraphicsExtractor.fill(n + 3, n2 - 1, n + 4, n2, -10443270);
        guiGraphicsExtractor.fill(n - 4, n2 - 1, n - 3, n2, -11870592);
        guiGraphicsExtractor.fill(n, n2 - 5, n + 1, n2 - 4, -10443270);
    }

    private static void donutRing(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        int n3 = PURPLE;
        int n4 = PURPLE_LIGHT;
        int n5 = 0xFF000000 | (n3 >> 16 & 0xFF) * 6 / 10 << 16 | (n3 >> 8 & 0xFF) * 6 / 10 << 8 | (n3 & 0xFF) * 6 / 10;
        double d = 8.0;
        double d2 = 3.2;
        int n6 = -9;
        while (n6 <= 9) {
            int n7 = -9;
            while (n7 <= 9) {
                double d3 = (double)n7 + 0.5;
                double d4 = (double)n6 + 0.5;
                double d5 = Math.sqrt(d3 * d3 + d4 * d4);
                if (!(d5 > d) && !(d5 < d2)) {
                    double d6 = -(d3 + d4) / Math.max(0.001, d5);
                    double d7 = (d5 - d2) / (d - d2);
                    int n8 = d6 > 0.55 && d7 > 0.25 ? n4 : (d6 < -0.45 ? n5 : n3);
                    guiGraphicsExtractor.fill(n + n7, n2 + n6, n + n7 + 1, n2 + n6 + 1, n8);
                }
                ++n7;
            }
            ++n6;
        }
    }

    private void glowText(GuiGraphicsExtractor guiGraphicsExtractor, String string, int n, int n2, int n3, float f) {
        int n4 = this.wb(string, f);
        int n5 = Math.round(9.0f * f);
        OverviewScreen.softGlow(guiGraphicsExtractor, n + n4 / 2, n2 + n5 / 2, n4 / 2 + 10, n5 / 2 + 6, PURPLE, 40);
        this.text(guiGraphicsExtractor, string, n, n2, n3, f, true);
    }

    private static void softGlow(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, int n5, int n6) {
        int n7 = 8;
        int n8 = Math.max(1, n6 / n7);
        int n9 = n7;
        while (n9 >= 1) {
            int n10 = n3 * n9 / n7;
            int n11 = n4 * n9 / n7;
            OverviewScreen.round(guiGraphicsExtractor, n - n10, n2 - n11, n10 * 2, n11 * 2, Math.min(Math.min(n10, n11), 12), OverviewScreen.alpha(n5, n8));
            --n9;
        }
    }

    private void gear(GuiGraphicsExtractor guiGraphicsExtractor, Button button, int n, int n2) {
        boolean bl = OverviewScreen.over(button, n, n2);
        this.cover(guiGraphicsExtractor, button);
        this.box(guiGraphicsExtractor, button.getX() - 2, button.getY() - 2, button.getWidth() + 4, button.getHeight() + 4, bl ? -14804434 : -15526625, bl ? PURPLE : -14934485);
        int n3 = button.getX() + button.getWidth() / 2;
        int n4 = button.getY() + button.getHeight() / 2;
        int n5 = bl ? -1 : -7696482;
        int n6 = 0;
        while (n6 < 8) {
            double d = (double)n6 * Math.PI / 4.0;
            int n7 = n3 + (int)Math.round(Math.cos(d) * 5.0);
            int n8 = n4 + (int)Math.round(Math.sin(d) * 5.0);
            guiGraphicsExtractor.fill(n7 - 1, n8 - 1, n7 + 1, n8 + 1, n5);
            ++n6;
        }
        n6 = -4;
        while (n6 <= 4) {
            int n9 = -4;
            while (n9 <= 4) {
                int n10 = n9 * n9 + n6 * n6;
                if (n10 <= 16 && n10 >= 5) {
                    guiGraphicsExtractor.fill(n3 + n9, n4 + n6, n3 + n9 + 1, n4 + n6 + 1, n5);
                }
                ++n9;
            }
            ++n6;
        }
    }

    private static String hms(long l) {
        long l2 = Math.max(0L, l / 1000L);
        return String.format("%d:%02d:%02d", l2 / 3600L, l2 % 3600L / 60L, l2 % 60L);
    }

    private void card(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, String string, String string2, String string3, int n5, int n6, int n7, long[] lArray) {
        this.box(guiGraphicsExtractor, n, n2, n3, n4, -15526625, -14934485);
        guiGraphicsExtractor.fill(n + 1, n2, n + n3 - 1, n2 + 2, n5);
        int n8 = 0;
        while (n8 < 10) {
            guiGraphicsExtractor.fill(n + 1, n2 + 2 + n8, n + n3 - 1, n2 + 3 + n8, OverviewScreen.alpha(n5, 26 - n8 * 2));
            ++n8;
        }
        this.caps(guiGraphicsExtractor, this.fitText(string, n3 - 20, 0.76f, true), n + 10, n2 + 10, -7696482);
        float f = this.wb(string2, 1.5f) <= n3 - 20 ? 1.5f : (this.wb(string2, 1.25f) <= n3 - 20 ? 1.25f : 1.0f);
        this.text(guiGraphicsExtractor, this.fitText(string2, n3 - 20, f, true), n + 10, n2 + 24 + (int)((1.5f - f) * 6.0f), n6, f, true);
        this.text(guiGraphicsExtractor, this.fitText(string3, n3 - 20, 0.7f, false), n + 10, n2 + 44, n7, 0.7f, false);
        int n9 = n + 10;
        int n10 = Math.max(1, n3 - 20);
        int n11 = n2 + 56;
        int n12 = Math.max(8, n4 - 64);
        if (lArray != null && lArray.length > 1) {
            this.thinLine(guiGraphicsExtractor, lArray, n9, n11, n10, n12, n5);
        }
    }

    private static void topGlow(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4) {
        guiGraphicsExtractor.fill(n + 1, n2, n + n3 - 1, n2 + 1, OverviewScreen.alpha(n4, 120));
        int n5 = 0;
        while (n5 < 14) {
            guiGraphicsExtractor.fill(n + 1, n2 + 1 + n5, n + n3 - 1, n2 + 2 + n5, OverviewScreen.alpha(n4, 22 - n5 * 3 / 2));
            ++n5;
        }
    }

    private void thinLine(GuiGraphicsExtractor guiGraphicsExtractor, long[] lArray, int n, int n2, int n3, int n4, int n5) {
        if (lArray.length < 2 || n3 < 2 || n4 < 3) {
            return;
        }
        long l = Long.MAX_VALUE;
        long l2 = Long.MIN_VALUE;
        long[] lArray2 = lArray;
        int n6 = lArray.length;
        int n7 = 0;
        while (n7 < n6) {
            long l3 = lArray2[n7];
            l = Math.min(l, l3);
            l2 = Math.max(l2, l3);
            ++n7;
        }
        double d = Math.max(1.0, (double)l2 - (double)l);
        double d2 = (double)l - d * 0.12;
        double d3 = (double)l2 + d * 0.12;
        int n8 = Integer.MIN_VALUE;
        int n9 = 0;
        while (n9 < n3) {
            double d4 = (double)n9 * (double)(lArray.length - 1) / (double)Math.max(1, n3 - 1);
            int n10 = (int)d4;
            int n11 = Math.min(lArray.length - 1, n10 + 1);
            double d5 = d4 - (double)n10;
            double d6 = (1.0 - Math.cos(Math.PI * d5)) / 2.0;
            double d7 = (double)lArray[n10] + (double)(lArray[n11] - lArray[n10]) * d6;
            int n12 = n2 + 1 + (int)Math.round((d3 - d7) / (d3 - d2) * (double)(n4 - 3));
            n12 = Math.max(n2 + 1, Math.min(n2 + n4 - 2, n12));
            int n13 = n8 == Integer.MIN_VALUE ? n12 : n8;
            guiGraphicsExtractor.fill(n + n9, n12 + 1, n + n9 + 1, n2 + n4, OverviewScreen.alpha(n5, 18));
            guiGraphicsExtractor.fill(n + n9, Math.min(n13, n12), n + n9 + 1, Math.max(n13, n12) + 1, n5);
            n8 = n12;
            ++n9;
        }
    }

    private void areaChart(GuiGraphicsExtractor guiGraphicsExtractor, long[] lArray, int n, int n2, int n3, int n4, int n5) {
        int n6 = n3;
        int n7 = 0;
        while (n7 <= 3) {
            int n8 = n2 + n7 * (n4 - 1) / 3;
            int n9 = 0;
            while (n9 < n3) {
                guiGraphicsExtractor.fill(n + n9, n8, n + Math.min(n3, n9 + 3), n8 + 1, OverviewScreen.alpha(-14934485, 170));
                n9 += 6;
            }
            ++n7;
        }
        long l = Long.MAX_VALUE;
        long l2 = Long.MIN_VALUE;
        long[] lArray2 = lArray;
        int n10 = lArray.length;
        int n11 = 0;
        while (n11 < n10) {
            long l3 = lArray2[n11];
            l = Math.min(l, l3);
            l2 = Math.max(l2, l3);
            ++n11;
        }
        if (l2 == l) {
            ++l2;
            --l;
        }
        int n12 = Integer.MIN_VALUE;
        int n13 = n2;
        n11 = 0;
        while (n11 < n6) {
            double d = (double)n11 * (double)(lArray.length - 1) / (double)Math.max(1, n3 - 1);
            int n14 = (int)d;
            int n15 = Math.min(lArray.length - 1, n14 + 1);
            double d2 = d - (double)n14;
            double d3 = (1.0 - Math.cos(Math.PI * d2)) / 2.0;
            double d4 = (double)lArray[n14] + (double)(lArray[n15] - lArray[n14]) * d3;
            int n16 = n2 + 4 + (int)Math.round(((double)l2 - d4) / (double)(l2 - l) * (double)(n4 - 8));
            int n17 = n12 == Integer.MIN_VALUE ? n16 : n12;
            guiGraphicsExtractor.fill(n + n11, n16 + 1, n + n11 + 1, n2 + n4, OverviewScreen.alpha(n5, 85));
            guiGraphicsExtractor.fill(n + n11, Math.min(n17, n16), n + n11 + 1, Math.max(n17, n16) + 1, OverviewScreen.mixColor(n5, -1, 0.25f));
            n12 = n16;
            n13 = n16;
            ++n11;
        }
        n11 = n + n6 - 1;
        OverviewScreen.round(guiGraphicsExtractor, n11 - 6, n13 - 6, 13, 13, 6, OverviewScreen.alpha(n5, 35));
        OverviewScreen.round(guiGraphicsExtractor, n11 - 4, n13 - 4, 9, 9, 4, OverviewScreen.alpha(n5, 80));
        OverviewScreen.round(guiGraphicsExtractor, n11 - 2, n13 - 2, 5, 5, 2, OverviewScreen.mixColor(n5, -1, 0.4f));
    }

    private void balanceChart(GuiGraphicsExtractor guiGraphicsExtractor, long[] lArray, long[] lArray2, int n, int n2, int n3, int n4, long l, long l2, int n5) {
        int n6;
        int n7;
        if (n3 < 2 || n4 < 12) {
            return;
        }
        int n8 = 0;
        while (n8 < 3) {
            n7 = n2 + 4 + n8 * (n4 - 8) / 2;
            n6 = 0;
            while (n6 < n3) {
                guiGraphicsExtractor.fill(n + n6, n7, n + Math.min(n3, n6 + 3), n7 + 1, 1077625426);
                n6 += 6;
            }
            ++n8;
        }
        n8 = -1;
        n7 = 0;
        while (n7 < n3) {
            n6 = BalanceTimeline.index(lArray2, BalanceTimeline.at(lArray2, n7, n3));
            int n9 = n2 + 4 + (int)Math.round(((double)l2 - (double)lArray[n6]) / Math.max(1.0, (double)l2 - (double)l) * (double)(n4 - 8));
            guiGraphicsExtractor.fill(n + n7, n9 + 1, n + n7 + 1, n2 + n4, OverviewScreen.alpha(n5, 38));
            guiGraphicsExtractor.fill(n + n7, n8 < 0 ? n9 : Math.min(n8, n9), n + n7 + 1, n8 < 0 ? n9 + 1 : Math.max(n8, n9) + 1, n5);
            n8 = n9;
            ++n7;
        }
        OverviewScreen.round(guiGraphicsExtractor, n + n3 - 3, n8 - 2, 5, 5, 2, n5);
    }

    private void chartHover(GuiGraphicsExtractor guiGraphicsExtractor, long[] lArray, long[] lArray2, int n, int n2, int n3, int n4, long l, long l2, int n5, int n6) {
        this.chartHover(guiGraphicsExtractor, lArray, lArray2, n, n2, n3, n4, l, l2, n5, n6, false);
    }

    private void chartHover(GuiGraphicsExtractor guiGraphicsExtractor, long[] lArray, long[] lArray2, int n, int n2, int n3, int n4, long l, long l2, int n5, int n6, boolean bl) {
        if (lArray.length == 0 || n3 < 2 || n4 < 2 || this.lastMouseX < n || this.lastMouseX >= n + n3 || this.lastMouseY < n2 || this.lastMouseY >= n2 + n4) {
            return;
        }
        int n7 = Math.max(0, Math.min(lArray.length - 1, (int)Math.round((double)(this.lastMouseX - n) * ((double)lArray.length - 1.0) / (double)(n3 - 1))));
        if (bl) {
            n7 = BalanceTimeline.index(lArray2, BalanceTimeline.at(lArray2, this.lastMouseX - n, n3));
        }
        int n8 = bl ? this.lastMouseX : n + (int)Math.round((double)n7 * ((double)n3 - 1.0) / (double)Math.max(1, lArray.length - 1));
        int n9 = n2 + n5 + (int)Math.round(((double)l2 - (double)lArray[n7]) / Math.max(1.0, (double)l2 - (double)l) * (double)(n4 - (n5 == 0 ? 1 : n5 * 2)));
        guiGraphicsExtractor.fill(n8, n2, n8 + 1, n2 + n4, -1711276033);
        guiGraphicsExtractor.fill(n, n9, n + n3, n9 + 1, 0x35FFFFFF);
        OverviewScreen.round(guiGraphicsExtractor, n8 - 4, n9 - 4, 9, 9, 4, OverviewScreen.alpha(n6, 100));
        OverviewScreen.round(guiGraphicsExtractor, n8 - 2, n9 - 2, 5, 5, 2, -1);
        String string = (lArray[n7] < 0L ? "-" : "") + String.format(Locale.US, "$%,d", Math.abs(lArray[n7]));
        String string2 = DateTimeFormatter.ofPattern("MMM d, yyyy  h:mm:ss a").withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(bl ? BalanceTimeline.at(lArray2, this.lastMouseX - n, n3) : lArray2[n7]));
        int n10 = Math.min(n3, Math.max(this.wb(string, 0.9f), this.w(string2, 0.7f)) + 16);
        int n11 = Math.max(n, Math.min(n + n3 - n10, this.lastMouseX + 12));
        int n12 = Math.max(n2, Math.min(n2 + n4 - 35, this.lastMouseY - 42));
        this.box(guiGraphicsExtractor, n11, n12, n10, 34, -15986662, n6);
        this.text(guiGraphicsExtractor, this.fitText(string, n10 - 12, 0.9f, true), n11 + 6, n12 + 6, -1, 0.9f, true);
        this.text(guiGraphicsExtractor, this.fitText(string2, n10 - 12, 0.7f, false), n11 + 6, n12 + 21, -4209196, 0.7f, false);
    }

    private String fitText(String object, int n, float f, boolean bl) {
        while (((String)object).length() > 3 && (bl ? this.wb((String)object, f) : this.w((String)object, f)) > n) {
            object = ((String)object).substring(0, ((String)object).length() - 2) + ".";
        }
        return object;
    }

    private void chart(GuiGraphicsExtractor guiGraphicsExtractor, long[] lArray, int n, int n2, int n3, int n4) {
        long l;
        int n5 = n3;
        if (lArray.length < 2) {
            this.text(guiGraphicsExtractor, "No payments in this range yet", n + 4, n2 + n4 / 2 - 4, -9538685, 0.85f, false);
            return;
        }
        long l2 = 0L;
        long l3 = 0L;
        long[] lArray2 = lArray;
        int n6 = lArray.length;
        int n7 = 0;
        while (n7 < n6) {
            l = lArray2[n7];
            l2 = Math.min(l2, l);
            l3 = Math.max(l3, l);
            ++n7;
        }
        l = Math.max(1L, (l3 - l2) / 12L);
        long l4 = l3 + l;
        long l5 = l2 - l;
        double d = l4 - l5;
        double d2 = (double)n2 + (double)l4 / d * (double)(n4 - 1);
        int n8 = (int)Math.round(d2);
        int n9 = 0;
        while (n9 < n3) {
            guiGraphicsExtractor.fill(n + n9, n8, n + Math.min(n3, n9 + 4), n8 + 1, -12962984);
            n9 += 8;
        }
        n9 = Integer.MIN_VALUE;
        double d3 = d2;
        int n10 = 0;
        while (n10 < n5) {
            double d4 = (double)n10 * (double)(lArray.length - 1) / (double)Math.max(1, n3 - 1);
            int n11 = (int)d4;
            int n12 = Math.min(lArray.length - 1, n11 + 1);
            double d5 = (double)lArray[n11] + (double)(lArray[n12] - lArray[n11]) * (d4 - (double)n11);
            double d6 = (double)n2 + ((double)l4 - d5) / d * (double)(n4 - 1);
            int n13 = (int)Math.round(d6);
            int n14 = d5 >= 0.0 ? -13710223 : -41385;
            int n15 = n + n10;
            if (n13 < n8) {
                guiGraphicsExtractor.fill(n15, n13 + 1, n15 + 1, n8, OverviewScreen.alpha(n14, 38));
            } else if (n13 > n8) {
                guiGraphicsExtractor.fill(n15, n8 + 1, n15 + 1, n13, OverviewScreen.alpha(n14, 38));
            }
            int n16 = n9 == Integer.MIN_VALUE ? n13 : n9;
            guiGraphicsExtractor.fill(n15, Math.min(n16, n13), n15 + 1, Math.max(n16, n13) + 2, n14);
            n9 = n13;
            d3 = d6;
            ++n10;
        }
        n10 = n + n5 - 1;
        int n17 = (int)Math.round(d3);
        int n18 = d3 <= d2 ? -13710223 : -41385;
        OverviewScreen.round(guiGraphicsExtractor, n10 - 5, n17 - 5, 11, 11, 5, OverviewScreen.alpha(n18, 45));
        OverviewScreen.round(guiGraphicsExtractor, n10 - 2, n17 - 2, 5, 5, 2, n18);
    }

    private int pageTitle(GuiGraphicsExtractor guiGraphicsExtractor, String string, String object) {
        int n = this.fx + 10;
        int n2 = this.contentY() + 4;
        this.text(guiGraphicsExtractor, string, n, n2, -1, 1.4f, true);
        int n3 = this.fw - 20 - (this.viewButtons.isEmpty() ? 0 : this.fx + this.fw - this.viewButtons.get(0).getX() + 2);
        while (((String)object).length() > 4 && this.w((String)object, 0.72f) > n3) {
            object = ((String)object).substring(0, ((String)object).length() - 2) + ".";
        }
        this.text(guiGraphicsExtractor, (String)object, n, n2 + 14, -7696482, 0.72f, false);
        return n2 + 26;
    }

    private int chip(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, String string, String string2, int n3) {
        int n4 = Math.max(this.capsW(string), this.wb(string2, 0.95f)) + 22;
        if (string.equals("UP ON YOU") || string.equals("DOWN ON YOU")) {
            n4 = this.counterWidths.getOrDefault(string, n4);
            this.counterBounds.put(string, new int[]{n, n2, n4, 36});
        }
        this.box(guiGraphicsExtractor, n, n2, n4, 36, -15526625, -14934485);
        this.caps(guiGraphicsExtractor, string, n + 11, n2 + 7, -7696482);
        this.text(guiGraphicsExtractor, string2, n + 11, n2 + 20, n3, 0.95f, true);
        return n4;
    }

    private void scrollbar(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, int n5) {
        if (n5 <= n4) {
            return;
        }
        guiGraphicsExtractor.fill(n, n2, n + 2, n2 + n3, -14936016);
        int n6 = Math.max(12, n3 * n4 / n5);
        int n7 = n2 + (n3 - n6) * scroll / Math.max(1, n5 - n4);
        guiGraphicsExtractor.fill(n, n7, n + 2, n7 + n6, -10527883);
    }

    private void drawSearchFrame(GuiGraphicsExtractor guiGraphicsExtractor, int n) {
        if (this.searchBox == null) {
            return;
        }
        int n2 = Math.min(170, this.fw / 4);
        int n3 = this.fx + this.fw - 10 - n2;
        this.searchField(guiGraphicsExtractor, n3, n + 8, n2, 20, this.searchBox, "Search players");
    }

    private void searchField(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, EditBox editBox, String string) {
        boolean bl = editBox != null && editBox.isFocused();
        String string2 = editBox == null ? "" : editBox.getValue();
        int n5 = Math.min(6, n4 / 2);
        OverviewScreen.round(guiGraphicsExtractor, n, n2, n3, n4, n5, bl ? PURPLE : -14934485);
        OverviewScreen.round(guiGraphicsExtractor, n + 1, n2 + 1, n3 - 2, n4 - 2, n5 - 1, OverviewScreen.mixColor(-15526625, -15526625, 0.6f));
        int n6 = bl ? PURPLE_LIGHT : -9538685;
        int n7 = n + 10;
        int n8 = n2 + n4 / 2 - 1;
        OverviewScreen.ring(guiGraphicsExtractor, n7, n8, 3, 1, 1.0f, n6, n6);
        guiGraphicsExtractor.fill(n7 + 2, n8 + 2, n7 + 3, n8 + 3, n6);
        guiGraphicsExtractor.fill(n7 + 3, n8 + 3, n7 + 4, n8 + 4, n6);
        int n9 = n + 19;
        int n10 = n2 + (n4 - 7) / 2;
        int n11 = n3 - 26;
        if (string2.isEmpty()) {
            this.text(guiGraphicsExtractor, string, n9, n10, -9538685, 0.66f, false);
        } else {
            String string3 = string2;
            while (string3.length() > 1 && this.w(string3, 0.66f) > n11) {
                string3 = string3.substring(1);
            }
            this.text(guiGraphicsExtractor, string3, n9, n10, -1, 0.66f, false);
            n9 += this.w(string3, 0.66f) + 1;
        }
        if (bl && System.currentTimeMillis() / 500L % 2L == 0L) {
            guiGraphicsExtractor.fill(n9, n10 - 1, n9 + 1, n10 + 8, -1);
        }
    }

    private void drawWinLoss(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        List<PaymentLedger.Entry> list = this.winLossList();
        int n3 = 0;
        int n4 = 0;
        long l = 0L;
        long l2 = 0L;
        for (PaymentLedger.Entry entry : list) {
            long l3 = entry.net();
            if (l3 > 0L) {
                ++n3;
                l += l3;
                continue;
            }
            if (l3 >= 0L) continue;
            ++n4;
            l2 -= l3;
        }
        int n5 = this.pageTitle(guiGraphicsExtractor, "Players", "Everyone who has gambled with you. Top = won the most off you, bottom = lost the most.");
        int n6 = this.fx + 10;
        n6 += this.chip(guiGraphicsExtractor, n6, n5, "PLAYERS", String.valueOf(list.size()), -1) + 6;
        this.counterWidths.put("UP ON YOU", Math.max(this.capsW("UP ON YOU"), this.wb(n3 + " \u00b7 +" + Money.format(l), 0.95f)) + 22);
        this.counterWidths.put("DOWN ON YOU", Math.max(this.capsW("DOWN ON YOU"), this.wb(n4 + " \u00b7 -" + Money.format(l2), 0.95f)) + 22);
        n6 += this.chip(guiGraphicsExtractor, n6, n5, "UP ON YOU", this.countValue("UP ON YOU", n3) + " \u00b7 +" + Money.format(this.countValue("UP ON YOU", l)), -13710223) + 6;
        this.chip(guiGraphicsExtractor, n6, n5, "DOWN ON YOU", this.countValue("DOWN ON YOU", n4) + " \u00b7 -" + Money.format(this.countValue("DOWN ON YOU", l2)), -41385);
        this.drawSearchFrame(guiGraphicsExtractor, n5);
        int n7 = n5 + 44;
        int n8 = this.contentBottom() - n7;
        int n9 = this.fw - 20;
        int n10 = this.fx + 10;
        this.box(guiGraphicsExtractor, n10, n7, n9, n8, -15526625, -14934485);
        int n11 = n10 + 10;
        int n12 = n10 + 28;
        int n13 = n10 + n9 * 48 / 100;
        int n14 = n10 + n9 * 62 / 100;
        int n15 = n10 + n9 - 14;
        int n16 = n7 + 8;
        this.caps(guiGraphicsExtractor, "#", n11, n16, -9538685);
        this.caps(guiGraphicsExtractor, "PLAYER", n12 + 14, n16, -9538685);
        this.caps(guiGraphicsExtractor, "BETS", n13, n16, -9538685);
        this.caps(guiGraphicsExtractor, "WAGERED", n14, n16, -9538685);
        this.caps(guiGraphicsExtractor, "NET (THEIR SIDE)", n15 - this.capsW("NET (THEIR SIDE)"), n16, -9538685);
        guiGraphicsExtractor.fill(n10 + 1, n16 + 13, n10 + n9 - 1, n16 + 14, -14934485);
        int n17 = 21;
        int n18 = n16 + 19;
        int n19 = Math.max(1, (n7 + n8 - 4 - n18) / n17);
        scroll = Math.max(0, Math.min(scroll, Math.max(0, list.size() - n19)));
        int n20 = 0;
        while (n20 < n19 && scroll + n20 < list.size()) {
            boolean bl;
            int n21 = scroll + n20;
            PaymentLedger.Entry entry = list.get(n21);
            int n22 = n18 + n20 * n17;
            boolean bl2 = bl = n >= n10 + 4 && n < n10 + n9 - 8 && n2 >= n22 - 3 && n2 < n22 + n17 - 3;
            if (bl) {
                guiGraphicsExtractor.fill(n10 + 4, n22 - 3, n10 + n9 - 8, n22 + n17 - 3, -14541514);
            } else if (n20 > 0) {
                guiGraphicsExtractor.fill(n10 + 8, n22 - 3, n10 + n9 - 10, n22 - 2, OverviewScreen.alpha(-14934485, 200));
            }
            if (bl) {
                guiGraphicsExtractor.fill(n10 + 4, n22 - 3, n10 + 6, n22 + n17 - 3, PURPLE);
            }
            this.text(guiGraphicsExtractor, String.valueOf(n21 + 1), n11, n22 + 1, -9538685, 0.76f, false);
            this.face(guiGraphicsExtractor, entry.name, n12, n22 - 1, 10);
            this.text(guiGraphicsExtractor, entry.name, n12 + 14, n22, bl ? PURPLE_LIGHT : -1, 0.8f, true);
            this.text(guiGraphicsExtractor, String.valueOf(entry.payments), n13, n22 + 1, -7696482, 0.75f, false);
            this.text(guiGraphicsExtractor, Money.format(entry.paidYou), n14, n22 + 1, -7696482, 0.75f, false);
            long l4 = entry.net();
            String string = OverviewScreen.signedMoney(l4);
            this.text(guiGraphicsExtractor, string, n15 - this.wb(string, 0.8f), n22, l4 > 0L ? -13710223 : (l4 < 0L ? -41385 : -7696482), 0.8f, true);
            ++n20;
        }
        if (list.isEmpty()) {
            this.text(guiGraphicsExtractor, "No players yet", n12, n18, -9538685, 0.85f, false);
        } else {
            this.text(guiGraphicsExtractor, "click a player to see their history", n12 + 14 + this.capsW("PLAYER") + 10, n16, -9538685, 0.66f, false);
        }
        this.scrollbar(guiGraphicsExtractor, n10 + n9 - 5, n18 - 3, n7 + n8 - n18, n19, list.size());
    }

    private void drawRakeback(GuiGraphicsExtractor guiGraphicsExtractor) {
        Settings settings = Settings.S;
        ArrayList<PaymentLedger.Entry> arrayList = new ArrayList<PaymentLedger.Entry>();
        int n = 0;
        long l = 0L;
        String string = search.trim().toLowerCase(Locale.US);
        for (PaymentLedger.Entry entry : PaymentLedger.entries(false)) {
            long l2 = -entry.net();
            if (l2 <= 0L || !string.isEmpty() && !entry.name.toLowerCase(Locale.US).contains(string)) continue;
            arrayList.add(entry);
            if (l2 < settings.rakeMin) continue;
            ++n;
            l += l2 * (long)settings.rakePct / 100L;
        }
        arrayList.sort(Comparator.comparingLong(PaymentLedger.Entry::net));
        int n2 = this.pageTitle(guiGraphicsExtractor, "Rakeback", settings.rakePct + "% back once a player has lost " + Money.format(settings.rakeMin) + ". Everyone, closest to " + Money.format(settings.rakeMin) + " first.");
        int n3 = this.fx + 10;
        n3 += this.chip(guiGraphicsExtractor, n3, n2, "PLAYERS", String.valueOf(arrayList.size()), -855814) + 6;
        n3 += this.chip(guiGraphicsExtractor, n3, n2, "READY", String.valueOf(n), -11870592) + 6;
        this.chip(guiGraphicsExtractor, n3, n2, "YOU OWE", Money.format(l), -855814);
        this.drawSearchFrame(guiGraphicsExtractor, n2);
        int n4 = n2 + 46;
        int n5 = this.contentBottom() - n4;
        int n6 = this.fw - 20;
        int n7 = this.fx + 10;
        int n8 = 24;
        int n9 = n4;
        int n10 = Math.max(1, (n4 + n5 - 4 - n9) / n8);
        scroll = Math.max(0, Math.min(scroll, Math.max(0, arrayList.size() - n10)));
        int n11 = 0;
        while (n11 < n10 && scroll + n11 < arrayList.size()) {
            int n12 = scroll + n11;
            PaymentLedger.Entry entry = (PaymentLedger.Entry)arrayList.get(n12);
            long l3 = -entry.net();
            boolean bl = l3 >= settings.rakeMin;
            int n13 = n9 + n11 * n8;
            int n14 = n7;
            int n15 = n6 - 8;
            this.box(guiGraphicsExtractor, n14, n13, n15, n8 - 4, -15593444, -14541261);
            this.text(guiGraphicsExtractor, String.valueOf(n12 + 1), n14 + 10, n13 + 5, -7698781, 0.7f, false);
            this.text(guiGraphicsExtractor, entry.name, n14 + 28, n13 + 4, -855814, 0.74f, true);
            String string2 = bl ? "READY  owe " + Money.format(l3 * (long)settings.rakePct / 100L) : Money.format(l3) + " / " + Money.format(settings.rakeMin);
            this.text(guiGraphicsExtractor, string2, n14 + n15 - 10 - this.wb(string2, 0.74f), n13 + 4, bl ? -11870592 : -855814, 0.74f, true);
            int n16 = n14 + 28;
            int n17 = n14 + n15 - 10;
            int n18 = n13 + n8 - 9;
            guiGraphicsExtractor.fill(n16, n18, n17, n18 + 2, OverviewScreen.alpha(-14541261, 255));
            int n19 = (int)((double)(n17 - n16) * Math.min(1.0, (double)l3 / (double)settings.rakeMin));
            int n20 = 0;
            while (n20 < n19) {
                float f = (float)n20 / (float)Math.max(1, n17 - n16);
                int n21 = f < 0.5f ? OverviewScreen.mixColor(-757066, -1185282, f * 2.0f) : OverviewScreen.mixColor(-1185282, -11870592, (f - 0.5f) * 2.0f);
                guiGraphicsExtractor.fill(n16 + n20, n18, n16 + Math.min(n19, n20 + 2), n18 + 2, n21);
                n20 += 2;
            }
            ++n11;
        }
        if (arrayList.isEmpty()) {
            this.text(guiGraphicsExtractor, (String)(string.isEmpty() ? "Nobody has lost to you yet" : "No players match \"" + search.trim() + "\""), n7 + 10, n9 + 4, -7698781, 0.85f, false);
        }
        this.scrollbar(guiGraphicsExtractor, n7 + n6 - 3, n9, Math.min(n5 - 8, n10 * n8), n10, arrayList.size());
    }

    private void drawViewSwitch(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        this.groupBar(guiGraphicsExtractor, this.viewButtons);
        int n3 = 0;
        while (n3 < this.viewButtons.size()) {
            this.pill(guiGraphicsExtractor, this.viewButtons.get(n3), n3 == playersView, n, n2, 0.7f);
            ++n3;
        }
    }

    private int[] payPanel() {
        int n = this.contentY() + 30 + 58;
        return new int[]{this.fx + 10, n, this.fw - 20, this.contentBottom() - n};
    }

    private int paySearchW() {
        return Math.min(150, Math.max(80, (this.fw - 20) / 4));
    }

    private int paySearchX() {
        int[] nArray = this.payPanel();
        int n = nArray[0] + nArray[2] - 10;
        String[] stringArray = new String[]{"ALL", "IN", "OUT"};
        int n2 = stringArray.length;
        int n3 = 0;
        while (n3 < n2) {
            String string = stringArray[n3];
            n -= this.small(string) + 3;
            ++n3;
        }
        return n - 8 - this.paySearchW();
    }

    private void initPayments() {
        int[] nArray = this.payPanel();
        int n = nArray[0] + nArray[2] - 10;
        String[] stringArray = new String[]{"OUT", "IN", "ALL"};
        int n2 = 0;
        while (n2 < 3) {
            int n3 = 2 - n2;
            int n4 = this.small(stringArray[n2]);
            this.filterButtons.add(0, this.btn(stringArray[n2], n - n4, nArray[1] + 6, n4, () -> {
                payFilter = n3;
                scroll = 0;
            }));
            n -= n4 + 3;
            ++n2;
        }
        this.searchBox = new EditBox(this.font, -4000, -4000, 200, 12, (Component)Component.literal((String)"Search"));
        this.searchBox.setMaxLength(32);
        this.searchBox.setValue(paySearch);
        this.searchBox.setResponder(string -> {
            paySearch = string;
            scroll = 0;
        });
        this.addWidget(this.searchBox);
        this.btn("", this.paySearchX(), nArray[1] + 6, this.paySearchW(), () -> {
            this.pendingFocus = this.searchBox;
        });
        n2 = this.wb("LOAD MORE", 0.7f) + 20;
        this.loadMoreBtn = this.btn("", nArray[0] + nArray[2] - 10 - n2, nArray[1] + nArray[3] - 21, n2, () -> {
            payShown = payShown + 12;
        });
    }

    private List<PaymentLedger.Payment> payList() {
        List<PaymentLedger.Payment> list = this.rangePayments();
        ArrayList<PaymentLedger.Payment> arrayList = new ArrayList<PaymentLedger.Payment>();
        String string = paySearch.trim().toLowerCase(Locale.US);
        int n = list.size() - 1;
        while (n >= 0) {
            boolean bl;
            PaymentLedger.Payment payment = list.get(n);
            boolean bl2 = bl = payment.paidYou > 0L;
            if (!(payFilter == 1 && !bl || payFilter == 2 && bl || !string.isEmpty() && !payment.name.toLowerCase(Locale.US).contains(string))) {
                arrayList.add(payment);
            }
            --n;
        }
        return arrayList;
    }

    private void drawPayments(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        int n3 = this.pageTitle(guiGraphicsExtractor, "Payments", "Every payment you sent or received.");
        List<PaymentLedger.Payment> list = this.rangePayments();
        long l = 0L;
        long l2 = 0L;
        int n4 = 0;
        int n5 = 0;
        for (PaymentLedger.Payment payment : list) {
            if (payment.paidYou > 0L) {
                l += payment.paidYou;
                ++n4;
            }
            if (payment.youPaid <= 0L) continue;
            l2 += payment.youPaid;
            ++n5;
        }
        long l3 = l - l2;
        int n6 = list.size();
        int n7 = 8;
        int n8 = (this.fw - 20 - n7 * 3) / 4;
        int n9 = this.fx + 10;
        this.statBox(guiGraphicsExtractor, n9, n3, n8, "RECEIVED", "+" + Money.format(this.countValue("RECEIVED", l)), -13710223, n4 + (n4 == 1 ? " payment" : " payments"));
        this.statBox(guiGraphicsExtractor, n9 + (n8 + n7), n3, n8, "SENT", "-" + Money.format(this.countValue("SENT", l2)), -41385, n5 + (n5 == 1 ? " payment" : " payments"));
        this.statBox(guiGraphicsExtractor, n9 + (n8 + n7) * 2, n3, n8, "NET", OverviewScreen.signedMoney(l3), l3 >= 0L ? -13710223 : -41385, "received minus sent");
        this.statBox(guiGraphicsExtractor, n9 + (n8 + n7) * 3, n3, n8, "TRANSACTIONS", String.valueOf(n6), -1, (String)(n6 == 0 ? "none yet" : "avg " + Money.format((l + l2) / (long)n6) + " each"));
        int[] nArray = this.payPanel();
        this.box(guiGraphicsExtractor, nArray[0], nArray[1], nArray[2], nArray[3], -15526625, -14934485);
        int n10 = this.caps(guiGraphicsExtractor, "HISTORY", nArray[0] + 12, nArray[1] + 10, -1);
        this.text(guiGraphicsExtractor, this.rangeWord(), nArray[0] + 20 + n10, nArray[1] + 11, -9538685, 0.66f, false);
        this.searchField(guiGraphicsExtractor, this.paySearchX(), nArray[1] + 6, this.paySearchW(), 16, this.searchBox, "Search player");
        this.groupBar(guiGraphicsExtractor, this.filterButtons);
        int n11 = 0;
        while (n11 < this.filterButtons.size()) {
            this.pill(guiGraphicsExtractor, this.filterButtons.get(n11), n11 == payFilter, n, n2);
            ++n11;
        }
        n11 = nArray[0] + 12;
        int n12 = nArray[0] + nArray[2] * 56 / 100;
        int n13 = nArray[0] + nArray[2] * 80 / 100;
        int n14 = nArray[0] + nArray[2] - 14;
        int n15 = nArray[1] + 32;
        this.caps(guiGraphicsExtractor, "PLAYER", n11, n15, -9538685);
        this.caps(guiGraphicsExtractor, "TYPE", n12, n15, -9538685);
        this.caps(guiGraphicsExtractor, "AMOUNT", n13 - this.capsW("AMOUNT"), n15, -9538685);
        this.caps(guiGraphicsExtractor, "WHEN", n14 - this.capsW("WHEN"), n15, -9538685);
        guiGraphicsExtractor.fill(nArray[0] + 1, n15 + 12, nArray[0] + nArray[2] - 1, n15 + 13, -14934485);
        List<PaymentLedger.Payment> list2 = this.payList();
        int n16 = list2.size();
        int n17 = Math.min(n16, payShown);
        int n18 = n15 + 18;
        int n19 = nArray[1] + nArray[3] - 22;
        int n20 = Math.max(1, (n19 - 4 - n18) / 20);
        scroll = Math.max(0, Math.min(scroll, Math.max(0, n17 - n20)));
        int n21 = 0;
        while (n21 < n20 && scroll + n21 < n17) {
            boolean bl;
            PaymentLedger.Payment payment = list2.get(scroll + n21);
            boolean bl2 = payment.paidYou > 0L;
            int n22 = n18 + n21 * 20;
            boolean bl3 = bl = n >= nArray[0] + 4 && n < nArray[0] + nArray[2] - 8 && n2 >= n22 - 4 && n2 < n22 + 20 - 4;
            if (bl) {
                guiGraphicsExtractor.fill(nArray[0] + 4, n22 - 4, nArray[0] + nArray[2] - 8, n22 + 20 - 4, 0x14FFFFFF);
            } else if (n21 > 0) {
                guiGraphicsExtractor.fill(nArray[0] + 8, n22 - 4, nArray[0] + nArray[2] - 10, n22 - 3, OverviewScreen.alpha(-14934485, 200));
            }
            this.face(guiGraphicsExtractor, payment.name, n11, n22 - 1, 12);
            Object object = payment.name;
            while (((String)object).length() > 3 && this.wb((String)object, 0.8f) > n12 - n11 - 26) {
                object = ((String)object).substring(0, ((String)object).length() - 2) + ".";
            }
            this.text(guiGraphicsExtractor, (String)object, n11 + 18, n22 + 1, -1, 0.8f, true);
            String string = bl2 ? "RECEIVED" : "SENT";
            int n23 = bl2 ? -13710223 : -41385;
            int n24 = this.wb(string, 0.6f) + 10;
            OverviewScreen.round(guiGraphicsExtractor, n12, n22 - 1, n24, 12, 2, OverviewScreen.alpha(n23, 140));
            OverviewScreen.round(guiGraphicsExtractor, n12 + 1, n22, n24 - 2, 10, 2, OverviewScreen.mixColor(-15526625, n23, 0.18f));
            this.text(guiGraphicsExtractor, string, n12 + 5, n22 + 2, n23, 0.6f, true);
            String string2 = OverviewScreen.signedMoney(bl2 ? payment.paidYou : -payment.youPaid);
            this.text(guiGraphicsExtractor, string2, n13 - this.wb(string2, 0.8f), n22 + 1, n23, 0.8f, true);
            String string3 = OverviewScreen.ago(payment.ts);
            this.text(guiGraphicsExtractor, string3, n14 - this.w(string3, 0.72f), n22 + 2, -7696482, 0.72f, false);
            ++n21;
        }
        if (n16 == 0) {
            this.text(guiGraphicsExtractor, (String)(paySearch.isBlank() ? "No payments in this range" : "No payments match \"" + paySearch.trim() + "\""), n11, n18 + 2, -9538685, 0.85f, false);
        }
        this.scrollbar(guiGraphicsExtractor, nArray[0] + nArray[2] - 5, n18 - 4, n19 - n18, n20, n17);
        guiGraphicsExtractor.fill(nArray[0] + 1, n19 - 2, nArray[0] + nArray[2] - 1, n19 - 1, OverviewScreen.alpha(-14934485, 200));
        String string = n16 == 0 ? "Showing 0 of 0" : "Showing " + (scroll + 1) + "-" + Math.min(n17, scroll + n20) + " of " + n16;
        this.text(guiGraphicsExtractor, string, nArray[0] + 12, n19 + 6, -9538685, 0.7f, false);
        if (this.loadMoreBtn != null && n17 < n16) {
            Button button = this.loadMoreBtn;
            boolean bl = OverviewScreen.over(button, n, n2);
            this.box(guiGraphicsExtractor, button.getX(), button.getY(), button.getWidth(), button.getHeight(), bl ? OverviewScreen.mixColor(-15526625, PURPLE, 0.3f) : -15526625, PURPLE);
            this.text(guiGraphicsExtractor, "LOAD MORE", button.getX() + (button.getWidth() - this.wb("LOAD MORE", 0.7f)) / 2, button.getY() + (button.getHeight() - 7) / 2, PURPLE_LIGHT, 0.7f, true);
        }
    }

    private List<SRow> streamerRows() {
        Settings settings = Settings.S;
        ArrayList<SRow> arrayList = new ArrayList<SRow>();
        arrayList.add(new SRow("BALANCE BOX", "Your balance box with your name (Balance HUD mod)", 0, () -> Panels.balance, bl -> {
            Panels.balance = bl;
        }));
        arrayList.add(new SRow("BALANCE OUTLINE (TEST)", "Outline color of your balance box. Testing only.", 3, null, null));
        arrayList.add(new SRow("PROFIT TRACKER", "The profit panel with the chart, top winners / losers and recent bets (Balance HUD mod)", 0, () -> Panels.profit, bl -> {
            Panels.profit = bl;
        }));
        arrayList.add(new SRow("RAKEBACK", settings.rakePct + "% back once a player has lost " + Money.format(settings.rakeMin) + ". Who is close and what you owe (Balance HUD mod)", 0, () -> Panels.rakeback, bl -> {
            Panels.rakeback = bl;
        }));
        return arrayList;
    }

    private int stTop() {
        return this.contentY() + 30;
    }

    private int stVisible() {
        return Math.max(1, (this.contentBottom() - 38 - this.stTop() + 6) / 42);
    }

    private static void saveAll() {
        Panels.save();
        Settings.save();
    }

    private void initStreamer() {
        Panels.load();
        List<SRow> list = this.streamerRows();
        scroll = Math.max(0, Math.min(scroll, Math.max(0, list.size() - this.stVisible())));
        int n = this.fx + 10;
        int n2 = this.fw - 20;
        int n3 = 0;
        while (n3 < this.stVisible() && scroll + n3 < list.size()) {
            Button[] buttonArray;
            SRow sRow = list.get(scroll + n3);
            int n4 = this.stTop() + n3 * 42;
            int n5 = n4 + 10;
            int n6 = n + n2 - 12;
            switch (sRow.kind()) {
                case 0: {
                    Button[] buttonArray2 = new Button[2];
                    buttonArray2[0] = this.btn("ON", n6 - 64, n5, 32, () -> {
                        sRow.set().accept(true);
                        OverviewScreen.saveAll();
                    });
                    buttonArray = buttonArray2;
                    buttonArray2[1] = this.btn("OFF", n6 - 32, n5, 32, () -> {
                        sRow.set().accept(false);
                        OverviewScreen.saveAll();
                    });
                    break;
                }
                case 1: {
                    Button[] buttonArray3 = new Button[1];
                    buttonArray = buttonArray3;
                    buttonArray3[0] = this.btn("", n6 - 110, n5, 110, () -> {
                        this.bindingKey = !this.bindingKey;
                    });
                    break;
                }
                case 2: {
                    this.hexBox = new EditBox(this.font, -4000, -4000, 80, 12, (Component)Component.literal((String)"Accent"));
                    this.hexBox.setMaxLength(7);
                    this.hexBox.setValue(Settings.hex(Settings.S.accent));
                    this.hexBox.setResponder(string -> {
                        String string2;
                        String string3 = string2 = string.startsWith("#") ? string.substring(1) : string;
                        if (string2.matches("[0-9a-fA-F]{6}")) {
                            Settings.applyAccent(Integer.parseInt(string2, 16));
                            Settings.save();
                        }
                    });
                    this.addWidget(this.hexBox);
                    EditBox editBox = this.hexBox;
                    Button[] buttonArray4 = new Button[1];
                    buttonArray = buttonArray4;
                    buttonArray4[0] = this.btn("", n6 - 16 - 8 - 80, n5, 80, () -> {
                        this.pendingFocus = editBox;
                    });
                    break;
                }
                default: {
                    Button[] buttonArray5 = new Button[3];
                    buttonArray5[0] = this.btn("", n6 - 160, n5, 16, () -> {
                        OverviewScreen.setBackground(OverviewScreen.backgroundIndex() - 1);
                    });
                    buttonArray5[1] = this.btn("", n6 - 16, n5, 16, () -> {
                        OverviewScreen.setBackground(OverviewScreen.backgroundIndex() + 1);
                    });
                    buttonArray = buttonArray5;
                    buttonArray5[2] = this.btn("", n6 - 138, n5, 120, () -> {
                        OverviewScreen.setBackground(OverviewScreen.backgroundIndex());
                    });
                }
            }
            this.streamButtons.add(buttonArray);
            ++n3;
        }
    }

    private void drawStreamer(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        this.pageTitle(guiGraphicsExtractor, "Streamer", "Turn your Balance HUD panels on or off. Everything else is in Settings.");
        List<SRow> list = this.streamerRows();
        int n3 = this.fx + 10;
        int n4 = this.fw - 20;
        int n5 = 0;
        while (n5 < this.streamButtons.size() && scroll + n5 < list.size()) {
            SRow sRow = list.get(scroll + n5);
            Button[] buttonArray = this.streamButtons.get(n5);
            int n6 = this.stTop() + n5 * 42;
            int n7 = n6 + 10;
            int n8 = n3 + n4 - 12;
            this.box(guiGraphicsExtractor, n3, n6, n4, 36, -15526625, -14934485);
            this.caps(guiGraphicsExtractor, sRow.title(), n3 + 12, n6 + 8, -1);
            int n9 = switch (sRow.kind()) {
                case 0 -> 64;
                case 1 -> 110;
                case 2 -> 104;
                default -> 160;
            };
            Object object = sRow.about();
            while (((String)object).length() > 4 && this.w((String)object, 0.7f) > n4 - 36 - n9) {
                object = ((String)object).substring(0, ((String)object).length() - 2) + ".";
            }
            this.text(guiGraphicsExtractor, (String)object, n3 + 12, n6 + 21, -9538685, 0.7f, false);
            switch (sRow.kind()) {
                case 0: {
                    boolean bl = sRow.get().getAsBoolean();
                    OverviewScreen.round(guiGraphicsExtractor, n8 - 65, n7 - 1, 66, 18, 2, -14934485);
                    this.segment(guiGraphicsExtractor, buttonArray[0], "ON", bl, n, n2);
                    this.segment(guiGraphicsExtractor, buttonArray[1], "OFF", !bl, n, n2);
                    break;
                }
                case 1: {
                    KeyMapping keyMapping = DashboardClient.streamerKey;
                    String string = this.bindingKey ? "Press a key..." : (keyMapping == null ? "-" : keyMapping.getTranslatedKeyMessage().getString());
                    if (!this.bindingKey && keyMapping != null && keyMapping.isUnbound()) {
                        string = "Not set";
                    }
                    boolean bl = OverviewScreen.over(buttonArray[0], n, n2);
                    this.box(guiGraphicsExtractor, buttonArray[0].getX(), n7, 110, 16, -16053742, this.bindingKey ? PURPLE : (bl ? PURPLE_LIGHT : -14934485));
                    this.text(guiGraphicsExtractor, string, buttonArray[0].getX() + (110 - this.w(string, 0.74f)) / 2, n7 + 4, this.bindingKey ? PURPLE_LIGHT : -932849, 0.74f, false);
                    break;
                }
                case 2: {
                    boolean bl = this.hexBox != null && this.hexBox.isFocused();
                    int n10 = buttonArray[0].getX();
                    this.box(guiGraphicsExtractor, n10, n7, 80, 16, -16053742, bl ? PURPLE : -14934485);
                    String string = this.hexBox == null ? Settings.hex(Settings.S.accent) : this.hexBox.getValue();
                    this.text(guiGraphicsExtractor, string, n10 + 7, n7 + 4, -1, 0.74f, false);
                    if (bl && System.currentTimeMillis() / 500L % 2L == 0L) {
                        int n11 = n10 + 8 + this.w(string, 0.74f);
                        guiGraphicsExtractor.fill(n11, n7 + 3, n11 + 1, n7 + 12, -1);
                    }
                    OverviewScreen.round(guiGraphicsExtractor, n8 - 16, n7, 16, 16, 2, -14934485);
                    OverviewScreen.round(guiGraphicsExtractor, n8 - 15, n7 + 1, 14, 14, 2, PURPLE);
                    break;
                }
                default: {
                    this.arrowBox(guiGraphicsExtractor, buttonArray[0], true, n, n2);
                    this.arrowBox(guiGraphicsExtractor, buttonArray[1], false, n, n2);
                    String string = OverviewScreen.backgroundName(OverviewScreen.backgroundIndex());
                    int n12 = (buttonArray[0].getX() + 16 + buttonArray[1].getX()) / 2;
                    this.text(guiGraphicsExtractor, string, n12 - this.wb(string, 0.74f) / 2, n7 + 4, -1, 0.74f, true);
                }
            }
            ++n5;
        }
        this.scrollbar(guiGraphicsExtractor, n3 + n4 + 3, this.stTop(), this.contentBottom() - 38 - this.stTop(), this.stVisible(), list.size());
        guiGraphicsExtractor.fill(n3, this.contentBottom() - 35, n3 + n4, this.contentBottom() - 34, -1723643301);
    }

    private void segment(GuiGraphicsExtractor guiGraphicsExtractor, Button button, String string, boolean bl, int n, int n2) {
        int n3 = button.getX();
        int n4 = button.getY();
        int n5 = button.getWidth();
        int n6 = button.getHeight();
        boolean bl2 = OverviewScreen.over(button, n, n2);
        guiGraphicsExtractor.fill(n3, n4, n3 + n5, n4 + n6, bl ? PURPLE : (bl2 ? OverviewScreen.mixColor(-15526625, PURPLE, 0.3f) : -16053742));
        this.text(guiGraphicsExtractor, string, n3 + (n5 - this.wb(string, 0.7f)) / 2, n4 + (n6 - 7) / 2, bl ? OverviewScreen.onAccent() : -7696482, 0.7f, true);
    }

    private void arrowBox(GuiGraphicsExtractor guiGraphicsExtractor, Button button, boolean bl, int n, int n2) {
        int n3 = button.getX();
        int n4 = button.getY();
        boolean bl2 = OverviewScreen.over(button, n, n2);
        this.box(guiGraphicsExtractor, n3, n4, 16, 16, bl2 ? OverviewScreen.mixColor(-15526625, PURPLE, 0.3f) : -16053742, bl2 ? PURPLE : -14934485);
        int n5 = bl2 ? -1 : -7696482;
        int n6 = n3 + 8;
        int n7 = n4 + 8;
        int n8 = 0;
        while (n8 < 4) {
            int n9 = bl ? n6 - 2 + n8 : n6 + 1 - n8;
            guiGraphicsExtractor.fill(n9, n7 - n8, n9 + 1, n7 + n8 + 1, n5);
            ++n8;
        }
    }

    private static void loadItems() {
        if (allItems != null) {
            return;
        }
        allItems = new ArrayList<ItemStack>();
        allNames = new ArrayList<String>();
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack itemStack = new ItemStack((ItemLike)item);
            if (itemStack.isEmpty()) continue;
            allItems.add(itemStack);
            allNames.add(itemStack.getHoverName().getString());
        }
    }

    private List<Integer> filteredItems() {
        OverviewScreen.loadItems();
        if (itemQuery.equals(cachedQuery) && cachedItems != null) {
            return cachedItems;
        }
        String string = itemQuery.trim().toLowerCase(Locale.US);
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        int n = 0;
        while (n < allItems.size()) {
            if (string.isEmpty() || allNames.get(n).toLowerCase(Locale.US).contains(string)) {
                arrayList.add(n);
            }
            ++n;
        }
        cachedQuery = itemQuery;
        cachedItems = arrayList;
        return arrayList;
    }

    private int aTop() {
        return this.contentY() + 30;
    }

    private int aLeftW() {
        return (this.fw - 28) * 49 / 100;
    }

    private int aRingH() {
        int n = 154;
        return Math.max(72, Math.min(170, this.contentBottom() - this.aTop() - n));
    }

    private int aCtrlY() {
        return this.aTop() + this.aRingH() + 8;
    }

    private int aFieldsY() {
        return this.aCtrlY() + 28;
    }

    private int aColorY() {
        return this.aFieldsY() + 36;
    }

    private int aSwitchY() {
        return this.aColorY() + 50;
    }

    private int aLastY() {
        return this.aSwitchY() + 34;
    }

    private int[] aGrid() {
        int n = this.fx + 10 + this.aLeftW() + 8;
        int n2 = this.fx + this.fw - 10 - n;
        return new int[]{n + 10, this.aTop() + 48, n2 - 24, this.contentBottom() - 92 - 8 - 18 - (this.aTop() + 48)};
    }

    private int[] aFields() {
        int n = this.fx + 10;
        int n2 = this.aLeftW();
        int n3 = n2 * 50 / 100;
        int n4 = (n2 - n3 - 12) / 2;
        return new int[]{n, n3, n + n3 + 6, n4, n + n3 + 12 + n4, n2 - n3 - 12 - n4};
    }

    private int[] aPicker() {
        int n = this.fx + 10;
        int n2 = this.aLeftW();
        int n3 = n + 76;
        int n4 = 64;
        int n5 = n2 - 76 - 10 - 8 - n4 - 6;
        return new int[]{n3, this.aColorY() + 13, n5, 30, n3 + n5 + 4, n3 + n5 + 18, n4};
    }

    private static int hsv(float f, float f2, float f3) {
        float f4;
        float f5;
        float f6;
        float f7 = f3 * f2;
        float f8 = f % 360.0f / 60.0f;
        float f9 = f7 * (1.0f - Math.abs(f8 % 2.0f - 1.0f));
        float f10 = f3 - f7;
        if (f8 < 1.0f) {
            f6 = f7;
            f5 = f9;
            f4 = 0.0f;
        } else if (f8 < 2.0f) {
            f6 = f9;
            f5 = f7;
            f4 = 0.0f;
        } else if (f8 < 3.0f) {
            f6 = 0.0f;
            f5 = f7;
            f4 = f9;
        } else if (f8 < 4.0f) {
            f6 = 0.0f;
            f5 = f9;
            f4 = f7;
        } else if (f8 < 5.0f) {
            f6 = f9;
            f5 = 0.0f;
            f4 = f7;
        } else {
            f6 = f7;
            f5 = 0.0f;
            f4 = f9;
        }
        return 0xFF000000 | Math.round((f6 + f10) * 255.0f) << 16 | Math.round((f5 + f10) * 255.0f) << 8 | Math.round((f4 + f10) * 255.0f);
    }

    private EditBox hiddenBox(String string2, int n, Predicate<String> predicate, Consumer<String> consumer) {
        EditBox editBox = new EditBox(this.font, -4000, -4000, 200, 12, (Component)Component.literal((String)""));
        editBox.setMaxLength(n);
        editBox.setValue(string2);
        editBox.setResponder(string -> {
            if (predicate != null && !predicate.test((String)string)) {
                editBox.setValue(string.replaceAll("[^0-9]", ""));
                return;
            }
            consumer.accept((String)string);
        });
        this.addWidget(editBox);
        return editBox;
    }

    private void initAuctions() {
        this.colorBtns.clear();
        int n = this.fx + 10;
        int n2 = this.aLeftW();
        this.startBtn = this.btn("START AUCTION", n, this.aCtrlY(), this.wb("START AUCTION", 0.76f) + 34, () -> {
            if (Auction.running) {
                Auction.endAt = System.currentTimeMillis();
            } else {
                Auction.start();
            }
        });
        this.startBtn.setHeight(20);
        this.clearBtn = this.btn("Clear", this.startBtn.getX() + this.startBtn.getWidth() + 8, this.aCtrlY(), this.w("Clear", 0.8f) + 22, () -> {
            Auction.clearItem();
            this.rebuildWidgets();
        });
        this.clearBtn.setHeight(20);
        int[] nArray = this.aFields();
        this.nameBox = this.hiddenBox(Auction.shortName() == null ? "" : Auction.shortName(), 40, null, string -> {
            Auction.customName = string;
        });
        this.qtyBox = this.hiddenBox(String.valueOf(Auction.quantity), 7, string -> string.matches("\\d*"), string -> {
            try {
                Auction.quantity = Math.max(1, Integer.parseInt(string));
            } catch (Exception exception) {
                Auction.quantity = 1;
            }
        });
        this.secBox = this.hiddenBox(String.valueOf(Auction.durationMs / 1000L), 4, string -> string.matches("\\d*"), string -> {
            try {
                if (!Auction.running) {
                    Auction.durationMs = (long)Math.max(5, Math.min(3600, Integer.parseInt(string))) * 1000L;
                }
            } catch (Exception exception) {}
        });
        int n3 = this.aFieldsY() + 12;
        this.nameClick = this.btn("", nArray[0], n3, nArray[1], () -> {
            this.pendingFocus = this.nameBox;
        });
        this.nameClick.setHeight(16);
        this.qtyClick = this.btn("", nArray[2], n3, nArray[3], () -> {
            this.pendingFocus = this.qtyBox;
        });
        this.qtyClick.setHeight(16);
        this.secClick = this.btn("", nArray[4], n3, nArray[5], () -> {
            this.pendingFocus = this.secBox;
        });
        this.secClick.setHeight(16);
        int n4 = 0;
        while (n4 < TIMER_COLORS.length) {
            int n5 = TIMER_COLORS[n4];
            Button button2 = this.btn("", n + n4 % 4 * 16, this.aColorY() + 13 + n4 / 4 * 16, 12, () -> {
                Auction.color = n5;
            });
            button2.setHeight(12);
            this.colorBtns.add(button2);
            ++n4;
        }
        int[] nArray2 = this.aPicker();
        this.svBtn = this.btn("", nArray2[0], nArray2[1], nArray2[2], this::pickSV);
        this.svBtn.setHeight(nArray2[3]);
        this.hueBtn = this.btn("", nArray2[4], nArray2[1], 8, this::pickHue);
        this.hueBtn.setHeight(nArray2[3]);
        this.switchBtn = this.btn("", n, this.aSwitchY(), n2, () -> {
            Auction.switchOn = !Auction.switchOn;
        });
        this.switchBtn.setHeight(28);
        int[] nArray3 = this.aGrid();
        int n6 = this.fx + 10 + this.aLeftW() + 8;
        int n7 = this.fx + this.fw - 10 - n6;
        this.itemSearch = this.hiddenBox(itemQuery, 32, null, string -> {
            itemQuery = string;
            scroll = 0;
        });
        Button button3 = this.btn("", n6 + 10, this.aTop() + 24, n7 - 20, () -> {
            this.pendingFocus = this.itemSearch;
        });
        button3.setHeight(18);
        this.gridBtn = (Button)this.addWidget(Button.builder((Component)Component.literal((String)""), button -> this.pickItemAtMouse()).bounds(nArray3[0], nArray3[1], nArray3[2], nArray3[3]).build());
    }

    private void pickSV() {
        int[] nArray = this.aPicker();
        sat = Math.max(0.0f, Math.min(1.0f, (float)(this.lastMouseX - nArray[0]) / (float)Math.max(1, nArray[2] - 1)));
        val = Math.max(0.0f, Math.min(1.0f, 1.0f - (float)(this.lastMouseY - nArray[1]) / (float)Math.max(1, nArray[3] - 1)));
        Auction.color = OverviewScreen.hsv(hue, sat, val);
    }

    private void pickHue() {
        int[] nArray = this.aPicker();
        hue = Math.max(0.0f, Math.min(359.0f, (float)(this.lastMouseY - nArray[1]) / (float)Math.max(1, nArray[3] - 1) * 360.0f));
        Auction.color = OverviewScreen.hsv(hue, sat, val);
    }

    private void pickItemAtMouse() {
        int[] nArray = this.aGrid();
        int n = Math.max(1, nArray[2] / 26);
        int n2 = (this.lastMouseX - nArray[0]) / 26;
        int n3 = (this.lastMouseY - nArray[1]) / 26;
        if (n2 < 0 || n2 >= n || n3 < 0) {
            return;
        }
        List<Integer> list = this.filteredItems();
        int n4 = (scroll + n3) * n + n2;
        if (n4 >= 0 && n4 < list.size() && !Auction.running) {
            Auction.item = allItems.get(list.get(n4)).copy();
            Auction.customName = Auction.itemName = allNames.get(list.get(n4));
            Auction.ended = false;
            this.rebuildWidgets();
        }
    }

    private void inputField(GuiGraphicsExtractor guiGraphicsExtractor, String string, int n, int n2, int n3, EditBox editBox, String string2) {
        this.caps(guiGraphicsExtractor, string, n, n2, -9538685);
        int n4 = n2 + 12;
        int n5 = 16;
        boolean bl = editBox != null && editBox.isFocused();
        this.box(guiGraphicsExtractor, n, n4, n3, n5, -16053742, bl ? PURPLE : -14934485);
        String string3 = editBox == null ? "" : editBox.getValue();
        int n6 = n + 7;
        int n7 = n4 + (n5 - 7) / 2;
        if (string3.isEmpty()) {
            this.text(guiGraphicsExtractor, string2, n6, n7, -9538685, 0.74f, false);
        } else {
            String string4 = string3;
            while (string4.length() > 1 && this.w(string4, 0.74f) > n3 - 14) {
                string4 = string4.substring(1);
            }
            this.text(guiGraphicsExtractor, string4, n6, n7, -1, 0.74f, false);
            n6 += this.w(string4, 0.74f) + 1;
        }
        if (bl && System.currentTimeMillis() / 500L % 2L == 0L) {
            guiGraphicsExtractor.fill(n6, n7 - 1, n6 + 1, n7 + 8, -1);
        }
    }

    static void ring(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, float f, int n5, int n6) {
        float f2 = n3;
        float f3 = n3 - n4;
        int n7 = n3 + 1;
        int n8 = -n7;
        while (n8 <= n7) {
            int n9 = 0;
            int n10 = 0;
            int n11 = -n7;
            while (n11 <= n7 + 1) {
                int n12;
                int n13 = 0;
                float f4 = 0.0f;
                if (n11 <= n7) {
                    float f6 = (float)Math.sqrt(((float)n11 + 0.5f) * ((float)n11 + 0.5f) + ((float)n8 + 0.5f) * ((float)n8 + 0.5f));
                    f4 = Math.max(0.0f, Math.min(1.0f, f2 - f6 + 0.5f)) * Math.max(0.0f, Math.min(1.0f, f6 - f3 + 0.5f));
                    if (f4 > 0.0f) {
                        double d = Math.toDegrees(Math.atan2((double)n11 + 0.5, -((double)n8 + 0.5)));
                        if (d < 0.0) {
                            d += 360.0;
                        }
                        n13 = n5 != 0 && d < (double)(f * 360.0f) ? n5 : n6;
                    }
                }
                boolean bl = f4 >= 0.99f;
                int n14 = n12 = bl ? n13 : 0;
                if (n12 != n10) {
                    if (n10 != 0) {
                        guiGraphicsExtractor.fill(n + n9, n2 + n8, n + n11, n2 + n8 + 1, n10);
                    }
                    n9 = n11;
                    n10 = n12;
                }
                if (!bl && f4 > 0.0f) {
                    int n15 = (int)((float)(n13 >>> 24 & 0xFF) * f4);
                    guiGraphicsExtractor.fill(n + n11, n2 + n8, n + n11 + 1, n2 + n8 + 1, n15 << 24 | n13 & 0xFFFFFF);
                }
                ++n11;
            }
            ++n8;
        }
    }

    private void itemIcon(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack, int n, int n2, float f) {
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate((float)n, (float)n2);
        guiGraphicsExtractor.pose().scale(f, f);
        guiGraphicsExtractor.item(itemStack, 0, 0);
        guiGraphicsExtractor.pose().popMatrix();
    }

    private void drawAuctions(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        int n3;
        Object object;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        int n10;
        int n11;
        boolean bl;
        int n12;
        Object object2;
        this.pageTitle(guiGraphicsExtractor, "Auctions", "Pick an item, set the timer and start. Whoever pays you the most before it hits 0 wins.");
        int n13 = this.fx + 10;
        int n14 = this.aLeftW();
        int n15 = this.aTop();
        int n16 = Auction.color;
        int n17 = this.aRingH();
        this.box(guiGraphicsExtractor, n13, n15, n14, n17, -15526625, -14934485);
        String string = "AUCTIONS";
        this.caps(guiGraphicsExtractor, string, n13 + (n14 - this.capsW(string)) / 2, n15 + 9, n16);
        int n18 = n13 + n14 / 2;
        int n19 = Math.max(12, Math.min(64, (n17 - 48) / 2));
        int n20 = n15 + 25 + n19;
        float f = Auction.running ? (float)Auction.left() / (float)Auction.durationMs : 1.0f;
        OverviewScreen.ring(guiGraphicsExtractor, n18, n20, n19, 6, f, n16, Auction.running ? OverviewScreen.alpha(n16, 60) : n16);
        int n21 = (n19 - 6) * 2 - 10;
        float f2 = Math.max(1.2f, Math.min(2.0f, (float)n19 / 30.0f));
        int n22 = (int)(16.0f * f2);
        int n23 = n20 - n19 + 10;
        if (!Auction.item.isEmpty()) {
            this.itemIcon(guiGraphicsExtractor, Auction.item, n18 - n22 / 2, n23, f2);
            if (Auction.quantity > 1) {
                object2 = "x" + Auction.quantity;
                n12 = this.wb((String)object2, 0.55f) + 8;
                int n24 = n18 + n22 / 2 - 2;
                int n25 = n23 + n22 - 10;
                OverviewScreen.round(guiGraphicsExtractor, n24, n25, n12, 11, 3, n16);
                this.text(guiGraphicsExtractor, (String)object2, n24 + 4, n25 + 2, OverviewScreen.onAccentFor(n16), 0.55f, true);
            }
        }
        object2 = Auction.running ? String.format("%d:%02d", Auction.left() / 60000L, Auction.left() / 1000L % 60L) : (Auction.ended ? (Auction.winner != null ? "SOLD" : "NO BIDS") : (Auction.item.isEmpty() ? "PICK" : String.format("%d:%02d", Auction.durationMs / 60000L, Auction.durationMs / 1000L % 60L)));
        float f3 = 1.05f;
        while (f3 > 0.6f && this.wb((String)object2, f3) > n21) {
            f3 -= 0.05f;
        }
        n12 = Auction.item.isEmpty() ? n20 - 8 : n23 + n22 + 4;
        this.text(guiGraphicsExtractor, (String)object2, n18 - this.wb((String)object2, f3) / 2, n12, Auction.running && Auction.left() <= 10000L ? -41385 : -1, f3, true);
        Object object3 = Auction.item.isEmpty() ? "an item" : Auction.displayName();
        while (((String)object3).length() > 3 && this.w((String)object3, 0.52f) > n21 - 8) {
            object3 = ((String)object3).substring(0, ((String)object3).length() - 2) + ".";
        }
        this.text(guiGraphicsExtractor, (String)object3, n18 - this.w((String)object3, 0.52f) / 2, n12 + 15, -7696482, 0.52f, false);
        String string2 = Auction.running ? "Bidding is open" : (Auction.ended && Auction.winner != null ? Auction.winner + " won it  -  " + Money.format(Auction.winAmount) : (Auction.item.isEmpty() ? "Pick an item on the right" : "Ready when you are"));
        this.text(guiGraphicsExtractor, string2, n18 - this.wb(string2, 0.7f) / 2, n15 + n17 - 13, Auction.ended && Auction.winner != null ? -13710223 : -7696482, 0.7f, true);
        Button button = this.startBtn;
        int n26 = button.getX();
        int n27 = button.getY();
        int n28 = button.getWidth();
        int n29 = button.getHeight();
        boolean bl2 = bl = !Auction.running && Auction.item.isEmpty();
        int n30 = Auction.running ? -41385 : (bl ? -12962480 : PURPLE);
        OverviewScreen.round(guiGraphicsExtractor, n26, n27, n28, n29, 3, OverviewScreen.over(button, n, n2) && !bl ? OverviewScreen.mixColor(n30, -1, 0.12f) : n30);
        int n31 = n26 + 10;
        int n32 = n27 + n29 / 2;
        if (Auction.running) {
            guiGraphicsExtractor.fill(n31, n32 - 3, n31 + 6, n32 + 3, -1);
        } else {
            int n33 = 0;
            while (n33 < 4) {
                guiGraphicsExtractor.fill(n31 + n33, n32 - 4 + n33, n31 + n33 + 1, n32 + 4 - n33, -1);
                ++n33;
            }
        }
        String string3 = Auction.running ? "END NOW" : "START AUCTION";
        this.text(guiGraphicsExtractor, string3, n26 + 22, n27 + (n29 - 7) / 2, -1, 0.76f, true);
        button = this.clearBtn;
        boolean bl3 = OverviewScreen.over(button, n, n2);
        this.box(guiGraphicsExtractor, button.getX(), button.getY(), button.getWidth(), button.getHeight(), bl3 ? OverviewScreen.mixColor(-15526625, PURPLE, 0.3f) : -15526625, bl3 ? PURPLE : -14934485);
        this.text(guiGraphicsExtractor, "Clear", button.getX() + (button.getWidth() - this.w("Clear", 0.8f)) / 2, button.getY() + (button.getHeight() - 7) / 2, bl3 ? -1 : -7696482, 0.8f, false);
        String string4 = "Biggest single payment wins (bids don't stack)";
        if (n13 + n14 - this.w(string4, 0.62f) > this.clearBtn.getX() + this.clearBtn.getWidth() + 8) {
            this.text(guiGraphicsExtractor, string4, n13 + n14 - this.w(string4, 0.62f), this.aCtrlY() + 7, -9538685, 0.62f, false);
        }
        OverviewScreen.round(guiGraphicsExtractor, n13 - 6, this.aFieldsY() - 6, n14 + 12, this.aSwitchY() - this.aFieldsY(), 4, OverviewScreen.alpha(-15526625, 200));
        int[] nArray = this.aFields();
        this.inputField(guiGraphicsExtractor, "NAME", nArray[0], this.aFieldsY(), nArray[1], this.nameBox, "Item name");
        this.inputField(guiGraphicsExtractor, "QUANTITY", nArray[2], this.aFieldsY(), nArray[3], this.qtyBox, "1");
        this.inputField(guiGraphicsExtractor, "SECONDS", nArray[4], this.aFieldsY(), nArray[5], this.secBox, "30");
        this.caps(guiGraphicsExtractor, "TIMER COLOR", n13, this.aColorY(), -9538685);
        int n34 = 0;
        while (n34 < this.colorBtns.size()) {
            Button button2 = this.colorBtns.get(n34);
            n11 = button2.getX();
            n10 = button2.getY();
            if (TIMER_COLORS[n34] == n16) {
                OverviewScreen.round(guiGraphicsExtractor, n11 - 1, n10 - 1, 14, 14, 7, -1);
            }
            OverviewScreen.round(guiGraphicsExtractor, n11, n10, 12, 12, 6, TIMER_COLORS[n34]);
            ++n34;
        }
        int[] nArray2 = this.aPicker();
        int n35 = 4;
        n11 = 0;
        while (n11 < nArray2[2]) {
            n10 = 0;
            while (n10 < nArray2[3]) {
                float f4 = (float)n11 / (float)Math.max(1, nArray2[2] - 1);
                float f5 = 1.0f - (float)n10 / (float)Math.max(1, nArray2[3] - 1);
                guiGraphicsExtractor.fill(nArray2[0] + n11, nArray2[1] + n10, nArray2[0] + Math.min(nArray2[2], n11 + n35), nArray2[1] + Math.min(nArray2[3], n10 + 3), OverviewScreen.hsv(hue, f4, f5));
                n10 += 3;
            }
            n11 += n35;
        }
        n11 = nArray2[0] + Math.round(sat * (float)(nArray2[2] - 1));
        n10 = nArray2[1] + Math.round((1.0f - val) * (float)(nArray2[3] - 1));
        OverviewScreen.ring(guiGraphicsExtractor, n11, n10, 3, 1, 1.0f, -1, -1);
        int n36 = 0;
        while (n36 < nArray2[3]) {
            guiGraphicsExtractor.fill(nArray2[4], nArray2[1] + n36, nArray2[4] + 8, nArray2[1] + Math.min(nArray2[3], n36 + 2), OverviewScreen.hsv((float)n36 / (float)nArray2[3] * 360.0f, 1.0f, 1.0f));
            n36 += 2;
        }
        n36 = nArray2[1] + Math.round(hue / 360.0f * (float)(nArray2[3] - 1));
        guiGraphicsExtractor.fill(nArray2[4] - 1, n36 - 1, nArray2[4] + 9, n36 + 1, -1);
        OverviewScreen.round(guiGraphicsExtractor, nArray2[5], nArray2[1], nArray2[6], nArray2[3], 4, n16);
        String string5 = Settings.hex(n16 & 0xFFFFFF);
        this.text(guiGraphicsExtractor, string5, nArray2[5] + (nArray2[6] - this.w(string5, 0.62f)) / 2, nArray2[1] + (nArray2[3] - 7) / 2, OverviewScreen.onAccentFor(n16), 0.62f, true);
        Button button3 = this.switchBtn;
        int n37 = button3.getX();
        int n38 = button3.getY();
        int n39 = button3.getWidth();
        int n40 = button3.getHeight();
        this.box(guiGraphicsExtractor, n37, n38, n39, n40, OverviewScreen.over(button3, n, n2) ? -15198938 : -15526625, -14934485);
        this.text(guiGraphicsExtractor, "Auction switch", n37 + 10, n38 + 5, -1, 0.76f, true);
        this.text(guiGraphicsExtractor, "On: while an auction runs, money paid to you counts as bids, not bets.", n37 + 10, n38 + 16, -9538685, 0.6f, false);
        int n41 = 24;
        int n42 = 12;
        int n43 = n37 + n39 - n41 - 10;
        int n44 = n38 + (n40 - n42) / 2;
        OverviewScreen.round(guiGraphicsExtractor, n43, n44, n41, n42, 6, Auction.switchOn ? PURPLE : -14014912);
        OverviewScreen.round(guiGraphicsExtractor, Auction.switchOn ? n43 + n41 - 10 : n43 + 2, n44 + 2, 8, 8, 4, Auction.switchOn ? -15526625 : -9738620);
        int n45 = this.aLastY();
        int n46 = this.contentBottom() - n45;
        if (n46 > 24) {
            Object object4;
            n9 = (n14 - 6) / 2;
            n8 = n13 + n9 + 6;
            this.box(guiGraphicsExtractor, n13, n45, n9, n46, -15526625, -14934485);
            this.caps(guiGraphicsExtractor, "TOP BIDDERS", n13 + 10, n45 + 8, -1);
            List<Map.Entry<String, Long>> list = Auction.sortedBids();
            int n47 = 15;
            int n48 = n45 + 22;
            n7 = Math.max(0, (n45 + n46 - 4 - n48) / n47);
            n6 = 0;
            while (n6 < Math.min(n7, list.size())) {
                Map.Entry<String, Long> entry = list.get(n6);
                n5 = n48 + n6 * n47;
                if (n6 == 0) {
                    OverviewScreen.round(guiGraphicsExtractor, n13 + 4, n5 - 2, n9 - 8, n47 - 1, 3, OverviewScreen.alpha(n16, 40));
                }
                this.face(guiGraphicsExtractor, entry.getKey(), n13 + 8, n5, 10);
                Object object5 = entry.getKey();
                while (((String)object5).length() > 3 && this.wb((String)object5, 0.62f) > n9 - 80) {
                    object5 = ((String)object5).substring(0, ((String)object5).length() - 2) + ".";
                }
                this.text(guiGraphicsExtractor, (String)object5, n13 + 22, n5 + 1, -1, 0.62f, true);
                object4 = Money.format(entry.getValue());
                this.text(guiGraphicsExtractor, (String)object4, n13 + n9 - 8 - this.wb((String)object4, 0.62f), n5 + 1, -13710223, 0.62f, true);
                ++n6;
            }
            if (list.isEmpty()) {
                this.text(guiGraphicsExtractor, Auction.running ? "Waiting for bids..." : "No bids yet", n13 + 10, n48, -9538685, 0.62f, false);
            }
            this.box(guiGraphicsExtractor, n8, n45, n14 - n9 - 6, n46, -15526625, -14934485);
            this.caps(guiGraphicsExtractor, "LAST AUCTIONS", n8 + 10, n45 + 8, -1);
            n6 = n14 - n9 - 6;
            n4 = 0;
            while (n4 < Math.min(n7, Auction.history.size())) {
                Auction.Past past = Auction.history.get(n4);
                int n49 = n48 + n4 * n47;
                object4 = (String)(past.qty() > 1 ? past.qty() + "x " : "") + past.name();
                String string6 = past.winner() == null ? "no bids" : past.winner();
                String string7 = (String)object4 + " > " + string6;
                while (string7.length() > 4 && this.w(string7, 0.58f) > n6 - 70) {
                    string7 = string7.substring(0, string7.length() - 2) + ".";
                }
                this.text(guiGraphicsExtractor, string7, n8 + 10, n49 + 1, -7696482, 0.58f, false);
                String string8 = past.winner() == null ? "-" : Money.format(past.amount());
                this.text(guiGraphicsExtractor, string8, n8 + n6 - 8 - this.wb(string8, 0.6f), n49 + 1, past.winner() == null ? -9538685 : -13710223, 0.6f, true);
                ++n4;
            }
            if (Auction.history.isEmpty()) {
                this.text(guiGraphicsExtractor, "Finished auctions show up here", n8 + 10, n48, -9538685, 0.6f, false);
            }
        }
        n9 = this.fx + 10 + n14 + 8;
        n8 = this.fx + this.fw - 10 - n9;
        int n50 = this.contentBottom() - 92 - 8 - n15;
        this.box(guiGraphicsExtractor, n9, n15, n8, n50, -15526625, -14934485);
        this.caps(guiGraphicsExtractor, "PICK AN ITEM", n9 + 10, n15 + 9, -1);
        if (!Auction.item.isEmpty()) {
            this.text(guiGraphicsExtractor, Auction.itemName, n9 + n8 - 10 - this.wb(Auction.itemName, 0.7f), n15 + 9, n16, 0.7f, true);
        }
        this.searchField(guiGraphicsExtractor, n9 + 10, n15 + 24, n8 - 20, 18, this.itemSearch, "Search every item and block...");
        int[] nArray3 = this.aGrid();
        List<Integer> list = this.filteredItems();
        n7 = Math.max(1, nArray3[2] / 26);
        n6 = Math.max(1, nArray3[3] / 26);
        n4 = (list.size() + n7 - 1) / n7;
        scroll = Math.max(0, Math.min(scroll, Math.max(0, n4 - n6)));
        n5 = 0;
        while (n5 < n6) {
            int n51;
            int n52 = 0;
            while (n52 < n7 && (n51 = (scroll + n5) * n7 + n52) < list.size()) {
                boolean bl4;
                int n53 = nArray3[0] + n52 * 26;
                int n54 = nArray3[1] + n5 * 26;
                object = allItems.get(list.get(n51));
                n3 = n >= n53 && n < n53 + 26 && n2 >= n54 && n2 < n54 + 26 ? 1 : 0;
                boolean bl5 = bl4 = !Auction.item.isEmpty() && Auction.item.getItem() == ((ItemStack)object).getItem();
                if (bl4) {
                    this.box(guiGraphicsExtractor, n53, n54, 24, 24, OverviewScreen.alpha(n16, 60), n16);
                } else if (n3 != 0) {
                    OverviewScreen.round(guiGraphicsExtractor, n53, n54, 24, 24, 3, -14804434);
                }
                this.itemIcon(guiGraphicsExtractor, (ItemStack)object, n53 + 2, n54 + 2, 1.25f);
                ++n52;
            }
            ++n5;
        }
        this.scrollbar(guiGraphicsExtractor, nArray3[0] + nArray3[2] + 4, nArray3[1], nArray3[3], n6, n4);
        this.text(guiGraphicsExtractor, list.size() + " items", n9 + 10, n15 + n50 - 12, -9538685, 0.64f, false);
        n5 = n15 + n50 + 8;
        this.box(guiGraphicsExtractor, n9, n5, n8, 92, -15526625, -14934485);
        this.caps(guiGraphicsExtractor, "TOP BIDDERS", n9 + 10, n5 + 8, -1);
        List<Map.Entry<String, Long>> list2 = Auction.sortedBids();
        int n55 = 15;
        int n56 = n5 + 24;
        int n57 = Math.max(0, (n5 + 92 - 4 - n56) / n55);
        int n58 = 0;
        while (n58 < Math.min(n57, list2.size())) {
            object = list2.get(n58);
            n3 = n56 + n58 * n55;
            if (n58 == 0) {
                OverviewScreen.round(guiGraphicsExtractor, n9 + 4, n3 - 2, n8 - 8, n55 - 1, 3, OverviewScreen.alpha(n16, 40));
            }
            @SuppressWarnings("unchecked") Map.Entry<String, Long> bid = (Map.Entry<String, Long>)object;
            this.face(guiGraphicsExtractor, bid.getKey(), n9 + 8, n3, 10);
            this.text(guiGraphicsExtractor, bid.getKey(), n9 + 22, n3 + 1, -1, 0.64f, true);
            String string9 = Money.format(bid.getValue());
            this.text(guiGraphicsExtractor, string9, n9 + n8 - 10 - this.wb(string9, 0.64f), n3 + 1, -13710223, 0.64f, true);
            ++n58;
        }
        if (list2.isEmpty()) {
            this.text(guiGraphicsExtractor, Auction.running ? "Waiting for bids..." : "No bids yet", n9 + 10, n56, -9538685, 0.64f, false);
        }
    }

    private static int onAccentFor(int n) {
        int n2 = n >> 16 & 0xFF;
        int n3 = n >> 8 & 0xFF;
        int n4 = n & 0xFF;
        return (n2 * 299 + n3 * 587 + n4 * 114) / 1000 > 170 ? -15658735 : -1;
    }

    private void stat(GuiGraphicsExtractor guiGraphicsExtractor, String string, String string2, int n, int n2, int n3) {
        this.caps(guiGraphicsExtractor, string, n, n2, -7696482);
        this.text(guiGraphicsExtractor, string2, n, n2 + 13, n3, 1.15f, true);
    }

    private record SRow(String title, String about, int kind, BooleanSupplier get, Consumer<Boolean> set) {
    }
}

