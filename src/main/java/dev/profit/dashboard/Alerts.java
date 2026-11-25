/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.PlayerFaceExtractor
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FontDescription
 *  net.minecraft.network.chat.FontDescription$Resource
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.chat.TextColor
 *  net.minecraft.resources.Identifier
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.entity.player.PlayerSkin
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Auction;
import dev.profit.dashboard.AuctionHudPanel;
import dev.profit.dashboard.Board;
import dev.profit.dashboard.DailyProfitHud;
import dev.profit.dashboard.Fonts;
import dev.profit.dashboard.Money;
import dev.profit.dashboard.OverlayGeometry;
import dev.profit.dashboard.OverviewScreen;
import dev.profit.dashboard.Panels;
import dev.profit.dashboard.PaymentListener;
import dev.profit.dashboard.Settings;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.PlayerSkin;

public final class Alerts {
    public static final String[] SOUND_NAMES = new String[]{"Arrow Ding", "XP Orb", "Level Up", "Pling", "Bubble"};
    private static int repeatsLeft = 0;
    private static int repeatSound = 0;
    private static long nextRepeat = 0L;
    private static final Deque<Popup> queue = new ArrayDeque<Popup>();
    private static Popup current = null;
    private static final Style INTER = Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)"inter")));
    private static final Style INTER_BOLD = Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)"inter_bold")));
    public static int[] lastProfitRect = new int[4];

    private Alerts() {
    }

    private static SoundEvent soundFor(int n) {
        return switch (Math.floorMod(n, SOUND_NAMES.length)) {
            case 0 -> SoundEvents.ARROW_HIT_PLAYER;
            case 1 -> SoundEvents.EXPERIENCE_ORB_PICKUP;
            case 2 -> SoundEvents.PLAYER_LEVELUP;
            case 3 -> SoundEvents.AMETHYST_BLOCK_CHIME;
            default -> SoundEvents.BUBBLE_POP;
        };
    }

    public static void playSound(int n, int n2) {
        Settings settings = Settings.S;
        Minecraft minecraft = Minecraft.getInstance();
        float f = Math.max(0.0f, Math.min(1.0f, (float)settings.soundVolume / 100.0f));
        minecraft.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)Alerts.soundFor(n), (float)settings.soundPitch, (float)f));
        repeatsLeft = Math.max(0, n2 - 1);
        repeatSound = n;
        nextRepeat = System.currentTimeMillis() + 250L;
    }

    public static void playConfirmSound() {
        if (Settings.S.soundConfirm) {
            Alerts.playSound(Settings.S.confirmSoundIndex, 1);
        }
    }

    public static void tick() {
        long l = System.currentTimeMillis();
        if (repeatsLeft > 0 && l >= nextRepeat) {
            Settings settings = Settings.S;
            float f = Math.max(0.0f, Math.min(1.0f, (float)settings.soundVolume / 100.0f));
            Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)Alerts.soundFor(repeatSound), (float)settings.soundPitch, (float)f));
            --repeatsLeft;
            nextRepeat = l + 250L;
        }
        if (current != null && l - Alerts.current.start > (long)(Settings.S.popupDuration * 1000.0f) + 300L) {
            current = null;
        }
        if (current == null && !queue.isEmpty()) {
            current = queue.poll();
            current = new Popup(Alerts.current.name, Alerts.current.amount, l);
            if (Settings.S.soundPopup) {
                Alerts.playSound(Settings.S.soundIndex, Settings.S.soundRepeat);
            }
        }
    }

    public static void onPayment(String string, long l, boolean bl) {
        Settings settings = Settings.S;
        if (!bl || !settings.popupShow || l < settings.popupMin) {
            return;
        }
        if (current != null && settings.soundPopup && settings.soundWhenQueued) {
            Alerts.playSound(settings.soundIndex, 1);
        }
        queue.add(new Popup(string, l, 0L));
    }

    public static void testPopup() {
        queue.add(new Popup("ExamplePlayer", Math.max(Settings.S.popupMin, 5000000000L), 0L));
    }

    public static void render(GuiGraphicsExtractor guiGraphicsExtractor) {
        if (Panels.allHidden) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }
        Font font = minecraft.font;
        Board.render(guiGraphicsExtractor, font, minecraft);
        if (Settings.S.hudShow) {
            Alerts.renderProfitBox(guiGraphicsExtractor, font, minecraft);
        }
        if (Settings.S.auctionHud) {
            Alerts.renderAuctionBox(guiGraphicsExtractor, font, minecraft);
        }
        if (Settings.S.streakShow) {
            Alerts.renderStreak(guiGraphicsExtractor, font, minecraft);
        }
        if (current != null) {
            Alerts.renderPopup(guiGraphicsExtractor, font, minecraft);
        }
    }

    private static int argb(int n, int n2) {
        return n2 << 24 | n & 0xFFFFFF;
    }

    private static void outline(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, int n3, int n4, int n5) {
        guiGraphicsExtractor.fill(n, n2, n + n3, n2 + 1, n5);
        guiGraphicsExtractor.fill(n, n2 + n4 - 1, n + n3, n2 + n4, n5);
        guiGraphicsExtractor.fill(n, n2, n + 1, n2 + n4, n5);
        guiGraphicsExtractor.fill(n + n3 - 1, n2, n + n3, n2 + n4, n5);
    }

    private static void renderProfitBox(GuiGraphicsExtractor guiGraphicsExtractor, Font font, Minecraft minecraft) {
        DailyProfitHud.draw(guiGraphicsExtractor, minecraft);
    }

    private static void itext(GuiGraphicsExtractor guiGraphicsExtractor, Font font, String string, int n, int n2, int n3, float f, boolean bl) {
        Fonts.draw(guiGraphicsExtractor, font, string, n, n2, n3, f, bl);
    }

    private static int iw(Font font, String string, float f, boolean bl) {
        return Fonts.width(font, string, f, bl);
    }

    private static void centered(GuiGraphicsExtractor guiGraphicsExtractor, Font font, String string, int n, int n2, int n3, float f, boolean bl) {
        Alerts.itext(guiGraphicsExtractor, font, string, n - Alerts.iw(font, string, f, bl) / 2, n2, n3, f, bl);
    }

    private static float easeOutBack(float f) {
        float f2 = 1.70158f;
        float f3 = f2 + 1.0f;
        return 1.0f + f3 * (float)Math.pow(f - 1.0f, 3.0) + f2 * (float)Math.pow(f - 1.0f, 2.0);
    }

    private static float clamp01(float f) {
        return Math.max(0.0f, Math.min(1.0f, f));
    }

    private static void renderWinCelebration(GuiGraphicsExtractor guiGraphicsExtractor, Font font, long l, int n, int n2) {
        int n3;
        int n4;
        int n5;
        int n6;
        float f;
        if (l > 6000L) {
            return;
        }
        float f2 = l > 5300L ? Alerts.clamp01(1.0f - (float)(l - 5300L) / 700.0f) : 1.0f;
        int n7 = n2 + 12 + 24;
        if (l < 700L) {
            f = (float)l / 700.0f;
            n6 = 14 + (int)(f * 56.0f);
            n5 = (int)((1.0f - f) * 200.0f * f2);
            OverviewScreen.ring(guiGraphicsExtractor, n, n7, n6, 3, 1.0f, Alerts.argb(Auction.color, n5), Alerts.argb(Auction.color, n5));
        }
        if (l < 2200L) {
            int[] nArray = new int[]{-757066, -139193, -10443270, -11870592, -3889665, -30107};
            float f3 = (float)l / 1000.0f;
            n5 = 0;
            while (n5 < 44) {
                double d = (double)n5 * 137.5 * Math.PI / 180.0;
                double d2 = 70 + n5 * 37 % 60;
                double d3 = (double)n + Math.cos(d) * d2 * (double)f3;
                double d4 = (double)n7 + Math.sin(d) * d2 * (double)f3 * 0.8 + (double)(90.0f * f3 * f3);
                n4 = (int)(255.0f * Alerts.clamp01(1.0f - (float)l / 2200.0f) * f2);
                int n8 = 2 + n5 % 2;
                guiGraphicsExtractor.fill((int)d3, (int)d4, (int)d3 + n8, (int)d4 + (n5 % 3 == 0 ? 1 : n8), Alerts.argb(nArray[n5 % nArray.length], n4));
                ++n5;
            }
        }
        f = l < 500L ? 3.0f + 1.2f * Alerts.easeOutBack((float)l / 500.0f) : 4.2f - 0.7f * Alerts.clamp01((float)(l - 500L) / 400.0f);
        n6 = (int)(16.0f * f);
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate((float)n - (float)n6 / 2.0f, (float)n7 - (float)n6 / 2.0f);
        guiGraphicsExtractor.pose().scale(f, f);
        guiGraphicsExtractor.item(Auction.item, 0, 0);
        guiGraphicsExtractor.pose().popMatrix();
        if (l > 200L) {
            float f4 = Alerts.clamp01((float)(l - 200L) / 260.0f);
            float f5 = 3.2f - 1.6f * f4;
            n3 = (int)(255.0f * f4 * f2);
            guiGraphicsExtractor.pose().pushMatrix();
            guiGraphicsExtractor.pose().translate((float)n, (float)(n7 + 6));
            guiGraphicsExtractor.pose().rotate(-0.14f);
            int n9 = Alerts.iw(font, "SOLD!", f5, true);
            Alerts.itext(guiGraphicsExtractor, font, "SOLD!", -n9 / 2 + 1, -5, Alerts.argb(0, (int)((float)n3 * 0.6f)), f5, true);
            Alerts.itext(guiGraphicsExtractor, font, "SOLD!", -n9 / 2, -6, Alerts.argb(16765530, n3), f5, true);
            guiGraphicsExtractor.pose().popMatrix();
        }
        if (l > 500L) {
            float f6 = Alerts.clamp01((float)(l - 500L) / 300.0f);
            int n10 = n7 + 34 + (int)((1.0f - f6) * 8.0f);
            n3 = (int)(255.0f * f6 * f2);
            String string = Auction.winner;
            String string2 = Money.format(Auction.winAmount);
            String string3 = "  ";
            int n11 = Alerts.iw(font, string, 1.0f, true);
            int n12 = Alerts.iw(font, string3, 1.0f, false);
            int n13 = Alerts.iw(font, string2, 1.0f, true);
            n4 = n - (n11 + n12 + n13) / 2;
            Alerts.itext(guiGraphicsExtractor, font, string, n4 + 1, n10 + 1, Alerts.argb(0, (int)((float)n3 * 0.6f)), 1.0f, true);
            Alerts.itext(guiGraphicsExtractor, font, string, n4, n10, Alerts.argb(0xFFFFFF, n3), 1.0f, true);
            Alerts.itext(guiGraphicsExtractor, font, string2, n4 + n11 + n12 + 1, n10 + 1, Alerts.argb(0, (int)((float)n3 * 0.6f)), 1.0f, true);
            Alerts.itext(guiGraphicsExtractor, font, string2, n4 + n11 + n12, n10, Alerts.argb(4906624, n3), 1.0f, true);
            String string4 = "won " + Auction.displayName() + "!";
            int n14 = Alerts.iw(font, string4, 0.75f, false);
            Alerts.itext(guiGraphicsExtractor, font, string4, n - n14 / 2 + 1, n10 + 13, Alerts.argb(0, (int)((float)n3 * 0.6f)), 0.75f, false);
            Alerts.itext(guiGraphicsExtractor, font, string4, n - n14 / 2, n10 + 12, Alerts.argb(13685726, n3), 0.75f, false);
        }
    }

    private static void shadowed(GuiGraphicsExtractor guiGraphicsExtractor, Font font, String string, int n, int n2, int n3, float f, boolean bl) {
        int n4 = Alerts.iw(font, string, f, bl);
        int n5 = n - n4 / 2;
        Alerts.itext(guiGraphicsExtractor, font, string, n5 + 1, n2 + 1, -1610612736, f, bl);
        Alerts.itext(guiGraphicsExtractor, font, string, n5, n2, n3, f, bl);
    }

    private static void renderAuctionBox(GuiGraphicsExtractor guiGraphicsExtractor, Font font, Minecraft minecraft) {
        boolean bl;
        long l = System.currentTimeMillis();
        boolean bl2 = bl = Auction.ended && l - Auction.endAt < 6000L;
        if (!Auction.running && !bl || Auction.item.isEmpty()) {
            return;
        }
        int n = minecraft.getWindow().getGuiScaledWidth();
        int n2 = minecraft.getWindow().getGuiScaledHeight();
        int n3 = n / 2 + Settings.S.auctionOffX;
        int n4 = n2 / 2 + 14 + Settings.S.auctionOffY;
        if (!Auction.running && Auction.winner != null) {
            guiGraphicsExtractor.pose().pushMatrix();
            guiGraphicsExtractor.pose().translate((float)n3, (float)n4);
            guiGraphicsExtractor.pose().scale(Settings.S.auctionHudSize, Settings.S.auctionHudSize);
            guiGraphicsExtractor.pose().translate((float)(-n3), (float)(-n4));
            Alerts.renderWinCelebration(guiGraphicsExtractor, font, l - Auction.endedAt, n3, n4);
            guiGraphicsExtractor.pose().popMatrix();
            return;
        }
        AuctionHudPanel.draw(guiGraphicsExtractor, font, minecraft, n3, n4);
    }

    private static int rainbow(double d) {
        int n;
        int n2;
        double d2 = (d % 1.0 + 1.0) % 1.0 * 6.0;
        int n3 = (int)d2;
        double d3 = d2 - (double)n3;
        int n4 = (int)(255.0 * (1.0 - d3));
        int n5 = (int)(255.0 * d3);
        int n6 = switch (n3) {
            case 0 -> {
                n2 = 255;
                n = n5;
                yield 0;
            }
            case 1 -> {
                n2 = n4;
                n = 255;
                yield 0;
            }
            case 2 -> {
                n2 = 0;
                n = 255;
                yield n5;
            }
            case 3 -> {
                n2 = 0;
                n = n4;
                yield 255;
            }
            case 4 -> {
                n2 = n5;
                n = 0;
                yield 255;
            }
            default -> {
                n2 = 255;
                n = 0;
                yield n4;
            }
        };
        return 0xFF000000 | n2 << 16 | n << 8 | n6;
    }

    public static int[] streakRect(Minecraft minecraft) {
        Settings settings = Settings.S;
        int n = (int)(110.0f * settings.streakSize);
        int n2 = (int)(40.0f * settings.streakSize);
        if (!settings.streakSeparate && Settings.S.hudShow) {
            return new int[]{lastProfitRect[0], lastProfitRect[1] + lastProfitRect[3] + 4, n, n2};
        }
        return new int[]{settings.streakX, settings.streakY, n, n2};
    }

    private static void renderStreak(GuiGraphicsExtractor guiGraphicsExtractor, Font font, Minecraft minecraft) {
        int n;
        Settings settings = Settings.S;
        int[] nArray = Alerts.streakRect(minecraft);
        int n2 = nArray[0];
        int n3 = nArray[1];
        float f = settings.streakSize;
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().translate((float)n2, (float)n3);
        guiGraphicsExtractor.pose().scale(f, f);
        int n4 = 110;
        int n5 = 40;
        int n6 = Alerts.argb(723732, Math.max(0, Math.min(255, settings.streakOpacity * 255 / 100)));
        guiGraphicsExtractor.fill(0, 0, n4, n5, n6);
        if (settings.streakBorder) {
            Alerts.outline(guiGraphicsExtractor, 0, 0, n4, n5, 0x40FFFFFF);
        }
        int n7 = settings.streak > 0 ? -11870592 : (n = settings.streak < 0 ? -42386 : -1);
        if (settings.streakAccent) {
            guiGraphicsExtractor.fill(0, 0, 2, n5, n);
        }
        Alerts.itext(guiGraphicsExtractor, font, "WIN STREAK", 8, 6, -4933432, 0.72f, true);
        String string = settings.streak > 0 ? settings.streak + (settings.streak == 1 ? " WIN" : " WINS") : (settings.streak < 0 ? -settings.streak + (settings.streak == -1 ? " LOSS" : " LOSSES") : "0");
        Alerts.itext(guiGraphicsExtractor, font, string, 8, 16, n, 1.3f, true);
        String string2 = "W " + settings.wins + "  -  L " + settings.losses;
        Alerts.itext(guiGraphicsExtractor, font, string2, n4 - 8 - Alerts.iw(font, string2, 0.7f, false), 8, -6643539, 0.7f, false);
        guiGraphicsExtractor.pose().popMatrix();
    }

    public static void addWin() {
        Settings settings = Settings.S;
        ++settings.wins;
        settings.streak = settings.streak >= 0 ? settings.streak + 1 : 1;
        Settings.save();
    }

    public static void addLoss() {
        Settings settings = Settings.S;
        ++settings.losses;
        settings.streak = settings.streak <= 0 ? settings.streak - 1 : -1;
        Settings.save();
    }

    public static void resetStreak() {
        Settings settings = Settings.S;
        settings.wins = 0;
        settings.losses = 0;
        settings.streak = 0;
        Settings.save();
    }

    public static boolean popupActive() {
        return current != null;
    }

    public static OverlayGeometry.Box popupBounds(Minecraft minecraft) {
        Settings settings = Settings.S;
        float f = settings.popupSize;
        String string = (settings.popupPlus ? "+" : "") + Money.format(current == null ? 1000000000L : Alerts.current.amount);
        String string2 = (current == null ? "ExamplePlayer" : Alerts.current.name) + " paid you";
        int n = settings.popupFace ? 30 : 0;
        int n2 = Math.max(190, Math.max(Fonts.width(minecraft.font, string, 2.0f, true) + 34 + n, Fonts.width(minecraft.font, string2, 0.85f, true) + 34 + n));
        int n3 = ((int)((float)minecraft.getWindow().getGuiScaledWidth() / f) - n2) / 2 + settings.popupOffX;
        int n4 = 24 + settings.popupOffY;
        return new OverlayGeometry.Box((int)((float)n3 * f), (int)((float)n4 * f), (int)((float)n2 * f), (int)(58.0f * f));
    }

    private static void renderPopup(GuiGraphicsExtractor guiGraphicsExtractor, Font font, Minecraft minecraft) {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        float f;
        Settings settings = Settings.S;
        long l = System.currentTimeMillis();
        long l2 = l - Alerts.current.start;
        long l3 = (long)(settings.popupDuration * 1000.0f);
        float f2 = Math.min(1.0f, (float)l2 / 250.0f);
        float f3 = Math.min(1.0f, Math.max(0.0f, (float)(l3 + 300L - l2) / 300.0f));
        float f4 = 1.0f - (1.0f - f2) * (1.0f - f2) * (1.0f - f2);
        float f5 = f = settings.popupAnim == 2 ? Math.min(f2, f3) : Math.min(Math.min(1.0f, f2 * 2.0f), f3);
        if (f <= 0.0f) {
            return;
        }
        float f6 = settings.popupSize;
        int n6 = (int)((float)minecraft.getWindow().getGuiScaledWidth() / f6);
        if (Auction.running || Auction.ended && l - Auction.endAt < 6000L) {
            Auction.item.isEmpty();
        }
        int n7 = 24 + settings.popupOffY;
        float f7 = settings.popupCountUp ? Math.min(1.0f, (float)l2 / 700.0f) : 1.0f;
        f7 = 1.0f - (1.0f - f7) * (1.0f - f7);
        long l4 = (long)((float)Alerts.current.amount * f7);
        String string = (settings.popupPlus ? "+" : "") + Money.format(l4);
        String string2 = (settings.popupPlus ? "+" : "") + Money.format(Alerts.current.amount);
        String string3 = Alerts.current.name + " paid you";
        int n8 = settings.popupFace ? 30 : 0;
        int n9 = Math.max(190, Math.max(Alerts.iw(font, string2, 2.0f, true) + 34 + n8, Alerts.iw(font, string3, 0.85f, true) + 34 + n8));
        int n10 = 58;
        int n11 = (n6 - n9) / 2 + settings.popupOffX;
        int n12 = n7;
        if (settings.popupAnim == 1) {
            n12 -= (int)((1.0f - f4) * 30.0f);
        }
        guiGraphicsExtractor.pose().pushMatrix();
        guiGraphicsExtractor.pose().scale(f6, f6);
        if (settings.popupAnim == 0) {
            float f8 = f2 < 1.0f ? 0.8f + 0.25f * (float)Math.sin((double)f2 * Math.PI * 0.75) + 0.0f * f2 : 1.0f;
            f8 = Math.min(f8, 1.05f);
            if (f2 >= 1.0f) {
                f8 = 1.0f;
            }
            guiGraphicsExtractor.pose().translate((float)n11 + (float)n9 / 2.0f, (float)n12 + (float)n10 / 2.0f);
            guiGraphicsExtractor.pose().scale(f8, f8);
            guiGraphicsExtractor.pose().translate(-((float)n11 + (float)n9 / 2.0f), -((float)n12 + (float)n10 / 2.0f));
        }
        int n13 = (int)(f * 255.0f);
        double d = (double)l / 1500.0;
        if (settings.popupGlow && settings.popupGlowPct > 0) {
            n5 = settings.popupRainbow ? Alerts.rainbow(d) : Alerts.argb(settings.popupBorder, 255);
            n4 = (int)(f * 22.0f * (float)settings.popupGlowPct / 100.0f);
            n3 = 4;
            while (n3 >= 1) {
                guiGraphicsExtractor.fill(n11 - n3 * 2, n12 - n3 * 2, n11 + n9 + n3 * 2, n12 + n10 + n3 * 2, Alerts.argb(n5, n4));
                --n3;
            }
        }
        guiGraphicsExtractor.fill(n11, n12, n11 + n9, n12 + n10, Alerts.argb(settings.popupBg, (int)(f * 255.0f * (float)settings.popupOpacity / 100.0f)));
        n5 = Math.max(1, Math.min(4, settings.popupBorderW));
        if (settings.popupRainbow) {
            n4 = 0;
            while (n4 < n9) {
                n2 = Alerts.rainbow(d + (double)n4 / (double)n9);
                guiGraphicsExtractor.fill(n11 + n4, n12, n11 + Math.min(n9, n4 + 3), n12 + n5, Alerts.argb(n2, n13));
                guiGraphicsExtractor.fill(n11 + n4, n12 + n10 - n5, n11 + Math.min(n9, n4 + 3), n12 + n10, Alerts.argb(n2, n13));
                n4 += 3;
            }
        } else {
            guiGraphicsExtractor.fill(n11, n12, n11 + n9, n12 + n5, Alerts.argb(settings.popupBorder, n13));
            guiGraphicsExtractor.fill(n11, n12 + n10 - n5, n11 + n9, n12 + n10, Alerts.argb(settings.popupBorder, n13));
        }
        n4 = 0;
        while (n4 < n10) {
            n2 = settings.popupRainbow ? Alerts.rainbow(d + (double)n4 / (double)n10 * 0.3) : Alerts.argb(settings.popupBorder, 255);
            n = settings.popupRainbow ? Alerts.rainbow(d + 0.5 + (double)n4 / (double)n10 * 0.3) : Alerts.argb(settings.popupBorder, 255);
            guiGraphicsExtractor.fill(n11, n12 + n4, n11 + n5, n12 + Math.min(n10, n4 + 3), Alerts.argb(n2, n13));
            guiGraphicsExtractor.fill(n11 + n9 - n5, n12 + n4, n11 + n9, n12 + Math.min(n10, n4 + 3), Alerts.argb(n, n13));
            n4 += 3;
        }
        if (settings.popupShine) {
            n4 = n11 + (int)((double)(l2 % 1400L) / 1400.0 * (double)(n9 + 40)) - 20;
            n3 = 0;
            while (n3 < 14) {
                n2 = Math.max(n11 + n5, Math.min(n11 + n9 - n5 - 1, n4 + n3));
                guiGraphicsExtractor.fill(n2, n12 + n5, n2 + 1, n12 + n10 - n5, Alerts.argb(0xFFFFFF, (int)(f * (float)(14 - Math.abs(n3 - 7) * 2))));
                ++n3;
            }
        }
        n4 = n11 + 12;
        if (settings.popupFace) {
            n2 = settings.popupFaceBounce ? (int)Math.round(Math.abs(Math.sin((double)l2 / 160.0)) * 3.0 * Math.max(0.0, 1.0 - (double)l2 / 1500.0)) : 0;
            try {
                PlayerInfo playerInfo;
                ClientPacketListener clientPacketListener = minecraft.getConnection();
                PlayerInfo playerInfo2 = playerInfo = clientPacketListener == null ? null : clientPacketListener.getPlayerInfo(Alerts.current.name);
                if (playerInfo != null) {
                    PlayerFaceExtractor.extractRenderState((GuiGraphicsExtractor)guiGraphicsExtractor, (PlayerSkin)playerInfo.getSkin(), (int)(n11 + 10), (int)(n12 + 16 - n2), (int)24);
                } else {
                    guiGraphicsExtractor.fill(n11 + 10, n12 + 16 - n2, n11 + 34, n12 + 40 - n2, Alerts.argb(9133302, n13));
                }
            } catch (Exception exception) {}
            n4 = n11 + 42;
        }
        Alerts.itext(guiGraphicsExtractor, font, "BIG PAYMENT", n4, n12 + 8, Alerts.argb(10133677, n13), 0.7f, true);
        Alerts.itext(guiGraphicsExtractor, font, Alerts.current.name, n4, n12 + 19, Alerts.argb(settings.popupName, n13), 0.85f, true);
        Alerts.itext(guiGraphicsExtractor, font, " paid you", n4 + Alerts.iw(font, Alerts.current.name, 0.85f, true), n12 + 19, Alerts.argb(0xFFFFFF, n13), 0.85f, false);
        Alerts.itext(guiGraphicsExtractor, font, string, n4, n12 + 31, Alerts.argb(settings.popupAmount, n13), 2.0f, true);
        if (settings.popupTimerBar) {
            float f9 = Math.max(0.0f, 1.0f - (float)l2 / (float)l3);
            guiGraphicsExtractor.fill(n11 + n5, n12 + n10 - n5 - 2, n11 + n5 + (int)((float)(n9 - 2 * n5) * f9), n12 + n10 - n5, Alerts.argb(settings.popupTimerColor, n13));
        }
        if (settings.popupSparkles) {
            int n14 = Math.max(0, Math.min(30, settings.popupSparkleCount));
            n = 0;
            while (n < n14) {
                int n15 = n * 73856093 ^ 0x5BD1E995;
                int n16 = n * 19349663 ^ 0x27D4EB2D;
                int n17 = n11 - 6 + Math.abs(n15 % (n9 + 12));
                int n18 = n12 - 6 + Math.abs(n16 % (n10 + 12));
                double d2 = Math.sin((double)l / 180.0 + (double)n * 1.7);
                if (d2 > 0.3) {
                    int n19 = Alerts.argb(0xFFFFFF, (int)((double)(f * 220.0f) * d2));
                    guiGraphicsExtractor.fill(n17, n18 - 1, n17 + 1, n18 + 2, n19);
                    guiGraphicsExtractor.fill(n17 - 1, n18, n17 + 2, n18 + 1, n19);
                }
                ++n;
            }
        }
        guiGraphicsExtractor.pose().popMatrix();
    }

    public static Component recolor(Component component) {
        Settings settings = Settings.S;
        if (!settings.chatColors) {
            return component;
        }
        PaymentListener.Parsed parsed = PaymentListener.parse(component.getString());
        if (parsed == null) {
            return component;
        }
        Style style = Style.EMPTY.withColor(TextColor.fromRgb((int)settings.nameColor)).withBold(Boolean.valueOf(settings.boldNames));
        Style style2 = Style.EMPTY.withColor(TextColor.fromRgb((int)(parsed.incoming() ? settings.receivedColor : settings.sentColor))).withBold(Boolean.valueOf(settings.boldAmounts));
        String string = "$" + parsed.amountText().trim();
        MutableComponent mutableComponent = Component.literal((String)"");
        if (parsed.incoming()) {
            mutableComponent.append((Component)Component.literal((String)parsed.name()).withStyle(style));
            mutableComponent.append((Component)Component.literal((String)" paid you "));
            mutableComponent.append((Component)Component.literal((String)string).withStyle(style2));
        } else {
            mutableComponent.append((Component)Component.literal((String)"You paid "));
            mutableComponent.append((Component)Component.literal((String)parsed.name()).withStyle(style));
            mutableComponent.append((Component)Component.literal((String)" "));
            mutableComponent.append((Component)Component.literal((String)string).withStyle(style2));
            if (settings.taxPct > 0.0f) {
                try {
                    long l = Math.round((double)Money.parse(parsed.amountText()) * ((double)settings.taxPct / 100.0));
                    Style style3 = Style.EMPTY.withColor(TextColor.fromRgb((int)(settings.colorTax ? settings.taxColor : 0xAAAAAA)));
                    mutableComponent.append((Component)Component.literal((String)(" (+" + Money.format(l) + " tax)")).withStyle(style3));
                } catch (Exception exception) {}
            }
        }
        return mutableComponent;
    }

    private record Popup(String name, long amount, long start) {
    }
}

