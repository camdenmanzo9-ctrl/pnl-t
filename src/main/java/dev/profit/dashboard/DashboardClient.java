/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommands
 *  net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
 *  net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
 *  net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.KeyMapping$Category
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.Identifier
 */
package dev.profit.dashboard;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.profit.dashboard.Alerts;
import dev.profit.dashboard.Auction;
import dev.profit.dashboard.Money;
import dev.profit.dashboard.OverviewScreen;
import dev.profit.dashboard.PayConfirmScreen;
import dev.profit.dashboard.PaymentLedger;
import dev.profit.dashboard.PaymentListener;
import dev.profit.dashboard.Settings;
import dev.profit.dashboard.SettingsScreen;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class DashboardClient
implements ClientModInitializer {
    private static boolean openNextTick = false;
    private static boolean openSettingsNextTick = false;
    private static long balAskedAt = 0L;
    private static long lastTick = 0L;
    private static final Pattern MONEY_RX = Pattern.compile("\\$\\s?([\\d,]+(?:\\.\\d+)?\\s?[KkMmBbTt]?)");
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register((Identifier)Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)"main"));
    private static KeyMapping openKey;
    private static KeyMapping winKey;
    private static KeyMapping loseKey;
    public static KeyMapping streamerKey;
    private static final Path BAL_HUD;
    private static long balHudChecked;
    private static long balHudMtime;

    static {
        BAL_HUD = FabricLoader.getInstance().getConfigDir().resolve("balancehud.txt");
        balHudChecked = 0L;
        balHudMtime = -1L;
    }

    public void onInitializeClient() {
        PaymentLedger.load();
        Settings.load();
        ClientSendMessageEvents.ALLOW_COMMAND.register(string -> {
            long l;
            String[] stringArray;
            block5: {
                if (PayConfirmScreen.approved != null && PayConfirmScreen.approved.equals(string.trim())) {
                    PayConfirmScreen.approved = null;
                    return true;
                }
                Settings settings = Settings.S;
                stringArray = string.trim().split("\\s+");
                if (!settings.payAsk || stringArray.length < 3 || !stringArray[0].equalsIgnoreCase("pay")) {
                    return true;
                }
                try {
                    l = Money.parse(stringArray[2]);
                    if (l >= settings.payFrom) break block5;
                    return true;
                } catch (Exception exception) {
                    return true;
                }
            }
            PayConfirmScreen.pending = new PayConfirmScreen(string.trim(), stringArray[1], l, false);
            return false;
        });
        HudElementRegistry.addLast((Identifier)Identifier.fromNamespaceAndPath((String)"bettors_dashboard", (String)"alerts"), (guiGraphicsExtractor, deltaTracker) -> Alerts.render(guiGraphicsExtractor));
        ClientReceiveMessageEvents.MODIFY_GAME.register((component, bl) -> bl ? component : Alerts.recolor(component));
        ClientSendMessageEvents.COMMAND.register(string -> {
            String string2 = string.trim().toLowerCase();
            if (string2.equals("bal") || string2.equals("balance")) {
                balAskedAt = System.currentTimeMillis();
            }
        });
        ClientReceiveMessageEvents.GAME.register((component, bl) -> {
            if (bl || balAskedAt == 0L) {
                return;
            }
            if (System.currentTimeMillis() - balAskedAt > 5000L) {
                balAskedAt = 0L;
                return;
            }
            String string = component.getString();
            if (PaymentListener.parse(string) != null) {
                return;
            }
            Matcher matcher = MONEY_RX.matcher(string);
            if (matcher.find()) {
                try {
                    PaymentLedger.setBalance(Money.parse(matcher.group(1)));
                } catch (Exception exception) {}
                balAskedAt = 0L;
            }
        });
        openKey = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.bettors_dashboard.open", InputConstants.Type.KEYSYM, 66, CATEGORY));
        winKey = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.bettors_dashboard.win", InputConstants.Type.KEYSYM, -1, CATEGORY));
        loseKey = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.bettors_dashboard.lose", InputConstants.Type.KEYSYM, -1, CATEGORY));
        streamerKey = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.bettors_dashboard.streamer", InputConstants.Type.KEYSYM, -1, CATEGORY));
        ClientReceiveMessageEvents.ALLOW_GAME.register((component, bl) -> {
            long l;
            Settings settings = Settings.S;
            if (bl || !settings.filterOn) {
                return true;
            }
            String string = component.getString();
            PaymentListener.Parsed parsed = PaymentListener.parse(string);
            if (parsed == null) {
                return true;
            }
            if (parsed.incoming() ? !settings.filterReceived : !settings.filterSent) {
                return true;
            }
            try {
                l = Money.parse(parsed.amountText());
            } catch (Exception exception) {
                return true;
            }
            if (l >= settings.filterUnder) {
                return true;
            }
            PaymentListener.handle(string);
            return false;
        });
        ClientReceiveMessageEvents.GAME.register((component, bl) -> {
            if (!bl) {
                PaymentListener.handle(component.getString());
            }
        });
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STOPPING.register(client -> PaymentLedger.flush());
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> PaymentLedger.flush());
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            PaymentLedger.tickSave();
            while (winKey.consumeClick()) {
                if (minecraft.screen != null) continue;
                Alerts.addWin();
            }
            while (loseKey.consumeClick()) {
                if (minecraft.screen != null) continue;
                Alerts.addLoss();
            }
            while (openKey.consumeClick()) {
                if (minecraft.screen != null) continue;
                openNextTick = true;
            }
            while (streamerKey.consumeClick()) {
                if (minecraft.screen != null) continue;
                OverviewScreen.openTab(4);
                openNextTick = true;
            }
            if (openNextTick) {
                openNextTick = false;
                minecraft.setScreen((Screen)new OverviewScreen());
            }
            if (openSettingsNextTick) {
                openSettingsNextTick = false;
                minecraft.setScreen((Screen)new SettingsScreen(null));
            }
            if (PayConfirmScreen.pending != null && minecraft.screen == null) {
                minecraft.setScreen((Screen)PayConfirmScreen.pending);
                PayConfirmScreen.pending = null;
            }
            Alerts.tick();
            Auction.tick();
            DashboardClient.syncBalanceHud();
            long l = System.currentTimeMillis();
            if (minecraft.player != null && lastTick > 0L) {
                PaymentLedger.addPlaytime(Math.min(1000L, l - lastTick));
            }
            lastTick = l;
        });
        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext) -> {
            commandDispatcher.register((LiteralArgumentBuilder)ClientCommands.literal((String)"win").executes(commandContext -> {
                if (!Settings.S.streakCommands) {
                    ((FabricClientCommandSource)commandContext.getSource()).sendFeedback((Component)Component.literal((String)"[Bettors] /win is turned off in Settings"));
                    return 1;
                }
                Alerts.addWin();
                return 1;
            }));
            commandDispatcher.register((LiteralArgumentBuilder)ClientCommands.literal((String)"lose").executes(commandContext -> {
                if (!Settings.S.streakCommands) {
                    ((FabricClientCommandSource)commandContext.getSource()).sendFeedback((Component)Component.literal((String)"[Bettors] /lose is turned off in Settings"));
                    return 1;
                }
                Alerts.addLoss();
                return 1;
            }));
        });
        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext) -> {
            LiteralCommandNode literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommands.literal((String)"bettors").executes(commandContext -> {
                openNextTick = true;
                return 1;
            })).then(ClientCommands.literal((String)"endsession").executes(commandContext -> {
                if (!PaymentLedger.isSessionActive()) {
                    ((FabricClientCommandSource)commandContext.getSource()).sendFeedback((Component)Component.literal((String)"[Bettors] No session running. Use /bettors startsession"));
                    return 1;
                }
                PaymentLedger.endSession();
                ((FabricClientCommandSource)commandContext.getSource()).sendFeedback((Component)Component.literal((String)"[Bettors] Session ended."));
                return 1;
            }))).then(ClientCommands.literal((String)"settings").executes(commandContext -> {
                openSettingsNextTick = true;
                return 1;
            }))).then(((LiteralArgumentBuilder)ClientCommands.literal((String)"theme").then(ClientCommands.literal((String)"christmas").executes(commandContext -> {
                PaymentLedger.setChristmas(true);
                return 1;
            }))).then(ClientCommands.literal((String)"classic").executes(commandContext -> {
                PaymentLedger.setChristmas(false);
                return 1;
            })))).then(ClientCommands.literal((String)"clearsessions").executes(commandContext -> {
                PaymentLedger.clearHistory();
                ((FabricClientCommandSource)commandContext.getSource()).sendFeedback((Component)Component.literal((String)"[Bettors] Session history cleared."));
                return 1;
            }))).then(ClientCommands.literal((String)"startsession").executes(commandContext -> {
                PaymentLedger.startSession();
                ((FabricClientCommandSource)commandContext.getSource()).sendFeedback((Component)Component.literal((String)"[Bettors] New session started."));
                return 1;
            }))).then(ClientCommands.literal((String)"session").executes(commandContext -> {
                ((FabricClientCommandSource)commandContext.getSource()).sendFeedback((Component)Component.literal((String)(PaymentLedger.isSessionActive() ? "[Bettors] Session running for " + PaymentLedger.formatDuration(PaymentLedger.sessionMillis()) : "[Bettors] No session running (last one lasted " + PaymentLedger.formatDuration(PaymentLedger.sessionMillis()) + ")")));
                return 1;
            }))).then(ClientCommands.literal((String)"debug").executes(commandContext -> {
                PaymentListener.debug = !PaymentListener.debug;
                ((FabricClientCommandSource)commandContext.getSource()).sendFeedback((Component)Component.literal((String)("[Bettors] Debug " + (PaymentListener.debug ? "ON" : "OFF"))));
                return 1;
            }))).then(ClientCommands.literal((String)"add").then(ClientCommands.argument((String)"player", (ArgumentType)StringArgumentType.word()).then(ClientCommands.argument((String)"paidYou", (ArgumentType)StringArgumentType.word()).then(ClientCommands.argument((String)"youPaid", (ArgumentType)StringArgumentType.word()).executes(commandContext -> DashboardClient.manual((CommandContext<FabricClientCommandSource>)commandContext, false))))))).then(ClientCommands.literal((String)"import").then(ClientCommands.argument((String)"player", (ArgumentType)StringArgumentType.word()).then(ClientCommands.argument((String)"paidYou", (ArgumentType)StringArgumentType.word()).then(ClientCommands.argument((String)"youPaid", (ArgumentType)StringArgumentType.word()).executes(commandContext -> DashboardClient.manual((CommandContext<FabricClientCommandSource>)commandContext, true)))))));
        });
    }

    private static void syncBalanceHud() {
        long l = System.currentTimeMillis();
        if (l - balHudChecked < 1000L) {
            return;
        }
        balHudChecked = l;
        try {
            if (!Files.exists(BAL_HUD, new LinkOption[0])) {
                return;
            }
            long l2 = Files.getLastModifiedTime(BAL_HUD, new LinkOption[0]).toMillis();
            if (l2 == balHudMtime) {
                return;
            }
            balHudMtime = l2;
            for (String string : Files.readAllLines(BAL_HUD)) {
                if (!string.startsWith("balance,")) continue;
                long l3 = Math.round(Double.parseDouble(string.substring(8).trim()));
                if (l3 >= 0L && l3 != PaymentLedger.balance()) {
                    PaymentLedger.setBalance(l3);
                }
                return;
            }
        } catch (Exception exception) {}
    }

    private static int manual(CommandContext<FabricClientCommandSource> commandContext, boolean bl) {
        String string = StringArgumentType.getString(commandContext, (String)"player");
        try {
            long l = Money.parse(StringArgumentType.getString(commandContext, (String)"paidYou"));
            long l2 = Money.parse(StringArgumentType.getString(commandContext, (String)"youPaid"));
            if (bl) {
                PaymentLedger.importAllTime(string, l, l2);
            } else {
                PaymentLedger.record(string, l, l2);
            }
            ((FabricClientCommandSource)commandContext.getSource()).sendFeedback((Component)Component.literal((String)("[Bettors] " + (bl ? "Imported " : "Added ") + string + ": paid you " + Money.format(l) + ", you paid " + Money.format(l2))));
        } catch (Exception exception) {
            ((FabricClientCommandSource)commandContext.getSource()).sendError((Component)Component.literal((String)"[Bettors] Amounts look like 50M, 1.5B or 2000 (no $ sign)"));
        }
        return 1;
    }
}

