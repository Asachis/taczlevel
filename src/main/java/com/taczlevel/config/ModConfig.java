package com.taczlevel.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final StatsCategory STATS = new StatsCategory();
    public static final XpCategory XP = new XpCategory();
    public static final StationCategory STATION = new StationCategory();
    public static final SoundCategory SOUND = new SoundCategory();
    public static final OptionNotificationCategory OPTION_NOTIFICATION = new OptionNotificationCategory();
    public static final DummyAmmoCategory DUMMY_AMMO = new DummyAmmoCategory();
    public static final WeightCategory WEIGHT = new WeightCategory();
    public static final GameRulesCategory GAME_RULES = new GameRulesCategory();
    public static final AutoRulesCategory AUTO_RULES = new AutoRulesCategory();

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static class StatsCategory {
        public final ModConfigSpec.IntValue maxLevel;
        public final ModConfigSpec.IntValue maxFireRateLevel;
        public final ModConfigSpec.DoubleValue reloadPerLevel;
        public final ModConfigSpec.DoubleValue recoilPerLevel;
        public final ModConfigSpec.DoubleValue penPerLevel;
        public final ModConfigSpec.DoubleValue fireRatePerLevel;
        public final ModConfigSpec.DoubleValue reloadMaxBonus;
        public final ModConfigSpec.DoubleValue recoilMaxBonus;
        public final ModConfigSpec.DoubleValue penMaxBonus;
        public final ModConfigSpec.DoubleValue fireRateMaxBonus;

        StatsCategory() {
            BUILDER.push("stats");
            maxLevel = BUILDER
                    .comment("Maximum level for reload, recoil, and armor penetration")
                    .defineInRange("max_level", 100, 1, 10000);
            maxFireRateLevel = BUILDER
                    .comment("Maximum level for fire rate")
                    .defineInRange("max_fire_rate_level", 100, 1, 10000);
            reloadPerLevel = BUILDER
                    .comment("Reload speed bonus percentage per level (e.g. 1.0 = +1% per level)")
                    .defineInRange("reload_per_level", 1.0, 0.01, 1000.0);
            recoilPerLevel = BUILDER
                    .comment("Recoil reduction percentage per level (e.g. 1.0 = -1% per level)")
                    .defineInRange("recoil_per_level", 1.0, 0.01, 1000.0);
            penPerLevel = BUILDER
                    .comment("Armor penetration percentage per level (e.g. 1.0 = +1% per level)")
                    .defineInRange("pen_per_level", 1.0, 0.01, 1000.0);
            fireRatePerLevel = BUILDER
                    .comment("Fire rate bonus percentage per level (e.g. 2.0 = +2% per level)")
                    .defineInRange("fire_rate_per_level", 2.0, 0.01, 1000.0);
            reloadMaxBonus = BUILDER
                    .comment("Maximum reload speed bonus as decimal (e.g. 1.0 = 100% max)")
                    .defineInRange("reload_max_bonus", 1.0, 0.01, 100.0);
            recoilMaxBonus = BUILDER
                    .comment("Maximum recoil reduction as decimal (e.g. 1.0 = 100% max)")
                    .defineInRange("recoil_max_bonus", 1.0, 0.01, 100.0);
            penMaxBonus = BUILDER
                    .comment("Maximum armor penetration as decimal (e.g. 1.0 = 100% max)")
                    .defineInRange("pen_max_bonus", 1.0, 0.01, 100.0);
            fireRateMaxBonus = BUILDER
                    .comment("Maximum fire rate bonus as decimal (e.g. 5.0 = 500% max)")
                    .defineInRange("fire_rate_max_bonus", 5.0, 0.01, 100.0);
            BUILDER.pop();
        }
    }

    public static class XpCategory {
        public final ModConfigSpec.IntValue baseExp;
        public final ModConfigSpec.IntValue expPerLevel;
        public final ModConfigSpec.DoubleValue damageExpMultiplier;
        public final ModConfigSpec.IntValue dragonExp;
        public final ModConfigSpec.IntValue witherExp;
        public final ModConfigSpec.IntValue bossExp;
        public final ModConfigSpec.IntValue eliteExp;
        public final ModConfigSpec.IntValue normalExp;
        public final ModConfigSpec.IntValue neutralExp;
        public final ModConfigSpec.IntValue animalExp;
        public final ModConfigSpec.IntValue otherExp;

        XpCategory() {
            BUILDER.push("xp");
            baseExp = BUILDER
                    .comment("Base experience for the level-up formula: baseExp + expPerLevel * level")
                    .defineInRange("base_exp", 25, 1, 100000);
            expPerLevel = BUILDER
                    .comment("Experience multiplier per level: baseExp + expPerLevel * level")
                    .defineInRange("exp_per_level", 25, 1, 100000);
            damageExpMultiplier = BUILDER
                    .comment("Experience gained from dealing damage: max(1, damage * this)")
                    .defineInRange("damage_exp_multiplier", 0.2, 0.0, 100.0);
            dragonExp = BUILDER
                    .comment("Experience for killing an Ender Dragon")
                    .defineInRange("dragon_exp", 100, 0, 100000);
            witherExp = BUILDER
                    .comment("Experience for killing a Wither")
                    .defineInRange("wither_exp", 80, 0, 100000);
            bossExp = BUILDER
                    .comment("Experience for killing a boss-level monster (maxHealth >= 100)")
                    .defineInRange("boss_exp", 50, 0, 100000);
            eliteExp = BUILDER
                    .comment("Experience for killing an elite monster (maxHealth >= 40)")
                    .defineInRange("elite_exp", 25, 0, 100000);
            normalExp = BUILDER
                    .comment("Base experience for killing a normal monster")
                    .defineInRange("normal_exp", 10, 0, 100000);
            neutralExp = BUILDER
                    .comment("Experience for killing a neutral mob")
                    .defineInRange("neutral_exp", 8, 0, 100000);
            animalExp = BUILDER
                    .comment("Experience for killing an animal")
                    .defineInRange("animal_exp", 3, 0, 100000);
            otherExp = BUILDER
                    .comment("Experience for killing other entities (non-player)")
                    .defineInRange("other_exp", 8, 0, 100000);
            BUILDER.pop();
        }
    }

    public static class StationCategory {
        public final ModConfigSpec.ConfigValue<String> activationItem;
        public final ModConfigSpec.IntValue activationCount;

        StationCategory() {
            BUILDER.push("station");
            activationItem = BUILDER
                    .comment("Item required to activate a new upgrade (resource location, e.g. minecraft:nether_star)")
                    .define("activation_item", "minecraft:nether_star");
            activationCount = BUILDER
                    .comment("Number of activation items consumed per upgrade")
                    .defineInRange("activation_count", 1, 1, 64);
            BUILDER.pop();
        }

        public Item getActivationItem() {
            String id = activationItem.get();
            ResourceLocation loc = ResourceLocation.tryParse(id);
            if (loc == null) {
                loc = ResourceLocation.tryParse("minecraft:nether_star");
            }
            Item item = BuiltInRegistries.ITEM.get(loc);
            return item != null ? item : net.minecraft.world.item.Items.NETHER_STAR;
        }
    }

    public static class SoundCategory {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.DoubleValue volume;
        public final ModConfigSpec.DoubleValue pitch;

        SoundCategory() {
            BUILDER.push("sound");
            enabled = BUILDER
                    .comment("Enable sound effects for level-up and activation")
                    .define("enabled", true);
            volume = BUILDER
                    .comment("Sound volume (0.0 = silent, 1.0 = full)")
                    .defineInRange("volume", 1.0, 0.0, 5.0);
            pitch = BUILDER
                    .comment("Sound pitch multiplier (0.5 = low, 2.0 = high)")
                    .defineInRange("pitch", 1.0, 0.01, 5.0);
            BUILDER.pop();
        }
    }

    @SuppressWarnings("unchecked")
    public static class OptionNotificationCategory {
        public final ModConfigSpec.ConfigValue<String>[] positions;
        public final ModConfigSpec.DoubleValue[] sizes;

        private static final String[] NAMES = {"reload", "recoil", "penetration", "fire_rate", "dummy_ammo", "weight"};
        private static final String[] LABELS = {"Reload Speed", "Recoil", "Armor Penetration", "Fire Rate", "Virtual Ammo", "Weight Reduction"};

        OptionNotificationCategory() {
            BUILDER.push("notification_options");
            positions = new ModConfigSpec.ConfigValue[6];
            sizes = new ModConfigSpec.DoubleValue[6];
            for (int i = 0; i < 6; i++) {
                BUILDER.push(NAMES[i]);
                positions[i] = BUILDER
                        .comment("Notification position for " + LABELS[i] + ": \"actionbar\", \"chat\", \"both\", or \"none\"")
                        .define("position", "actionbar");
                sizes[i] = BUILDER
                        .comment("Text scale for " + LABELS[i] + " (0.5 = small, 1.0 = normal, 2.0 = large)")
                        .defineInRange("size", 1.0, 0.5, 5.0);
                BUILDER.pop();
            }
            BUILDER.pop();
        }

        public String getPosition(int optionIndex) {
            if (optionIndex < 0 || optionIndex >= positions.length) return "actionbar";
            return positions[optionIndex].get();
        }

        public double getSize(int optionIndex) {
            if (optionIndex < 0 || optionIndex >= sizes.length) return 1.0;
            return sizes[optionIndex].get();
        }
    }

    public static class DummyAmmoCategory {
        public final ModConfigSpec.IntValue maxLevel;
        public final ModConfigSpec.IntValue basePool;
        public final ModConfigSpec.IntValue poolPerLevel;
        public final ModConfigSpec.DoubleValue regenPerSecond;
        public final ModConfigSpec.DoubleValue regenPerSecondPerLevel;
        public final ModConfigSpec.IntValue regenDelayTicks;

        DummyAmmoCategory() {
            BUILDER.push("dummy_ammo");
            maxLevel = BUILDER
                    .comment("Maximum level for virtual ammo")
                    .defineInRange("max_level", 10, 1, 100);
            basePool = BUILDER
                    .comment("Base ammo pool at level 1")
                    .defineInRange("base_pool", 60, 1, 99999);
            poolPerLevel = BUILDER
                    .comment("Additional ammo pool per level beyond 1")
                    .defineInRange("pool_per_level", 60, 1, 99999);
            regenPerSecond = BUILDER
                    .comment("Base ammo regenerated per second at level 1")
                    .defineInRange("regen_per_second", 5.0, 0.0, 1000.0);
            regenPerSecondPerLevel = BUILDER
                    .comment("Additional ammo regenerated per second per level beyond 1")
                    .defineInRange("regen_per_second_per_level", 5.0, 0.0, 1000.0);
            regenDelayTicks = BUILDER
                    .comment("Delay in ticks before regen starts after using ammo (20 ticks = 1 second)")
                    .defineInRange("regen_delay_ticks", 40, 0, 6000);
            BUILDER.pop();
        }

        public int getMaxPool(int level) {
            return basePool.get() + poolPerLevel.get() * (level - 1);
        }

        public double getRegenPerSecond(int level) {
            return regenPerSecond.get() + regenPerSecondPerLevel.get() * (level - 1);
        }
    }

    public static class WeightCategory {
        public final ModConfigSpec.IntValue maxLevel;
        public final ModConfigSpec.DoubleValue weightPerLevel;
        public final ModConfigSpec.DoubleValue weightMaxReduction;

        WeightCategory() {
            BUILDER.push("weight");
            maxLevel = BUILDER
                    .comment("Maximum level for weight reduction")
                    .defineInRange("max_level", 100, 1, 10000);
            weightPerLevel = BUILDER
                    .comment("Weight reduction percentage per level (e.g. 1.0 = -1% per level)")
                    .defineInRange("weight_per_level", 1.0, 0.01, 1000.0);
            weightMaxReduction = BUILDER
                    .comment("Maximum weight reduction as decimal (e.g. 1.0 = 100% max)")
                    .defineInRange("weight_max_reduction", 1.0, 0.01, 100.0);
            BUILDER.pop();
        }
    }

    public static class GameRulesCategory {
        public final ModConfigSpec.BooleanValue startWithUpgrades;

        GameRulesCategory() {
            BUILDER.push("game_rules");
            startWithUpgrades = BUILDER
                    .comment("When enabled, players receive tacz guns with all upgrades at level 1 by default")
                    .define("start_with_upgrades", false);
            BUILDER.pop();
        }
    }

    public static class AutoRulesCategory {
        public final ModConfigSpec.BooleanValue autoDummyAmmo;

        AutoRulesCategory() {
            BUILDER.push("auto_rules");
            autoDummyAmmo = BUILDER
                    .comment("When enabled, automatically applies dummy ammo upgrades to new guns")
                    .define("auto_dummy_ammo", false);
            BUILDER.pop();
        }
    }
}
