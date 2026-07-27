package com.anchoropti;

import com.anchoropti.config.AnchorConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.light.LightingProvider;

/**
 * Explosion Brightness Fix by sz_co (@1szco1).
 * 
 * When an explosion destroys blocks nearby, Minecraft's light engine tries to
 * recalculate lighting gradually, causing stutters and dark spots. This fix
 * forces immediate light updates for all blocks around the player after an
 * explosion, eliminating the lag from light recalculation.
 */
public class ExplosionBrightnessFixer {

    private static long lastBrightnessFixTime = 0;
    private static final long COOLDOWN_MS = 50; // Don't run more than once per 50ms

    /**
     * Called after an explosion near the player.
     * Forces immediate light updates for all blocks in radius.
     */
    public static void onExplosionNearPlayer(double explosionX, double explosionY, double explosionZ) {
        if (!AnchorConfig.get().explosionBrightnessFix) return;

        long now = System.currentTimeMillis();
        if (now - lastBrightnessFixTime < COOLDOWN_MS) return;
        lastBrightnessFixTime = now;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        ClientWorld world = client.world;
        if (world == null) return;

        BlockPos playerPos = client.player.getBlockPos();
        int radius = AnchorConfig.get().brightnessFixRadius;

        // Only fix if explosion is reasonably close to player
        // 1.16.5: getSquaredDistance needs 4th boolean param (treatYAsZero)
        double distSq = playerPos.getSquaredDistance(explosionX, explosionY, explosionZ, false);
        if (distSq > 400) return; // > 20 blocks away, skip

        LightingProvider lighting = world.getLightingProvider();

        // Force light updates in a box around the player
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = -radius; x <= radius; x += 2) {  // Step by 2 for performance
            for (int y = -radius; y <= radius; y += 2) {
                for (int z = -radius; z <= radius; z += 2) {
                    mutable.set(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);

                    // Force the lighting engine to check and update this block
                    lighting.checkBlock(mutable);
                }
            }
        }

        // Also force chunk light status updates for chunks in view distance
        // 1.16.5: ChunkPos doesn't have asBlockPos(), construct manually
        ChunkPos chunkPos = new ChunkPos(playerPos);
        int chunkRadius = (radius >> 4) + 1;
        for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
            for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
                int chunkX = chunkPos.x + cx;
                int chunkZ = chunkPos.z + cz;
                BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
                // Enable light updates for this chunk section
                lighting.setSectionStatus(chunkBlockPos, false);
            }
        }
    }
}
