package com.anchoropti;

import com.anchoropti.config.AnchorConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Client-side logic for Anchor Optimizer + Crystal Optimizer + Ghost Block Fix + Explosion Optimizer.
 */
public class AnchorOptimizerClient implements ClientModInitializer {

    private static final Map<BlockPos, PredictedExplosion> predictedExplosions = new HashMap<>();
    private static int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounter++;
            if (client.world != null) {
                cleanupPredictions(client.world);
                GhostBlockFixer.onClientTick();
                CrystalOptimizer.onClientTick();

                // Ghost anchor fix: every 5 ticks, check predicted positions
                if (tickCounter % 5 == 0) {
                    fixGhostAnchors(client.world);
                }
            }
        });
    }

    /**
     * Called when we predict an anchor will explode at the given position.
     */
    public static void predictExplosion(ClientWorld world, BlockPos pos) {
        if (world == null || pos == null) return;

        BlockState current = world.getBlockState(pos);
        if (!current.isOf(Blocks.RESPAWN_ANCHOR)) return;

        predictedExplosions.put(pos, new PredictedExplosion(world.getRegistryKey(), tickCounter));

        if (AnchorConfig.get().zeroPingMode) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
        } else if (AnchorConfig.get().fakeAnchorMode) {
            world.setBlockState(pos, AnchorOptimizerMod.FAKE_ANCHOR.getDefaultState(), 0);
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
        }

        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.offset(dir);
            if (world.getBlockState(neighbor).isOf(AnchorOptimizerMod.FAKE_ANCHOR)) {
                world.setBlockState(neighbor, Blocks.AIR.getDefaultState(), 0);
            }
        }
    }

    private static void cleanupPredictions(ClientWorld world) {
        int timeout = AnchorConfig.get().predictionTimeout;
        Iterator<Map.Entry<BlockPos, PredictedExplosion>> it = predictedExplosions.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<BlockPos, PredictedExplosion> entry = it.next();
            PredictedExplosion pred = entry.getValue();

            if (tickCounter - pred.tick > timeout) {
                it.remove();
            }
        }
    }

    private static void fixGhostAnchors(ClientWorld world) {
        if (!AnchorConfig.get().removeGhostAnchors) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        BlockPos playerPos = client.player.getBlockPos();
        int range = 6;

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (state.isOf(Blocks.RESPAWN_ANCHOR)) {
                        PredictedExplosion pred = predictedExplosions.get(pos);
                        if (pred != null && tickCounter - pred.tick > AnchorConfig.get().predictionTimeout + 10) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
                            predictedExplosions.remove(pos);
                        }
                    }

                    if (state.isOf(AnchorOptimizerMod.FAKE_ANCHOR)) {
                        PredictedExplosion pred = predictedExplosions.get(pos);
                        if (pred == null || tickCounter - pred.tick > AnchorConfig.get().predictionTimeout) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
                        }
                    }
                }
            }
        }
    }

    public static boolean isPredicted(BlockPos pos) {
        return predictedExplosions.containsKey(pos);
    }

    private static class PredictedExplosion {
        final net.minecraft.util.registry.RegistryKey<net.minecraft.world.World> dimension;
        final int tick;

        PredictedExplosion(net.minecraft.util.registry.RegistryKey<net.minecraft.world.World> dimension, int tick) {
            this.dimension = dimension;
            this.tick = tick;
        }
    }
}
