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
                        allHidden = bl;
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
                    }
                }
            }
        } catch (Exception exception) {}
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            Files.writeString(FILE, (CharSequence)("hide_all=" + (allHidden ? "on" : "off") + "\nbalance=" + (balance ? "on" : "off") + "\nprofit=" + (profit ? "on" : "off") + "\nrakeback=" + (rakeback ? "on" : "off") + "\n"), new OpenOption[0]);
        } catch (Exception exception) {}
    }
}

