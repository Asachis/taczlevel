package com.taczlevel.attribute;

import com.taczlevel.TaczLevelMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegisterEvent;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, TaczLevelMod.MODID);

    public static final RegistryObject<Attribute> ADS_TIME = ATTRIBUTES.register("ads_time",
            () -> new RangedAttribute("attribute.name.taczlevel.ads_time", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> AMMO_SPEED = ATTRIBUTES.register("ammo_speed",
            () -> new RangedAttribute("attribute.name.taczlevel.ammo_speed", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> ARMOR_IGNORE = ATTRIBUTES.register("armor_ignore",
            () -> new RangedAttribute("attribute.name.taczlevel.armor_ignore", 1.0D, 0.01D, 999999.0D).setSyncable(true));
    public static final RegistryObject<Attribute> EFFECTIVE_RANGE = ATTRIBUTES.register("effective_range",
            () -> new RangedAttribute("attribute.name.taczlevel.effective_range", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> EXPLOSION_RADIUS = ATTRIBUTES.register("explosion_radius",
            () -> new RangedAttribute("attribute.name.taczlevel.explosion_radius", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> EXPLOSION_DAMAGE = ATTRIBUTES.register("explosion_damage",
            () -> new RangedAttribute("attribute.name.taczlevel.explosion_damage", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> EXPLOSION_KNOCKBACK = ATTRIBUTES.register("explosion_knockback",
            () -> new RangedAttribute("attribute.name.taczlevel.explosion_knockback", 1.0D, 0.01D, 3.0D).setSyncable(true));
    public static final RegistryObject<Attribute> EXPLOSION_DESTROY_BLOCK = ATTRIBUTES.register("explosion_destroy_block",
            () -> new RangedAttribute("attribute.name.taczlevel.explosion_destroy_block", 1.0D, 0.01D, 3.0D).setSyncable(true));
    public static final RegistryObject<Attribute> EXPLOSION_DELAY = ATTRIBUTES.register("explosion_delay",
            () -> new RangedAttribute("attribute.name.taczlevel.explosion_delay", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> EXPLOSION_ENABLED = ATTRIBUTES.register("explosion_enabled",
            () -> new RangedAttribute("attribute.name.taczlevel.explosion_enabled", 1.0D, 0.01D, 3.0D).setSyncable(true));
    public static final RegistryObject<Attribute> MOVE_SPEED = ATTRIBUTES.register("move_speed",
            () -> new RangedAttribute("attribute.name.taczlevel.move_speed", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> HEADSHOT_MULTIPLIER = ATTRIBUTES.register("headshot_multiplier",
            () -> new RangedAttribute("attribute.name.taczlevel.headshot_multiplier", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> IGNITE = ATTRIBUTES.register("ignite",
            () -> new RangedAttribute("attribute.name.taczlevel.ignite", 1.0D, 0.01D, 3.0D).setSyncable(true));
    public static final RegistryObject<Attribute> INACCURACY = ATTRIBUTES.register("inaccuracy",
            () -> new RangedAttribute("attribute.name.taczlevel.inaccuracy", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> INACCURACY_STAND = ATTRIBUTES.register("inaccuracy_stand",
            () -> new RangedAttribute("attribute.name.taczlevel.inaccuracy_stand", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> INACCURACY_MOVE = ATTRIBUTES.register("inaccuracy_move",
            () -> new RangedAttribute("attribute.name.taczlevel.inaccuracy_move", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> INACCURACY_SNEAK = ATTRIBUTES.register("inaccuracy_sneak",
            () -> new RangedAttribute("attribute.name.taczlevel.inaccuracy_sneak", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> INACCURACY_LIE = ATTRIBUTES.register("inaccuracy_lie",
            () -> new RangedAttribute("attribute.name.taczlevel.inaccuracy_lie", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> INACCURACY_AIM = ATTRIBUTES.register("inaccuracy_aim",
            () -> new RangedAttribute("attribute.name.taczlevel.inaccuracy_aim", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> KNOCKBACK = ATTRIBUTES.register("knockback",
            () -> new RangedAttribute("attribute.name.taczlevel.knockback", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> PIERCE = ATTRIBUTES.register("pierce",
            () -> new RangedAttribute("attribute.name.taczlevel.pierce", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> RECOIL = ATTRIBUTES.register("recoil",
            () -> new RangedAttribute("attribute.name.taczlevel.recoil", 1.0D, 0.01D, 999999.0D).setSyncable(true));
    public static final RegistryObject<Attribute> RECOIL_PITCH = ATTRIBUTES.register("recoil_pitch",
            () -> new RangedAttribute("attribute.name.taczlevel.recoil_pitch", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> RECOIL_YAW = ATTRIBUTES.register("recoil_yaw",
            () -> new RangedAttribute("attribute.name.taczlevel.recoil_yaw", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> ROUNDS_PER_MINUTE = ATTRIBUTES.register("rounds_per_minute",
            () -> new RangedAttribute("attribute.name.taczlevel.rounds_per_minute", 1.0D, 0.01D, 999999.0D).setSyncable(true));
    public static final RegistryObject<Attribute> SILENCE = ATTRIBUTES.register("silence",
            () -> new RangedAttribute("attribute.name.taczlevel.silence", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> WEIGHT = ATTRIBUTES.register("weight",
            () -> new RangedAttribute("attribute.name.taczlevel.weight", 1.0D, 0.01D, 999999.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE = ATTRIBUTES.register("bullet_gundamage",
            () -> new RangedAttribute("attribute.name.taczlevel.bullet_gundamage", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_COUNT = ATTRIBUTES.register("bullet_count",
            () -> new RangedAttribute("attribute.name.taczlevel.bullet_count", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> MAGAZINE_CAPACITY = ATTRIBUTES.register("magazine_capacity",
            () -> new RangedAttribute("attribute.name.taczlevel.magazine_capacity", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> RELOAD_TIME = ATTRIBUTES.register("reload_time",
            () -> new RangedAttribute("attribute.name.taczlevel.reload_time", 1.0D, 0.01D, 999999.0D).setSyncable(true));

    public static final RegistryObject<Attribute> MELEE_DAMAGE = ATTRIBUTES.register("melee_damage",
            () -> new RangedAttribute("attribute.name.taczlevel.melee_damage", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> MELEE_DISTANCE = ATTRIBUTES.register("melee_distance",
            () -> new RangedAttribute("attribute.name.taczlevel.melee_distance", 0.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_PISTOL = ATTRIBUTES.register("bullet_gundamage_pistol",
            () -> new RangedAttribute("attribute.name.taczlevel.bullet_gundamage_pistol", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_RIFLE = ATTRIBUTES.register("bullet_gundamage_rifle",
            () -> new RangedAttribute("attribute.name.taczlevel.bullet_gundamage_rifle", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SHOTGUN = ATTRIBUTES.register("bullet_gundamage_shotgun",
            () -> new RangedAttribute("attribute.name.taczlevel.bullet_gundamage_shotgun", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SNIPER = ATTRIBUTES.register("bullet_gundamage_sniper",
            () -> new RangedAttribute("attribute.name.taczlevel.bullet_gundamage_sniper", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SMG = ATTRIBUTES.register("bullet_gundamage_smg",
            () -> new RangedAttribute("attribute.name.taczlevel.bullet_gundamage_smg", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LMG = ATTRIBUTES.register("bullet_gundamage_lmg",
            () -> new RangedAttribute("attribute.name.taczlevel.bullet_gundamage_lmg", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LAUNCHER = ATTRIBUTES.register("bullet_gundamage_launcher",
            () -> new RangedAttribute("attribute.name.taczlevel.bullet_gundamage_launcher", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    private static final String[][] TAA_ATTRS = {
        {"reload_time", "attribute.name.taa.reload_time"},
        {"recoil", "attribute.name.taa.recoil"},
        {"armor_ignore", "attribute.name.taa.armor_ignore"},
        {"rounds_per_minute", "attribute.name.taa.rounds_per_minute"},
        {"weight", "attribute.name.taa.weight"},
        {"move_speed", "attribute.name.taa.move_speed"},
    };

    public static void registerTaaFallbackAttributes(RegisterEvent event) {
        if (ModList.get().isLoaded("taa")) return;
        for (String[] attr : TAA_ATTRS) {
            event.register(Registries.ATTRIBUTE,
                new ResourceLocation("taa", attr[0]),
                () -> new RangedAttribute(attr[1], 1.0D, 0.0D, 999999.0D).setSyncable(true));
        }
    }
}
