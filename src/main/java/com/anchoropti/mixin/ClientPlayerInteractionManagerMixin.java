package com.anchoropti.mixin;

import com.anchoropti.AnchorOptimizerClient;
import com.anchoropti.CrystalOptimizer;
import com.anchoropti.GhostBlockFixer;
import com.anchoropti.config.AnchorConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Core mixin for ALL optimizations:
 * - Anchor optimization (existing)
 * - Crystal hit prediction (Marlow-style)
 * - Crystal placement prediction
 * - Ghost block fix (THE MAIN FEATURE)
 */
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    // === ANCHOR OPTIMIZATION (existing) ===
    @Inject(method = "interactBlock", at = @At("TAIL"))
    private void onInteractBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult,
                                 CallbackInfoReturnable<ActionResult> cir) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);

        // --- CRYSTAL PLACEMENT PREDICTION ---
        ItemStack held = player.getStackInHand(hand);
        if (held.getItem() == Items.END_CRYSTAL) {
            // Predict crystal placement on obsidian/bedrock
            BlockState targetState = world.getBlockState(pos);
            if (targetState.isOf(Blocks.OBSIDIAN) || targetState.isOf(Blocks.BEDROCK)) {
                BlockPos placementPos = pos.up();
                CrystalOptimizer.onCrystalPlacement(world, placementPos);
            }
            return;
        }

        // --- GHOST BLOCK FIX ---
        // If we successfully placed a block, predict it immediately
        ActionResult result = cir.getReturnValue();
        if (result == ActionResult.SUCCESS) {
            // The block was placed at the adjacent position (hit result side)
            BlockPos placementPos = pos.offset(hitResult.getSide());
            GhostBlockFixer.predictBlockPlacement(world, placementPos, held, hitResult.getSide());
        }

        // --- ANCHOR OPTIMIZATION ---
        if (!state.isOf(Blocks.RESPAWN_ANCHOR)) return;

        int charges = state.get(RespawnAnchorBlock.CHARGES);
        boolean holdingGlowstone = held.getItem() == Items.GLOWSTONE;
        boolean isNether = world.getRegistryKey() == World.NETHER;

        // Glowstone optimizer
        if (AnchorConfig.get().glowstoneOptimizer && holdingGlowstone) {
            if (charges >= 4) {
                if (!isNether) {
                    AnchorOptimizerClient.predictExplosion(world, pos);
                }
                return;
            }
        }

        // Anchor explosion prediction
        if (!isNether && charges > 0) {
            if (!holdingGlowstone || charges == 4) {
                AnchorOptimizerClient.predictExplosion(world, pos);
            }
        }
    }

    // === CRYSTAL HIT PREDICTION (Marlow-style) ===
    // attackEntity takes PlayerEntity, NOT ClientPlayerEntity in 1.16.5
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (!AnchorConfig.get().crystalOptimizer || !AnchorConfig.get().crystalBreakPrediction) return;

        if (target instanceof EndCrystalEntity) {
            CrystalOptimizer.onCrystalHit((EndCrystalEntity) target);
        }
    }
}
