package com.anchoropti;

import com.anchoropti.config.AnchorConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Explosion lag reducer.
 * Reduces visual and audio lag from explosions (TNT, anchors, crystals, beds).
 * All changes are client-side visual/audio only.
 */
public class ExplosionOptimizer {

    private static long lastExplosionTime = 0;
    private static int explosionCount = 0;

    /**
     * Called when an explosion happens. Reduces screen shake intensity.
     */
    public static float reduceScreenShake(float originalDamage) {
        if (!AnchorConfig.get().explosionOptimizer || !AnchorConfig.get().explosionScreenShake) {
            return originalDamage;
        }

        // Reduce screen shake by 50% - you still feel the hit but less violently
        return originalDamage * 0.5f;
    }

    /**
     * Called when explosion particles are about to spawn.
     * Returns true if particles should be skipped.
     */
    public static boolean shouldSkipParticle() {
        if (!AnchorConfig.get().explosionOptimizer) return false;

        int reduction = AnchorConfig.get().explosionParticleReduction;
        if (reduction <= 0) return false;
        if (reduction >= 100) return true;

        // Skip particles based on reduction percentage
        return Math.random() * 100 < reduction;
    }

    /**
     * Called when explosion smoke particles are about to spawn.
     */
    public static boolean shouldSkipSmoke() {
        if (!AnchorConfig.get().explosionOptimizer) return false;
        return AnchorConfig.get().explosionSmokeReduction;
    }

    /**
     * Returns reduced volume for explosion sounds.
     */
    public static float getExplosionVolume(float originalVolume) {
        if (!AnchorConfig.get().explosionOptimizer) return originalVolume;

        int reduction = AnchorConfig.get().explosionSoundReduction;
        if (reduction <= 0) return originalVolume;
        if (reduction >= 100) return 0.0f;

        return originalVolume * (1.0f - reduction / 100.0f);
    }

    /**
     * Track rapid explosions to further reduce particles during chain reactions.
     */
    public static void onExplosion() {
        long now = System.currentTimeMillis();
        if (now - lastExplosionTime < 100) {
            explosionCount++;
        } else {
            explosionCount = 1;
        }
        lastExplosionTime = now;
    }

    public static int getChainExplosionReduction() {
        // During chain reactions, reduce particles even more
        if (explosionCount > 3) {
            return Math.min(95, AnchorConfig.get().explosionParticleReduction + 20);
        }
        return AnchorConfig.get().explosionParticleReduction;
    }
}
