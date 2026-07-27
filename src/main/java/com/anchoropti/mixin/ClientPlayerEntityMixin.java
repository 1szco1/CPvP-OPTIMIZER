package com.anchoropti.mixin;

import com.anchoropti.ExplosionOptimizer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Reduces screen shake / damage tilt from explosions.
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float modifyDamageAmount(float amount, DamageSource source) {
        // Reduce screen shake from explosions
        if (source.isExplosive()) {
            return ExplosionOptimizer.reduceScreenShake(amount);
        }
        return amount;
    }
}
