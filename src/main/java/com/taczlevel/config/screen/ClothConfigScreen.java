package com.taczlevel.config.screen;

import com.taczlevel.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigScreen {

    public static Screen buildScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.taczlevel.title"))
                .setSavingRunnable(ClothConfigScreen::save);
        ConfigEntryBuilder eb = builder.entryBuilder();

        buildStats(builder, eb);
        buildXp(builder, eb);
        buildStation(builder, eb);
        buildSound(builder, eb);
        buildOptionNotifications(builder, eb);

        return builder.build();
    }

    private static void buildStats(ConfigBuilder builder, ConfigEntryBuilder eb) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("config.taczlevel.section.stats"));

        cat.addEntry(eb.startIntField(
                        Component.translatable("config.taczlevel.stats.max_level"),
                        ModConfig.STATS.maxLevel.get())
                .setDefaultValue(100)
                .setTooltip(Component.translatable("config.taczlevel.stats.max_level.tooltip"))
                .setSaveConsumer(v -> ModConfig.STATS.maxLevel.set(v))
                .build());

        cat.addEntry(eb.startIntField(
                        Component.translatable("config.taczlevel.stats.max_fire_rate_level"),
                        ModConfig.STATS.maxFireRateLevel.get())
                .setDefaultValue(100)
                .setTooltip(Component.translatable("config.taczlevel.stats.max_fire_rate_level.tooltip"))
                .setSaveConsumer(v -> ModConfig.STATS.maxFireRateLevel.set(v))
                .build());

        addDoubleEntry(cat, eb, "config.taczlevel.stats.reload_per_level",
                ModConfig.STATS.reloadPerLevel.get(), 1.0, v -> ModConfig.STATS.reloadPerLevel.set(v));
        addDoubleEntry(cat, eb, "config.taczlevel.stats.recoil_per_level",
                ModConfig.STATS.recoilPerLevel.get(), 1.0, v -> ModConfig.STATS.recoilPerLevel.set(v));
        addDoubleEntry(cat, eb, "config.taczlevel.stats.pen_per_level",
                ModConfig.STATS.penPerLevel.get(), 1.0, v -> ModConfig.STATS.penPerLevel.set(v));
        addDoubleEntry(cat, eb, "config.taczlevel.stats.fire_rate_per_level",
                ModConfig.STATS.fireRatePerLevel.get(), 2.0, v -> ModConfig.STATS.fireRatePerLevel.set(v));

        addDoubleEntry(cat, eb, "config.taczlevel.stats.reload_max_bonus",
                ModConfig.STATS.reloadMaxBonus.get(), 1.0, v -> ModConfig.STATS.reloadMaxBonus.set(v));
        addDoubleEntry(cat, eb, "config.taczlevel.stats.recoil_max_bonus",
                ModConfig.STATS.recoilMaxBonus.get(), 1.0, v -> ModConfig.STATS.recoilMaxBonus.set(v));
        addDoubleEntry(cat, eb, "config.taczlevel.stats.pen_max_bonus",
                ModConfig.STATS.penMaxBonus.get(), 1.0, v -> ModConfig.STATS.penMaxBonus.set(v));
        addDoubleEntry(cat, eb, "config.taczlevel.stats.fire_rate_max_bonus",
                ModConfig.STATS.fireRateMaxBonus.get(), 5.0, v -> ModConfig.STATS.fireRateMaxBonus.set(v));
    }

    private static void buildXp(ConfigBuilder builder, ConfigEntryBuilder eb) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("config.taczlevel.section.xp"));

        addIntEntry(cat, eb, "config.taczlevel.xp.base_exp", ModConfig.XP.baseExp.get(), 25,
                v -> ModConfig.XP.baseExp.set(v));
        addIntEntry(cat, eb, "config.taczlevel.xp.exp_per_level", ModConfig.XP.expPerLevel.get(), 25,
                v -> ModConfig.XP.expPerLevel.set(v));
        addDoubleEntry(cat, eb, "config.taczlevel.xp.damage_exp_multiplier",
                ModConfig.XP.damageExpMultiplier.get(), 0.2, v -> ModConfig.XP.damageExpMultiplier.set(v));
        addIntEntry(cat, eb, "config.taczlevel.xp.dragon_exp", ModConfig.XP.dragonExp.get(), 100,
                v -> ModConfig.XP.dragonExp.set(v));
        addIntEntry(cat, eb, "config.taczlevel.xp.wither_exp", ModConfig.XP.witherExp.get(), 80,
                v -> ModConfig.XP.witherExp.set(v));
        addIntEntry(cat, eb, "config.taczlevel.xp.boss_exp", ModConfig.XP.bossExp.get(), 50,
                v -> ModConfig.XP.bossExp.set(v));
        addIntEntry(cat, eb, "config.taczlevel.xp.elite_exp", ModConfig.XP.eliteExp.get(), 25,
                v -> ModConfig.XP.eliteExp.set(v));
        addIntEntry(cat, eb, "config.taczlevel.xp.normal_exp", ModConfig.XP.normalExp.get(), 10,
                v -> ModConfig.XP.normalExp.set(v));
        addIntEntry(cat, eb, "config.taczlevel.xp.neutral_exp", ModConfig.XP.neutralExp.get(), 8,
                v -> ModConfig.XP.neutralExp.set(v));
        addIntEntry(cat, eb, "config.taczlevel.xp.animal_exp", ModConfig.XP.animalExp.get(), 3,
                v -> ModConfig.XP.animalExp.set(v));
        addIntEntry(cat, eb, "config.taczlevel.xp.other_exp", ModConfig.XP.otherExp.get(), 8,
                v -> ModConfig.XP.otherExp.set(v));
    }

    private static void buildStation(ConfigBuilder builder, ConfigEntryBuilder eb) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("config.taczlevel.section.station"));

        cat.addEntry(eb.startStrField(
                        Component.translatable("config.taczlevel.station.activation_item"),
                        ModConfig.STATION.activationItem.get())
                .setDefaultValue("minecraft:nether_star")
                .setTooltip(Component.translatable("config.taczlevel.station.activation_item.tooltip"))
                .setSaveConsumer(v -> ModConfig.STATION.activationItem.set(v))
                .build());

        addIntEntry(cat, eb, "config.taczlevel.station.activation_count",
                ModConfig.STATION.activationCount.get(), 1, v -> ModConfig.STATION.activationCount.set(v));
    }

    private static void buildSound(ConfigBuilder builder, ConfigEntryBuilder eb) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("config.taczlevel.section.sound"));

        cat.addEntry(eb.startBooleanToggle(
                        Component.translatable("config.taczlevel.sound.enabled"),
                        ModConfig.SOUND.enabled.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.taczlevel.sound.enabled.tooltip"))
                .setSaveConsumer(v -> ModConfig.SOUND.enabled.set(v))
                .build());

        addDoubleEntry(cat, eb, "config.taczlevel.sound.volume",
                ModConfig.SOUND.volume.get(), 1.0, v -> ModConfig.SOUND.volume.set(v));
        addDoubleEntry(cat, eb, "config.taczlevel.sound.pitch",
                ModConfig.SOUND.pitch.get(), 1.0, v -> ModConfig.SOUND.pitch.set(v));
    }

    private static void buildOptionNotifications(ConfigBuilder builder, ConfigEntryBuilder eb) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("config.taczlevel.section.notification_options"));

        String[] optionKeys = {"reload", "recoil", "penetration", "fire_rate"};
        for (int i = 0; i < 4; i++) {
            String base = "config.taczlevel.notification_options." + optionKeys[i];
            int idx = i;

            cat.addEntry(eb.startStrField(
                            Component.translatable(base + ".position"),
                            ModConfig.OPTION_NOTIFICATION.positions[idx].get())
                    .setDefaultValue("actionbar")
                    .setTooltip(Component.translatable(base + ".position.tooltip"))
                    .setSaveConsumer(v -> ModConfig.OPTION_NOTIFICATION.positions[idx].set(v))
                    .build());

            cat.addEntry(eb.startDoubleField(
                            Component.translatable(base + ".size"),
                            ModConfig.OPTION_NOTIFICATION.sizes[idx].get())
                    .setDefaultValue(1.0)
                    .setTooltip(Component.translatable(base + ".size.tooltip"))
                    .setSaveConsumer(v -> ModConfig.OPTION_NOTIFICATION.sizes[idx].set(v))
                    .build());
        }
    }

    private static void addIntEntry(ConfigCategory cat, ConfigEntryBuilder eb,
                                    String key, int value, int def,
                                    java.util.function.Consumer<Integer> save) {
        cat.addEntry(eb.startIntField(Component.translatable(key), value)
                .setDefaultValue(def)
                .setTooltip(Component.translatable(key + ".tooltip"))
                .setSaveConsumer(save)
                .build());
    }

    private static void addDoubleEntry(ConfigCategory cat, ConfigEntryBuilder eb,
                                       String key, double value, double def,
                                       java.util.function.Consumer<Double> save) {
        cat.addEntry(eb.startDoubleField(Component.translatable(key), value)
                .setDefaultValue(def)
                .setTooltip(Component.translatable(key + ".tooltip"))
                .setSaveConsumer(save)
                .build());
    }

    private static void save() {
        ModConfig.SPEC.save();
    }
}
