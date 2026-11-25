/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FontDescription
 *  net.minecraft.network.chat.FontDescription$Resource
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Fonts;
import dev.profit.dashboard.OverlayGeometry;
import dev.profit.dashboard.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class Board {
    private static final int GOLD = -15289;
    private static final float CONDENSE = 0.84f;
    private static final Style[] BOARD_FONTS = new Style[19];

    static {
        int n = 6;
        while (n <= 18) {
            Board.BOARD_FONTS[n] = Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)("board_bold_" + n))));
            ++n;
        }
    }

    private static void icon(GuiGraphicsExtractor guiGraphicsExtractor, Font font, String string, int n, int n2) {
        String string2;
        if (string != null && string.contains(":")) {
            try {
                Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse((String)string));
                if (item != null) {
                    guiGraphicsExtractor.pose().pushMatrix();
                    guiGraphicsExtractor.pose().translate((float)n, (float)n2);
                    guiGraphicsExtractor.pose().scale(0.65f, 0.65f);
                    guiGraphicsExtractor.item(new ItemStack((ItemLike)item), 0, 0);
                    guiGraphicsExtractor.pose().popMatrix();
                    return;
                }
            } catch (Exception exception) {}
        }
        string2 = string == null ? "cards" : string;
        int n3 = -3887;
        int n4 = -14274498;
        int n5 = -1751211;
        switch (string2) {
            case "cards": {
                guiGraphicsExtractor.fill(n, n2 + 1, n + 6, n2 + 10, n4);
                guiGraphicsExtractor.fill(n + 1, n2 + 2, n + 5, n2 + 9, n3);
                guiGraphicsExtractor.fill(n + 2, n2 + 3, n + 4, n2 + 5, n4);
                guiGraphicsExtractor.fill(n + 4, n2, n + 11, n2 + 11, -7767451);
                guiGraphicsExtractor.fill(n + 5, n2 + 1, n + 10, n2 + 10, -1299);
                guiGraphicsExtractor.fill(n + 6, n2 + 3, n + 7, n2 + 4, n5);
                guiGraphicsExtractor.fill(n + 8, n2 + 3, n + 9, n2 + 4, n5);
                guiGraphicsExtractor.fill(n + 6, n2 + 4, n + 9, n2 + 5, n5);
                guiGraphicsExtractor.fill(n + 7, n2 + 5, n + 8, n2 + 6, n5);
                guiGraphicsExtractor.fill(n + 8, n2 + 8, n + 9, n2 + 9, n5);
                break;
            }
            case "roulette": 
            case "wheel": {
                int n6 = 0;
                while (n6 < 11) {
                    int n7 = 0;
                    while (n7 < 11) {
                        double d = n7 - 5;
                        double d2 = n6 - 5;
                        double d3 = d * d + d2 * d2;
                        if (!(d3 > 29.0)) {
                            int n8 = d3 > 19.0 ? -2773415 : (d3 < 4.0 ? -2112134 : (((int)Math.floor((Math.atan2(d2, d) + Math.PI) * 8.0 / Math.PI) & 1) == 0 ? n5 : n4));
                            if (string2.equals("wheel") && d3 <= 19.0 && d3 >= 4.0) {
                                n8 = ((int)Math.floor((Math.atan2(d2, d) + Math.PI) * 4.0 / Math.PI) & 1) == 0 ? -5147676 : -11502635;
                            }
                            guiGraphicsExtractor.fill(n + n7, n2 + n6, n + n7 + 1, n2 + n6 + 1, n8);
                        }
                        ++n7;
                    }
                    ++n6;
                }
                guiGraphicsExtractor.fill(n + 7, n2 + 1, n + 9, n2 + 3, -1);
                break;
            }
            case "paper": {
                int n10 = 0;
                while (n10 < 9) {
                    int n11 = Math.abs(n10 - 4);
                    guiGraphicsExtractor.fill(n + n11, n2 + 1 + n10, n + 11 - n11, n2 + 2 + n10, n3);
                    ++n10;
                }
                guiGraphicsExtractor.fill(n + 4, n2 + 4, n + 8, n2 + 5, -4214897);
                guiGraphicsExtractor.fill(n + 3, n2 + 6, n + 7, n2 + 7, -4214897);
                break;
            }
            case "arrows": {
                int n12 = -10365365;
                guiGraphicsExtractor.fill(n + 2, n2 + 2, n + 4, n2 + 11, n12);
                guiGraphicsExtractor.fill(n + 1, n2 + 3, n + 5, n2 + 5, n12);
                guiGraphicsExtractor.fill(n, n2 + 4, n + 6, n2 + 5, n12);
                guiGraphicsExtractor.fill(n + 7, n2, n + 9, n2 + 9, n5);
                guiGraphicsExtractor.fill(n + 6, n2 + 6, n + 10, n2 + 8, n5);
                guiGraphicsExtractor.fill(n + 5, n2 + 6, n + 11, n2 + 7, n5);
                break;
            }
            case "horse": {
                int n13 = -10233393;
                guiGraphicsExtractor.fill(n + 1, n2 + 5, n + 8, n2 + 8, n13);
                guiGraphicsExtractor.fill(n + 6, n2 + 2, n + 9, n2 + 7, n13);
                guiGraphicsExtractor.fill(n + 7, n2 + 1, n + 10, n2 + 5, n13);
                guiGraphicsExtractor.fill(n + 9, n2 + 3, n + 11, n2 + 5, n13);
                guiGraphicsExtractor.fill(n + 7, n2, n + 8, n2 + 2, n13);
                guiGraphicsExtractor.fill(n + 1, n2 + 7, n + 3, n2 + 11, n13);
                guiGraphicsExtractor.fill(n + 6, n2 + 7, n + 8, n2 + 11, n13);
                guiGraphicsExtractor.fill(n, n2 + 4, n + 2, n2 + 7, n13);
                guiGraphicsExtractor.fill(n + 8, n2 + 2, n + 9, n2 + 3, n4);
                break;
            }
            default: {
                Fonts.draw(guiGraphicsExtractor, font, string2, n, n2 + 1, -1, 0.9f, true);
            }
        }
    }

    private static int fontSize(float f) {
        return Math.clamp((long)Math.round(f * 8.0f), 6, 18);
    }

    private static Component component(String string, float f) {
        return Component.literal((String)(string == null ? "" : string)).withStyle(BOARD_FONTS[Board.fontSize(f)]);
    }

    private static int width(Font font, String string, float f, boolean bl) {
        return Math.round((float)font.width((FormattedText)Board.component(string, f)) * 0.84f);
    }

    private static void text(GuiGraphicsExtractor guiGraphicsExtractor, Font font, String string, int n, int n2, int n3, float f, boolean bl) {
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate((float)n, (float)(n2 - Math.round(7.0f - 0.73f * (float)Board.fontSize(f))));
        guiGraphicsExtractor.pose().scale(0.84f, 1.0f);
        guiGraphicsExtractor.text(font, Board.component(string, f), 0, 0, n3, false);
        guiGraphicsExtractor.pose().popMatrix();
    }

    private static int naturalWidth(Font font) {
        Settings settings = Settings.S;
        int n = 154;
        n = Math.max(n, 53 + Math.round((float)Board.width(font, settings.boardIgn, 2.2f, true) * 1.4f / 0.84f));
        n = Math.max(n, 16 + Board.width(font, "Maximum:", 1.0f, true) + Board.width(font, settings.boardMax, 1.0f, true));
        if (settings.boardTiers != null) {
            for (Settings.BoardTier boardTier : settings.boardTiers) {
                if (boardTier.games == null) continue;
                for (Settings.BoardGame boardGame : boardTier.games) {
                    n = Math.max(n, 20 + Board.width(font, boardGame.name, 1.0f, true) + Board.width(font, boardGame.note, 0.75f, false));
                }
            }
        }
        return n;
    }

    private static int naturalHeight() {
        int n = 47;
        if (Settings.S.boardTiers != null) {
            for (Settings.BoardTier boardTier : Settings.S.boardTiers) {
                n += 20 + (boardTier.games == null ? 0 : boardTier.games.size() * 14);
            }
        }
        return n;
    }

    private static float scale(Minecraft minecraft) {
        return Math.max(0.25f, Settings.S.boardSize) / Math.max(1.0f, (float)minecraft.getWindow().getGuiScale());
    }

    public static OverlayGeometry.Box bounds(Minecraft minecraft) {
        Settings settings = Settings.S;
        float f = Board.scale(minecraft);
        return new OverlayGeometry.Box(settings.boardX, settings.boardY, Math.round((float)(Board.naturalWidth(minecraft.font) + 8) * f), Math.round((float)Board.naturalHeight() * f));
    }

    public static void render(GuiGraphicsExtractor guiGraphicsExtractor, Font font, Minecraft minecraft) {
        Settings settings = Settings.S;
        if (!settings.boardShow || settings.boardTiers == null) {
            return;
        }
        float f = Board.scale(minecraft);
        int n = Board.naturalWidth(font);
        Board.naturalHeight();
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate((float)settings.boardX, (float)settings.boardY);
        guiGraphicsExtractor.pose().scale(f, f);
        Board.text(guiGraphicsExtractor, font, "I G N", 9, 12, -15289, 1.0f, true);
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate(43.0f, 5.0f);
        guiGraphicsExtractor.pose().scale(1.6666667f, 1.4f);
        Board.text(guiGraphicsExtractor, font, settings.boardIgn, 0, 0, -1, 2.2f, true);
        guiGraphicsExtractor.pose().popMatrix();
        Board.text(guiGraphicsExtractor, font, "Maximum:", 9, 29, -1, 1.0f, true);
        Board.text(guiGraphicsExtractor, font, settings.boardMax, 10 + Board.width(font, "Maximum:", 1.0f, true), 29, -15289, 1.0f, true);
        int n2 = 47;
        for (Settings.BoardTier boardTier : settings.boardTiers) {
            Board.text(guiGraphicsExtractor, font, "M I N I M U M", 9, n2 + 2, -1, 0.75f, true);
            Board.text(guiGraphicsExtractor, font, boardTier.min, 14 + Board.width(font, "M I N I M U M", 0.75f, true), n2, -15289, 1.2f, true);
            n2 += 14;
            if (boardTier.games != null) {
                for (Settings.BoardGame boardGame : boardTier.games) {
                    guiGraphicsExtractor.fill(9, n2, n, n2 + 12, -938470632);
                    guiGraphicsExtractor.fill(9, n2, 10, n2 + 12, -15289);
                    guiGraphicsExtractor.pose().pushMatrix();
                    guiGraphicsExtractor.pose().translate(13.0f, (float)(n2 + 3));
                    guiGraphicsExtractor.pose().scale(0.6f, 0.6f);
                    Board.icon(guiGraphicsExtractor, font, boardGame.icon, 0, 0);
                    guiGraphicsExtractor.pose().popMatrix();
                    Board.text(guiGraphicsExtractor, font, boardGame.name, 22, n2 + 3, -1, 0.9f, true);
                    if (boardGame.note != null && !boardGame.note.isEmpty()) {
                        Board.text(guiGraphicsExtractor, font, boardGame.note, 26 + Board.width(font, boardGame.name, 0.9f, true), n2 + 4, -15289, 0.75f, false);
                    }
                    n2 += 14;
                }
            }
            n2 += 6;
        }
        guiGraphicsExtractor.pose().popMatrix();
    }
}

