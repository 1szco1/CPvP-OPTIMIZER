package com.anchoropti;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

/**
 * A fake anchor block that looks like an anchor but is instantly replaceable.
 * Used by Hero's-style optimizer to allow instant re-placement without waiting for server confirmation.
 */
public class FakeAnchorBlock extends Block {

    public FakeAnchorBlock() {
        super(AbstractBlock.Settings.of(net.minecraft.block.Material.METAL)
                .strength(0.0f)
                .noCollision()
                .breakInstantly()
                .dropsNothing());
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }
}
