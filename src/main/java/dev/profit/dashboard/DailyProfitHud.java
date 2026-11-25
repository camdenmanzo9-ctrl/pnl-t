/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Alerts;
import dev.profit.dashboard.Money;
import dev.profit.dashboard.OverlayGeometry;
import dev.profit.dashboard.OverviewScreen;
import dev.profit.dashboard.PaymentLedger;
import dev.profit.dashboard.Settings;
import java.time.LocalDate;
import java.time.ZoneId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

final class DailyProfitHud {
    DailyProfitHud() {
    }

    private static Data data(Minecraft minecraft) {
        long l = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long l2 = 0L;
        for (PaymentLedger.Payment object2 : PaymentLedger.paymentsSince(l)) {
            l2 += object2.paidYou - object2.youPaid;
        }
        String string = (l2 > 0L ? "+" : (l2 < 0L ? "-" : "")) + Money.format(Math.abs(l2));
        String string2 = "Win streak: " + Math.max(0, Settings.S.streak);
        int n = Math.max(150, Math.max(minecraft.font.width("PROFIT OF THE DAY") + 40, Math.max(minecraft.font.width(string) * 2 + 24, minecraft.font.width(string2) + 24)));
        return new Data(l2, string, string2, n, 60);
    }

    static OverlayGeometry.Box bounds(Minecraft minecraft) {
        Data data = DailyProfitHud.data(minecraft);
        Settings settings = Settings.S;
        float f = settings.hudSize;
        int n = (int)((float)minecraft.getWindow().getGuiScaledWidth() / f);
        int n2 = (int)((float)minecraft.getWindow().getGuiScaledHeight() / f);
        int n3 = (settings.hudCorner % 2 == 0 ? 6 : n - data.w - 6) + settings.hudOffX;
        int n4 = (settings.hudCorner < 2 ? 6 : n2 - data.h - 40) + settings.hudOffY;
        return new OverlayGeometry.Box((int)((float)n3 * f), (int)((float)n4 * f), (int)((float)data.w * f), (int)((float)data.h * f));
    }

    static void draw(GuiGraphicsExtractor guiGraphicsExtractor, Minecraft minecraft) {
        Data data = DailyProfitHud.data(minecraft);
        Settings settings = Settings.S;
        float f = settings.hudSize;
        int n = (int)((float)minecraft.getWindow().getGuiScaledWidth() / f);
        int n2 = (int)((float)minecraft.getWindow().getGuiScaledHeight() / f);
        int n3 = (settings.hudCorner % 2 == 0 ? 6 : n - data.w - 6) + settings.hudOffX;
        int n4 = (settings.hudCorner < 2 ? 6 : n2 - data.h - 40) + settings.hudOffY;
        Alerts.lastProfitRect = new int[]{(int)((float)n3 * f), (int)((float)n4 * f), (int)((float)data.w * f), (int)((float)data.h * f)};
        int n5 = Math.max(0, Math.min(255, settings.hudOpacity * 255 / 100));
        int n6 = 0xFF000000 | settings.hudAccentColor & 0xFFFFFF;
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().scale(f, f);
        OverviewScreen.round(guiGraphicsExtractor, n3, n4, data.w, data.h, 2, n5 << 24 | settings.hudBg & 0xFFFFFF);
        if (settings.hudBorder) {
            guiGraphicsExtractor.fill(n3 + 2, n4, n3 + data.w - 2, n4 + 1, Integer.MIN_VALUE | n6 & 0xFFFFFF);
            guiGraphicsExtractor.fill(n3 + 2, n4 + data.h - 1, n3 + data.w - 2, n4 + data.h, Integer.MIN_VALUE | n6 & 0xFFFFFF);
            guiGraphicsExtractor.fill(n3 + data.w - 1, n4 + 1, n3 + data.w, n4 + data.h - 1, 0x60000000 | n6 & 0xFFFFFF);
        }
        if (settings.hudAccentBar) {
            guiGraphicsExtractor.fill(n3 + 1, n4 + 2, n3 + 5, n4 + data.h - 2, n6);
        }
        guiGraphicsExtractor.text(minecraft.font, "PROFIT OF THE DAY", n3 + 11, n4 + 7, 0xFF000000 | settings.hudTitle, true);
        int n7 = 0xFF000000 | (data.profit > 0L ? settings.hudGain : (data.profit < 0L ? settings.hudLoss : settings.hudZero));
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate((float)(n3 + 11), (float)(n4 + 21));
        guiGraphicsExtractor.pose().scale(2.0f, 2.0f);
        guiGraphicsExtractor.text(minecraft.font, data.value, 0, 0, n7, true);
        guiGraphicsExtractor.pose().popMatrix();
        String string = "Win streak: ";
        guiGraphicsExtractor.text(minecraft.font, string, n3 + 11, n4 + 46, -1448235, true);
        guiGraphicsExtractor.text(minecraft.font, Integer.toString(Math.max(0, settings.streak)), n3 + 11 + minecraft.font.width(string), n4 + 46, n6, true);
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate((float)(n3 + data.w - 17), (float)(n4 + 4));
        guiGraphicsExtractor.pose().scale(0.75f, 0.75f);
        guiGraphicsExtractor.item(new ItemStack((ItemLike)Items.EMERALD), 0, 0);
        guiGraphicsExtractor.pose().popMatrix();
        guiGraphicsExtractor.pose().popMatrix();
    }

    private record Data(long profit, String value, String streak, int w, int h) {
    }
}

