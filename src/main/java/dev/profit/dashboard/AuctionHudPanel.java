/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.PlayerFaceExtractor
 *  net.minecraft.world.entity.player.PlayerSkin
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Auction;
import dev.profit.dashboard.AuctionActivity;
import dev.profit.dashboard.AuctionItemPreview;
import dev.profit.dashboard.Fonts;
import dev.profit.dashboard.HistorySkinCache;
import dev.profit.dashboard.Money;
import dev.profit.dashboard.OverlayGeometry;
import dev.profit.dashboard.OverviewScreen;
import dev.profit.dashboard.Settings;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.world.entity.player.PlayerSkin;

final class AuctionHudPanel {
    static final int WIDTH = 208;
    static final int HEIGHT = 192;
    static final int ANCHOR_OFFSET = 94;
    private static final int GOLD = -13755;

    AuctionHudPanel() {
    }

    private static void text(GuiGraphicsExtractor guiGraphicsExtractor, Font font, String string, int n, int n2, int n3, float f, boolean bl) {
        Fonts.draw(guiGraphicsExtractor, font, string, n, n2, n3, f, bl);
    }

    private static void centered(GuiGraphicsExtractor guiGraphicsExtractor, Font font, String string, int n, int n2, int n3, float f, boolean bl) {
        AuctionHudPanel.text(guiGraphicsExtractor, font, string, n - Fonts.width(font, string, f, bl) / 2, n2, n3, f, bl);
    }

    /*
     * Unable to fully structure code
     */
    private static String fit(Font font, String s, int max, float scale) {
        if (Fonts.width(font, s, scale, true) <= max) return s;
        while (!s.isEmpty() && Fonts.width(font, s + "\u2026", scale, true) > max) s = s.substring(0, s.length() - 1);
        return s + "\u2026";
    }

    private static OverlayGeometry.Box logicalBounds(Minecraft minecraft, int n, int n2, float f) {
        int n3 = Math.max(1, Math.round(4.0f / f));
        int n4 = Math.max(n3, Math.min((int)((float)minecraft.getWindow().getGuiScaledWidth() / f) - 208 - n3, Math.round((float)n / f) - 104));
        int n5 = Math.max(n3, Math.min((int)((float)minecraft.getWindow().getGuiScaledHeight() / f) - 192 - n3, Math.round((float)n2 / f) - 94));
        return new OverlayGeometry.Box(n4, n5, 208, 192);
    }

    static OverlayGeometry.Box bounds(Minecraft minecraft) {
        float f = OverlayGeometry.scale(Settings.S.auctionHudSize);
        OverlayGeometry.Box box = AuctionHudPanel.logicalBounds(minecraft, minecraft.getWindow().getGuiScaledWidth() / 2 + Settings.S.auctionOffX, minecraft.getWindow().getGuiScaledHeight() / 2 + 14 + Settings.S.auctionOffY, f);
        return new OverlayGeometry.Box(Math.round((float)box.x() * f), Math.round((float)box.y() * f), Math.round((float)box.w() * f), Math.round((float)box.h() * f));
    }

    private static void crown(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2) {
        guiGraphicsExtractor.fill(n - 7, n2 + 4, n + 7, n2 + 6, -13755);
        guiGraphicsExtractor.fill(n - 7, n2 + 1, n - 4, n2 + 5, -13755);
        guiGraphicsExtractor.fill(n - 2, n2, n + 2, n2 + 5, -13755);
        guiGraphicsExtractor.fill(n + 4, n2 + 1, n + 7, n2 + 5, -13755);
    }

    private static void podium(GuiGraphicsExtractor guiGraphicsExtractor, Font font, Minecraft minecraft, List<Map.Entry<String, Long>> list, int n, int n2, int n3) {
        int n4;
        int n5 = 58;
        int n6 = n3 + (n == 0 ? 110 : (n == 1 ? 117 : 120));
        int n7 = n == 0 ? 17 : (n4 = n == 1 ? 10 : 7);
        int n8 = n == 0 ? -13755 : (n == 1 ? -4207663 : -1598641);
        boolean bl = n < list.size();
        int n9 = bl ? n8 : 0x50000000 | n8 & 0xFFFFFF;
        guiGraphicsExtractor.fill(n2, n6, n2 + n5, n6 + n4, n9);
        AuctionHudPanel.centered(guiGraphicsExtractor, font, n == 0 ? "1ST" : (n == 1 ? "2ND" : "3RD"), n2 + n5 / 2, n6 + (n4 - 6) / 2, -14211809, 0.75f, true);
        if (!bl) {
            AuctionHudPanel.centered(guiGraphicsExtractor, font, "\u2014", n2 + n5 / 2, n3 + 132, -8552308, 0.75f, false);
            return;
        }
        Map.Entry<String, Long> entry = list.get(n);
        int n10 = n == 0 ? 21 : 18;
        int n11 = n6 - n10 - 4;
        if (Settings.S.skinFaces) {
            PlayerFaceExtractor.extractRenderState((GuiGraphicsExtractor)guiGraphicsExtractor, (PlayerSkin)HistorySkinCache.get(minecraft, entry.getKey()), (int)(n2 + (n5 - n10) / 2), (int)n11, (int)n10);
        } else {
            AuctionHudPanel.centered(guiGraphicsExtractor, font, Integer.toString(n + 1), n2 + n5 / 2, n11 + 4, n8, 1.4f, true);
        }
        if (n == 0) {
            AuctionHudPanel.crown(guiGraphicsExtractor, n2 + n5 / 2, n11 - 8);
        }
        AuctionHudPanel.centered(guiGraphicsExtractor, font, AuctionHudPanel.fit(font, entry.getKey(), n5 - 2, 0.75f), n2 + n5 / 2, n3 + 131, -1184779, 0.75f, true);
        AuctionHudPanel.centered(guiGraphicsExtractor, font, Money.format(entry.getValue()), n2 + n5 / 2, n3 + 141, -9375880, 0.75f, true);
    }

    static void draw(GuiGraphicsExtractor guiGraphicsExtractor, Font font, Minecraft minecraft, int n, int n2) {
        float f = OverlayGeometry.scale(Settings.S.auctionHudSize);
        OverlayGeometry.Box box = AuctionHudPanel.logicalBounds(minecraft, n, n2, f);
        int n3 = box.x();
        int n4 = box.y();
        int n5 = box.w();
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().scale(f, f);
        OverviewScreen.round(guiGraphicsExtractor, n3, n4, n5, 192, 2, -1123546350);
        guiGraphicsExtractor.fill(n3 + 1, n4, n3 + n5 - 1, n4 + 1, 1895811653);
        AuctionHudPanel.centered(guiGraphicsExtractor, font, "\u2022 LIVE AUCTION \u2022", n3 + n5 / 2, n4 + 8, -13755, 0.85f, true);
        guiGraphicsExtractor.fill(n3 + 1, n4 + 21, n3 + n5 - 1, n4 + 22, 1358940741);
        AuctionItemPreview.drawScaled(guiGraphicsExtractor, Auction.item, n3 + 20, n4 + 25, 21, f);
        AuctionHudPanel.centered(guiGraphicsExtractor, font, AuctionHudPanel.fit(font, Auction.displayName(), n5 - 68, 0.9f), n3 + n5 / 2 + 14, n4 + 28, -856328, 0.9f, true);
        long l = ((Auction.running ? Auction.left() : 0L) + 999L) / 1000L;
        AuctionHudPanel.centered(guiGraphicsExtractor, font, String.format(Locale.ROOT, "%d:%02d", l / 60L, l % 60L), n3 + n5 / 2, n4 + 43, -461057, 2.0f, true);
        guiGraphicsExtractor.fill(n3 + 1, n4 + 64, n3 + n5 - 1, n4 + 65, 1358940741);
        AuctionHudPanel.centered(guiGraphicsExtractor, font, "\u2022 TOP BIDDERS \u2022", n3 + n5 / 2, n4 + 69, -13755, 0.8f, true);
        List<Map.Entry<String, Long>> list = Auction.sortedBids();
        AuctionHudPanel.podium(guiGraphicsExtractor, font, minecraft, list, 1, n3 + 8, n4);
        AuctionHudPanel.podium(guiGraphicsExtractor, font, minecraft, list, 0, n3 + 75, n4);
        AuctionHudPanel.podium(guiGraphicsExtractor, font, minecraft, list, 2, n3 + 142, n4);
        if (list.isEmpty()) {
            AuctionHudPanel.centered(guiGraphicsExtractor, font, "Awaiting bids", n3 + n5 / 2, n4 + 91, -7235931, 0.75f, false);
        }
        List<AuctionActivity.Bid> list2 = AuctionActivity.recent();
        int n6 = 0;
        while (n6 < Math.min(2, list2.size())) {
            AuctionActivity.Bid bid = list2.get(n6);
            int n7 = n4 + 156 + n6 * 10;
            if (n6 % 2 == 0) {
                guiGraphicsExtractor.fill(n3 + 1, n7 - 1, n3 + n5 - 1, n7 + 9, 1075518250);
            }
            AuctionHudPanel.text(guiGraphicsExtractor, font, AuctionHudPanel.fit(font, bid.name(), n5 - 86, 0.75f), n3 + 8, n7, -2369817, 0.75f, true);
            String string = "+" + Money.format(bid.amount());
            AuctionHudPanel.text(guiGraphicsExtractor, font, string, n3 + n5 - 8 - Fonts.width(font, string, 0.75f, true), n7, -9375880, 0.75f, true);
            ++n6;
        }
        guiGraphicsExtractor.fill(n3 + 1, n4 + 178, n3 + n5 - 1, n4 + 179, 1090505285);
        AuctionHudPanel.text(guiGraphicsExtractor, font, "TOTAL " + Money.format(AuctionActivity.total()), n3 + 8, n4 + 182, -13755, 0.75f, true);
        String string = list.size() + (list.size() == 1 ? " bidder" : " bidders");
        AuctionHudPanel.text(guiGraphicsExtractor, font, string, n3 + n5 - 8 - Fonts.width(font, string, 0.7f, false), n4 + 182, -4738365, 0.7f, false);
        guiGraphicsExtractor.pose().popMatrix();
    }
}

