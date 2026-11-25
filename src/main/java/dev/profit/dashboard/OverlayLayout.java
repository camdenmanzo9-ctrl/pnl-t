/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 */
package dev.profit.dashboard;

import dev.profit.dashboard.Alerts;
import dev.profit.dashboard.Auction;
import dev.profit.dashboard.AuctionHudPanel;
import dev.profit.dashboard.Board;
import dev.profit.dashboard.DailyProfitHud;
import dev.profit.dashboard.OverlayGeometry;
import dev.profit.dashboard.Panels;
import dev.profit.dashboard.Settings;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

final class OverlayLayout {
    OverlayLayout() {
    }

    static List<Panel> panels() {
        ArrayList<Panel> arrayList = new ArrayList<Panel>();
        arrayList.add(new BoardPanel());
        arrayList.add(new NativePanel(0, "Profit HUD"));
        arrayList.add(new NativePanel(1, "Auction"));
        arrayList.add(new NativePanel(2, "Win streak"));
        arrayList.add(new NativePanel(3, "Payment popup"));
        String[] stringArray = new String[]{"Balance HUD", "Profit tracker", "Rakeback", "Auction log"};
        int n = 0;
        while (n < stringArray.length) {
            arrayList.add(new BalancePanel(n, stringArray[n]));
            ++n;
        }
        return arrayList;
    }

    private static OverlayGeometry.Box profit(Minecraft minecraft) {
        return DailyProfitHud.bounds(minecraft);
    }

    private static final class BalanceBridge {
        static final BalanceBridge instance = BalanceBridge.create();
        private final Class<?> hud;
        private final Map<String, Field> fields = new HashMap<String, Field>();
        private final Method profitX;
        private final Method width;
        private final Method save;

        static BalanceBridge create() {
            if (!FabricLoader.getInstance().isModLoaded("balancehud")) {
                return null;
            }
            try {
                return new BalanceBridge();
            } catch (ReflectiveOperationException reflectiveOperationException) {
                System.err.println("[Bettors] Overlay could not connect to Balance HUD: " + String.valueOf(reflectiveOperationException));
                return null;
            }
        }

        BalanceBridge() throws ReflectiveOperationException {
            this.hud = Class.forName("com.snowy.balancehud.BalanceHudClient");
            String[] stringArray = new String[]{"SCALE", "PANEL_SCALES", "BAL", "PROFIT_POS", "RAKE_POS", "AUC_POS", "BAL_D", "PROFIT_D", "RAKE_D", "AUC_D", "NETS", "RAKE_BASE", "balance", "HISTORY", "AUC_LOG", "aucActive", "balHidden", "profitHidden", "rakeHidden", "aucHidden"};
            int n = stringArray.length;
            int n2 = 0;
            while (n2 < n) {
                String string = stringArray[n2];
                Field field = this.hud.getDeclaredField(string);
                field.setAccessible(true);
                this.fields.put(string, field);
                ++n2;
            }
            this.profitX = this.hud.getDeclaredMethod("profitX", new Class[0]);
            this.profitX.setAccessible(true);
            this.width = this.hud.getDeclaredMethod("width", Font.class, String.class);
            this.width.setAccessible(true);
            this.save = this.hud.getDeclaredMethod("save", new Class[0]);
            this.save.setAccessible(true);
        }

        Object value(String string) {
            try {
                return this.fields.get(string).get(null);
            } catch (IllegalAccessException illegalAccessException) {
                throw new IllegalStateException(illegalAccessException);
            }
        }

        float global() {
            return ((Number)this.value("SCALE")).floatValue();
        }

        float[] scales() {
            return (float[])this.value("PANEL_SCALES");
        }

        int[] position(int n) {
            return (int[])this.value((new String[]{"BAL", "PROFIT_POS", "RAKE_POS", "AUC_POS"})[n]);
        }

        int[] defaults(int n) {
            return (int[])this.value((new String[]{"BAL_D", "PROFIT_D", "RAKE_D", "AUC_D"})[n]);
        }

        int profitX() {
            try {
                return (Integer)this.profitX.invoke(null, new Object[0]);
            } catch (ReflectiveOperationException reflectiveOperationException) {
                throw new IllegalStateException(reflectiveOperationException);
            }
        }

        int balanceWidth(Font font) {
            try {
                double d = ((Number)this.value("balance")).doubleValue();
                String string = d < 0.0 ? "$---" : String.format(Locale.US, "$%,d", (long)d);
                return Math.max(200, (int)((float)((Integer)this.width.invoke(null, font, string)).intValue() * 1.8f) + 48);
            } catch (ReflectiveOperationException reflectiveOperationException) {
                throw new IllegalStateException(reflectiveOperationException);
            }
        }

        int rakeRows() {
            @SuppressWarnings("unchecked") Map<String, Double> map = (Map<String, Double>)this.value("NETS");
            @SuppressWarnings("unchecked") Map<String, Double> map2 = (Map<String, Double>)this.value("RAKE_BASE");
            int n = 0;
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                if (!((Double)entry.getValue() < 0.0) || !(-((Double)entry.getValue()).doubleValue() - map2.getOrDefault(entry.getKey(), 0.0) > 0.0)) continue;
                ++n;
            }
            return Math.min(7, n);
        }

        int auctionHeight() {
            int n = Math.min(5, ((List)this.value("AUC_LOG")).size());
            return 28 + ((Boolean)this.value("aucActive") != false ? 46 : 0) + (n > 0 ? 12 + n * 20 : 0);
        }

        boolean visible(int n) {
            if (((Boolean)this.value((new String[]{"balHidden", "profitHidden", "rakeHidden", "aucHidden"})[n])).booleanValue()) {
                return false;
            }
            return switch (n) {
                case 0 -> {
                    if (Panels.balance && ((Number)this.value("balance")).doubleValue() >= 0.0) {
                        yield true;
                    }
                    yield false;
                }
                case 1 -> {
                    if (!(!Panels.profit || ((List)this.value("HISTORY")).isEmpty() && ((Map)this.value("NETS")).isEmpty())) {
                        yield true;
                    }
                    yield false;
                }
                case 2 -> Panels.rakeback;
                default -> (Boolean)this.value("aucActive") != false || !((List)this.value("AUC_LOG")).isEmpty();
            };
        }

        void save() {
            try {
                this.save.invoke(null, new Object[0]);
            } catch (ReflectiveOperationException reflectiveOperationException) {
                throw new IllegalStateException("Could not save Balance HUD layout", reflectiveOperationException);
            }
        }
    }

    private static final class BalancePanel
    extends Panel {
        final int slot;
        final BalanceBridge bridge;

        BalancePanel(int n, String string) {
            super(string);
            this.slot = n;
            this.bridge = BalanceBridge.instance;
        }

        @Override
        boolean available() {
            return this.bridge != null;
        }

        @Override
        float size() {
            return this.available() ? this.bridge.scales()[this.slot] : 1.0f;
        }

        @Override
        void setSize(float f) {
            if (this.available()) {
                this.bridge.scales()[this.slot] = f;
            }
        }

        @Override
        OverlayGeometry.Box bounds(Minecraft minecraft) {
            int n;
            if (!this.available()) {
                return new OverlayGeometry.Box(8, 8 + this.slot * 35, 150, 28);
            }
            float f = this.bridge.global();
            float f2 = f * this.size();
            int[] nArray = this.bridge.position(this.slot);
            int n2 = nArray[0];
            if (this.slot == 1 && n2 < 0) {
                n2 = this.bridge.profitX();
            }
            int n3 = this.slot == 0 ? this.bridge.balanceWidth(minecraft.font) : (this.slot == 1 ? 220 : (n = this.slot == 2 ? 180 : 190));
            int n4 = this.slot == 0 ? 48 : (this.slot == 1 ? 286 : (this.slot == 2 ? 26 + 17 * Math.max(1, this.bridge.rakeRows()) : this.bridge.auctionHeight()));
            return new OverlayGeometry.Box(Math.round((float)n2 * f), Math.round((float)nArray[1] * f), Math.max(12, Math.round((float)n * f2)), Math.max(12, Math.round((float)n4 * f2)));
        }

        @Override
        void move(Minecraft minecraft, int n, int n2) {
            if (this.available()) {
                int[] nArray = this.bridge.position(this.slot);
                nArray[0] = OverlayGeometry.raw(n, this.bridge.global());
                nArray[1] = OverlayGeometry.raw(n2, this.bridge.global());
            }
        }

        @Override
        void reset() {
            if (this.available()) {
                int[] nArray = this.bridge.position(this.slot);
                int[] nArray2 = this.bridge.defaults(this.slot);
                nArray[0] = nArray2[0];
                nArray[1] = nArray2[1];
                this.bridge.scales()[this.slot] = 1.0f;
            }
        }

        @Override
        void save() {
            if (this.available()) {
                this.bridge.save();
            }
        }

        @Override
        boolean active() {
            return this.available() && this.bridge.visible(this.slot);
        }
    }

    private static final class BoardPanel
    extends Panel {
        BoardPanel() {
            super("Game text");
        }

        @Override
        OverlayGeometry.Box bounds(Minecraft minecraft) {
            return Board.bounds(minecraft);
        }

        @Override
        float size() {
            return Settings.S.boardSize;
        }

        @Override
        void setSize(float f) {
            Settings.S.boardSize = f;
        }

        @Override
        void move(Minecraft minecraft, int n, int n2) {
            Settings.S.boardX = n;
            Settings.S.boardY = n2;
        }

        @Override
        void reset() {
            Settings.S.boardX = 8;
            Settings.S.boardY = 32;
            Settings.S.boardSize = 1.0f;
        }

        @Override
        void save() {
            Settings.save();
        }

        @Override
        boolean active() {
            return Settings.S.boardShow;
        }
    }

    private static final class NativePanel
    extends Panel {
        final int kind;

        NativePanel(int n, String string) {
            super(string);
            this.kind = n;
        }

        @Override
        OverlayGeometry.Box bounds(Minecraft minecraft) {
            if (this.kind == 0) {
                return OverlayLayout.profit(minecraft);
            }
            if (this.kind == 1) {
                return AuctionHudPanel.bounds(minecraft);
            }
            if (this.kind == 2) {
                int[] nArray = Alerts.streakRect(minecraft);
                return new OverlayGeometry.Box(nArray[0], nArray[1], nArray[2], nArray[3]);
            }
            return Alerts.popupBounds(minecraft);
        }

        @Override
        float size() {
            return switch (this.kind) {
                case 0 -> Settings.S.hudSize;
                case 1 -> Settings.S.auctionHudSize;
                case 2 -> Settings.S.streakSize;
                default -> Settings.S.popupSize;
            };
        }

        @Override
        void setSize(float f) {
            switch (this.kind) {
                case 0: {
                    Settings.S.hudSize = f;
                    break;
                }
                case 1: {
                    Settings.S.auctionHudSize = f;
                    break;
                }
                case 2: {
                    Settings.S.streakSize = f;
                    Settings.S.streakSeparate = true;
                    break;
                }
                default: {
                    Settings.S.popupSize = f;
                }
            }
        }

        @Override
        void move(Minecraft minecraft, int n, int n2) {
            Settings settings = Settings.S;
            float f = this.size();
            switch (this.kind) {
                case 0: {
                    OverlayGeometry.Box box = OverlayLayout.profit(minecraft);
                    settings.hudOffX += Math.round((float)(n - box.x()) / f);
                    settings.hudOffY += Math.round((float)(n2 - box.y()) / f);
                    break;
                }
                case 1: {
                    settings.auctionOffX = Math.round((float)n + 208.0f * f / 2.0f - (float)minecraft.getWindow().getGuiScaledWidth() / 2.0f);
                    settings.auctionOffY = Math.round((float)n2 + 94.0f * f - (float)minecraft.getWindow().getGuiScaledHeight() / 2.0f - 14.0f);
                    break;
                }
                case 2: {
                    settings.streakSeparate = true;
                    settings.streakX = n;
                    settings.streakY = n2;
                    break;
                }
                default: {
                    OverlayGeometry.Box box = Alerts.popupBounds(minecraft);
                    settings.popupOffX += Math.round((float)(n - box.x()) / f);
                    settings.popupOffY += Math.round((float)(n2 - box.y()) / f);
                }
            }
        }

        @Override
        void reset() {
            Settings settings = Settings.S;
            switch (this.kind) {
                case 0: {
                    settings.hudOffX = 0;
                    settings.hudOffY = 0;
                    settings.hudSize = 1.0f;
                    break;
                }
                case 1: {
                    settings.auctionOffX = 0;
                    settings.auctionOffY = 0;
                    settings.auctionHudSize = 1.0f;
                    break;
                }
                case 2: {
                    settings.streakX = 8;
                    settings.streakY = 60;
                    settings.streakSize = 0.9f;
                    settings.streakSeparate = true;
                    break;
                }
                default: {
                    settings.popupOffX = 0;
                    settings.popupOffY = 0;
                    settings.popupSize = 1.0f;
                }
            }
        }

        @Override
        void save() {
            Settings.save();
        }

        @Override
        boolean active() {
            return switch (this.kind) {
                case 0 -> Settings.S.hudShow;
                case 1 -> {
                    if (Settings.S.auctionHud && (Auction.running || Auction.ended)) {
                        yield true;
                    }
                    yield false;
                }
                case 2 -> Settings.S.streakShow;
                default -> Settings.S.popupShow && Alerts.popupActive();
            };
        }
    }

    static abstract class Panel {
        final String name;

        Panel(String string) {
            this.name = string;
        }

        boolean available() {
            return true;
        }

        abstract OverlayGeometry.Box bounds(Minecraft var1);

        abstract float size();

        abstract void setSize(float var1);

        abstract void move(Minecraft var1, int var2, int var3);

        abstract void reset();

        abstract void save();

        abstract boolean active();

        void resize(Minecraft minecraft, float f) {
            OverlayGeometry.Box box = this.bounds(minecraft);
            this.setSize(OverlayGeometry.scale(f));
            this.move(minecraft, box.x(), box.y());
        }
    }
}

