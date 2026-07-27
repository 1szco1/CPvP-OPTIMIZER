package com.anchoropti;

import com.anchoropti.config.AnchorConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Ghost Block Fix - THE MAIN FEATURE.
 * 
 * When you place a block on high ping, Minecraft waits for server confirmation
 * before showing it. This causes blocks to "disappear" for a split second,
 * making chaining (anchor chaining, crystal chaining, block clutching) feel terrible.
 * 
 * This fix immediately shows the placed block client-side and handles server
 * corrections gracefully.
 */
public class GhostBlockFixer {

    private static final Map<BlockPos, PredictedBlock> predictedBlocks = new HashMap<>();
    private static int tickCounter = 0;

    public static void onClientTick() {
        tickCounter++;
        cleanupPredictions();
    }

    /**
     * Called immediately after a successful block placement interaction.
     * Sets the block client-side without waiting for server confirmation.
     */
    public static void predictBlockPlacement(ClientWorld world, BlockPos pos, ItemStack stack, Direction facing) {
        if (!AnchorConfig.get().ghostBlockFix) return;
        if (world == null || pos == null || stack == null) return;
        if (!(stack.getItem() instanceof BlockItem)) return;

        BlockItem blockItem = (BlockItem) stack.getItem();
        Block block = blockItem.getBlock();

        // Get the placement state based on facing direction
        BlockState placementState = block.getDefaultState();
        // In 1.16.5, we use the block's default state; rotation handling is complex
        // so we just use default state - server will correct if needed

        // Store prediction
        predictedBlocks.put(pos, new PredictedBlock(tickCounter, placementState));

        // IMMEDIATELY set the block client-side (this is the fix!)
        world.setBlockState(pos, placementState, 0);

        // Play placement sound immediately so it feels responsive
        if (AnchorConfig.get().ghostBlockSoundFix) {
            world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                block.getSoundGroup(placementState).getPlaceSound(),
                SoundCategory.BLOCKS, 1.0f, 0.8f, false);
        }
    }

    /**
     * Called when the server sends a block update for a position.
     * If we predicted this block, accept the server's version (it wins).
     */
    public static void onServerBlockUpdate(ClientWorld world, BlockPos pos, BlockState serverState) {
        PredictedBlock pred = predictedBlocks.get(pos);
        if (pred != null) {
            // Server has responded - remove our prediction
            predictedBlocks.remove(pos);

            // If server says something different, let it override (server is authoritative)
            if (!serverState.equals(pred.state)) {
                world.setBlockState(pos, serverState, 0);
            }
        }
    }

    /**
     * Check if a position has a predicted block.
     */
    public static boolean isPredicted(BlockPos pos) {
        return predictedBlocks.containsKey(pos);
    }

    private static void cleanupPredictions() {
        int timeout = AnchorConfig.get().ghostBlockTimeout;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        Iterator<Map.Entry<BlockPos, PredictedBlock>> it = predictedBlocks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, PredictedBlock> entry = it.next();
            PredictedBlock pred = entry.getValue();

            if (tickCounter - pred.tick > timeout) {
                BlockPos pos = entry.getKey();
                BlockState current = world.getBlockState(pos);

                // If our predicted block is still there and server hasn't corrected,
                // check if it should be removed (server might have rejected it)
                if (current.equals(pred.state)) {
                    // Server never corrected - either it was accepted or we never got packet
                    // Leave it; if server rejected it, we'd have gotten an update
                }

                it.remove();
            }
        }
    }

    private static class PredictedBlock {
        final int tick;
        final BlockState state;

        PredictedBlock(int tick, BlockState state) {
            this.tick = tick;
            this.state = state;
        }
    }
}
