package com.taczlevel.event;

import com.taczlevel.attribute.ModAttributes;
import com.taczlevel.config.ModConfig;
import com.taczlevel.data.GunLevelManager;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

@SuppressWarnings({"deprecation", "removal"})
public class TAACompatHandler {

    private static final ResourceLocation RELOAD_ID = new ResourceLocation("taczlevel", "reload_upgrade");
    private static final ResourceLocation RECOIL_ID = new ResourceLocation("taczlevel", "recoil_upgrade");
    private static final ResourceLocation PEN_ID = new ResourceLocation("taczlevel", "pen_upgrade");
    private static final ResourceLocation FIRE_RATE_ID = new ResourceLocation("taczlevel", "fire_rate_upgrade");
    private static final ResourceLocation WEIGHT_ID = new ResourceLocation("taczlevel", "weight_upgrade");
    private static final ResourceLocation MOVE_SPEED_ID = new ResourceLocation("taczlevel", "move_speed_upgrade");

    private Attribute reloadAttr;
    private Attribute recoilAttr;
    private Attribute penAttr;
    private Attribute fireRateAttr;
    private Attribute weightAttr;
    private Attribute moveSpeedAttr;
    private boolean initialized;

    private static Attribute getTaaAttribute(String path) {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("taa", path));
    }

    private void init() {
        if (initialized) return;
        initialized = true;
        reloadAttr = pickAttribute("reload_time", ModAttributes.RELOAD_TIME.get());
        recoilAttr = pickAttribute("recoil", ModAttributes.RECOIL.get());
        penAttr = pickAttribute("armor_ignore", ModAttributes.ARMOR_IGNORE.get());
        fireRateAttr = pickAttribute("rounds_per_minute", ModAttributes.ROUNDS_PER_MINUTE.get());
        weightAttr = pickAttribute("weight", ModAttributes.WEIGHT.get());
        moveSpeedAttr = pickAttribute("move_speed", ModAttributes.MOVE_SPEED.get());
    }

    private static Attribute pickAttribute(String taaPath, Attribute fallback) {
        Attribute taa = getTaaAttribute(taaPath);
        return taa != null ? taa : fallback;
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!ModConfig.GAME_RULES.useTaaAttributes.get()) return;
        init();
        LivingEntity entity = event.getEntity();
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
            setAttributeModifier(entity, reloadAttr, RELOAD_ID, factor, AttributeModifier.Operation.MULTIPLY_BASE);
        }

        double recoilBonus = GunLevelManager.getRecoilReduction(gun);
        if (recoilBonus > 0) {
            double factor = 1.0 - recoilBonus;
            setAttributeModifier(entity, recoilAttr, RECOIL_ID, factor, AttributeModifier.Operation.MULTIPLY_BASE);
        }

        double penBonus = GunLevelManager.getArmorPenetration(gun);
        if (penBonus > 0) {
            double factor = 1.0 + penBonus;
            setAttributeModifier(entity, penAttr, PEN_ID, factor, AttributeModifier.Operation.MULTIPLY_BASE);
        }

        double fireRateBonus = GunLevelManager.getFireRateBonus(gun);
        if (fireRateBonus > 0) {
            double factor = 1.0 + fireRateBonus;
            setAttributeModifier(entity, fireRateAttr, FIRE_RATE_ID, factor, AttributeModifier.Operation.MULTIPLY_BASE);
        }

        removeModifier(entity, moveSpeedAttr, MOVE_SPEED_ID);

        double weightReduction = GunLevelManager.getWeightReduction(gun);
        if (weightReduction > 0) {
            double factor = 1.0 - weightReduction;
            setAttributeModifier(entity, weightAttr, WEIGHT_ID, factor, AttributeModifier.Operation.MULTIPLY_BASE);

            if (weightReduction > 1.0) {
                double excess = weightReduction - 1.0;
                double bonus = excess * 0.2;
                setAttributeModifier(entity, moveSpeedAttr, MOVE_SPEED_ID, 1.0 + bonus, AttributeModifier.Operation.MULTIPLY_BASE);
            }
        } else {
            removeModifier(entity, weightAttr, WEIGHT_ID);
        }
    }

    private void removeAttributes(LivingEntity entity) {
        removeModifier(entity, reloadAttr, RELOAD_ID);
        removeModifier(entity, recoilAttr, RECOIL_ID);
        removeModifier(entity, penAttr, PEN_ID);
        removeModifier(entity, fireRateAttr, FIRE_RATE_ID);
        removeModifier(entity, weightAttr, WEIGHT_ID);
        removeModifier(entity, moveSpeedAttr, MOVE_SPEED_ID);
    }

    private void setAttributeModifier(LivingEntity entity, Attribute attr, ResourceLocation id, double value, AttributeModifier.Operation operation) {
        if (attr == null) return;
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance == null) return;
        java.util.UUID uuid = java.util.UUID.nameUUIDFromBytes(id.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        if (instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
        instance.addTransientModifier(new AttributeModifier(uuid, id.toString(), value - 1.0, operation));
    }

    private void removeModifier(LivingEntity entity, Attribute attr, ResourceLocation id) {
        if (attr == null) return;
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance == null) return;
        java.util.UUID uuid = java.util.UUID.nameUUIDFromBytes(id.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        instance.removeModifier(uuid);
    }
}
