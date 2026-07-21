package com.taczlevel.event;

import com.taczlevel.attribute.ModAttributes;
import com.taczlevel.data.GunLevelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
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
        if (TAA_LOADED) {
            reloadAttr = getTaaAttribute("reload_time");
            recoilAttr = getTaaAttribute("recoil");
            penAttr = getTaaAttribute("armor_ignore");
            fireRateAttr = getTaaAttribute("rounds_per_minute");
        } else {
            reloadAttr = ModAttributes.RELOAD_TIME;
            recoilAttr = ModAttributes.RECOIL;
            penAttr = ModAttributes.ARMOR_IGNORE;
            fireRateAttr = ModAttributes.ROUNDS_PER_MINUTE;
        }
        weightAttr = ModAttributes.WEIGHT;
    }

    private static Holder<Attribute> getTaaAttribute(String path) {
        return BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.fromNamespaceAndPath("taa", path)).orElse(null);
    }

    @SubscribeEvent
    public void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (player.tickCount % 10 != 0) return;

        ItemStack gun = player.getMainHandItem();

        if (GunEvents.isTaczGun(gun) && GunLevelManager.hasAnyUpgrade(gun)) {
            applyAll(player, gun);
        } else {
            removeAttributes(player);
        }
    }

    private void applyAll(Player player, ItemStack gun) {
        double reloadBonus = GunLevelManager.getReloadSpeedBonus(gun);
        if (reloadBonus > 0) {
            double factor = 1.0 / (1.0 + reloadBonus);
            setAttributeModifier(player, reloadAttr, RELOAD_ID, factor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double recoilBonus = GunLevelManager.getRecoilReduction(gun);
        if (recoilBonus > 0) {
            double factor = 1.0 - recoilBonus;
            setAttributeModifier(player, recoilAttr, RECOIL_ID, factor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double penBonus = GunLevelManager.getArmorPenetration(gun);
        if (penBonus > 0) {
            double factor = 1.0 + penBonus;
            setAttributeModifier(player, penAttr, PEN_ID, factor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double fireRateBonus = GunLevelManager.getFireRateBonus(gun);
        if (fireRateBonus > 0) {
            double factor = 1.0 + fireRateBonus;
            setAttributeModifier(player, fireRateAttr, FIRE_RATE_ID, factor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double weightReduction = GunLevelManager.getWeightReduction(gun);
        if (weightReduction > 0) {
            double factor = 1.0 - weightReduction;
            setAttributeModifier(player, weightAttr, WEIGHT_ID, factor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    private void removeAttributes(Player player) {
        removeModifier(player, reloadAttr, RELOAD_ID);
        removeModifier(player, recoilAttr, RECOIL_ID);
        removeModifier(player, penAttr, PEN_ID);
        removeModifier(player, fireRateAttr, FIRE_RATE_ID);
        removeModifier(player, weightAttr, WEIGHT_ID);
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
