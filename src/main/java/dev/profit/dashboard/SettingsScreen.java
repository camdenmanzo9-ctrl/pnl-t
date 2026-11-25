/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.ConfirmScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Alerts;
import dev.profit.dashboard.DashboardColorPicker;
import dev.profit.dashboard.Money;
import dev.profit.dashboard.MoveHudsScreen;
import dev.profit.dashboard.OverviewScreen;
import dev.profit.dashboard.PayConfirmScreen;
import dev.profit.dashboard.Settings;
import dev.profit.dashboard.SettingsColorValues;
import dev.profit.dashboard.TypedPayValues;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class SettingsScreen
extends Screen {
    private int[] embeddedBounds;
    private static Section section = Section.GENERAL;
    private final Screen parent;
    private final List<Row> rows = new ArrayList<Row>();
    private final List<Button> sideButtons = new ArrayList<Button>();
    private final List<Section> sideSections = new ArrayList<Section>();
    private Button doneButton;
    private Button closeButton;
    private int wx;
    private int wy;
    private int ww;
    private int wh;
    private static final int WIN = -234089438;
    private static final int HEAD = -15655890;
    private static final int ROW = -15326406;
    private static final int TEXT = -1511169;
    private static final int MUTED = -8087888;
    private static final int VALUE = -7420417;
    private static final int BOX = -16446956;
    private static final int KNOB = -1;
    private static final int OFF = -14009771;
    private static final int ROW_H = 20;
    private static final int ROW_GAP = 5;
    private static final int WIN_BG = -183036137;
    private static final int HEADER = -15000805;
    private static final int ROW_BG = -14277082;
    private static final int ROW_HOVER = -13750738;
    private static final int T_TEXT = -986896;
    private static final int T_MUTED = -4934476;
    private static final int T_DIM = -7566196;
    private static final int YELLOW = -1522361;
    private long openedAt = System.currentTimeMillis();
    private Object pendingFocus;
    private static final int[] THEME_PRESETS = new int[]{0xFF8800, 8313175, 2733814, 9137151, 16742832, 0xFF5757, 16765786, 4249797};
    private static final String[] THEME_NAMES = new String[]{"Orange", "Green", "Blue", "Purple", "Pink", "Red", "Gold", "Teal"};

    void mount(int n, int n2, int n3, int n4, int n5, int n6) {
        this.width = n;
        this.height = n2;
        this.embeddedBounds = new int[]{n3, n4, n5, n6};
        this.rebuildWidgets();
    }

    boolean contains(double d, double d2) {
        return this.embeddedBounds != null && d >= (double)this.wx && d < (double)(this.wx + this.ww) && d2 >= (double)this.wy && d2 < (double)(this.wy + this.wh);
    }

    private Screen returnScreen() {
        return this.embeddedBounds == null ? this : this.parent;
    }

    public SettingsScreen(Screen screen) {
        super((Component)Component.literal((String)"Dashboard Mod"));
        this.parent = screen;
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void onClose() {
        Screen screen;
        Settings.save();
        if (this.embeddedBounds != null && (screen = this.parent) instanceof OverviewScreen) {
            OverviewScreen overviewScreen = (OverviewScreen)screen;
            overviewScreen.closeEmbeddedSettings();
            return;
        }
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    private static int accent() {
        return 0xFF000000 | Settings.S.settingsThemeAccent & 0xFFFFFF;
    }

    static int theme(float f) {
        int n = SettingsScreen.accent();
        int n2 = 1514014;
        int n3 = Math.round((float)(n2 >> 16 & 0xFF) * (1.0f - f) + (float)(n >> 16 & 0xFF) * f);
        int n4 = Math.round((float)(n2 >> 8 & 0xFF) * (1.0f - f) + (float)(n >> 8 & 0xFF) * f);
        int n5 = Math.round((float)(n2 & 0xFF) * (1.0f - f) + (float)(n & 0xFF) * f);
        return 0xFF000000 | n3 << 16 | n4 << 8 | n5;
    }

    private static int mix(int n, float f) {
        int n2 = (int)((float)(n >> 16 & 0xFF) * f);
        int n3 = (int)((float)(n >> 8 & 0xFF) * f);
        int n4 = (int)((float)(n & 0xFF) * f);
        return 0xFF000000 | n2 << 16 | n3 << 8 | n4;
    }

    private Row toggle(String string, BooleanSupplier booleanSupplier, Consumer<Boolean> consumer) {
        Row row = new Row();
        row.kind = Kind.TOGGLE;
        row.label = string;
        row.on = booleanSupplier;
        row.setOn = consumer;
        this.rows.add(row);
        return row;
    }

    private Row stepper(String string, Supplier<String> supplier, Runnable runnable, Runnable runnable2) {
        Row row = new Row();
        row.kind = Kind.STEPPER;
        row.label = string;
        row.value = supplier;
        row.minus = runnable;
        row.plus = runnable2;
        this.rows.add(row);
        return row;
    }

    private Row picker(String string, Supplier<String> supplier, Runnable runnable, Runnable runnable2) {
        Row row = new Row();
        row.kind = Kind.PICKER;
        row.label = string;
        row.value = supplier;
        row.minus = runnable;
        row.plus = runnable2;
        this.rows.add(row);
        return row;
    }

    private Row color(String string, IntSupplier intSupplier, IntConsumer intConsumer, boolean bl) {
        Row row = new Row();
        row.kind = Kind.COLOR;
        row.label = string;
        row.color = intSupplier;
        row.setColor = intConsumer;
        row.bg = bl;
        this.rows.add(row);
        return row;
    }

    private Row textRow(String string, Supplier<String> supplier, Consumer<String> consumer) {
        Row row = new Row();
        row.kind = Kind.TEXT;
        row.label = string;
        row.textGet = supplier;
        row.textSet = consumer;
        this.rows.add(row);
        return row;
    }

    private Row action(String string, Runnable runnable) {
        Row row = new Row();
        row.kind = Kind.ACTION;
        row.label = string;
        row.action = runnable;
        this.rows.add(row);
        return row;
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, (float)Math.round(f * 10.0f) / 10.0f));
    }

    private static int clampI(int n, int n2, int n3) {
        return Math.max(n2, Math.min(n3, n));
    }

    private static void cycleBackground(int n) {
        Settings settings = Settings.S;
        int n2 = settings.settingsAnimatedBackground ? 2 : (settings.bgGradient ? 1 : 0);
        int n3 = Math.floorMod(n2 + n, 3);
        settings.settingsAnimatedBackground = n3 == 2;
        settings.bgGradient = n3 != 0;
    }

    private static int presetIndex() {
        int n = 0;
        while (n < THEME_PRESETS.length) {
            if (THEME_PRESETS[n] == Settings.S.settingsThemeAccent) {
                return n;
            }
            ++n;
        }
        return -1;
    }

    private static void cyclePreset(int n) {
        int n2 = SettingsScreen.presetIndex();
        Settings.S.settingsThemeAccent = THEME_PRESETS[Math.floorMod((n2 < 0 ? 0 : n2) + n, THEME_PRESETS.length)];
    }

    private void buildRows() {
        this.rows.clear();
        Settings settings = Settings.S;
        switch (section.ordinal()) {
            case 0: {
                this.picker("Background", () -> settings.settingsAnimatedBackground ? "Animated" : (settings.bgGradient ? "Gradient" : "Solid"), () -> SettingsScreen.cycleBackground(-1), () -> SettingsScreen.cycleBackground(1));
                this.toggle("Black backdrop", () -> settings.settingsBlackBackdrop, bl -> {
                    boolean bl2 = settings.settingsBlackBackdrop = bl.booleanValue();
                });
                this.toggle("Colored tabs", () -> settings.settingsColoredTabs, bl -> {
                    boolean bl2 = settings.settingsColoredTabs = bl.booleanValue();
                });
                this.toggle("Rainbow header", () -> settings.settingsRainbowHeader, bl -> {
                    boolean bl2 = settings.settingsRainbowHeader = bl.booleanValue();
                });
                this.picker("Color preset", () -> SettingsScreen.presetIndex() < 0 ? "Custom" : THEME_NAMES[SettingsScreen.presetIndex()], () -> SettingsScreen.cyclePreset(-1), () -> SettingsScreen.cyclePreset(1));
                this.color("Top tint", () -> settings.bgTop, n -> {
                    settings.bgTop = n;
                }, true);
                this.color("Bottom tint", () -> settings.bgBottom, n -> {
                    settings.bgBottom = n;
                }, true);
                this.stepper("Opacity", () -> settings.bgOpacity + "%", () -> {
                    settings.bgOpacity = SettingsScreen.clampI(settings.bgOpacity - 5, 30, 100);
                }, () -> {
                    settings.bgOpacity = SettingsScreen.clampI(settings.bgOpacity + 5, 30, 100);
                });
                this.stepper("Brightness", () -> (settings.bgBrightness >= 0 ? "+" : "") + settings.bgBrightness + "%", () -> {
                    settings.bgBrightness = SettingsScreen.clampI(settings.bgBrightness - 5, 0, 50);
                }, () -> {
                    settings.bgBrightness = SettingsScreen.clampI(settings.bgBrightness + 5, 0, 50);
                });
                this.stepper("Dim game", () -> settings.dimGame + "%", () -> {
                    settings.dimGame = SettingsScreen.clampI(settings.dimGame - 5, 0, 95);
                }, () -> {
                    settings.dimGame = SettingsScreen.clampI(settings.dimGame + 5, 0, 95);
                });
                this.toggle("Skin faces", () -> settings.skinFaces, bl -> {
                    settings.skinFaces = bl;
                });
                this.action("Reset all settings", () -> {
                    Settings.resetAll();
                    this.rebuildWidgets();
                });
                this.color("Theme color", () -> settings.settingsThemeAccent, n -> {
                    settings.settingsThemeAccent = n & 0xFFFFFF;
                }, false);
                break;
            }
            case 1: {
                this.toggle("Chat colors", () -> settings.chatColors, bl -> {
                    settings.chatColors = bl;
                });
                this.toggle("Count tax in profit", () -> settings.countTax, bl -> {
                    settings.countTax = bl;
                });
                this.color("Name", () -> settings.nameColor, n -> {
                    settings.nameColor = n;
                }, false);
                this.color("Received $", () -> settings.receivedColor, n -> {
                    settings.receivedColor = n;
                }, false);
                this.color("Sent $", () -> settings.sentColor, n -> {
                    settings.sentColor = n;
                }, false);
                this.color("Tax text", () -> settings.taxColor, n -> {
                    settings.taxColor = n;
                }, false);
                this.toggle("Color the tax", () -> settings.colorTax, bl -> {
                    settings.colorTax = bl;
                });
                this.toggle("Bold names", () -> settings.boldNames, bl -> {
                    settings.boldNames = bl;
                });
                this.toggle("Bold amounts", () -> settings.boldAmounts, bl -> {
                    settings.boldAmounts = bl;
                });
                this.stepper("Tax %", () -> String.valueOf(settings.taxPct), () -> {
                    settings.taxPct = SettingsScreen.clamp(settings.taxPct - 0.5f, 0.0f, 20.0f);
                }, () -> {
                    settings.taxPct = SettingsScreen.clamp(settings.taxPct + 0.5f, 0.0f, 20.0f);
                });
                break;
            }
            case 2: {
                this.toggle("Hide small", () -> settings.filterOn, bl -> {
                    settings.filterOn = bl;
                });
                this.textRow("Hide under", () -> Long.toString(settings.filterUnder), string -> TypedPayValues.money(string).ifPresent(l -> {
                    long l2 = settings.filterUnder = l;
                }));
                this.toggle("Hide received", () -> settings.filterReceived, bl -> {
                    settings.filterReceived = bl;
                });
                this.toggle("Hide sent", () -> settings.filterSent, bl -> {
                    settings.filterSent = bl;
                });
                break;
            }
            case 4: {
                this.toggle("Show streak", () -> settings.streakShow, bl -> {
                    settings.streakShow = bl;
                });
                this.toggle("Separate HUD", () -> settings.streakSeparate, bl -> {
                    settings.streakSeparate = bl;
                });
                this.toggle("/win and /lose", () -> settings.streakCommands, bl -> {
                    settings.streakCommands = bl;
                });
                this.stepper("Size", () -> settings.streakSize + "x", () -> {
                    settings.streakSize = SettingsScreen.clamp(settings.streakSize - 0.1f, 0.5f, 2.0f);
                }, () -> {
                    settings.streakSize = SettingsScreen.clamp(settings.streakSize + 0.1f, 0.5f, 2.0f);
                });
                this.toggle("Accent bar", () -> settings.streakAccent, bl -> {
                    settings.streakAccent = bl;
                });
                this.toggle("Border", () -> settings.streakBorder, bl -> {
                    settings.streakBorder = bl;
                });
                this.stepper("Opacity", () -> settings.streakOpacity + "%", () -> {
                    settings.streakOpacity = SettingsScreen.clampI(settings.streakOpacity - 5, 0, 100);
                }, () -> {
                    settings.streakOpacity = SettingsScreen.clampI(settings.streakOpacity + 5, 0, 100);
                });
                this.action("Reset streak", Alerts::resetStreak);
                break;
            }
            case 6: {
                String[] stringArray = new String[]{"Pop", "Slide", "Fade"};
                this.picker("Animation", () -> stringArray[Math.floorMod(settings.popupAnim, 3)], () -> {
                    settings.popupAnim = Math.floorMod(settings.popupAnim - 1, 3);
                }, () -> {
                    settings.popupAnim = Math.floorMod(settings.popupAnim + 1, 3);
                });
                this.stepper("Opacity", () -> settings.popupOpacity + "%", () -> {
                    settings.popupOpacity = SettingsScreen.clampI(settings.popupOpacity - 5, 20, 100);
                }, () -> {
                    settings.popupOpacity = SettingsScreen.clampI(settings.popupOpacity + 5, 20, 100);
                });
                this.stepper("Border width", () -> settings.popupBorderW + "px", () -> {
                    settings.popupBorderW = SettingsScreen.clampI(settings.popupBorderW - 1, 1, 4);
                }, () -> {
                    settings.popupBorderW = SettingsScreen.clampI(settings.popupBorderW + 1, 1, 4);
                });
                this.stepper("Glow", () -> settings.popupGlowPct + "%", () -> {
                    settings.popupGlowPct = SettingsScreen.clampI(settings.popupGlowPct - 10, 0, 100);
                }, () -> {
                    settings.popupGlowPct = SettingsScreen.clampI(settings.popupGlowPct + 10, 0, 100);
                });
                this.stepper("Sparkles", () -> String.valueOf(settings.popupSparkleCount), () -> {
                    settings.popupSparkleCount = SettingsScreen.clampI(settings.popupSparkleCount - 1, 0, 30);
                }, () -> {
                    settings.popupSparkleCount = SettingsScreen.clampI(settings.popupSparkleCount + 1, 0, 30);
                });
                this.toggle("+ before amount", () -> settings.popupPlus, bl -> {
                    settings.popupPlus = bl;
                });
                this.toggle("Count-up amount", () -> settings.popupCountUp, bl -> {
                    settings.popupCountUp = bl;
                });
                this.toggle("Face bounce", () -> settings.popupFaceBounce, bl -> {
                    settings.popupFaceBounce = bl;
                });
                this.toggle("Timer bar", () -> settings.popupTimerBar, bl -> {
                    settings.popupTimerBar = bl;
                });
                this.color("Timer color", () -> settings.popupTimerColor, n -> {
                    settings.popupTimerColor = n;
                }, false);
                this.action("Test popup (closes menu)", () -> {
                    Alerts.testPopup();
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(null);
                    }
                });
                break;
            }
            case 3: {
                this.toggle("Show HUD", () -> settings.hudShow, bl -> {
                    settings.hudShow = bl;
                });
                this.toggle("Accent bar", () -> settings.hudAccentBar, bl -> {
                    settings.hudAccentBar = bl;
                });
                this.toggle("Border", () -> settings.hudBorder, bl -> {
                    settings.hudBorder = bl;
                });
                this.stepper("Size", () -> settings.hudSize + "x", () -> {
                    settings.hudSize = SettingsScreen.clamp(settings.hudSize - 0.1f, 0.5f, 2.0f);
                }, () -> {
                    settings.hudSize = SettingsScreen.clamp(settings.hudSize + 0.1f, 0.5f, 2.0f);
                });
                this.stepper("Opacity", () -> settings.hudOpacity + "%", () -> {
                    settings.hudOpacity = SettingsScreen.clampI(settings.hudOpacity - 5, 10, 100);
                }, () -> {
                    settings.hudOpacity = SettingsScreen.clampI(settings.hudOpacity + 5, 10, 100);
                });
                this.color("Title", () -> settings.hudTitle, n -> {
                    settings.hudTitle = n;
                }, false);
                this.color("Gain", () -> settings.hudGain, n -> {
                    settings.hudGain = n;
                }, false);
                this.color("Loss", () -> settings.hudLoss, n -> {
                    settings.hudLoss = n;
                }, false);
                this.color("Zero", () -> settings.hudZero, n -> {
                    settings.hudZero = n;
                }, false);
                this.color("BG", () -> settings.hudBg, n -> {
                    settings.hudBg = n;
                }, true);
                this.color("Accent", () -> settings.hudAccentColor, n -> {
                    settings.hudAccentColor = n;
                }, false);
                String[] stringArray = new String[]{"Top left", "Top right", "Bottom left", "Bottom right"};
                this.picker("Position", () -> stringArray[settings.hudCorner], () -> {
                    settings.hudCorner = Math.floorMod(settings.hudCorner - 1, 4);
                }, () -> {
                    settings.hudCorner = Math.floorMod(settings.hudCorner + 1, 4);
                });
                break;
            }
            case 5: {
                this.toggle("Show popup", () -> settings.popupShow, bl -> {
                    settings.popupShow = bl;
                });
                this.toggle("Skin face", () -> settings.popupFace, bl -> {
                    settings.popupFace = bl;
                });
                this.picker("Min", () -> Money.format(settings.popupMin), () -> {
                    settings.popupMin = Settings.stepMoney(settings.popupMin, -1);
                }, () -> {
                    settings.popupMin = Settings.stepMoney(settings.popupMin, 1);
                });
                this.stepper("Duration", () -> settings.popupDuration + "s", () -> {
                    settings.popupDuration = SettingsScreen.clamp(settings.popupDuration - 0.5f, 1.0f, 10.0f);
                }, () -> {
                    settings.popupDuration = SettingsScreen.clamp(settings.popupDuration + 0.5f, 1.0f, 10.0f);
                });
                this.stepper("Size", () -> settings.popupSize + "x", () -> {
                    settings.popupSize = SettingsScreen.clamp(settings.popupSize - 0.1f, 0.5f, 2.0f);
                }, () -> {
                    settings.popupSize = SettingsScreen.clamp(settings.popupSize + 0.1f, 0.5f, 2.0f);
                });
                this.toggle("Rainbow border", () -> settings.popupRainbow, bl -> {
                    settings.popupRainbow = bl;
                });
                this.toggle("Sparkles", () -> settings.popupSparkles, bl -> {
                    settings.popupSparkles = bl;
                });
                this.toggle("Glow", () -> settings.popupGlow, bl -> {
                    settings.popupGlow = bl;
                });
                this.toggle("Shine", () -> settings.popupShine, bl -> {
                    settings.popupShine = bl;
                });
                this.color("Amount", () -> settings.popupAmount, n -> {
                    settings.popupAmount = n;
                }, false);
                this.color("Name", () -> settings.popupName, n -> {
                    settings.popupName = n;
                }, false);
                this.color("BG", () -> settings.popupBg, n -> {
                    settings.popupBg = n;
                }, true);
                this.color("Border", () -> settings.popupBorder, n -> {
                    settings.popupBorder = n;
                }, false);
                this.action("Test popup (closes menu)", () -> {
                    Alerts.testPopup();
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(null);
                    }
                });
                break;
            }
            case 7: {
                this.toggle("Popup sound", () -> settings.soundPopup, bl -> {
                    settings.soundPopup = bl;
                });
                this.picker("Sound", () -> Alerts.SOUND_NAMES[Math.floorMod(settings.soundIndex, Alerts.SOUND_NAMES.length)], () -> {
                    settings.soundIndex = Math.floorMod(settings.soundIndex - 1, Alerts.SOUND_NAMES.length);
                }, () -> {
                    settings.soundIndex = Math.floorMod(settings.soundIndex + 1, Alerts.SOUND_NAMES.length);
                });
                this.stepper("Volume", () -> settings.soundVolume + "%", () -> {
                    settings.soundVolume = SettingsScreen.clampI(settings.soundVolume - 10, 0, 100);
                }, () -> {
                    settings.soundVolume = SettingsScreen.clampI(settings.soundVolume + 10, 0, 100);
                });
                this.stepper("Pitch", () -> String.valueOf(settings.soundPitch), () -> {
                    settings.soundPitch = SettingsScreen.clamp(settings.soundPitch - 0.1f, 0.5f, 2.0f);
                }, () -> {
                    settings.soundPitch = SettingsScreen.clamp(settings.soundPitch + 0.1f, 0.5f, 2.0f);
                });
                this.stepper("Repeat", () -> settings.soundRepeat + "x", () -> {
                    settings.soundRepeat = SettingsScreen.clampI(settings.soundRepeat - 1, 1, 5);
                }, () -> {
                    settings.soundRepeat = SettingsScreen.clampI(settings.soundRepeat + 1, 1, 5);
                });
                this.toggle("Also when queued", () -> settings.soundWhenQueued, bl -> {
                    settings.soundWhenQueued = bl;
                });
                this.action("Preview popup sound", () -> Alerts.playSound(settings.soundIndex, settings.soundRepeat));
                this.toggle("Confirm sound", () -> settings.soundConfirm, bl -> {
                    settings.soundConfirm = bl;
                });
                this.picker("Confirm", () -> Alerts.SOUND_NAMES[Math.floorMod(settings.confirmSoundIndex, Alerts.SOUND_NAMES.length)], () -> {
                    settings.confirmSoundIndex = Math.floorMod(settings.confirmSoundIndex - 1, Alerts.SOUND_NAMES.length);
                }, () -> {
                    settings.confirmSoundIndex = Math.floorMod(settings.confirmSoundIndex + 1, Alerts.SOUND_NAMES.length);
                });
                this.action("Preview confirm sound", () -> Alerts.playSound(settings.confirmSoundIndex, 1));
                break;
            }
            case 8: {
                this.toggle("Ask before /pay", () -> settings.payAsk, bl -> {
                    settings.payAsk = bl;
                });
                this.textRow("Ask from", () -> Long.toString(settings.payFrom), string -> TypedPayValues.money(string).ifPresent(l -> {
                    long l2 = settings.payFrom = l;
                }));
                this.textRow("Delay (s)", () -> Float.toString(settings.payDelay), string -> TypedPayValues.decimal(string, 5.0).ifPresent(d -> {
                    float f = settings.payDelay = (float)d;
                }));
                this.textRow("Tax %", () -> Float.toString(settings.payTax), string -> TypedPayValues.decimal(string, 20.0).ifPresent(d -> {
                    float f = settings.payTax = (float)d;
                }));
                this.toggle("Show tax", () -> settings.payShowTax, bl -> {
                    settings.payShowTax = bl;
                });
                this.toggle("Skin face", () -> settings.skinFaces, bl -> {
                    settings.skinFaces = bl;
                });
                this.toggle("Rainbow border", () -> settings.payRainbow, bl -> {
                    settings.payRainbow = bl;
                });
                this.textRow("Dim %", () -> Integer.toString(settings.payDim), string -> TypedPayValues.decimal(string, 95.0).ifPresent(d -> {
                    int n = settings.payDim = (int)Math.round(d);
                }));
                this.color("Accent", () -> settings.payAccent, n -> {
                    settings.payAccent = n;
                }, false);
                this.color("BG", () -> settings.payBg, n -> {
                    settings.payBg = n;
                }, true);
                this.color("Name", () -> settings.payName, n -> {
                    settings.payName = n;
                }, false);
                this.color("Amount", () -> settings.payAmount, n -> {
                    settings.payAmount = n;
                }, false);
                this.color("Confirm btn", () -> settings.payConfirm, n -> {
                    settings.payConfirm = n;
                }, false);
                this.color("Cancel btn", () -> settings.payCancel, n -> {
                    settings.payCancel = n;
                }, false);
                this.action("Test confirm screen", () -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen((Screen)new PayConfirmScreen("", "ExamplePlayer", Math.max(settings.payFrom, 5000000000L), true));
                    }
                });
            }
        }
    }

    protected void init() {
        this.openedAt = System.currentTimeMillis();
        this.ww = Math.min(this.width - 24, 520);
        this.wh = Math.min(this.height - 16, 300);
        this.wx = (this.width - this.ww) / 2;
        this.wy = (this.height - this.wh) / 2;
        if (this.embeddedBounds != null) {
            this.wx = this.embeddedBounds[0];
            this.wy = this.embeddedBounds[1];
            this.ww = this.embeddedBounds[2];
            this.wh = this.embeddedBounds[3];
        }
        this.sideButtons.clear();
        this.sideSections.clear();
        int n2 = this.wy + 36;
        Section[] sectionArray = Section.values();
        int n3 = sectionArray.length;
        int n4 = 0;
        while (n4 < n3) {
            Section section = sectionArray[n4];
            Button button2 = (Button)this.addWidget((GuiEventListener)Button.builder((Component)Component.literal((String)section.label), button -> {
                SettingsScreen.section = section;
                this.rebuildWidgets();
            }).bounds(this.wx + 12, n2, 102, 16).build());
            this.sideButtons.add(button2);
            this.sideSections.add(section);
            n2 += 18;
            ++n4;
        }
        this.doneButton = (Button)this.addWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"Move HUDs"), button -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen((Screen)new MoveHudsScreen(this.returnScreen()));
            }
        }).bounds(this.wx + 12, this.wy + this.wh - 48, 102, 18).build());
        this.closeButton = this.invisible(this.wx + 12, this.wy + this.wh - 26, 102, 18, this::onClose);
        this.buildRows();
        n4 = this.wx + 130;
        n3 = this.ww - 140;
        int n5 = (n3 - 8) / 2;
        int n6 = this.wy + 52;
        int n7 = 0;
        while (n7 < this.rows.size()) {
            Row row = this.rows.get(n7);
            int n8 = n7 % 2;
            int n9 = n7 / 2;
            row.x = n4 + n8 * (n5 + 8);
            row.y = n6 + n9 * 25;
            row.w = n5;
            switch (row.kind.ordinal()) {
                case 0: {
                    row.b1 = this.invisible(row.x, row.y, row.w, 20, () -> {
                        row.setOn.accept(!row.on.getAsBoolean());
                        Settings.save();
                    });
                    break;
                }
                case 1: {
                    row.b1 = this.invisible(row.x + row.w - 66, row.y + 3, 14, 14, () -> {
                        row.minus.run();
                        Settings.save();
                    });
                    row.b2 = this.invisible(row.x + row.w - 20, row.y + 3, 14, 14, () -> {
                        row.plus.run();
                        Settings.save();
                    });
                    break;
                }
                case 2: {
                    row.b1 = this.invisible(row.x, row.y, row.w / 2, 20, () -> {
                        row.minus.run();
                        Settings.save();
                    });
                    row.b2 = this.invisible(row.x + row.w / 2, row.y, row.w - row.w / 2, 20, () -> {
                        row.plus.run();
                        Settings.save();
                    });
                    break;
                }
                case 3: {
                    row.box = new EditBox(this.font, row.x + row.w - 94, row.y + 5, 66, 12, (Component)Component.literal((String)row.label));
                    row.box.setBordered(false);
                    row.box.setTextShadow(false);
                    row.box.setMaxLength(7);
                    row.box.setValue(Settings.hex(row.color.getAsInt()));
                    row.box.addFormatter((string, n) -> Component.literal((String)string).withStyle(SettingsScreen.StyleForInput()).getVisualOrderText());
                    row.box.setResponder(string -> SettingsColorValues.parse(string).ifPresent(n -> {
                        row.setColor.accept(n);
                        Settings.save();
                    }));
                    this.addWidget((GuiEventListener)row.box);
                    row.b1 = this.invisible(row.x + row.w - 21, row.y + 3, 16, 14, () -> this.minecraft.setScreen((Screen)new DashboardColorPicker(this.returnScreen(), row.label, row.color.getAsInt(), n -> {
                        row.setColor.accept(n);
                        Settings.save();
                    })));
                    break;
                }
                case 4: {
                    row.b1 = this.invisible(row.x, row.y, row.w, 20, () -> {
                        if (!row.label.startsWith("Reset")) {
                            row.action.run();
                            return;
                        }
                        this.minecraft.setScreen((Screen)new ConfirmScreen(bl -> {
                            if (bl) {
                                if (row.label.equals("Reset all settings")) {
                                    int n = Settings.S.wins;
                                    int n2 = Settings.S.losses;
                                    int n3 = Settings.S.streak;
                                    Settings.S = new Settings();
                                    Settings.S.wins = n;
                                    Settings.S.losses = n2;
                                    Settings.S.streak = n3;
                                    Settings.applyAccent(Settings.S.accent);
                                    Settings.save();
                                } else {
                                    row.action.run();
                                }
                            }
                            this.minecraft.setScreen(this.returnScreen());
                        }, (Component)Component.literal((String)(row.label + "?")), (Component)Component.literal((String)"Payment history and balances will be kept.")));
                    });
                    break;
                }
                case 5: {
                    int n10 = row.x + 72;
                    int n11 = row.w - 80;
                    row.box = new EditBox(this.font, n10 + 3, row.y + 5, n11 - 6, 12, (Component)Component.literal((String)row.label));
                    row.box.setBordered(false);
                    row.box.setTextShadow(false);
                    row.box.addFormatter((string, n) -> Component.literal((String)string).withStyle(SettingsScreen.StyleForInput()).getVisualOrderText());
                    row.box.setMaxLength(48);
                    row.box.setValue(row.textGet.get());
                    row.box.setResponder(string -> {
                        row.textSet.accept((String)string);
                        Settings.save();
                    });
                    this.addWidget((GuiEventListener)row.box);
                }
            }
            ++n7;
        }
    }

    private Button invisible(int n, int n2, int n3, int n4, Runnable runnable) {
        return (Button)this.addWidget((GuiEventListener)Button.builder((Component)Component.literal((String)""), button -> runnable.run()).bounds(n, n2, n3, n4).build());
    }

    private static boolean over(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n < n3 + n5 && n2 >= n4 && n2 < n4 + n6;
    }

    private static int alpha(int n, float f) {
        int n2 = n >>> 24 & 0xFF;
        return Math.max(0, Math.min(255, (int)((float)n2 * f))) << 24 | n & 0xFFFFFF;
    }

    private static int lighten(int n, float f) {
        int n2 = n >> 16 & 0xFF;
        int n3 = n >> 8 & 0xFF;
        int n4 = n & 0xFF;
        n2 += (int)((float)(255 - n2) * f);
        n3 += (int)((float)(255 - n3) * f);
        n4 += (int)((float)(255 - n4) * f);
        return 0xFF000000 | n2 << 16 | n3 << 8 | n4;
    }

    private static int rainbow(double d) {
        int n;
        int n2;
        double d2 = (d % 1.0 + 1.0) % 1.0 * 6.0;
        int n3 = (int)d2;
        double d3 = d2 - (double)n3;
        int n4 = (int)(255.0 * (1.0 - d3));
        int n5 = (int)(255.0 * d3);
        int n6 = switch (n3) {
            case 0 -> {
                n2 = 255;
                n = n5;
                yield 0;
            }
            case 1 -> {
                n2 = n4;
                n = 255;
                yield 0;
            }
            case 2 -> {
                n2 = 0;
                n = 255;
                yield n5;
            }
            case 3 -> {
                n2 = 0;
                n = n4;
                yield 255;
            }
            case 4 -> {
                n2 = n5;
                n = 0;
                yield 255;
            }
            default -> {
                n2 = 255;
                n = 0;
                yield n4;
            }
        };
        return 0xFF000000 | n2 << 16 | n << 8 | n6;
    }

    private int textWidth(String string) {
        return this.font.width(string);
    }

    private static Style StyleForInput() {
        return Style.EMPTY;
    }

    private void txt(GuiGraphicsExtractor guiGraphicsExtractor, String string, int n, int n2, int n3) {
        if ((n3 >>> 24 & 0xFF) < 8) {
            return;
        }
        guiGraphicsExtractor.text(this.font, string, n, n2, n3, true);
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f) {
        if (Settings.S.settingsBlackBackdrop) {
            guiGraphicsExtractor.fill(0, 0, this.width, this.height, -16777216);
        }
    }

    private static int bright(int n) {
        int n2 = SettingsScreen.lighten(0xFF000000 | n, (float)Settings.S.bgBrightness / 100.0f);
        int n3 = Math.round(17.279999f + (float)(n2 >> 16 & 0xFF) * 0.04f);
        int n4 = Math.round(18.24f + (float)(n2 >> 8 & 0xFF) * 0.04f);
        int n5 = Math.round(23.039999f + (float)(n2 & 0xFF) * 0.04f);
        return n3 << 16 | n4 << 8 | n5;
    }

    private void windowBackground(GuiGraphicsExtractor guiGraphicsExtractor) {
        int n;
        int n2;
        Settings settings = Settings.S;
        int n3 = Math.max(0, Math.min(255, settings.bgOpacity * 255 / 100));
        int n4 = SettingsScreen.bright(settings.bgTop);
        int n5 = SettingsScreen.bright(settings.bgGradient || settings.settingsAnimatedBackground ? settings.bgBottom : settings.bgTop);
        double d = (double)System.nanoTime() / 1.0E9;
        int n6 = 0;
        while (n6 < this.wh) {
            float f = (float)n6 / (float)Math.max(1, this.wh);
            if (settings.settingsAnimatedBackground) {
                f = (float)(0.5 + 0.5 * Math.sin((double)f * 3.2 - d * 0.25));
            }
            int n7 = (int)((float)(n4 >> 16 & 0xFF) + (float)((n5 >> 16 & 0xFF) - (n4 >> 16 & 0xFF)) * f);
            int n8 = (int)((float)(n4 >> 8 & 0xFF) + (float)((n5 >> 8 & 0xFF) - (n4 >> 8 & 0xFF)) * f);
            n2 = (int)((float)(n4 & 0xFF) + (float)((n5 & 0xFF) - (n4 & 0xFF)) * f);
            if (settings.settingsAnimatedBackground) {
                float f2 = 0.025f + 0.09f * (float)Math.pow(0.5 + 0.5 * Math.sin((double)n6 / (double)Math.max(1, this.wh) * 5.0 - d * 0.35), 3.0);
                n = settings.settingsThemeAccent;
                n7 = Math.round((float)n7 * (1.0f - f2) + (float)(n >> 16 & 0xFF) * f2);
                n8 = Math.round((float)n8 * (1.0f - f2) + (float)(n >> 8 & 0xFF) * f2);
                n2 = Math.round((float)n2 * (1.0f - f2) + (float)(n & 0xFF) * f2);
            }
            guiGraphicsExtractor.fill(this.wx, this.wy + n6, this.wx + this.ww, Math.min(this.wy + this.wh, this.wy + n6 + 4), n3 << 24 | n7 << 16 | n8 << 8 | n2);
            n6 += 4;
        }
        if (settings.settingsAnimatedBackground) {
            n6 = settings.settingsThemeAccent & 0xFFFFFF;
            int n9 = 0;
            while (n9 < 18) {
                double d2 = (double)n9 * 0.61803398875;
                n2 = this.wx + 8 + (int)((d2 + d * 0.012) % 1.0 * (double)(this.ww - 16));
                int n10 = this.wy + 29 + (int)((((double)n9 * 0.381966 - d * 0.009) % 1.0 + 1.0) % 1.0 * (double)(this.wh - 38));
                n = (int)((18.0 + 22.0 * (0.5 + 0.5 * Math.sin(d * 0.7 + (double)n9))) * (double)n3 / 255.0);
                guiGraphicsExtractor.fill(n2, n10, n2 + 2, n10 + 2, n << 24 | n6);
                ++n9;
            }
        }
    }

    private void outline(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, int n5) {
        guiGraphicsExtractor.fill(n, n2, n + n3, n2 + 1, n5);
        guiGraphicsExtractor.fill(n, n2 + n4 - 1, n + n3, n2 + n4, n5);
        guiGraphicsExtractor.fill(n, n2, n + 1, n2 + n4, n5);
        guiGraphicsExtractor.fill(n + n3 - 1, n2, n + n3, n2 + n4, n5);
    }

    private void soft(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, int n5) {
        OverviewScreen.round(guiGraphicsExtractor, n, n2, n3, n4, 1, n5);
    }

    private float rowProgress(int n) {
        long l = System.currentTimeMillis() - this.openedAt - (long)n * (Settings.S.slowSlideDown ? 18L : 8L);
        float f = Math.max(0.0f, Math.min(1.0f, (float)l / (Settings.S.slowSlideDown ? 300.0f : 160.0f)));
        return 1.0f - (1.0f - f) * (1.0f - f) * (1.0f - f);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f) {
        int n3;
        int n4;
        int n5;
        int n6;
        Object object = this.pendingFocus;
        if (object instanceof EditBox) {
            EditBox editBox = (EditBox)object;
            this.setFocused((GuiEventListener)editBox);
            this.pendingFocus = null;
        }
        if (this.embeddedBounds == null) {
            super.extractRenderState(guiGraphicsExtractor, n, n2, f);
        }
        int n7 = SettingsScreen.accent();
        this.windowBackground(guiGraphicsExtractor);
        guiGraphicsExtractor.fill(this.wx + 1, this.wy + 27, this.wx + 122, this.wy + this.wh - 1, -787409893);
        this.outline(guiGraphicsExtractor, this.wx, this.wy, this.ww, this.wh, SettingsScreen.theme(0.55f));
        this.soft(guiGraphicsExtractor, this.wx + 1, this.wy + 1, this.ww - 2, 25, -15000283);
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate((float)(this.wx + 10), (float)(this.wy + 8));
        guiGraphicsExtractor.pose().scale(1.35f, 1.35f);
        guiGraphicsExtractor.text(this.font, "DonutGambleTracker", 0, 0, -1, true);
        guiGraphicsExtractor.pose().popMatrix();
        if (Settings.S.settingsRainbowHeader) {
            double d = (double)System.nanoTime() / 8.0E9;
            int n8 = 1;
            while (n8 < this.ww - 1) {
                guiGraphicsExtractor.fill(this.wx + n8, this.wy + 26, this.wx + n8 + 1, this.wy + 27, SettingsScreen.rainbow((double)n8 / (double)Math.max(1, this.ww - 2) + d));
                ++n8;
            }
        } else {
            guiGraphicsExtractor.fill(this.wx + 1, this.wy + 26, this.wx + this.ww - 1, this.wy + 27, SettingsScreen.theme(0.65f));
        }
        guiGraphicsExtractor.fill(this.wx + 122, this.wy + 28, this.wx + 123, this.wy + this.wh, -13882324);
        int n9 = 0;
        while (n9 < this.sideButtons.size()) {
            Button button = this.sideButtons.get(n9);
            this.sideButton(guiGraphicsExtractor, button, button.getMessage().getString(), this.sideSections.get(n9), this.sideSections.get(n9) == section, n, n2);
            ++n9;
        }
        Button button = this.doneButton;
        boolean bl = SettingsScreen.over(n, n2, n6 = button.getX(), n5 = button.getY(), n4 = button.getWidth(), n3 = button.getHeight());
        this.soft(guiGraphicsExtractor, n6, n5, n4, n3, bl ? SettingsScreen.theme(0.34f) : SettingsScreen.theme(0.23f));
        this.txt(guiGraphicsExtractor, "Move HUDs", n6 + (n4 - this.textWidth("Move HUDs")) / 2, n5 + (n3 - 8) / 2, -986896);
        Button button2 = this.closeButton;
        this.soft(guiGraphicsExtractor, button2.getX(), button2.getY(), button2.getWidth(), button2.getHeight(), button2.isMouseOver((double)n, (double)n2) ? SettingsScreen.theme(0.34f) : SettingsScreen.theme(0.23f));
        this.txt(guiGraphicsExtractor, "Done", button2.getX() + (button2.getWidth() - this.textWidth("Done")) / 2, button2.getY() + 5, -986896);
        int n10 = this.wx + 130;
        int n11 = this.ww - 140;
        float f2 = this.rowProgress(0);
        int n12 = this.wy + 34 + (int)((1.0f - f2) * -6.0f);
        this.txt(guiGraphicsExtractor, SettingsScreen.section.about, n10, n12, SettingsScreen.alpha(-4934476, f2));
        guiGraphicsExtractor.fill(n10, n12 + 11, n10 + (int)((float)n11 * f2), n12 + 12, SettingsScreen.alpha(SettingsScreen.lighten(n7, 0.25f), f2));
        int n13 = 0;
        while (n13 < this.rows.size()) {
            this.drawRow(guiGraphicsExtractor, this.rows.get(n13), n, n2, this.rowProgress(n13 + 1));
            ++n13;
        }
        if (section == Section.CHAT) {
            this.drawChatPreview(guiGraphicsExtractor);
        }
        if (section == Section.FILTER) {
            Settings settings = Settings.S;
            int n14 = this.rows.isEmpty() ? this.wy + 52 : this.rows.get((int)(this.rows.size() - 1)).y + 32;
            String string = settings.filterOn ? "Filter is on: payments under " + Money.format(settings.filterUnder) + " are hidden." : "Filter is off: all payments are shown.";
            this.txt(guiGraphicsExtractor, string, this.wx + 130, n14, -986896);
            this.txt(guiGraphicsExtractor, "Hidden payments still count for the profit and the popup.", this.wx + 130, n14 + 12, -7566196);
        }
        this.txt(guiGraphicsExtractor, "Click a color to choose, or type its hex code", n10, this.wy + this.wh - 12, -7566196);
    }

    private void sideButton(GuiGraphicsExtractor guiGraphicsExtractor, Button button, String string, Section section, boolean bl, int n, int n2) {
        int n3;
        int n4 = button.getX();
        int n5 = button.getY();
        int n6 = button.getWidth();
        int n7 = button.getHeight();
        boolean bl2 = SettingsScreen.over(n, n2, n4, n5, n6, n7);
        int[] nArray = new int[]{-12894907, -14133732, -14923933, -9219064, -10340593, -10476969, -10078892, -15445672, -9886428};
        int n8 = n3 = Settings.S.settingsColoredTabs ? nArray[section.ordinal()] : SettingsScreen.accent();
        int n9 = Settings.S.settingsColoredTabs ? (bl ? SettingsScreen.lighten(n3, 0.18f) : (bl2 ? SettingsScreen.lighten(n3, 0.1f) : n3)) : (bl ? SettingsScreen.theme(0.44f) : (bl2 ? SettingsScreen.theme(0.34f) : SettingsScreen.theme(0.23f)));
        this.soft(guiGraphicsExtractor, n4, n5, n6, n7, n9);
        if (bl) {
            this.outline(guiGraphicsExtractor, n4, n5, n6, n7, Settings.S.settingsColoredTabs ? SettingsScreen.lighten(n3, 0.5f) : SettingsScreen.theme(0.7f));
            guiGraphicsExtractor.fill(n4 - 4, n5 + 1, n4 - 2, n5 + n7 - 1, -1);
        }
        this.txt(guiGraphicsExtractor, string, n4 + (n6 - this.textWidth(string)) / 2, n5 + (n7 - 8) / 2, bl || bl2 ? -1 : -1644826);
    }

    private void drawRow(GuiGraphicsExtractor guiGraphicsExtractor, Row row, int n, int n2, float f) {
        if (f <= 0.01f) {
            return;
        }
        int n3 = row.x;
        int n4 = row.w;
        int n5 = 20;
        int n6 = row.y + (int)((1.0f - f) * (Settings.S.slowSlideDown ? -10.0f : -6.0f));
        boolean bl = SettingsScreen.over(n, n2, row.x, row.y, n4, n5);
        int n7 = n6 + (n5 - 8) / 2;
        if (row.kind == Kind.ACTION) {
            this.soft(guiGraphicsExtractor, n3, n6, n4, n5, SettingsScreen.alpha(bl ? SettingsScreen.theme(0.34f) : SettingsScreen.theme(0.23f), f));
            this.txt(guiGraphicsExtractor, row.label, n3 + (n4 - this.textWidth(row.label)) / 2, n7, SettingsScreen.alpha(-4934476, f));
            return;
        }
        if (row.kind == Kind.TEXT) {
            this.soft(guiGraphicsExtractor, n3, n6, n4, n5, SettingsScreen.alpha(bl ? -13618627 : -14342351, f));
            this.txt(guiGraphicsExtractor, row.label, n3 + 8, n7, SettingsScreen.alpha(-986896, f));
            int n8 = n3 + 72;
            int n9 = n4 - 80;
            this.soft(guiGraphicsExtractor, n8, n6 + 3, n9, 14, -16053232);
            row.box.setY(n6 + 5);
            row.box.extractRenderState(guiGraphicsExtractor, n, n2, 0.0f);
            return;
        }
        this.soft(guiGraphicsExtractor, n3, n6, n4, n5, SettingsScreen.alpha(bl ? -13618627 : -14342351, f));
        switch (row.kind.ordinal()) {
            case 0: {
                this.txt(guiGraphicsExtractor, row.label, n3 + 8, n7, SettingsScreen.alpha(-986896, f));
                boolean bl2 = row.on.getAsBoolean();
                int n10 = 24;
                int n11 = 11;
                int n12 = n3 + n4 - n10 - 8;
                int n13 = n6 + (n5 - n11) / 2;
                if (bl2) {
                    this.soft(guiGraphicsExtractor, n12, n13, n10, n11, SettingsScreen.alpha(SettingsScreen.accent(), f));
                    this.soft(guiGraphicsExtractor, n12 + n10 - 12, n13 + 1, 10, n11 - 2, SettingsScreen.alpha(-1, f));
                    break;
                }
                this.soft(guiGraphicsExtractor, n12, n13, n10, n11, SettingsScreen.alpha(-12829624, f));
                this.soft(guiGraphicsExtractor, n12 + 2, n13 + 1, 10, n11 - 2, SettingsScreen.alpha(-1, f));
                break;
            }
            case 1: {
                this.txt(guiGraphicsExtractor, row.label, n3 + 8, n7, SettingsScreen.alpha(-986896, f));
                this.smallBox(guiGraphicsExtractor, n3 + n4 - 66, n6 + 3, "-", SettingsScreen.over(n, n2, n3 + n4 - 66, row.y + 3, 14, 14), f);
                this.smallBox(guiGraphicsExtractor, n3 + n4 - 20, n6 + 3, "+", SettingsScreen.over(n, n2, n3 + n4 - 20, row.y + 3, 14, 14), f);
                String string = row.value.get();
                int n14 = n3 + n4 - 52;
                int n15 = 32;
                this.txt(guiGraphicsExtractor, string, n14 + (n15 - this.textWidth(string)) / 2, n7, SettingsScreen.alpha(-1522361, f));
                break;
            }
            case 2: {
                String string = row.label + ": " + row.value.get();
                this.txt(guiGraphicsExtractor, "<", n3 + 8, n7, SettingsScreen.alpha(-986896, f));
                this.txt(guiGraphicsExtractor, ">", n3 + n4 - 8 - this.textWidth(">"), n7, SettingsScreen.alpha(-986896, f));
                this.txt(guiGraphicsExtractor, string, n3 + (n4 - this.textWidth(string)) / 2, n7, SettingsScreen.alpha(-986896, f));
                break;
            }
            case 3: {
                this.txt(guiGraphicsExtractor, row.label, n3 + 8, n7, SettingsScreen.alpha(-986896, f));
                int n16 = row.color.getAsInt();
                int n17 = n3 + n4 - 98;
                boolean bl3 = SettingsScreen.over(n, n2, n17, row.y + 3, 92, 14);
                guiGraphicsExtractor.fill(n17, n6 + 2, n17 + 74, n6 + 18, SettingsScreen.alpha(-16053232, f));
                this.outline(guiGraphicsExtractor, n17, n6 + 2, 74, 16, SettingsScreen.alpha(bl3 ? SettingsScreen.accent() : -10855846, f));
                row.box.setY(n6 + 5);
                row.box.extractRenderState(guiGraphicsExtractor, n, n2, 0.0f);
                row.b1.setY(n6 + 3);
                guiGraphicsExtractor.fill(n17 + 77, n6 + 2, n17 + 93, n6 + 18, SettingsScreen.alpha(0xFF000000 | n16, f));
            }
        }
    }

    private void smallBox(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, String string, boolean bl, float f) {
        guiGraphicsExtractor.fill(n, n2, n + 14, n2 + 14, SettingsScreen.alpha(bl ? -9803158 : -11908534, f));
        this.txt(guiGraphicsExtractor, string, n + (14 - this.textWidth(string)) / 2 + 1, n2 + 3, SettingsScreen.alpha(-986896, f));
    }

    private void drawChatPreview(GuiGraphicsExtractor guiGraphicsExtractor) {
        Settings settings = Settings.S;
        int n = this.rows.isEmpty() ? this.wy + 52 : this.rows.get((int)(this.rows.size() - 1)).y + 32;
        int n2 = this.wx + 130;
        this.txt(guiGraphicsExtractor, "Preview", n2, n, -4934476);
        int n3 = n + 12;
        int n4 = n + 24;
        int n5 = settings.chatColors ? 0xFF000000 | settings.nameColor : -1;
        int n6 = settings.chatColors ? 0xFF000000 | settings.receivedColor : -1;
        int n7 = settings.chatColors ? 0xFF000000 | settings.sentColor : -1;
        int n8 = n2;
        n8 = this.seg(guiGraphicsExtractor, "ExamplePlayer", n8, n3, n5, settings.chatColors && settings.boldNames);
        n8 = this.seg(guiGraphicsExtractor, " paid you ", n8, n3, -1, false);
        this.seg(guiGraphicsExtractor, "$5B", n8, n3, n6, settings.chatColors && settings.boldAmounts);
        n8 = n2;
        n8 = this.seg(guiGraphicsExtractor, "You paid ", n8, n4, -1, false);
        n8 = this.seg(guiGraphicsExtractor, "ExamplePlayer", n8, n4, n5, settings.chatColors && settings.boldNames);
        n8 = this.seg(guiGraphicsExtractor, " ", n8, n4, -1, false);
        n8 = this.seg(guiGraphicsExtractor, "$1M", n8, n4, n7, settings.chatColors && settings.boldAmounts);
        if (settings.taxPct > 0.0f) {
            long l = Math.round(1000000.0 * ((double)settings.taxPct / 100.0));
            this.seg(guiGraphicsExtractor, " (+" + Money.format(l) + " tax)", n8, n4, settings.chatColors && settings.colorTax ? 0xFF000000 | settings.taxColor : -5592406, false);
        }
    }

    private int seg(GuiGraphicsExtractor guiGraphicsExtractor, String string, int n, int n2, int n3, boolean bl) {
        MutableComponent mutableComponent = Component.literal((String)string).withStyle(Style.EMPTY.withBold(Boolean.valueOf(bl)));
        guiGraphicsExtractor.text(this.font, (Component)mutableComponent, n, n2, n3, true);
        return n + this.font.width((FormattedText)mutableComponent);
    }

    private static enum Kind {
        TOGGLE,
        STEPPER,
        PICKER,
        COLOR,
        ACTION,
        TEXT;

    }

    private static final class Row {
        Kind kind;
        String label;
        BooleanSupplier on;
        Consumer<Boolean> setOn;
        Supplier<String> value;
        Runnable minus;
        Runnable plus;
        IntSupplier color;
        IntConsumer setColor;
        boolean bg;
        Runnable action;
        Supplier<String> textGet;
        Consumer<String> textSet;
        EditBox box;
        int x;
        int y;
        int w;
        Button b1;
        Button b2;

        private Row() {
        }
    }

    private static enum Section {
        GENERAL("General", "Theme and general options"),
        CHAT("Chat", "How payment messages look in chat"),
        FILTER("Chat filter", "Hide small payments in chat"),
        HUD("Profit HUD", "The profit of the day box"),
        STREAK("Win streak", "Win streak counter (/win, /lose or keybinds)"),
        POPUP("Popup alert", "The alert for big payments you receive"),
        STYLE("Popup style", "Animations and looks of the popup alert"),
        SOUNDS("Sounds", "Sound effects for alerts and confirmations"),
        PAY("Pay confirm", "The screen that asks before a big /pay (you press Enter)");

        final String label;
        final String about;

        private Section(String string2, String string3) {
            this.label = string2;
            this.about = string3;
        }

        int color() {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> -10854808;
                case 1 -> -12613076;
                case 2 -> -13802864;
                case 3 -> -7374308;
                case 4 -> -6663144;
                case 5 -> -7458946;
                case 6 -> -7451520;
                case 7 -> -14714240;
                case 8 -> -7721426;
            };
        }
    }
}

