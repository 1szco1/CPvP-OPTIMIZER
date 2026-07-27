package com.anchoropti.mixin;

import com.anchoropti.CrystalOptimizer;
import com.anchoropti.GhostBlockFixer;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handles server corrections for:
 * - Ghost blocks (server sends different block state)
 * - Crystal placements (server spawns real crystal)
 * - Crystal breaks (server destroys crystal)
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow private ClientWorld world;

    /**
     * Handle server block updates for ghost block correction.
     */
    @Inject(method = "onBlockUpdate", at = @At("TAIL"))
    private void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
        BlockPos pos = packet.getPos();
        BlockState state = packet.getState();

        // Ghost block correction
        if (GhostBlockFixer.isPredicted(pos)) {
            GhostBlockFixer.onServerBlockUpdate(world, pos, state);
        }
    }

    /**
     * Handle server entity spawns for crystal prediction.
     */
    @Inject(method = "onEntitySpawn", at = @At("TAIL"))
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        if (packet.getEntityTypeId() == EntityType.END_CRYSTAL) {
            BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
            CrystalOptimizer.onServerCrystalSpawn(world, packet.getId(), pos);
        }
    }

    /**
     * Handle server entity destroys for crystal prediction cleanup.
     */
    @Inject(method = "onEntitiesDestroy", at = @At("TAIL"))
    private void onEntitiesDestroy(EntitiesDestroyS2CPacket packet, CallbackInfo ci) {
        for (int id : packet.getEntityIds()) {
            CrystalOptimizer.onServerCrystalDestroy(id);
        }
    }
}
