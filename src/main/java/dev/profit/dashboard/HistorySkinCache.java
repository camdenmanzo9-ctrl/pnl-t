/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.DefaultPlayerSkin
 *  net.minecraft.world.entity.player.PlayerSkin
 */
package dev.profit.dashboard;

import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.PlayerSkin;

final class HistorySkinCache {
    private static final ConcurrentHashMap<String, PlayerSkin> SKINS = new ConcurrentHashMap();
    private static final Set<String> REQUESTED = ConcurrentHashMap.newKeySet();

    HistorySkinCache() {
    }

    static PlayerSkin get(Minecraft minecraft, String string) {
        String string2 = string.toLowerCase(Locale.ROOT);
        PlayerSkin playerSkin = SKINS.get(string2);
        if (playerSkin != null) {
            return playerSkin;
        }
        if (string.matches("[A-Za-z0-9_]{1,16}") && REQUESTED.size() < 512 && REQUESTED.add(string2)) {
            CompletableFuture.supplyAsync(() -> minecraft.services().profileResolver().fetchByName(string))
                    .thenAccept(profile -> profile.ifPresent(gameProfile -> minecraft.execute(() ->
                            minecraft.getSkinManager().get(gameProfile).thenAccept(skin -> skin.ifPresent(found -> SKINS.put(string2, found))))))
                    .exceptionally(throwable -> null);
        }
        return DefaultPlayerSkin.get((GameProfile)new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(StandardCharsets.UTF_8)), string));
    }
}

