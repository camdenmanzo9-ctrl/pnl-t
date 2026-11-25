/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FontDescription
 *  net.minecraft.network.chat.FontDescription$Resource
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.Identifier
 */
package dev.profit.dashboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public final class Fonts {
    private static final int[] SIZES = new int[]{6, 7, 8, 9, 10, 12, 14, 18};
    private static final int[] OVERSAMPLES = new int[]{2, 3, 4};
    private static final Style[][] REG = new Style[OVERSAMPLES.length][SIZES.length];
    private static final Style[][] BOLD = new Style[OVERSAMPLES.length][SIZES.length];

    private Fonts() {
    }

    private static int quality() {
        try {
            int s = Math.round(Minecraft.getInstance().getWindow().getGuiScale());
            if (s <= 2) {
                return 0;
            }
            if (s == 3) {
                return 1;
            }
            return 2;
        } catch (Exception e) {
            return 0;
        }
    }

    private static int pick(float scale) {
        float target = 8.0f * scale;
        int best = 0;
        for (int i = 1; i < SIZES.length; ++i) {
            if (!(Math.abs((float)SIZES[i] - target) < Math.abs((float)SIZES[best] - target))) continue;
            best = i;
        }
        return best;
    }

    private static Component comp(String s, int i, boolean bold) {
        int q = Fonts.quality();
        return Component.literal((String)s).withStyle(bold ? BOLD[q][i] : REG[q][i]);
    }

    public static void draw(GuiGraphicsExtractor g, Font f, String s, int x, int y, int color, float scale, boolean bold) {
        g.text(f, Fonts.comp(s, Fonts.pick(scale), bold), x, y, color, false);
    }

    public static int width(Font f, String s, float scale, boolean bold) {
        return f.width((FormattedText)Fonts.comp(s, Fonts.pick(scale), bold));
    }

    public static float capHeight(float scale) {
        return 0.73f * (float)SIZES[Fonts.pick(scale)];
    }

    public static float capTop(float scale) {
        return 7.0f - 0.73f * (float)SIZES[Fonts.pick(scale)];
    }

    static {
        for (int o = 0; o < OVERSAMPLES.length; ++o) {
            String suffix = OVERSAMPLES[o] == 2 ? "" : "_o" + OVERSAMPLES[o];
            for (int i = 0; i < SIZES.length; ++i) {
                Fonts.REG[o][i] = Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)("inter_" + SIZES[i] + suffix))));
                Fonts.BOLD[o][i] = Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)("inter_bold_" + SIZES[i] + suffix))));
            }
        }
    }
}

