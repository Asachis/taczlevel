package com.taczlevel.data;

import com.taczlevel.config.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class GunLevelManager {
    private static final String TAG = "taczlevel";
    private static final String RELOAD_LEVEL_TAG = "reload_level";
    private static final String RECOIL_LEVEL_TAG = "recoil_level";
    private static final String PEN_LEVEL_TAG = "pen_level";
    private static final String FIRE_RATE_LEVEL_TAG = "fire_rate_level";
    private static final String DUMMY_AMMO_LEVEL_TAG = "dummy_ammo_level";
    private static final String EXP_TAG = "exp";
    private static final String ACTIVE_SLOT_TAG = "active_slot";
    private static final String GATE_UNLOCKED_TAG = "gate_unlocked";

    private static final String[] LEVEL_TAGS = {
        RELOAD_LEVEL_TAG, RECOIL_LEVEL_TAG, PEN_LEVEL_TAG,
        FIRE_RATE_LEVEL_TAG, DUMMY_AMMO_LEVEL_TAG
    };
    private static final int OPTION_COUNT = 5;

    public static int getMaxLevel() {
        return ModConfig.STATS.maxLevel.get();
    }

    public static int getMaxFireRateLevel() {
        return ModConfig.STATS.maxFireRateLevel.get();
    }

    private static int getMaxLevelByIndex(int i) {
        if (i < 3) return getMaxLevel();
        if (i == 3) return getMaxFireRateLevel();
        return ModConfig.DUMMY_AMMO.maxLevel.get();
    }

    public static int getGateLevel() {
        return Math.max(1, getMaxLevel() / 2);
    }

    // === Active slot (regular station single-upgrade mode) ===

    public static int getActiveSlot(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG)) {
            CompoundTag data = tag.getCompound(TAG);
            if (data.contains(ACTIVE_SLOT_TAG)) {
                return data.getInt(ACTIVE_SLOT_TAG);
            }
        }
        return -1;
    }

    public static void setActiveSlot(ItemStack stack, int slot) {
        CompoundTag tag = stack.getOrCreateTagElement(TAG);
        tag.putInt(ACTIVE_SLOT_TAG, slot);
    }

    public static void clearActiveSlot(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement(TAG);
        tag.remove(ACTIVE_SLOT_TAG);
    }

    public static boolean isGateUnlocked(ItemStack stack, int optionIndex) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG)) {
            int mask = tag.getCompound(TAG).getInt(GATE_UNLOCKED_TAG);
            return (mask & (1 << optionIndex)) != 0;
        }
        return false;
    }

    public static void unlockGate(ItemStack stack, int optionIndex) {
        CompoundTag tag = stack.getOrCreateTagElement(TAG);
        int mask = tag.getInt(GATE_UNLOCKED_TAG);
        mask |= (1 << optionIndex);
        tag.putInt(GATE_UNLOCKED_TAG, mask);
    }

    public static boolean isAtGateLevel(ItemStack stack, int optionIndex) {
        int lvl = getLevel(stack, optionIndex);
        return lvl >= getGateLevel() && lvl < getMaxLevelForOption(optionIndex);
    }

    // === Multi-upgrade mode (creative station) ===

    public static boolean hasAnyUpgrade(ItemStack stack) {
        for (String tagKey : LEVEL_TAGS) {
            if (getTagInt(stack, tagKey) > 0) return true;
        }
        return false;
    }

    public static int getOverallLevel(ItemStack stack) {
        int max = 0;
        for (String tagKey : LEVEL_TAGS) {
            max = Math.max(max, getTagInt(stack, tagKey));
        }
        return max;
    }

    // === Exp system (creative: shared XP) ===

    public static int getExp(ItemStack stack) {
        return getTagInt(stack, EXP_TAG);
    }

    public static int getExpToNextLevel(int level) {
        return ModConfig.XP.baseExp.get() + ModConfig.XP.expPerLevel.get() * level;
    }

    public static int getExpToNext(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG)) {
            CompoundTag data = tag.getCompound(TAG);
            int lowestActive = -1;
            for (int i = 0; i < OPTION_COUNT; i++) {
                int lvl = data.getInt(LEVEL_TAGS[i]);
                int max = getMaxLevelByIndex(i);
                if (lvl > 0 && lvl < max) {
                    if (lowestActive == -1 || lvl < lowestActive) {
                        lowestActive = lvl;
                    }
                }
            }
            if (lowestActive == -1) return 0;
            return getExpToNextLevel(lowestActive);
        }
        return 0;
    }

    public static int addExp(ItemStack stack, int amount) {
        CompoundTag tag = stack.getOrCreateTagElement(TAG);

        // Gate check: if gun has an active slot and the active upgrade is at gate level
        // with gate not unlocked, block all XP gain.
        int activeSlot = getActiveSlot(stack);
        if (activeSlot >= 0 && activeSlot < OPTION_COUNT) {
            int activeLvl = tag.getInt(LEVEL_TAGS[activeSlot]);
            int activeMax = getMaxLevelByIndex(activeSlot);
            if (activeLvl > 0 && activeLvl < activeMax && activeLvl >= getGateLevel() && !isGateUnlocked(stack, activeSlot)) {
                return 0;
            }
        }

        int currentExp = tag.getInt(EXP_TAG);

        int[] levels = new int[OPTION_COUNT];
        boolean anyActiveNotMaxed = false;
        for (int i = 0; i < OPTION_COUNT; i++) {
            levels[i] = tag.getInt(LEVEL_TAGS[i]);
            int max = getMaxLevelByIndex(i);
            if (levels[i] > 0 && levels[i] < max) {
                anyActiveNotMaxed = true;
            }
        }
        if (!anyActiveNotMaxed) return 0;

        int lowestActiveLevel = -1;
        for (int i = 0; i < OPTION_COUNT; i++) {
            int max = getMaxLevelByIndex(i);
            if (levels[i] > 0 && levels[i] < max) {
                if (lowestActiveLevel == -1 || levels[i] < lowestActiveLevel) {
                    lowestActiveLevel = levels[i];
                }
            }
        }

        currentExp += amount;
        int needed = getExpToNextLevel(lowestActiveLevel);

        if (currentExp >= needed) {
            currentExp -= needed;
            int bitmask = 0;
            for (int i = 0; i < OPTION_COUNT; i++) {
                int max = getMaxLevelByIndex(i);
                if (levels[i] > 0 && levels[i] < max) {
                    levels[i]++;
                    tag.putInt(LEVEL_TAGS[i], levels[i]);
                    bitmask |= (1 << i);
                }
            }
            tag.putInt(EXP_TAG, currentExp);
            return bitmask;
        } else {
            tag.putInt(EXP_TAG, currentExp);
            return 0;
        }
    }

    // === Single-upgrade exp (regular station) ===

    public static int addExpToActive(ItemStack stack, int amount) {
        int slot = getActiveSlot(stack);
        if (slot < 0 || slot >= OPTION_COUNT) return 0;

        CompoundTag tag = stack.getOrCreateTagElement(TAG);
        int currentExp = tag.getInt(EXP_TAG);
        int lvl = tag.getInt(LEVEL_TAGS[slot]);
        int max = getMaxLevelByIndex(slot);
        if (lvl <= 0 || lvl >= max) return 0;

        // Gate: at level >= half max, need gate unlocked
        if (lvl >= getGateLevel() && !isGateUnlocked(stack, slot)) return 0;

        currentExp += amount;
        int needed = getExpToNextLevel(lvl);

        if (currentExp >= needed) {
            currentExp -= needed;
            lvl++;
            tag.putInt(LEVEL_TAGS[slot], lvl);
            tag.putInt(EXP_TAG, currentExp);

            // If reached max, clear active slot
            if (lvl >= max) {
                clearActiveSlot(stack);
            }
            return (1 << slot);
        } else {
            tag.putInt(EXP_TAG, currentExp);
            return 0;
        }
    }

    public static int getExpToNextActive(ItemStack stack) {
        int slot = getActiveSlot(stack);
        if (slot < 0 || slot >= OPTION_COUNT) return 0;

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG)) return 0;

        CompoundTag data = tag.getCompound(TAG);
        int lvl = data.getInt(LEVEL_TAGS[slot]);
        int max = getMaxLevelByIndex(slot);
        if (lvl <= 0 || lvl >= max) return 0;

        // Gate check
        if (lvl >= getGateLevel() && !isGateUnlocked(stack, slot)) return 0;

        return getExpToNextLevel(lvl);
    }

    // === Reload level ===

    public static int getReloadLevel(ItemStack stack) {
        return getTagInt(stack, RELOAD_LEVEL_TAG);
    }

    public static boolean upgradeReload(ItemStack stack) {
        return incrementTag(stack, RELOAD_LEVEL_TAG, getMaxLevel());
    }

    public static void setReloadLevel(ItemStack stack, int level) {
        setTagInt(stack, RELOAD_LEVEL_TAG, Math.min(level, getMaxLevel()));
    }

    public static double getReloadSpeedBonus(ItemStack stack) {
        return Math.min(getReloadLevel(stack) * (ModConfig.STATS.reloadPerLevel.get() / 100.0), ModConfig.STATS.reloadMaxBonus.get());
    }

    // === Recoil reduction ===

    public static int getRecoilLevel(ItemStack stack) {
        return getTagInt(stack, RECOIL_LEVEL_TAG);
    }

    public static boolean upgradeRecoil(ItemStack stack) {
        return incrementTag(stack, RECOIL_LEVEL_TAG, getMaxLevel());
    }

    public static void setRecoilLevel(ItemStack stack, int level) {
        setTagInt(stack, RECOIL_LEVEL_TAG, Math.min(level, getMaxLevel()));
    }

    public static double getRecoilReduction(ItemStack stack) {
        return Math.min(getRecoilLevel(stack) * (ModConfig.STATS.recoilPerLevel.get() / 100.0), ModConfig.STATS.recoilMaxBonus.get());
    }

    // === Armor penetration ===

    public static int getPenLevel(ItemStack stack) {
        return getTagInt(stack, PEN_LEVEL_TAG);
    }

    public static boolean upgradePen(ItemStack stack) {
        return incrementTag(stack, PEN_LEVEL_TAG, getMaxLevel());
    }

    public static void setPenLevel(ItemStack stack, int level) {
        setTagInt(stack, PEN_LEVEL_TAG, Math.min(level, getMaxLevel()));
    }

    public static double getArmorPenetration(ItemStack stack) {
        return Math.min(getPenLevel(stack) * (ModConfig.STATS.penPerLevel.get() / 100.0), ModConfig.STATS.penMaxBonus.get());
    }

    // === Fire rate ===

    public static int getFireRateLevel(ItemStack stack) {
        return getTagInt(stack, FIRE_RATE_LEVEL_TAG);
    }

    public static boolean upgradeFireRate(ItemStack stack) {
        return incrementTag(stack, FIRE_RATE_LEVEL_TAG, getMaxFireRateLevel());
    }

    public static void setFireRateLevel(ItemStack stack, int level) {
        setTagInt(stack, FIRE_RATE_LEVEL_TAG, Math.min(level, getMaxFireRateLevel()));
    }

    public static double getFireRateBonus(ItemStack stack) {
        return Math.min(getFireRateLevel(stack) * (ModConfig.STATS.fireRatePerLevel.get() / 100.0), ModConfig.STATS.fireRateMaxBonus.get());
    }

    // === Virtual ammo ===

    public static int getDummyAmmoLevel(ItemStack stack) {
        return getTagInt(stack, DUMMY_AMMO_LEVEL_TAG);
    }

    public static boolean upgradeDummyAmmo(ItemStack stack) {
        return incrementTag(stack, DUMMY_AMMO_LEVEL_TAG, ModConfig.DUMMY_AMMO.maxLevel.get());
    }

    public static void setDummyAmmoLevel(ItemStack stack, int level) {
        setTagInt(stack, DUMMY_AMMO_LEVEL_TAG, Math.min(level, ModConfig.DUMMY_AMMO.maxLevel.get()));
    }

    public static int getDummyAmmoMaxPool(ItemStack stack) {
        int level = getDummyAmmoLevel(stack);
        if (level <= 0) return 0;
        return ModConfig.DUMMY_AMMO.getMaxPool(level);
    }

    public static void initDefaultUpgrades(ItemStack stack) {
        if (hasAnyUpgrade(stack)) return;
        setTagInt(stack, RELOAD_LEVEL_TAG, 1);
        setTagInt(stack, RECOIL_LEVEL_TAG, 1);
        setTagInt(stack, PEN_LEVEL_TAG, 1);
        setTagInt(stack, FIRE_RATE_LEVEL_TAG, 1);
        clearActiveSlot(stack);
    }

    // === Activation checks ===

    public static boolean isActivated(ItemStack stack, int optionIndex) {
        return switch (optionIndex) {
            case 0 -> getReloadLevel(stack) > 0;
            case 1 -> getRecoilLevel(stack) > 0;
            case 2 -> getPenLevel(stack) > 0;
            case 3 -> getFireRateLevel(stack) > 0;
            case 4 -> getDummyAmmoLevel(stack) > 0;
            default -> false;
        };
    }

    public static int getLevel(ItemStack stack, int optionIndex) {
        return switch (optionIndex) {
            case 0 -> getReloadLevel(stack);
            case 1 -> getRecoilLevel(stack);
            case 2 -> getPenLevel(stack);
            case 3 -> getFireRateLevel(stack);
            case 4 -> getDummyAmmoLevel(stack);
            default -> 0;
        };
    }

    public static int getMaxLevelForOption(int optionIndex) {
        return switch (optionIndex) {
            case 0, 1, 2 -> getMaxLevel();
            case 3 -> getMaxFireRateLevel();
            case 4 -> ModConfig.DUMMY_AMMO.maxLevel.get();
            default -> 0;
        };
    }

    public static String getStatNameKey(int optionIndex) {
        return switch (optionIndex) {
            case 0 -> "gui.taczlevel.reload_speed";
            case 1 -> "gui.taczlevel.recoil";
            case 2 -> "gui.taczlevel.armor_pen";
            case 3 -> "gui.taczlevel.fire_rate";
            case 4 -> "gui.taczlevel.dummy_ammo";
            default -> "";
        };
    }

    public static String getOptionNameKey(int optionIndex) {
        return switch (optionIndex) {
            case 0 -> "gui.taczlevel.option_name_reload";
            case 1 -> "gui.taczlevel.option_name_recoil";
            case 2 -> "gui.taczlevel.option_name_pen";
            case 3 -> "gui.taczlevel.option_name_fire_rate";
            case 4 -> "gui.taczlevel.option_name_dummy_ammo";
            default -> "";
        };
    }

    public static double getStatPercentage(int optionIndex, int level) {
        return switch (optionIndex) {
            case 0 -> level * ModConfig.STATS.reloadPerLevel.get();
            case 1 -> level * ModConfig.STATS.recoilPerLevel.get();
            case 2 -> level * ModConfig.STATS.penPerLevel.get();
            case 3 -> level * ModConfig.STATS.fireRatePerLevel.get();
            case 4 -> ModConfig.DUMMY_AMMO.getMaxPool(level);
            default -> 0.0;
        };
    }

    // === Internal helpers ===

    private static int getTagInt(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG)) {
            return tag.getCompound(TAG).getInt(key);
        }
        return 0;
    }

    private static void setTagInt(ItemStack stack, String key, int value) {
        CompoundTag tag = stack.getOrCreateTagElement(TAG);
        tag.putInt(key, value);
    }

    private static boolean incrementTag(ItemStack stack, String key, int max) {
        CompoundTag tag = stack.getOrCreateTagElement(TAG);
        int level = tag.getInt(key);
        if (level >= max) return false;
        tag.putInt(key, level + 1);
        return true;
    }
}
