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
    static final int HEIGHT = 96;
    static final int ANCHOR_OFFSET = 46;
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
        int n4 = Math.max(n3, Math.min((int)((float)minecraft.getWindow().getGuiScaledWidth() / f) - WIDTH - n3, Math.round((float)n / f) - WIDTH / 2));
        int n5 = Math.max(n3, Math.min((int)((float)minecraft.getWindow().getGuiScaledHeight() / f) - HEIGHT - n3, Math.round((float)n2 / f) - ANCHOR_OFFSET));
        return new OverlayGeometry.Box(n4, n5, WIDTH, HEIGHT);
    }

    static OverlayGeometry.Box bounds(Minecraft minecraft) {
        float f = OverlayGeometry.scale(Settings.S.auctionHudSize);
        OverlayGeometry.Box box = AuctionHudPanel.logicalBounds(minecraft, minecraft.getWindow().getGuiScaledWidth() / 2 + Settings.S.auctionOffX, minecraft.getWindow().getGuiScaledHeight() / 2 + 14 + Settings.S.auctionOffY, f);
        return new OverlayGeometry.Box(Math.round((float)box.x() * f), Math.round((float)box.y() * f), Math.round((float)box.w() * f), Math.round((float)box.h() * f));
    }

    static void draw(GuiGraphicsExtractor g, Font font, Minecraft minecraft, int n, int n2) {
        float f = OverlayGeometry.scale(Settings.S.auctionHudSize);
        OverlayGeometry.Box box = AuctionHudPanel.logicalBounds(minecraft, n, n2, f);
        int x = box.x(), y = box.y(), w = box.w();
        g.pose().pushMatrix();
        g.pose().scale(f, f);
        OverviewScreen.round(g, x, y, w, HEIGHT, 6, 0xE6142233);
        // item tile with the item turning inside
        OverviewScreen.round(g, x + 10, y + 10, 34, 34, 6, 0xFF0B0F16);
        AuctionItemPreview.drawScaled(g, Auction.item, x + 12, y + 12, 30, f);
        // name + count
        AuctionHudPanel.text(g, font, AuctionHudPanel.fit(font, Auction.displayName(), w - 62, 1.0f), x + 52, y + 14, 0xFFFFFFFF, 1.0f, true);
        int count = Math.max(1, Auction.quantity);
        AuctionHudPanel.text(g, font, count + (count == 1 ? " item" : " items"), x + 52, y + 28, 0xFF8A93A3, 0.75f, false);
        // big timer
        long left = ((Auction.running ? Auction.left() : 0L) + 999L) / 1000L;
        AuctionHudPanel.text(g, font, String.format(Locale.ROOT, "%d:%02d", left / 60L, left % 60L), x + 10, y + 52, 0xFFFFFFFF, 2.0f, true);
        // red bar: fills up as time runs out
        int bx = x + 10, bw = w - 20, by = y + 76;
        g.fill(bx, by, bx + bw, by + 3, 0xFF2A2F38);
        double done = Auction.running && Auction.durationMs > 0 ? 1.0 - Math.max(0.0, Math.min(1.0, (double)Auction.left() / Auction.durationMs)) : (Auction.ended ? 1.0 : 0.0);
        int fill = (int)(bw * done);
        if (fill > 0) g.fill(bx, by, bx + fill, by + 3, 0xFFE53950);
        // status line
        List<Map.Entry<String, Long>> bids = Auction.sortedBids();
        String status;
        int color = 0xFF8A93A3;
        if (Auction.ended && Auction.winner != null) {
            status = "Sold to " + Auction.winner + " for " + Money.format(Auction.winAmount);
            color = 0xFF4DF58C;
        } else if (bids.isEmpty()) {
            status = "Waiting for bids";
        } else {
            status = "Top bid " + Money.format(bids.get(0).getValue()) + " by " + bids.get(0).getKey();
            color = 0xFFFFFFFF;
        }
        AuctionHudPanel.text(g, font, AuctionHudPanel.fit(font, status, w - 20, 0.75f), x + 10, y + 84, color, 0.75f, false);
        g.pose().popMatrix();
    }
}
