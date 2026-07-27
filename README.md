# CPvP Optimizer 1.16.5

**Created by sz_co (@1szco1)**

A lightweight, client-side Fabric mod for Minecraft 1.16.5 that optimizes competitive PvP gameplay. Combines anchor optimization, crystal optimization, ghost block fixing, explosion lag reduction, and explosion brightness fixing into one allowed mod.

---

## Features

### Anchor Optimizer
- **Zero-Ping Mode**: Instantly hides the anchor when it explodes so you can place another block immediately ~ no waiting for server confirmation.
- **Fake Anchor Mode**: Replaces the exploded anchor with a client-side fake anchor that is replaceable like tall grass/ferns.
- **Glowstone Optimizer**: Prevents wasting glowstone on fully-charged anchors that are about to explode.
- **Ghost Anchor Fix**: Automatically cleans up laggy ghost anchors that appear due to desync.

### Crystal Optimizer (Marlow-style)
- **Instant Crystal Removal**: End crystals vanish the moment you left-click them. Zero-ping crystal breaking.
- **Instant Crystal Placement**: Crystals appear immediately when you place them.
- **Smart Crystal Hiding**: When a player is standing above the block you're placing a crystal on, the mod skips spawning the fake crystal entity. The crystal would be hidden/occluded anyway, so this avoids unnecessary entity overhead that can slow down rapid placement on normal blocks.
- **Server Correction**: When the server confirms the real crystal, the fake one is seamlessly replaced.

### Ghost Block Fix (The Main Feature)
- **Instant Block Placement**: When you place a block, it appears immediately on your screen without waiting for the server.
- **No More Disappearing Blocks**: Blocks no longer vanish for a split second when chaining anchors, crystals, or block clutching.
- **Immediate Sound**: Placement sound plays instantly for responsive feedback.
- **Server Correction**: If the server rejects the placement, the block is corrected automatically.

### Explosion Brightness Fix
- **No More Light Lag**: When an explosion destroys blocks near you, Minecraft's light engine tries to recalculate lighting gradually, causing stutters and dark spots. This fix forces immediate light updates for all blocks around you after an explosion, eliminating the lag from light recalculation.

### Explosion Lag Reducer
- **Particle Reduction**: Reduces explosion particles by 75% (configurable). Massive FPS boost during crystal/anchor PvP.
- **Screen Shake Reduction**: Reduces the camera shake / damage tilt from explosions by 50%.
- **Smoke Reduction**: Removes lingering smoke particles from explosions.

---

## FAQ

### Is this a cheat / hacked client?
**No.** CPvP Optimizer is 100% client-side visual and prediction optimization. It does NOT:
- Send any extra packets to the server
- Change hitboxes or reach
- Give you faster explosions server-side
- Modify crystal break speed server-side
- Automate anything
- Give you any unfair gameplay advantage

It only changes what your client renders and how it handles block/entity placement prediction. All competitive CPvP servers allow client-side prediction mods like this.

### Will I get banned for using this?
This mod is designed to be fully client-side and compliant with tier-list rules. However, **always check your specific server's rules** before using any mod. Most competitive anarchy/CPvP servers (2b2t, Crystal PvP servers, etc.) allow client-side optimization mods. If in doubt, ask staff.

### Does this work on servers with anti-cheat?
Yes. Since the mod is purely client-side and does not send any modified packets, server anti-cheats cannot detect it. It only affects your local rendering.

### Can I use this with other optimization mods?
Yes. CPvP Optimizer is compatible with Sodium, Lithium, Phosphor, Iris, and other Fabric optimization mods.

### Does this work in singleplayer?
Yes, though the ghost block fix and crystal prediction are most noticeable on multiplayer servers with ping.

### How do I toggle features?
Edit `.minecraft/config/cpvpoptimizer.json`. All features can be enabled or disabled individually.

---

## Building

### Requirements
- Java 17 (for building ~ the mod targets Java 8 for MC 1.16.5)
- Gradle 7.6 (included wrapper)

### Steps
```bash
# 1. Install Java 17
sdk install java 17.0.13-tem
sdk use java 17.0.13-tem

# 2. Set Java path in gradle.properties
# org.gradle.java.home=/home/YOURNAME/.sdkman/candidates/java/17.0.13-tem

# 3. Build
./gradlew build
```

The compiled `.jar` will be in `build/libs/cpvp-optimizer-1.0.0.jar`.

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.16.5
2. Download and install [Fabric API](https://modrinth.com/mod/fabric-api) for 1.16.5
3. Put `cpvp-optimizer-1.0.0.jar` into your `.minecraft/mods/` folder
4. Launch Minecraft

---

## Config

Edit `.minecraft/config/cpvpoptimizer.json`:

```json
{
  "zeroPingMode": true,
  "fakeAnchorMode": false,
  "glowstoneOptimizer": true,
  "removeGhostAnchors": true,
  "predictionTimeout": 20,

  "crystalOptimizer": true,
  "crystalPlacementPrediction": true,
  "crystalBreakPrediction": true,
  "smartCrystalHiding": true,

  "ghostBlockFix": true,
  "ghostBlockTimeout": 10,
  "ghostBlockSoundFix": true,

  "explosionOptimizer": true,
  "explosionParticleReduction": 75,
  "explosionSoundReduction": 50,
  "explosionScreenShake": true,
  "explosionSmokeReduction": true,

  "explosionBrightnessFix": true,
  "brightnessFixRadius": 16
}
```

| Option | Description |
|--------|-------------|
| `zeroPingMode` | Instantly remove anchor on explosion |
| `fakeAnchorMode` | Use fake replaceable anchor instead of air |
| `glowstoneOptimizer` | Prevent glowstone waste on full anchors |
| `removeGhostAnchors` | Clean up desynced anchors |
| `crystalOptimizer` | Enable crystal optimization |
| `crystalPlacementPrediction` | Show crystals instantly on placement |
| `crystalBreakPrediction` | Remove crystals instantly on hit |
| `smartCrystalHiding` | Skip fake crystal when player is above block |
| `ghostBlockFix` | THE MAIN FEATURE ~ instant block placement |
| `ghostBlockSoundFix` | Play placement sound immediately |
| `explosionOptimizer` | Enable explosion lag reduction |
| `explosionParticleReduction` | % of explosion particles to remove (0-100) |
| `explosionScreenShake` | Reduce camera shake from explosions |
| `explosionBrightnessFix` | Force immediate light updates after explosions |
| `brightnessFixRadius` | Radius around player for brightness fix |

---

## Credits

**Created by sz_co (@1szco1)**

Inspired by:
- Hero's Anchor Optimizer (fake anchor / replaceable approach)
- cutebow's Anchor Optimizer (zero-ping instant removal)
- Marlow's Crystal Optimizer (client-side crystal removal)
- ClientSideCrystals (instant crystal placement prediction)

---

## License

MIT License : feel free to share, modify, and distribute.
