/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.resources.Identifier
 */
package dev.profit.dashboard;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

final class ScenicBackgrounds {
    private static final String[] NAMES = new String[]{"midnight", "winter_lights", "sunset_palms", "dusk", "cherry_blossom", "maple", "snowpine", "savanna", "moonbloom"};
    private static final Identifier[] ART = new Identifier[NAMES.length];

    static {
        int n = 0;
        while (n < NAMES.length) {
            ScenicBackgrounds.ART[n] = Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)("textures/gui/" + NAMES[n] + ".png"));
            ++n;
        }
    }

    ScenicBackgrounds() {
    }

    static void draw(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, int n5, boolean bl) {
        if (n3 <= 0 || n4 <= 0) {
            return;
        }
        int n6 = Math.floorMod(n5, ART.length);
        float f = (float)n3 / (float)n4;
        float f2 = 1.7777778f;
        float f3 = f < f2 ? f / f2 : 1.0f;
        float f4 = f > f2 ? f2 / f : 1.0f;
        guiGraphicsExtractor.blit(ART[n6], n, n2, n + n3, n2 + n4, (1.0f - f3) / 2.0f, (1.0f + f3) / 2.0f, (1.0f - f4) / 2.0f, (1.0f + f4) / 2.0f);
        guiGraphicsExtractor.fill(n, n2, n + n3, n2 + n4, 806031392);
        if (!bl) {
            return;
        }
        double d = (double)System.nanoTime() / 1.0E9;
        int n7 = n6 == 1 || n6 == 6 ? 32 : (n6 == 4 ? 30 : (n6 == 5 ? 23 : 16));
        int n8 = 0;
        while (n8 < n7) {
            double d2 = (double)n8 * 2.399963;
            double d3 = ((double)n8 * 0.618034 + d * (n6 == 7 ? 0.023 : 0.007 + (double)(n8 % 4) * 0.001)) % 1.0;
            double d4 = ((double)n8 * 0.381966 + d * (n6 == 0 || n6 == 8 ? 0.003 : 0.012 + (double)(n8 % 5) * 0.002)) % 1.0;
            int n9 = n + 4 + (int)(d3 * (double)Math.max(1, n3 - 8) + Math.sin(d * 0.8 + d2) * 3.0);
            int n10 = n2 + 4 + (int)(d4 * (double)Math.max(1, n4 - 8));
            if (n6 == 4 || n6 == 5) {
                guiGraphicsExtractor.pose().pushMatrix();
                guiGraphicsExtractor.pose().translate((float)n9, (float)n10);
                guiGraphicsExtractor.pose().rotate((float)(d2 + d * 0.6));
                guiGraphicsExtractor.fill(-1, -2, 1, 2, n6 == 4 ? -1191198498 : -1377199549);
                guiGraphicsExtractor.fill(0, -1, 2, 1, n6 == 4 ? -1862278929 : -1863951054);
                guiGraphicsExtractor.pose().popMatrix();
            } else if (n6 == 1 || n6 == 6) {
                guiGraphicsExtractor.fill(n9, n10, n9 + 1 + (n8 % 6 == 0 ? 1 : 0), n10 + 1, -1460865793);
            } else {
                int n11 = 35 + (int)(45.0 * (0.5 + 0.5 * Math.sin(d + d2)));
                int n12 = n6 == 7 ? 14333316 : (n6 == 8 ? 12828150 : (n6 == 2 ? 16768427 : 0xD5DFF5));
                guiGraphicsExtractor.fill(n9, n10, n9 + 1, n10 + 1, n11 << 24 | n12);
            }
            ++n8;
        }
    }
}

