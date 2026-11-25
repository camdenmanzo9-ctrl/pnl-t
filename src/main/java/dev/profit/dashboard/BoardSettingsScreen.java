/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package dev.profit.dashboard;

import dev.profit.dashboard.OverlayEditorScreen;
import dev.profit.dashboard.Settings;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class BoardSettingsScreen
extends Screen {
    private final Screen parent;
    private int selected;
    private int left;
    private int top;

    public BoardSettingsScreen(Screen screen) {
        super((Component)Component.literal((String)"Game text overlay"));
        this.parent = screen;
    }

    public boolean isPauseScreen() {
        return false;
    }

    private void button(int n, int n2, int n3, String string, Runnable runnable) {
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)string), button -> runnable.run()).bounds(n, n2, n3, 20).build());
    }

    private void field(int n, String string, String string2, Consumer<String> consumer) {
        EditBox editBox = new EditBox(this.font, this.left + 125, n, 190, 18, (Component)Component.literal((String)string));
        editBox.setMaxLength(120);
        editBox.setValue(string2 == null ? "" : string2);
        editBox.setResponder(consumer);
        this.addRenderableWidget((GuiEventListener)editBox);
    }

    private Settings.BoardTier tier() {
        int n = this.selected;
        for (Settings.BoardTier boardTier : Settings.S.boardTiers) {
            if (n < boardTier.games.size()) {
                return boardTier;
            }
            n -= boardTier.games.size();
        }
        return Settings.S.boardTiers.getFirst();
    }

    private Settings.BoardGame game() {
        int n = this.selected;
        for (Settings.BoardTier boardTier : Settings.S.boardTiers) {
            if (n < boardTier.games.size()) {
                return boardTier.games.get(n);
            }
            n -= boardTier.games.size();
        }
        return this.tier().games.getFirst();
    }

    private int count() {
        return Settings.S.boardTiers.stream().mapToInt(boardTier -> boardTier.games.size()).sum();
    }

    protected void init() {
        this.left = (this.width - 330) / 2;
        this.top = Math.max(8, (this.height - 270) / 2);
        Settings settings = Settings.S;
        this.button(this.left, this.top + 25, 155, "Overlay: " + (settings.boardShow ? "ON" : "OFF"), () -> {
            settings.boardShow = !settings.boardShow;
            Settings.save();
            this.rebuildWidgets();
        });
        this.button(this.left + 165, this.top + 25, 150, "Move / resize", () -> {
            settings.boardShow = true;
            Settings.save();
            this.minecraft.setScreen((Screen)new OverlayEditorScreen(this));
        });
        this.field(this.top + 54, "IGN", settings.boardIgn, string -> {
            String string2 = settings.boardIgn = string;
        });
        this.field(this.top + 78, "Maximum", settings.boardMax, string -> {
            String string2 = settings.boardMax = string;
        });
        this.button(this.left, this.top + 104, 30, "<", () -> {
            Settings.save();
            this.selected = Math.floorMod(this.selected - 1, this.count());
            this.rebuildWidgets();
        });
        this.button(this.left + 285, this.top + 104, 30, ">", () -> {
            Settings.save();
            this.selected = (this.selected + 1) % this.count();
            this.rebuildWidgets();
        });
        Settings.BoardTier boardTier = this.tier();
        Settings.BoardGame boardGame = this.game();
        this.field(this.top + 132, "Minimum (group)", boardTier.min, string -> {
            String string2 = boardTier.min = string;
        });
        this.field(this.top + 156, "Game name", boardGame.name, string -> {
            String string2 = boardGame.name = string;
        });
        this.field(this.top + 180, "Note", boardGame.note, string -> {
            String string2 = boardGame.note = string;
        });
        this.field(this.top + 204, "Image / item ID", boardGame.icon, string -> {
            String string2 = boardGame.icon = string;
        });
        this.button(this.left + 215, this.top + 244, 100, "Done", this::onClose);
    }

    public void onClose() {
        Settings.save();
        this.minecraft.setScreen(this.parent);
    }

    public void removed() {
        Settings.save();
        super.removed();
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f) {
        guiGraphicsExtractor.fill(0, 0, this.width, this.height, -300935136);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f) {
        super.extractRenderState(guiGraphicsExtractor, n, n2, f);
        guiGraphicsExtractor.text(this.font, "Game text overlay", this.left, this.top, -15276, true);
        String[] stringArray = new String[]{"IGN", "Maximum", "Minimum (group)", "Game name", "Note", "Image / item ID"};
        int[] nArray = new int[]{54, 78, 132, 156, 180, 204};
        int n3 = 0;
        while (n3 < stringArray.length) {
            guiGraphicsExtractor.text(this.font, stringArray[n3], this.left, this.top + nArray[n3] + 5, -1, false);
            ++n3;
        }
        guiGraphicsExtractor.text(this.font, "Game " + (this.selected + 1) + " / " + this.count(), this.left + 108, this.top + 110, -1, false);
        guiGraphicsExtractor.text(this.font, "Icons: cards, roulette, paper, wheel, arrows, horse", this.left, this.top + 228, -4799018, false);
        guiGraphicsExtractor.text(this.font, "Or type minecraft:diamond, minecraft:paper, etc.", this.left, this.top + 240, -4799018, false);
    }
}

