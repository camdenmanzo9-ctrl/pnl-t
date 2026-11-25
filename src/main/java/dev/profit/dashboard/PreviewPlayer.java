/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.RemotePlayer
 *  net.minecraft.world.entity.player.PlayerModelPart
 */
package dev.profit.dashboard;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.player.PlayerModelPart;

final class PreviewPlayer
extends RemotePlayer {
    PreviewPlayer(ClientLevel level, GameProfile profile) {
        super(level, profile);
        this.refreshParts();
    }

    void refreshParts() {
        Minecraft mc = Minecraft.getInstance();
        int mask = 0;
        for (PlayerModelPart p : PlayerModelPart.values()) {
            if (p == PlayerModelPart.CAPE || !mc.options.isModelPartEnabled(p)) continue;
            mask |= p.getMask();
        }
        this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte)mask);
    }
}

