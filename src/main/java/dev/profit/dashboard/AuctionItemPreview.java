/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.renderer.entity.state.EntityRenderState
 *  net.minecraft.client.renderer.entity.state.ItemEntityRenderState
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.profit.dashboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

final class AuctionItemPreview {
    AuctionItemPreview() {
    }

    static void drawScaled(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack, int n, int n2, int n3, float f) {
        AuctionItemPreview.draw(guiGraphicsExtractor, itemStack, Math.round((float)n * f), Math.round((float)n2 * f), Math.max(4, Math.round((float)n3 * f)));
    }

    static void draw(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack, int n, int n2, int n3) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || itemStack.isEmpty()) {
            return;
        }
        ItemStack itemStack2 = itemStack.copy();
        itemStack2.setCount(1);
        ItemEntity itemEntity = new ItemEntity((Level)minecraft.level, 0.0, 0.0, 0.0, itemStack2, 0.0, 0.0, 0.0);
        ItemEntityRenderState itemEntityRenderState = (ItemEntityRenderState)minecraft.getEntityRenderDispatcher().extractEntity((Entity)itemEntity, 0.0f);
        itemEntityRenderState.ageInTicks = 0.0f;
        itemEntityRenderState.bobOffset = 0.0f;
        itemEntityRenderState.count = 1;
        itemEntityRenderState.lightCoords = 0xF000F0;
        AABB aABB = itemEntityRenderState.item.getModelBoundingBox();
        // spin (one turn every 4 seconds) + gentle float
        double time = (double)(System.currentTimeMillis() % 40000L) / 1000.0;
        double d = time * (Math.PI * 2.0 / 4.0);
        float bob = (float)Math.sin(time * Math.PI) * 0.06f;
        double d2 = Math.toRadians(28.0);
        // size for the widest angle so the item never gets cut off while spinning
        double d3 = Math.sqrt(aABB.getXsize() * aABB.getXsize() + aABB.getZsize() * aABB.getZsize());
        double d4 = d3;
        double d5 = Math.abs(Math.cos(d2)) * aABB.getYsize() + Math.abs(Math.sin(d2)) * d4;
        float f = (float)Math.max(0.25, Math.max(d3, d5));
        float f2 = (float)n3 * 0.80f / f;
        float f3 = (float)aABB.getYsize() / 2.0f + 0.1625f + bob;
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI).rotateX((float)d2).rotateY((float)d);
        guiGraphicsExtractor.entity((EntityRenderState)itemEntityRenderState, f2, new Vector3f(0.0f, f3, 0.0f), quaternionf, new Quaternionf(), n, n2, n + n3, n2 + n3);
    }
}

