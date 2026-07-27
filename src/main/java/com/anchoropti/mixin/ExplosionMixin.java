package com.anchoropti.mixin;

import com.anchoropti.ExplosionBrightnessFixer;
import com.anchoropti.ExplosionOptimizer;
import com.anchoropti.config.AnchorConfig;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Reduces explosion lag and fixes brightness updates.
 * by sz_co (@1szco1)
 */
@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow @Final private World world;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;

    @Inject(method = "affectWorld", at = @At("HEAD"))
    private void onAffectWorld(boolean particles, CallbackInfo ci) {
        ExplosionOptimizer.onExplosion();

        // Trigger brightness fix for explosions near the player
        if (!world.isClient) return;
        ExplosionBrightnessFixer.onExplosionNearPlayer(x, y, z);
    }

    /**
     * Reduce explosion particles.
     */
    @Inject(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"), cancellable = true)
    private void onAddExplosionParticle(boolean particles, CallbackInfo ci) {
        if (ExplosionOptimizer.shouldSkipParticle()) {
            ci.cancel();
        }
    }

    /**
     * Reduce explosion smoke particles.
     */
    @Inject(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V", ordinal = 1), cancellable = true)
    private void onAddSmokeParticle(boolean particles, CallbackInfo ci) {
        if (ExplosionOptimizer.shouldSkipSmoke()) {
            ci.cancel();
        }
    }
}
