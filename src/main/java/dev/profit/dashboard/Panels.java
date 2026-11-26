/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 */
package dev.profit.dashboard;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.fabricmc.loader.api.FabricLoader;

public final class Panels {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("snowy-panels.txt");
    public static boolean allHidden = false;
    public static boolean balance = true;
    public static boolean profit = true;
    public static boolean rakeback = true;

    // TEST: Balance HUD outline color (Streamer tab). 0 = default gray.
    public static int balBorder = 0;
    public static final String[] BORDER_NAMES = {"Default", "Green", "Mint", "Pink", "Purple", "Blue", "Cyan", "Gold", "Orange", "Red", "White", "Rainbow"};
    private static final int[] BORDER_COLORS = {0xFF515A67, 0xFF4DF58C, 0xFF98FFD6, 0xFFF2557D, 0xFFA77BFF, 0xFF4C8DFF, 0xFF3FE0F0, 0xFFFFC83D, 0xFFFF8A3D, 0xFFFF4040, 0xFFFFFFFF, 0};

    /** Read by Balance HUD. */
    public static int balBorderColor() {
        int i = Math.floorMod(balBorder, BORDER_COLORS.length);
        if (BORDER_NAMES[i].equals("Rainbow")) {
            double h = (System.currentTimeMillis() % 4000L) / 4000.0 * 6.0;
            int r = (int) (255 * Math.max(0, Math.min(1, Math.abs(h - 3) - 1)));
            int g = (int) (255 * Math.max(0, Math.min(1, 2 - Math.abs(h - 2))));
            int b = (int) (255 * Math.max(0, Math.min(1, 2 - Math.abs(h - 4))));
            return 0xFF000000 | r << 16 | g << 8 | b;
        }
        return BORDER_COLORS[i];
    }

    private Panels() {
    }

    public static void load() {
        try {
            if (!Files.exists(FILE, new LinkOption[0])) {
                return;
            }
            for (String string : Files.readAllLines(FILE)) {
                String[] stringArray = string.split("=");
                if (stringArray.length < 2) continue;
                boolean bl = stringArray[1].trim().equalsIgnoreCase("on");
                switch (stringArray[0].trim().toLowerCase()) {
                    case "hide_all": {
                        allHidden = false;   // "Hide all overlays" button was removed
                        break;
                    }
                    case "balance": {
                        balance = bl;
                        break;
                    }
                    case "profit": {
                        profit = bl;
                        break;
                    }
                    case "rakeback": {
                        rakeback = bl;
                        break;
                    }
                    case "bal_border": {
                        try { balBorder = Integer.parseInt(stringArray[1].trim()); } catch (NumberFormatException e) { balBorder = 0; }
                    }
                }
            }
        } catch (Exception exception) {}
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            Files.writeString(FILE, (CharSequence)("hide_all=" + (allHidden ? "on" : "off") + "\nbalance=" + (balance ? "on" : "off") + "\nprofit=" + (profit ? "on" : "off") + "\nrakeback=" + (rakeback ? "on" : "off") + "\nbal_border=" + balBorder + "\n"), new OpenOption[0]);
        } catch (Exception exception) {}
    }
}

