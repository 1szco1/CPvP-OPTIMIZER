package com.anchoropti;

import com.anchoropti.config.AnchorConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Marlow-style crystal optimizer by sz_co (@1szco1).
 * - Instantly removes crystals client-side when you hit them
 * - Instantly shows crystals client-side when you place them
 * - SMART HIDING: skips fake crystal spawn when a player is standing
 *   above the placement block (crystal would be hidden anyway, no need
 *   to spawn entity that slows down placement)
 * - Tracks predictions and handles server corrections
 */
public class CrystalOptimizer {

    private static final Map<Integer, PredictedCrystal> predictedCrystals = new HashMap<>();
    private static final Map<BlockPos, PredictedPlacement> predictedPlacements = new HashMap<>();
    private static int tickCounter = 0;

    public static void onClientTick() {
        tickCounter++;
        cleanupPredictions();
    }

    /**
     * Called when player left-clicks an end crystal.
     * Immediately removes it client-side for zero-ping feel.
     */
    public static void onCrystalHit(EndCrystalEntity crystal) {
        if (!AnchorConfig.get().crystalOptimizer || !AnchorConfig.get().crystalBreakPrediction) return;

        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        // Immediately remove the crystal entity client-side
        crystal.remove();

        // Track for cleanup if server disagrees
        predictedCrystals.put(crystal.getEntityId(), 
            new PredictedCrystal(tickCounter, crystal.getPos()));
    }

    /**
     * Called when player right-clicks with an end crystal item.
     * Immediately spawns a fake crystal entity client-side.
     * SMART: If a player is standing above the block, skip fake spawn
     * to avoid unnecessary entity overhead that slows placement.
     */
    public static void onCrystalPlacement(ClientWorld world, BlockPos pos) {
        if (!AnchorConfig.get().crystalOptimizer || !AnchorConfig.get().crystalPlacementPrediction) return;
        if (world == null || pos == null) return;

        // Check if there's already a crystal there
        Box box = new Box(pos).expand(0.5);
        for (Entity entity : world.getEntitiesByType(EntityType.END_CRYSTAL, box, e -> true)) {
            return; // Already a crystal there, don't duplicate
        }

        // SMART HIDING: Check if any player is standing directly above this block
        // If so, the crystal would be hidden/occluded anyway, so skip fake spawn
        // to avoid entity overhead that can slow down rapid placement
        if (AnchorConfig.get().smartCrystalHiding) {
            BlockPos above = pos.up();
            Box aboveBox = new Box(above).expand(0.1, 1.0, 0.1);
            for (PlayerEntity player : world.getPlayers()) {
                if (player.getBoundingBox().intersects(aboveBox)) {
                    // Player is above this block - skip fake crystal spawn
                    // but still track it lightly so we know to expect a server crystal
                    predictedPlacements.put(pos, new PredictedPlacement(tickCounter, -1));
                    return;
                }
            }
        }

        // Spawn a client-side crystal immediately
        EndCrystalEntity crystal = new EndCrystalEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        crystal.setShowBottom(false);
        world.addEntity(crystal.getEntityId(), crystal);

        // Track prediction
        predictedPlacements.put(pos, new PredictedPlacement(tickCounter, crystal.getEntityId()));
    }

    /**
     * Called when server sends entity spawn packet for an end crystal.
     * If we predicted this placement, remove our fake one.
     */
    public static void onServerCrystalSpawn(ClientWorld world, int entityId, BlockPos pos) {
        PredictedPlacement pred = predictedPlacements.get(pos);
        if (pred != null) {
            // Remove our fake crystal since server confirmed the real one
            // (if fakeEntityId is -1, we used smart hiding - nothing to remove)
            if (pred.fakeEntityId != -1) {
                Entity fake = world.getEntityById(pred.fakeEntityId);
                if (fake != null) {
                    fake.remove();
                }
            }
            predictedPlacements.remove(pos);
        }

        // Also clean up any hit predictions that match this new entity
        predictedCrystals.remove(entityId);
    }

    /**
     * Called when server sends entity destroy packet.
     * Clean up any predictions.
     */
    public static void onServerCrystalDestroy(int entityId) {
        predictedCrystals.remove(entityId);
    }

    private static void cleanupPredictions() {
        int timeout = AnchorConfig.get().predictionTimeout;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        // Clean up old hit predictions
        Iterator<Map.Entry<Integer, PredictedCrystal>> hitIt = predictedCrystals.entrySet().iterator();
        while (hitIt.hasNext()) {
            Map.Entry<Integer, PredictedCrystal> entry = hitIt.next();
            if (tickCounter - entry.getValue().tick > timeout) {
                hitIt.remove();
            }
        }

        // Clean up old placement predictions
        Iterator<Map.Entry<BlockPos, PredictedPlacement>> placeIt = predictedPlacements.entrySet().iterator();
        while (placeIt.hasNext()) {
            Map.Entry<BlockPos, PredictedPlacement> entry = placeIt.next();
            if (tickCounter - entry.getValue().tick > timeout) {
                // Remove fake crystal if still there (skip if smart hidden)
                if (entry.getValue().fakeEntityId != -1) {
                    Entity fake = world.getEntityById(entry.getValue().fakeEntityId);
                    if (fake != null) {
                        fake.remove();
                    }
                }
                placeIt.remove();
            }
        }
    }

    private static class PredictedCrystal {
        final int tick;
        final Vec3d pos;

        PredictedCrystal(int tick, Vec3d pos) {
            this.tick = tick;
            this.pos = pos;
        }
    }

    private static class PredictedPlacement {
        final int tick;
        final int fakeEntityId;

        PredictedPlacement(int tick, int fakeEntityId) {
            this.tick = tick;
            this.fakeEntityId = fakeEntityId;
        }
    }
}
