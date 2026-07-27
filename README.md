

**The first combined Anchor + Crystal Optimizer for Minecraft 1.16.5 Fabric**

Created by **sz_co** (@1szco1)

---

## What is CPvP Optimizer?

CPvP Optimizer is a **100% client-side** Fabric mod for Minecraft 1.16.5 that eliminates the visual and input lag you experience during competitive Crystal PvP. It combines anchor optimization, crystal optimization, ghost block fixing, explosion brightness fixing, and explosion lag reduction ~ all in one lightweight mod.

Unlike other mods that only fix one thing, CPvP Optimizer handles the **entire CPvP chain**: anchors, crystals, blocks, explosions, and lighting.

---

## How It Works

### The Problem

When you play on a server with ping (even 20-50ms), your client waits for the server to confirm every action before showing it on screen:

- **Anchor explosions**: You click a charged anchor, but the block stays visible for a split second before disappearing ~ you can't place your next block immediately.
- **Crystal placement**: You right-click to place a crystal, but it doesn't appear until the server sends the entity spawn packet ~ making chaining feel sluggish.
- **Crystal breaking**: You left-click a crystal, but it stays rendered for a moment ~ you can't tell if your hit registered.
- **Ghost blocks**: You place a block, it appears for a frame, then vanishes waiting for server confirmation ~ then reappears. This makes block clutching and chaining nearly impossible on high ping.
- **Explosion light lag**: When explosions destroy blocks, Minecraft's light engine recalculates lighting block-by-block, causing massive stutters and dark spots.
- **Explosion particles**: TNT, anchors, beds, and crystals spawn hundreds of particles that tank your FPS during chain reactions.

### The Solution

CPvP Optimizer predicts these actions **client-side** and applies them instantly, before the server even responds. When the server finally confirms (or corrects) the action, the mod seamlessly updates to match. You get zero-ping responsiveness without actually having zero ping.

---

## Features

### Anchor Optimizer
| Feature | Description |
|---------|-------------|
| **Zero-Ping Removal** | Instantly removes respawn anchors when they explode. Place your next block immediately ~ no waiting for server confirmation. |
| **Fake Anchor Mode** | Replaces exploded anchors with a client-side fake anchor that's instantly replaceable (like tall grass). Toggle in config. |
| **Glowstone Optimizer** | Prevents wasting glowstone on fully-charged anchors that are about to explode. |
| **Ghost Anchor Fix** | Automatically removes desynced anchors that reappear due to lag. |

### Crystal Optimizer (Marlow-style)
| Feature | Description |
|---------|-------------|
| **Instant Crystal Break** | Crystals vanish the exact frame you left-click them. Know immediately that your hit registered. |
| **Instant Crystal Placement** | Crystals appear instantly when you place them. Chain crystals as fast as you can click. |
| **Smart Crystal Hiding** | When a player is standing above the block you're placing a crystal on, the mod skips spawning the fake prediction entity. The crystal would be hidden anyway, so this avoids unnecessary entity overhead that slows down rapid placement on normal blocks. |
| **Server Correction** | When the server sends the real crystal entity, the fake one is seamlessly replaced. If the server rejects your placement, it's corrected automatically. |

### Ghost Block Fix
| Feature | Description |
|---------|-------------|
| **Instant Block Placement** | Blocks appear the moment you place them ~ no disappearing, no reappearing. |
| **Placement Sound** | The placement sound plays immediately for tactile feedback. |
| **Server Correction** | If the server rejects your placement, the block is corrected without any visual glitch. |

### Explosion Brightness Fix
| Feature | Description |
|---------|-------------|
| **Instant Light Updates** | After any explosion near you, forces the light engine to recalculate all blocks in radius immediately. No more stutters or dark spots from gradual light updates. |

### Explosion Lag Reducer
| Feature | Description |
|---------|-------------|
| **Particle Reduction** | Removes 75% of explosion particles (configurable). Massive FPS boost during crystal/anchor chain reactions. |
| **Screen Shake Reduction** | Cuts explosion camera shake / damage tilt by 50%. See clearly during fights. |
| **Smoke Reduction** | Removes lingering smoke particles from explosions. |

---

## Why It's Allowed (Tier-List Compliant)

CPvP Optimizer is **fully client-side** and does NOT:

- Send any extra packets to the server
- Modify hitboxes, reach, or attack speed
- Give you faster explosions server-side
- Automate any actions (placing, breaking, clicking)
- Modify damage calculation or knockback
- Use any form of ESP, tracers, or wallhacks
- Interfere with anti-cheat systems

It only changes **what your client renders** and how it handles **local prediction**. The server has full authority ~ if the server rejects an action, the client is corrected. This is the same principle used by vanilla Minecraft's own block placement prediction (which is why blocks sometimes appear then disappear).

Most competitive CPvP servers (anarchy, crystal PvP, tier-list servers) explicitly allow client-side prediction mods. However, **always check your specific server's rules** if unsure.

---

## Installation

### Requirements
- Minecraft 1.16.5
- [Fabric Loader](https://fabricmc.net/use/) for 1.16.5
- [Fabric API](https://modrinth.com/mod/fabric-api) for 1.16.5

### Steps
1. Download and install Fabric Loader for 1.16.5
2. Download Fabric API and place it in `.minecraft/mods/`
3. Download `cpvp-optimizer-1.0.0.jar` and place it in `.minecraft/mods/`
4. Launch Minecraft

### Config
Edit `.minecraft/config/cpvpoptimizer.json` to toggle features:

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

---

## FAQ

### Is this a cheat / hacked client?
**No.** CPvP Optimizer is purely client-side visual and input prediction. It does not give you any gameplay advantage that a player with zero ping wouldn't already have. It doesn't modify packets, hitboxes, damage, or automate anything.

### Will I get banned for using this?
This mod is designed to be compliant with tier-list rules. Most competitive CPvP servers allow client-side prediction mods. However, **always check your specific server's rules** before using any mod. When in doubt, ask staff.

### Does this work with anti-cheat?
Yes. Since the mod is purely client-side and does not send modified packets, server anti-cheats cannot detect it. It only affects local rendering.

### Can I use this with Sodium/Lithium/Iris?
Yes. CPvP Optimizer is fully compatible with all major Fabric optimization mods.

### Does this work in singleplayer?
Yes, though the features are most noticeable on multiplayer servers with ping.

### Why 1.16.5?
1.16.5 is very old version for competitive Crystal PvP. Most Low-end CPvP players target this version. This is the **first mod to combine anchor optimization, crystal optimization, ghost block fixing, and explosion optimization** specifically for 1.16.5.

### Can I disable specific features?
Yes. Every feature can be toggled individually in the config file.

### Does this work on Forge?
No, this is a Fabric-only mod. A Forge port is not planned.

---

## Credits

**Created by sz_co (@1szco1)**

Inspired by:
- Hero's Anchor Optimizer
- cutebow's Anchor Optimizer
- Marlow's Crystal Optimizer
- ClientSideCrystals

---

## Socials

- **YouTube**: [My YouTube Channel](https://youtube.com/@1szco1)
- **Discord**: [MY Discord Profile](https://discord.com/users/1134017208410968064)


---

## License

MIT License - feel free to share, modify, and distribute. Give credit if you fork it.
