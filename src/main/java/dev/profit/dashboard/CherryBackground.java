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

final class CherryBackground {
    private static final Identifier ART = Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)"textures/gui/cherry_blossom.png");

    CherryBackground() {
    }

    static void draw(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, boolean bl) {
        float f = 1.7777778f;
        float f2 = (float)n3 / (float)Math.max(1, n4);
        float f3 = f2 < f ? f2 / f : 1.0f;
        float f4 = f2 > f ? f / f2 : 1.0f;
        guiGraphicsExtractor.blit(ART, n, n2, n + n3, n2 + n4, (1.0f - f3) / 2.0f, (1.0f + f3) / 2.0f, (1.0f - f4) / 2.0f, (1.0f + f4) / 2.0f);
        guiGraphicsExtractor.fill(n, n2, n + n3, n2 + n4, 806096928);
        if (!bl) {
            return;
        }
        double d = (double)System.nanoTime() / 1.0E9;
        int n5 = 0;
        while (n5 < 34) {
            double d2 = (double)n5 * 2.399963;
            double d3 = ((double)n5 * 0.618034 + d * (0.009 + (double)(n5 % 4) * 0.001)) % 1.0;
            double d4 = ((double)n5 * 0.381966 + d * (0.018 + (double)(n5 % 5) * 0.003)) % 1.0;
            int n6 = n + 4 + (int)(d3 * (double)Math.max(1, n3 - 8) + Math.sin(d * 0.9 + d2) * 3.0);
            int n7 = n2 + 4 + (int)(d4 * (double)Math.max(1, n4 - 8));
            guiGraphicsExtractor.pose().pushMatrix();
            guiGraphicsExtractor.pose().translate((float)n6, (float)n7);
            guiGraphicsExtractor.pose().rotate((float)(d2 + d * 0.6));
            guiGraphicsExtractor.fill(-1, -2, 1, 2, -1191198498);
            guiGraphicsExtractor.fill(0, -1, 2, 1, -1862278929);
            guiGraphicsExtractor.pose().popMatrix();
            ++n5;
        }
    }
}

