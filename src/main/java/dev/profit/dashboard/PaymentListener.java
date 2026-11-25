/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Alerts;
import dev.profit.dashboard.Auction;
import dev.profit.dashboard.Money;
import dev.profit.dashboard.PaymentLedger;
import dev.profit.dashboard.Settings;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class PaymentListener {
    public static boolean debug = false;
    private static final String PRE = "^[^A-Za-z0-9_.]*";
    private static final String NAME = "([.A-Za-z0-9_]{2,20})";
    // SAME rules as Balance HUD (BalanceHudClient): a K/M/B/T suffix only counts when no letter follows it.
    private static final String AMT = "\\$?\\s?([\\d,]+(?:\\.\\d+)?\\s?[KkMmBbTt]?)(?![A-Za-z])";
    private static final Pattern IN = Pattern.compile(PRE + NAME + " paid you " + AMT, Pattern.CASE_INSENSITIVE);
    private static final Pattern IN2 = Pattern.compile(PRE + "You received " + AMT + " from " + NAME, Pattern.CASE_INSENSITIVE);
    private static final Pattern IN3 = Pattern.compile(PRE + NAME + " sent you " + AMT, Pattern.CASE_INSENSITIVE);
    private static final Pattern OUT = Pattern.compile(PRE + "You paid " + NAME + " " + AMT, Pattern.CASE_INSENSITIVE);
    private static final Pattern OUT2 = Pattern.compile(PRE + "You sent " + AMT + " to " + NAME, Pattern.CASE_INSENSITIVE);

    private PaymentListener() {
    }

    public static void handle(String text) {
        Parsed p;
        if (debug && (text.contains("$") || text.toLowerCase(Locale.ROOT).contains("paid"))) {
            PaymentListener.chat("debug raw: " + text, ChatFormatting.GRAY);
        }
        if ((p = PaymentListener.parse(text)) != null) {
            PaymentListener.log(p.name(), p.amountText(), p.incoming());
        }
    }

    public static Parsed parse(String text) {
        Matcher m = OUT.matcher(text = text.replace('\u00a0', ' ').replace('\u202f', ' ').replaceAll("\\s+", " "));
        if (m.find()) {
            return new Parsed(m.group(1), m.group(2), false);
        }
        m = OUT2.matcher(text);
        if (m.find()) {
            return new Parsed(m.group(2), m.group(1), false);
        }
        m = IN.matcher(text);
        if (m.find()) {
            return new Parsed(m.group(1), m.group(2), true);
        }
        m = IN2.matcher(text);
        if (m.find()) {
            return new Parsed(m.group(2), m.group(1), true);
        }
        m = IN3.matcher(text);
        if (m.find()) {
            return new Parsed(m.group(1), m.group(2), true);
        }
        return null;
    }

    private static void log(String player, String amount, boolean incoming) {
        try {
            long amt = Money.parse(amount);
            if (!incoming && Settings.S.countTax && Settings.S.taxPct > 0.0f) {
                amt += Math.round((double)amt * ((double)Settings.S.taxPct / 100.0));
            }
            if (incoming && Auction.capturing()) {
                Auction.bid(player, amt);
                if (debug) {
                    PaymentListener.chat("auction bid " + player + " " + Money.format(amt), ChatFormatting.LIGHT_PURPLE);
                }
                return;
            }
            if (incoming) {
                PaymentLedger.record(player, amt, 0L);
            } else {
                PaymentLedger.record(player, 0L, amt);
            }
            Alerts.onPayment(player, amt, incoming);
            PaymentLedger.adjustBalance(incoming ? amt : -amt);
            if (debug) {
                PaymentListener.chat("logged " + (incoming ? player + " paid you " : "you paid " + player + " ") + Money.format(amt), ChatFormatting.GREEN);
            }
        } catch (Exception e) {
            if (debug) {
                PaymentListener.chat("couldn't read amount '" + amount + "'", ChatFormatting.RED);
            }
        }
    }

    public static void announceEnd(PaymentLedger.SessionSummary s) {
        long p = s.profit();
        PaymentListener.chat("Session ended after " + PaymentLedger.formatDuration(s.lengthMs()) + " | received " + Money.format(s.received()) + " | sent " + Money.format(s.sent()) + " | you " + (p >= 0L ? "+" : "") + Money.format(p) + " | " + s.players() + " players. Use Start Session or /bettors startsession for a new one.", ChatFormatting.GOLD);
    }

    public static void chat(String msg, ChatFormatting color) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage((Component)Component.literal((String)("[Bettors] " + msg)).withStyle(color));
        }
    }

    public record Parsed(String name, String amountText, boolean incoming) {
    }
}

