package com.taczlevel.event;

import com.taczlevel.data.GunLevelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class TAACompatHandler {

    private static final boolean TAA_LOADED = ModList.get().isLoaded("taa");

    private static final UUID RELOAD_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID RECOIL_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
    private static final UUID PEN_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012");
    private static final UUID FIRE_RATE_UUID = UUID.fromString("d4e5f6a7-b8c9-0123-defa-234567890123");

    private static final String MODIFIER_NAME = "TaczLevel Upgrade";

    private final Lazy<Attribute> reloadAttr = Lazy.of(() -> getTaaAttribute("reload_time"));
    private final Lazy<Attribute> recoilAttr = Lazy.of(() -> getTaaAttribute("recoil"));
    private final Lazy<Attribute> penAttr = Lazy.of(() -> getTaaAttribute("armor_ignore"));
    private final Lazy<Attribute> fireRateAttr = Lazy.of(() -> getTaaAttribute("rounds_per_minute"));

    @SuppressWarnings({"deprecation", "removal"})
    private static Attribute getTaaAttribute(String path) {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("taa", path));
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!TAA_LOADED) return;
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getEntity().tickCount % 10 != 0) return;

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
            setAttributeModifier(player, reloadAttr.get(), RELOAD_UUID, factor, AttributeModifier.Operation.MULTIPLY_BASE);
        }

        double recoilBonus = GunLevelManager.getRecoilReduction(gun);
        if (recoilBonus > 0) {
            double factor = 1.0 - recoilBonus;
            setAttributeModifier(player, recoilAttr.get(), RECOIL_UUID, factor, AttributeModifier.Operation.MULTIPLY_BASE);
        }

        double penBonus = GunLevelManager.getArmorPenetration(gun);
        if (penBonus > 0) {
            double factor = 1.0 + penBonus;
            setAttributeModifier(player, penAttr.get(), PEN_UUID, factor, AttributeModifier.Operation.MULTIPLY_BASE);
        }

        double fireRateBonus = GunLevelManager.getFireRateBonus(gun);
        if (fireRateBonus > 0) {
            double factor = 1.0 + fireRateBonus;
            setAttributeModifier(player, fireRateAttr.get(), FIRE_RATE_UUID, factor, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }

    private void removeAttributes(Player player) {
        removeModifier(player, reloadAttr.get(), RELOAD_UUID);
        removeModifier(player, recoilAttr.get(), RECOIL_UUID);
        removeModifier(player, penAttr.get(), PEN_UUID);
        removeModifier(player, fireRateAttr.get(), FIRE_RATE_UUID);
    }

    private void setAttributeModifier(LivingEntity entity, Attribute attr, UUID uuid, double value, AttributeModifier.Operation operation) {
        if (attr == null) return;
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance == null) return;
        if (instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
        instance.addTransientModifier(new AttributeModifier(uuid, MODIFIER_NAME, value - 1.0, operation));
    }

    private void removeModifier(LivingEntity entity, Attribute attr, UUID uuid) {
        if (attr == null) return;
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance == null) return;
        instance.removeModifier(uuid);
    }
}