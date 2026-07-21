package com.taczlevel.event;

import com.taczlevel.attribute.ModAttributes;
import com.taczlevel.config.ModConfig;
import com.taczlevel.data.GunLevelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class TAACompatHandler {

    private static final boolean TAA_LOADED = ModList.get().isLoaded("taa");

    private static final ResourceLocation RELOAD_ID = ResourceLocation.fromNamespaceAndPath("taczlevel", "reload_upgrade");
    private static final ResourceLocation RECOIL_ID = ResourceLocation.fromNamespaceAndPath("taczlevel", "recoil_upgrade");
    private static final ResourceLocation PEN_ID = ResourceLocation.fromNamespaceAndPath("taczlevel", "pen_upgrade");
    private static final ResourceLocation FIRE_RATE_ID = ResourceLocation.fromNamespaceAndPath("taczlevel", "fire_rate_upgrade");
    private static final ResourceLocation WEIGHT_ID = ResourceLocation.fromNamespaceAndPath("taczlevel", "weight_upgrade");

    private Holder<Attribute> reloadAttr;
    private Holder<Attribute> recoilAttr;
    private Holder<Attribute> penAttr;
    private Holder<Attribute> fireRateAttr;
    private Holder<Attribute> weightAttr;

    public TAACompatHandler() {
        reloadAttr = ModAttributes.RELOAD_TIME;
        recoilAttr = ModAttributes.RECOIL;
        penAttr = ModAttributes.ARMOR_IGNORE;
        fireRateAttr = ModAttributes.ROUNDS_PER_MINUTE;
        weightAttr = ModAttributes.WEIGHT;
        if (TAA_LOADED) {
            Holder<Attribute> taaReload = getTaaAttribute("reload_time");
            Holder<Attribute> taaRecoil = getTaaAttribute("recoil");
            Holder<Attribute> taaPen = getTaaAttribute("armor_ignore");
            Holder<Attribute> taaFireRate = getTaaAttribute("rounds_per_minute");
            Holder<Attribute> taaWeight = getTaaAttribute("weight");
            if (taaReload != null) reloadAttr = taaReload;
            if (taaRecoil != null) recoilAttr = taaRecoil;
            if (taaPen != null) penAttr = taaPen;
            if (taaFireRate != null) fireRateAttr = taaFireRate;
            if (taaWeight != null) weightAttr = taaWeight;
        }
    }

    private static Holder<Attribute> getTaaAttribute(String path) {
        return BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.fromNamespaceAndPath("taa", path)).orElse(null);
    }

    @SubscribeEvent
    public void onLivingTick(EntityTickEvent.Post event) {
        if (!ModConfig.useEntityAttributes()) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.level().isClientSide) return;
        if (entity.tickCount % 10 != 0) return;

        ItemStack gun = entity.getMainHandItem();

        if (GunEvents.isTaczGun(gun) && GunLevelManager.hasAnyUpgrade(gun)) {
            applyAll(entity, gun);
        } else {
            removeAttributes(entity);
        }
    }

    private void applyAll(LivingEntity entity, ItemStack gun) {
        double reloadBonus = GunLevelManager.getReloadSpeedBonus(gun);
        if (reloadBonus > 0) {
            double factor = 1.0 / (1.0 + reloadBonus);
            setAttributeModifier(entity, reloadAttr, RELOAD_ID, factor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double recoilBonus = GunLevelManager.getRecoilReduction(gun);
        if (recoilBonus > 0) {
            double factor = 1.0 - recoilBonus;
            setAttributeModifier(entity, recoilAttr, RECOIL_ID, factor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double penBonus = GunLevelManager.getArmorPenetration(gun);
        if (penBonus > 0) {
            double factor = 1.0 + penBonus;
            setAttributeModifier(entity, penAttr, PEN_ID, factor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double fireRateBonus = GunLevelManager.getFireRateBonus(gun);
        if (fireRateBonus > 0) {
            double factor = 1.0 + fireRateBonus;
            setAttributeModifier(entity, fireRateAttr, FIRE_RATE_ID, factor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double weightReduction = GunLevelManager.getWeightReduction(gun);
        if (weightReduction > 0) {
            double factor = 1.0 - weightReduction;
            setAttributeModifier(entity, weightAttr, WEIGHT_ID, factor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    private void removeAttributes(LivingEntity entity) {
        removeModifier(entity, reloadAttr, RELOAD_ID);
        removeModifier(entity, recoilAttr, RECOIL_ID);
        removeModifier(entity, penAttr, PEN_ID);
        removeModifier(entity, fireRateAttr, FIRE_RATE_ID);
        removeModifier(entity, weightAttr, WEIGHT_ID);
    }

    private void setAttributeModifier(LivingEntity entity, Holder<Attribute> attr, ResourceLocation id, double value, AttributeModifier.Operation operation) {
        if (attr == null) return;
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance == null) return;
        if (instance.getModifier(id) != null) {
            instance.removeModifier(id);
        }
        instance.addTransientModifier(new AttributeModifier(id, value - 1.0, operation));
    }

    private void removeModifier(LivingEntity entity, Holder<Attribute> attr, ResourceLocation id) {
        if (attr == null) return;
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance == null) return;
        instance.removeModifier(id);
    }
}
