package com.anchoropti;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AnchorOptimizerMod implements ModInitializer {
    public static final String MOD_ID = "cpvpoptimizer";

    public static final Block FAKE_ANCHOR = new FakeAnchorBlock();

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "fake_anchor"), FAKE_ANCHOR);
    }
}
