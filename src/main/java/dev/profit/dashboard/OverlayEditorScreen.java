/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.network.chat.Component
 */
package dev.profit.dashboard;

import dev.profit.dashboard.BoardSettingsScreen;
import dev.profit.dashboard.Fonts;
import dev.profit.dashboard.OverlayGeometry;
import dev.profit.dashboard.OverlayLayout;
import dev.profit.dashboard.OverviewScreen;
import dev.profit.dashboard.Settings;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class OverlayEditorScreen
extends Screen {
    private final Screen parent;
    private final List<OverlayLayout.Panel> panels = OverlayLayout.panels();
    private final List<Button> buttons = new ArrayList<Button>();
    private int selected;
    private OverlayLayout.Panel dragging;
    private OverlayGeometry.Box start;
    private float initialSize;
    private double grabX;
    private double grabY;
    private boolean resizing;
    private Button panelButton;
    private Button snapButton;

    public OverlayEditorScreen(Screen screen) {
        super((Component)Component.literal((String)"Overlay editor"));
        this.parent = screen;
    }

    public boolean isPauseScreen() {
        return false;
    }

    private int accent() {
        return 0xFF000000 | Settings.S.settingsThemeAccent;
    }

    private Button button(int n, int n2, int n3, String string, Runnable runnable) {
        Button button2 = Button.builder((Component)Component.literal((String)string), button -> runnable.run()).bounds(n, n2, n3, 20).build();
        this.buttons.add(button2);
        return (Button)this.addWidget((GuiEventListener)button2);
    }

    protected void init() {
        this.buttons.clear();
        int n = this.height - 56;
        this.button(216, n + 27, 108, "Edit game text", () -> this.minecraft.setScreen((Screen)new BoardSettingsScreen(this)));
        this.panelButton = this.button(8, n, Math.max(96, Math.min(200, this.width - 140)), "", () -> {
            this.selected = (this.selected + 1) % this.panels.size();
        });
        this.button(this.width - 116, n, 50, "Reset", () -> {
            OverlayLayout.Panel panel = this.panels.get(this.selected);
            if (panel.available()) {
                panel.reset();
                panel.save();
            }
        });
        this.button(this.width - 60, n, 52, "Done", this::onClose);
        this.button(8, n + 27, 22, "\u2212", () -> this.changeSize(-0.05f));
        this.button(34, n + 27, 22, "+", () -> this.changeSize(0.05f));
        this.snapButton = this.button(128, n + 27, 76, "", () -> {
            Settings.S.overlaySnap = !Settings.S.overlaySnap;
            Settings.save();
        });
    }

    private void changeSize(float f) {
        OverlayLayout.Panel panel = this.panels.get(this.selected);
        if (panel.available()) {
            panel.resize(this.minecraft, panel.size() + f);
            panel.save();
        }
    }

    private void begin(int n, MouseButtonEvent mouseButtonEvent, boolean bl) {
        this.selected = n;
        this.dragging = this.panels.get(n);
        this.start = this.dragging.bounds(this.minecraft);
        this.initialSize = this.dragging.size();
        this.resizing = bl;
        this.grabX = mouseButtonEvent.x() - (double)this.start.x();
        this.grabY = mouseButtonEvent.y() - (double)this.start.y();
    }

    private void finishDrag() {
        if (this.dragging != null) {
            this.dragging.save();
            this.dragging = null;
        }
    }

    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (super.mouseClicked(mouseButtonEvent, bl)) {
            return true;
        }
        if (mouseButtonEvent.button() != 0) {
            return false;
        }
        OverlayLayout.Panel panel = this.panels.get(this.selected);
        if (panel.available() && panel.bounds(this.minecraft).handle(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            this.begin(this.selected, mouseButtonEvent, true);
            return true;
        }
        if (panel.available() && panel.bounds(this.minecraft).contains(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            this.begin(this.selected, mouseButtonEvent, false);
            return true;
        }
        int n = this.panels.size() - 1;
        while (n >= 0) {
            OverlayLayout.Panel panel2 = this.panels.get(n);
            if (panel2.available() && panel2.active() && panel2.bounds(this.minecraft).contains(mouseButtonEvent.x(), mouseButtonEvent.y())) {
                this.begin(n, mouseButtonEvent, false);
                return true;
            }
            --n;
        }
        return false;
    }

    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double d2) {
        if (this.dragging == null) {
            return super.mouseDragged(mouseButtonEvent, d, d2);
        }
        if (this.resizing) {
            this.dragging.resize(this.minecraft, OverlayGeometry.resized(this.start, this.initialSize, mouseButtonEvent.x(), mouseButtonEvent.y()));
            this.dragging.move(this.minecraft, this.start.x(), this.start.y());
        } else {
            OverlayGeometry.Box box = this.dragging.bounds(this.minecraft);
            int n = OverlayGeometry.clamp(OverlayGeometry.snap(mouseButtonEvent.x() - this.grabX, Settings.S.overlaySnap), box.w(), this.width);
            int n2 = OverlayGeometry.clamp(OverlayGeometry.snap(mouseButtonEvent.y() - this.grabY, Settings.S.overlaySnap), box.h(), this.height);
            this.dragging.move(this.minecraft, n, n2);
        }
        return true;
    }

    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        if (this.dragging != null) {
            this.finishDrag();
            return true;
        }
        return super.mouseReleased(mouseButtonEvent);
    }

    public boolean keyPressed(KeyEvent keyEvent) {
        OverlayLayout.Panel panel = this.panels.get(this.selected);
        int n = keyEvent.key();
        if (panel.available() && n >= 262 && n <= 265) {
            int n2 = (keyEvent.modifiers() & 1) != 0 ? 10 : 1;
            OverlayGeometry.Box box = panel.bounds(this.minecraft);
            int n3 = box.x() + (n == 262 ? n2 : (n == 263 ? -n2 : 0));
            int n4 = box.y() + (n == 264 ? n2 : (n == 265 ? -n2 : 0));
            panel.move(this.minecraft, OverlayGeometry.clamp(n3, box.w(), this.width), OverlayGeometry.clamp(n4, box.h(), this.height));
            panel.save();
            return true;
        }
        if (n == 82 && panel.available()) {
            panel.reset();
            panel.save();
            return true;
        }
        return super.keyPressed(keyEvent);
    }

    public void onClose() {
        this.finishDrag();
        Settings.save();
        this.minecraft.setScreen(this.parent);
    }

    public void removed() {
        this.finishDrag();
        super.removed();
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f) {
        guiGraphicsExtractor.fill(0, 0, this.width, this.height, 0x18000000);
    }

    private void text(GuiGraphicsExtractor guiGraphicsExtractor, String string, int n, int n2, int n3, float f, boolean bl) {
        Fonts.draw(guiGraphicsExtractor, this.font, string, n, n2, n3, f, bl);
    }

    private void drawPanel(GuiGraphicsExtractor guiGraphicsExtractor, OverlayLayout.Panel panel, boolean bl) {
        int n;
        if (!panel.available() || !bl) {
            return;
        }
        OverlayGeometry.Box box = panel.bounds(this.minecraft);
        int n2 = n = bl ? this.accent() : -1715417633;
        if (!panel.active()) {
            OverviewScreen.round(guiGraphicsExtractor, box.x(), box.y(), box.w(), box.h(), 3, 1745885222);
            if (box.h() > 28) {
                this.text(guiGraphicsExtractor, "Empty / disabled preview", box.x() + 6, box.y() + 18, -4537132, 0.7f, false);
            }
        }
        guiGraphicsExtractor.fill(box.x(), box.y(), box.x() + box.w(), box.y() + 1, n);
        guiGraphicsExtractor.fill(box.x(), box.y() + box.h() - 1, box.x() + box.w(), box.y() + box.h(), n);
        guiGraphicsExtractor.fill(box.x(), box.y(), box.x() + 1, box.y() + box.h(), n);
        guiGraphicsExtractor.fill(box.x() + box.w() - 1, box.y(), box.x() + box.w(), box.y() + box.h(), n);
        guiGraphicsExtractor.fill(box.x() + box.w() - 8, box.y() + box.h() - 8, box.x() + box.w(), box.y() + box.h(), n);
        guiGraphicsExtractor.fill(box.x() + box.w() - 6, box.y() + box.h() - 5, box.x() + box.w() - 1, box.y() + box.h() - 4, -15656924);
        guiGraphicsExtractor.fill(box.x() + box.w() - 5, box.y() + box.h() - 6, box.x() + box.w() - 4, box.y() + box.h() - 1, -15656924);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f) {
        super.extractRenderState(guiGraphicsExtractor, n, n2, f);
        OverlayLayout.Panel panel = this.panels.get(this.selected);
        this.drawPanel(guiGraphicsExtractor, panel, true);
        guiGraphicsExtractor.fill(0, this.height - 63, this.width, this.height, -284355551);
        this.panelButton.setMessage((Component)Component.literal((String)("Panel: " + panel.name)));
        this.snapButton.setMessage((Component)Component.literal((String)("Snap: " + (Settings.S.overlaySnap ? "ON" : "OFF"))));
        for (Button b : this.buttons) {
            OverviewScreen.round(guiGraphicsExtractor, b.getX(), b.getY(), b.getWidth(), b.getHeight(), 3, b.isMouseOver((double)n, (double)n2) ? -13285793 : -14470840);
            String string = b.getMessage().getString();
            this.text(guiGraphicsExtractor, string, b.getX() + (b.getWidth() - Fonts.width(this.font, string, 0.9f, true)) / 2, b.getY() + 6, -985860, 0.9f, true);
        }
        this.text(guiGraphicsExtractor, Math.round(panel.size() * 100.0f) + "%", 65, this.height - 23, this.accent(), 0.9f, true);
        String string = panel.available() ? "Drag to move \u00b7 corner to resize \u00b7 arrows for precision" : "Install Balance HUD to edit this panel";
        if (this.width > 650) {
            this.text(guiGraphicsExtractor, string, 334, this.height - 23, -4931116, 0.7f, false);
        }
    }
}

