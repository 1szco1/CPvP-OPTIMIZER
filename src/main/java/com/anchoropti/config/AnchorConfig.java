package com.anchoropti.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Config for CPvP Optimizer by sz_co (@1szco1).
 * All features are client-side only and allowed on competitive servers.
 */
public class AnchorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "cpvpoptimizer.json");
    private static AnchorConfig INSTANCE;

    // === ANCHOR OPTIMIZER ===
    public boolean zeroPingMode = true;
    public boolean fakeAnchorMode = false;
    public boolean glowstoneOptimizer = true;
    public boolean removeGhostAnchors = true;
    public int predictionTimeout = 20;

    // === CRYSTAL OPTIMIZER (Marlow-style) ===
    public boolean crystalOptimizer = true;
    public boolean crystalPlacementPrediction = true;
    public boolean crystalBreakPrediction = true;
    public boolean smartCrystalHiding = true;  // Hide fake crystal when player above block

    // === GHOST BLOCK FIX ===
    public boolean ghostBlockFix = true;
    public int ghostBlockTimeout = 10;
    public boolean ghostBlockSoundFix = true;

    // === EXPLOSION LAG REDUCER ===
    public boolean explosionOptimizer = true;
    public int explosionParticleReduction = 75;
    public int explosionSoundReduction = 50;
    public boolean explosionScreenShake = true;
    public boolean explosionSmokeReduction = true;

    // === EXPLOSION BRIGHTNESS FIX ===
    public boolean explosionBrightnessFix = true;
    public int brightnessFixRadius = 16;  // Radius around player to force light updates

    public static AnchorConfig get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, AnchorConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
                INSTANCE = new AnchorConfig();
            }
        } else {
            INSTANCE = new AnchorConfig();
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
