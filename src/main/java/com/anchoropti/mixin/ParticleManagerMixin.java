package com.anchoropti.mixin;

import com.anchoropti.ExplosionOptimizer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Limits explosion particles at the particle manager level.
 * Catches particles that slip through the Explosion mixin.
 */
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"), cancellable = true)
    private void onAddParticle(ParticleEffect parameters, double x, double y, double z, 
                                double velocityX, double velocityY, double velocityZ,
                                CallbackInfoReturnable<Particle> cir) {
        // Check for explosion-related particles
        if (parameters.getType() == ParticleTypes.EXPLOSION || 
            parameters.getType() == ParticleTypes.EXPLOSION_EMITTER ||
            parameters.getType() == ParticleTypes.LARGE_SMOKE) {

            if (ExplosionOptimizer.shouldSkipParticle()) {
                cir.setReturnValue(null);
            }
        }
    }
}
