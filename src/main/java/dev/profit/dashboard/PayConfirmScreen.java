/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.ChatScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Alerts;
import dev.profit.dashboard.Money;
import dev.profit.dashboard.Settings;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PayConfirmScreen
extends Screen {
    public static String approved = null;
    public static PayConfirmScreen pending = null;
    private final String command;
    private final String target;
    private final long amount;
    private final boolean test;
    private final long opened = System.currentTimeMillis();
    private Button confirm;
    private Button cancel;

    public PayConfirmScreen(String command, String target, long amount, boolean test) {
        super((Component)Component.literal((String)"Confirm payment"));
        this.command = command;
        this.target = target;
        this.amount = amount;
        this.test = test;
    }

    public boolean isPauseScreen() {
        return false;
    }

    protected void init() {
        int w = 220;
        int h = 124;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;
        this.confirm = (Button)this.addRenderableWidget(Button.builder((Component)Component.literal((String)"Confirm"), b -> this.doConfirm()).bounds(x + 12, y + h - 30, (w - 36) / 2, 20).build());
        this.cancel = (Button)this.addRenderableWidget(Button.builder((Component)Component.literal((String)"Cancel"), b -> this.onClose()).bounds(x + 24 + (w - 36) / 2, y + h - 30, (w - 36) / 2, 20).build());
    }

    private long waitLeft() {
        return Math.max(0L, (long)(Settings.S.payDelay * 1000.0f) - (System.currentTimeMillis() - this.opened));
    }

    private void doConfirm() {
        if (this.waitLeft() > 0L) {
            return;
        }
        Alerts.playConfirmSound();
        if (this.test || this.minecraft == null) {
            this.onClose();
            return;
        }
        approved = this.command;
        this.minecraft.setScreen((Screen)new ChatScreen("/" + this.command, false));
    }

    private static int argb(int rgb, int a) {
        return a << 24 | rgb & 0xFFFFFF;
    }

    private static int rainbow(double t) {
        int g;
        int r;
        double h = (t % 1.0 + 1.0) % 1.0 * 6.0;
        int i = (int)h;
        double fr = h - (double)i;
        int q = (int)(255.0 * (1.0 - fr));
        int tt = (int)(255.0 * fr);
        return 0xFF000000 | r << 16 | g << 8 | (switch (i) {
            case 0 -> {
                r = 255;
                g = tt;
                yield 0;
            }
            case 1 -> {
                r = q;
                g = 255;
                yield 0;
            }
            case 2 -> {
                r = 0;
                g = 255;
                yield tt;
            }
            case 3 -> {
                r = 0;
                g = q;
                yield 255;
            }
            case 4 -> {
                r = tt;
                g = 0;
                yield 255;
            }
            default -> {
                r = 255;
                g = 0;
                yield q;
            }
        });
    }

    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        g.fill(0, 0, this.width, this.height, PayConfirmScreen.argb(0, Settings.S.payDim * 255 / 100));
    }

    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        int c;
        super.extractRenderState(g, mouseX, mouseY, delta);
        Settings s = Settings.S;
        int w = 220;
        int h = 124;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;
        double t = (double)System.currentTimeMillis() / 1500.0;
        g.fill(x, y, x + w, y + h, PayConfirmScreen.argb(s.payBg, 255));
        for (int px = 0; px < w; ++px) {
            c = s.payRainbow ? PayConfirmScreen.rainbow(t + (double)px / (double)w) : PayConfirmScreen.argb(s.payAccent, 255);
            g.fill(x + px, y, x + px + 1, y + 2, c);
            g.fill(x + px, y + h - 1, x + px + 1, y + h, c);
        }
        for (int py = 0; py < h; ++py) {
            c = s.payRainbow ? PayConfirmScreen.rainbow(t + (double)py / (double)h * 0.3) : PayConfirmScreen.argb(s.payAccent, 255);
            g.fill(x, y + py, x + 1, y + py + 1, c);
            g.fill(x + w - 1, y + py, x + w, y + py + 1, c);
        }
        g.text(this.font, this.test ? "CONFIRM PAYMENT (TEST)" : "CONFIRM PAYMENT", x + 12, y + 10, PayConfirmScreen.argb(s.payAccent, 255), false);
        g.text(this.font, "Pay", x + 12, y + 26, -1, false);
        g.text(this.font, this.target, x + 12 + this.font.width("Pay "), y + 26, PayConfirmScreen.argb(s.payName, 255), false);
        g.pose().pushMatrix();
        g.pose().translate((float)(x + 12), (float)(y + 40));
        g.pose().scale(2.0f, 2.0f);
        g.text(this.font, Money.format(this.amount), 0, 0, PayConfirmScreen.argb(s.payAmount, 255), false);
        g.pose().popMatrix();
        if (s.payShowTax) {
            long tax = Math.round((double)this.amount * ((double)s.payTax / 100.0));
            g.text(this.font, "+" + Money.format(tax) + " tax (" + s.payTax + "%)", x + 12, y + 62, -6643539, false);
        }
        g.text(this.font, "Confirm puts it in chat - you press Enter", x + 12, y + 76, -7696492, false);
        long left = this.waitLeft();
        this.drawButton(g, this.confirm, (String)(left > 0L ? "Wait " + String.format("%.1f", (double)left / 1000.0) + "s" : "Confirm"), left > 0L ? 0x555555 : s.payConfirm, mouseX, mouseY);
        this.drawButton(g, this.cancel, "Cancel", s.payCancel, mouseX, mouseY);
    }

    private void drawButton(GuiGraphicsExtractor g, Button b, String label, int color, int mx, int my) {
        int x = b.getX();
        int y = b.getY();
        int w = b.getWidth();
        int h = b.getHeight();
        boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
        g.fill(x, y, x + w, y + h, PayConfirmScreen.argb(color, hover ? 255 : 210));
        g.text(this.font, label, x + (w - this.font.width(label)) / 2, y + (h - 8) / 2, -1, false);
    }
}

