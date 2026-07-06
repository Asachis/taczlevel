# taczlevel — Developer Resume Notes

## Project
- **Mod ID**: `taczlevel`
- **Path**: `forge-1.20.1-47.4.20-mdk`
- **Build**: `./gradlew clean build` → `build/libs/taczlevel-1.0.0.jar`
- **JDK**: Java 21.0.5 (compatible with Java 17+ targets)

## Concept
Forge 1.20.1 mod that lets tacz guns level up via mob kills. Stats improve per level (5% per level, 20 levels max per stat). Upgrades activated via a "枪械升级台" (Gun Upgrade Station) block with Nether Stars.

## 5 Upgrade Stats
| Stat | Max | Per Level | Max Level |
|------|-----|-----------|-----------|
| Reload Speed | +100% | +1% | 100 |
| Recoil Reduction | -100% | -1% | 100 |
| Armor Penetration | +100% | +1% | 100 |
| Fire Rate | +200% | +2% | 100 |
| Ammo Restore | 50% chance, 1~5 bullets | +0.5% chance | 100 |

## Source Layout (15 classes)
```
com/taczlevel/
  TaczLevel.java                    — main mod class, registration
  block/
    GunUpgradeBlock.java            — block (smithing table look, functional tab)
    entity/
      GunUpgradeBlockEntity.java    — BE with 2-slot inventory + upgrade logic
  command/
    TaczLevelCommand.java           — /taczlevel command
  data/
    GunLevelManager.java            — NBT storage on ItemStack, level/XP/stat calc
  event/
    GunAttachmentHandler.java       — reflection hook into tacz AttachmentPropertyEvent
    TAACompatHandler.java           — player-level AttributeModifiers when TAA present
  gui/
    GunUpgradeMenu.java             — MenuProvider, MenuType, slot layout
    GunUpgradeScreen.java           — Screen with 4 buttons, EXP bar, tooltips
  network/
    PacketHandler.java              — SimpleImpl network channel
    UpgradeMessage.java             — C2S packet for upgrade activation
```

## Key Architecture
- **No compile dep on tacz** — uses reflection for tacz internal classes
- **TAA compat via feature detection** — `ModList.get().isLoaded("taa")`; skips direct cache mod when TAA present
- **EXP shared** across all active non-maxed upgrades; kills apply to lowest-level option
- **Fire rate**: `base * (1.0 + fireRate)` (multiplication, correct for RPM cache)
- **Armor pen**: additive with existing pen, clamped to 1.0f

## Known Issue
**TAA 1.3.4 crash with KubeJS**: `NoClassDefFoundError: dev/latvian/mods/kubejs/event/EventJS` when `taa-1.3.4+1.20.1.jar` is installed without KubeJS. Fixes:
1. Install KubeJS Forge 1.20.1
2. Use older TAA version (e.g. 1.0.6/1.0.7)
3. Remove TAA entirely

Mod works fine standalone (no TAA needed).

## Next Possible Features / TODOs
- Config file for XP rates, max levels, per-level values
- Better texture/model for the upgrade station (vs. smithing table reuse)
- Sound effects on upgrade/level-up
- Particle effects on level-up
- More gun stats (magazine size, bullet speed, etc.)
- Data-driven upgrade recipes (different items per stat)
