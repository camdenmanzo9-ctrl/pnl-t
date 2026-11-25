/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FontDescription
 *  net.minecraft.network.chat.FontDescription$Resource
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.Identifier
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Fonts;
import dev.profit.dashboard.OverviewScreen;
import dev.profit.dashboard.Settings;
import dev.profit.dashboard.SettingsColorValues;
import dev.profit.dashboard.SettingsScreen;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public final class DashboardColorPicker
extends Screen {
    private final Screen parent;
    private final IntConsumer changed;
    private final String label;
    private float hue;
    private float saturation;
    private float brightness;
    private int rgb;
    private int wx;
    private int wy;
    private int ww;
    private int wh;
    private int sx;
    private int sy;
    private int sw;
    private int sh;
    private int dragging;
    private int page;
    private int pages;
    private boolean updating;
    private boolean editSession;
    private int lastPublished;
    private EditBox hex;
    private final List<Button> buttons = new ArrayList<Button>();
    private final List<Integer> colors = new ArrayList<Integer>();
    private static final int[] EXTRA;

    static {
        int[] nArray = new int[48];
        nArray[0] = 0xFF8800;
        nArray[1] = 0xFF6600;
        nArray[2] = 16753920;
        nArray[3] = 16757575;
        nArray[4] = 16764032;
        nArray[5] = 16011550;
        nArray[6] = 12531212;
        nArray[7] = 0xFF6B6B;
        nArray[8] = 16717636;
        nArray[9] = 15483002;
        nArray[10] = 16027569;
        nArray[11] = 13538264;
        nArray[12] = 12216520;
        nArray[13] = 9315498;
        nArray[14] = 8280002;
        nArray[15] = 11771355;
        nArray[16] = 6056896;
        nArray[17] = 9489145;
        nArray[18] = 2733814;
        nArray[19] = 58879;
        nArray[20] = 8445674;
        nArray[21] = 2533018;
        nArray[22] = 11010027;
        nArray[23] = 58998;
        nArray[24] = 8505220;
        nArray[25] = 11730777;
        nArray[26] = 14477173;
        nArray[27] = 0xFFFF8D;
        nArray[28] = 16766287;
        nArray[29] = 16763432;
        nArray[30] = 0xFFFFFF;
        nArray[31] = 15527921;
        nArray[32] = 11583173;
        nArray[33] = 7901340;
        nArray[34] = 4545124;
        nArray[35] = 2503224;
        nArray[37] = 1054752;
        nArray[38] = 1774901;
        nArray[39] = 3283495;
        nArray[40] = 1060138;
        nArray[41] = 3154191;
        nArray[42] = 14142664;
        nArray[43] = 10586239;
        nArray[44] = 7951688;
        nArray[45] = 5125166;
        nArray[46] = 16744619;
        nArray[47] = 15368444;
        EXTRA = nArray;
    }

    DashboardColorPicker(Screen screen, String string, int n, IntConsumer intConsumer) {
        super((Component)Component.literal((String)("Choose " + string)));
        this.parent = screen;
        this.label = string;
        this.changed = intConsumer;
        this.setRgb(n);
        this.lastPublished = this.rgb;
    }

    private void setRgb(int n) {
        this.rgb = n & 0xFFFFFF;
        float[] fArray = Color.RGBtoHSB(this.rgb >> 16 & 0xFF, this.rgb >> 8 & 0xFF, this.rgb & 0xFF, null);
        this.hue = fArray[0];
        this.saturation = fArray[1];
        this.brightness = fArray[2];
    }

    private int accent() {
        return 0xFF000000 | Settings.S.settingsThemeAccent;
    }

    private void text(GuiGraphicsExtractor guiGraphicsExtractor, String string, int n, int n2, int n3) {
        Fonts.draw(guiGraphicsExtractor, this.font, string, n, n2, n3, 1.0f, false);
    }

    private void box(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, int n5) {
        OverviewScreen.round(guiGraphicsExtractor, n, n2, n3, n4, 2, n5);
    }

    protected void init() {
        if (!this.editSession) {
            Settings.beginColorEdit();
            this.editSession = true;
        }
        this.ww = Math.min(this.width - 16, 410);
        this.wh = Math.min(this.height - 12, 340);
        this.wx = (this.width - this.ww) / 2;
        this.wy = (this.height - this.wh) / 2;
        this.sx = this.wx + 14;
        this.sy = this.wy + 40;
        this.sw = this.ww - 54;
        this.sh = Math.max(36, Math.min(100, this.wh - 190));
        this.buttons.clear();
        this.colors.clear();
        this.hex = new EditBox(this.font, this.wx + 22, this.sy + this.sh + 16, 100, 16, (Component)Component.literal((String)"Hex color"));
        this.hex.setBordered(false);
        this.hex.setMaxLength(7);
        this.hex.setValue(Settings.hex(this.rgb));
        this.hex.addFormatter((string, n) -> Component.literal((String)string).withStyle(Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)"inter_8")))).getVisualOrderText());
        this.hex.setTextShadow(false);
        this.hex.setResponder(string -> {
            if (this.updating) {
                return;
            }
            SettingsColorValues.parse(string).ifPresent(n -> {
                this.setRgb(n);
                this.applyColor();
            });
        });
        this.addWidget(this.hex);
        int n2 = Math.max(1, (this.ww - 28) / 20);
        int n3 = Math.max(1, (this.wy + this.wh - 36 - (this.sy + this.sh + 46) - 16) / 20 + 1);
        int n4 = n2 * n3;
        this.pages = (EXTRA.length + n4 - 1) / n4;
        this.page = Math.min(this.page, this.pages - 1);
        int n5 = 0;
        while (n5 < n4 && this.page * n4 + n5 < EXTRA.length) {
            int n6 = EXTRA[this.page * n4 + n5];
            int n7 = this.wx + 14 + n5 % n2 * 20;
            int n8 = this.sy + this.sh + 46 + n5 / n2 * 20;
            Button button2 = Button.builder((Component)Component.literal((String)Settings.hex(n6)), button -> {
                this.setRgb(n6);
                this.publish();
            }).bounds(n7, n8, 16, 16).build();
            this.addWidget(button2);
            this.buttons.add(button2);
            this.colors.add(n6);
            ++n5;
        }
        this.addWidget(Button.builder((Component)Component.literal((String)"Done"), button -> this.onClose()).bounds(this.wx + this.ww - 74, this.wy + this.wh - 28, 60, 20).build());
        if (this.pages > 1) {
            this.addWidget(Button.builder((Component)Component.literal((String)"Previous presets"), button -> {
                this.page = Math.floorMod(this.page - 1, this.pages);
                this.rebuildWidgets();
            }).bounds(this.wx + 14, this.wy + this.wh - 28, 22, 20).build());
            this.addWidget(Button.builder((Component)Component.literal((String)"Next presets"), button -> {
                this.page = (this.page + 1) % this.pages;
                this.rebuildWidgets();
            }).bounds(this.wx + 40, this.wy + this.wh - 28, 22, 20).build());
        }
    }

    private void applyColor() {
        if (this.rgb != this.lastPublished) {
            this.lastPublished = this.rgb;
            this.changed.accept(this.rgb);
        }
    }

    private void publish() {
        this.rgb = Color.HSBtoRGB(this.hue, this.saturation, this.brightness) & 0xFFFFFF;
        this.applyColor();
        String string = Settings.hex(this.rgb);
        if (!string.equals(this.hex.getValue())) {
            this.updating = true;
            this.hex.setValue(string);
            this.updating = false;
        }
    }

    private void pick(double d, double d2) {
        if (this.dragging == 1) {
            this.saturation = (float)Math.max(0.0, Math.min(1.0, (d - (double)this.sx) / ((double)this.sw - 1.0)));
            this.brightness = 1.0f - (float)Math.max(0.0, Math.min(1.0, (d2 - (double)this.sy) / ((double)this.sh - 1.0)));
        } else if (this.dragging == 2) {
            this.hue = (float)Math.max(0.0, Math.min(1.0, (d2 - (double)this.sy) / ((double)this.sh - 1.0)));
        }
        this.publish();
    }

    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() == 0 && mouseButtonEvent.y() >= (double)this.sy && mouseButtonEvent.y() < (double)(this.sy + this.sh)) {
            if (mouseButtonEvent.x() >= (double)this.sx && mouseButtonEvent.x() < (double)(this.sx + this.sw)) {
                this.dragging = 1;
            } else if (mouseButtonEvent.x() >= (double)(this.sx + this.sw + 10) && mouseButtonEvent.x() < (double)(this.sx + this.sw + 24)) {
                this.dragging = 2;
            }
            if (this.dragging != 0) {
                this.pick(mouseButtonEvent.x(), mouseButtonEvent.y());
                return true;
            }
        }
        if (mouseButtonEvent.button() == 0 && mouseButtonEvent.x() >= (double)(this.wx + this.ww - 74) && mouseButtonEvent.x() < (double)(this.wx + this.ww - 14) && mouseButtonEvent.y() >= (double)(this.wy + this.wh - 28) && mouseButtonEvent.y() < (double)(this.wy + this.wh - 8)) {
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseButtonEvent, bl);
    }

    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double d2) {
        if (this.dragging != 0) {
            this.pick(mouseButtonEvent.x(), mouseButtonEvent.y());
            return true;
        }
        return super.mouseDragged(mouseButtonEvent, d, d2);
    }

    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        this.dragging = 0;
        return super.mouseReleased(mouseButtonEvent);
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void onClose() {
        Settings.endColorEdit();
        this.minecraft.setScreen(this.parent);
    }

    public void removed() {
        Settings.endColorEdit();
        super.removed();
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f) {
        int n3;
        int n4;
        guiGraphicsExtractor.fill(0, 0, this.width, this.height, -1610612736);
        this.box(guiGraphicsExtractor, this.wx, this.wy, this.ww, this.wh, -15131869);
        Fonts.draw(guiGraphicsExtractor, this.font, "Choose " + this.label, this.wx + 14, this.wy + 14, -722948, 1.1f, true);
        int n5 = 0;
        while (n5 < this.sw) {
            n4 = Color.HSBtoRGB(this.hue, (float)n5 / (float)Math.max(1, this.sw - 1), 1.0f);
            guiGraphicsExtractor.fillGradient(this.sx + n5, this.sy, this.sx + n5 + 1, this.sy + this.sh, n4, -16777216);
            ++n5;
        }
        n5 = 0;
        while (n5 < 6) {
            n4 = this.sy + this.sh * n5 / 6;
            n3 = this.sy + this.sh * (n5 + 1) / 6;
            guiGraphicsExtractor.fillGradient(this.sx + this.sw + 10, n4, this.sx + this.sw + 24, n3, Color.HSBtoRGB((float)n5 / 6.0f, 1.0f, 1.0f), Color.HSBtoRGB((float)(n5 + 1) / 6.0f, 1.0f, 1.0f));
            ++n5;
        }
        n5 = this.sx + Math.round(this.saturation * (float)(this.sw - 1));
        n4 = this.sy + Math.round((1.0f - this.brightness) * (float)(this.sh - 1));
        guiGraphicsExtractor.fill(n5 - 3, n4 - 1, n5 + 4, n4 + 2, -1);
        guiGraphicsExtractor.fill(n5 - 1, n4 - 3, n5 + 2, n4 + 4, -1);
        n3 = this.sy + Math.round(this.hue * (float)(this.sh - 1));
        guiGraphicsExtractor.fill(this.sx + this.sw + 8, n3 - 1, this.sx + this.sw + 26, n3 + 1, -1);
        this.box(guiGraphicsExtractor, this.wx + 14, this.sy + this.sh + 10, 114, 25, SettingsColorValues.parse(this.hex.getValue()).isPresent() ? -16249321 : -9357502);
        this.hex.extractRenderState(guiGraphicsExtractor, n, n2, f);
        this.box(guiGraphicsExtractor, this.wx + 140, this.sy + this.sh + 10, this.ww - 154, 25, 0xFF000000 | this.rgb);
        int n6 = 0;
        while (n6 < this.buttons.size()) {
            Button button = this.buttons.get(n6);
            guiGraphicsExtractor.fill(button.getX(), button.getY(), button.getX() + 16, button.getY() + 16, 0xFF000000 | this.colors.get(n6));
            if (button.isMouseOver((double)n, (double)n2)) {
                guiGraphicsExtractor.setTooltipForNextFrame(this.font, (Component)Component.literal((String)Settings.hex(this.colors.get(n6))), n, n2);
            }
            ++n6;
        }
        this.box(guiGraphicsExtractor, this.wx + this.ww - 74, this.wy + this.wh - 28, 60, 20, SettingsScreen.theme(0.44f));
        this.text(guiGraphicsExtractor, "Done", this.wx + this.ww - 56, this.wy + this.wh - 22, -1);
        if (this.pages > 1) {
            this.box(guiGraphicsExtractor, this.wx + 14, this.wy + this.wh - 28, 22, 20, -14208184);
            this.box(guiGraphicsExtractor, this.wx + 40, this.wy + this.wh - 28, 22, 20, -14208184);
            this.text(guiGraphicsExtractor, "<", this.wx + 22, this.wy + this.wh - 22, -1);
            this.text(guiGraphicsExtractor, ">", this.wx + 48, this.wy + this.wh - 22, -1);
            this.text(guiGraphicsExtractor, this.page + 1 + " / " + this.pages, this.wx + 70, this.wy + this.wh - 22, -4602925);
        }
    }
}

