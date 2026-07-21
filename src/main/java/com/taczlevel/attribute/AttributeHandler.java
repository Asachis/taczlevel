package com.taczlevel.attribute;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.taczlevel.data.GunLevelManager;
import com.taczlevel.event.GunEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

public class AttributeHandler {

    private static final long TICK_MS = 50L;
    private static final int CHECK_INTERVAL = 10;

    public static void registerAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(type -> {
            event.add(type, ModAttributes.ADS_TIME);
            event.add(type, ModAttributes.AMMO_SPEED);
            event.add(type, ModAttributes.ARMOR_IGNORE);
            event.add(type, ModAttributes.EFFECTIVE_RANGE);
            event.add(type, ModAttributes.EXPLOSION_RADIUS);
            event.add(type, ModAttributes.EXPLOSION_DAMAGE);
            event.add(type, ModAttributes.EXPLOSION_KNOCKBACK);
            event.add(type, ModAttributes.EXPLOSION_DESTROY_BLOCK);
            event.add(type, ModAttributes.EXPLOSION_DELAY);
            event.add(type, ModAttributes.EXPLOSION_ENABLED);
            event.add(type, ModAttributes.MOVE_SPEED);
            event.add(type, ModAttributes.HEADSHOT_MULTIPLIER);
            event.add(type, ModAttributes.IGNITE);
            event.add(type, ModAttributes.INACCURACY);
            event.add(type, ModAttributes.INACCURACY_STAND);
            event.add(type, ModAttributes.INACCURACY_MOVE);
            event.add(type, ModAttributes.INACCURACY_SNEAK);
            event.add(type, ModAttributes.INACCURACY_LIE);
            event.add(type, ModAttributes.INACCURACY_AIM);
            event.add(type, ModAttributes.KNOCKBACK);
            event.add(type, ModAttributes.PIERCE);
            event.add(type, ModAttributes.RECOIL);
            event.add(type, ModAttributes.RECOIL_PITCH);
            event.add(type, ModAttributes.RECOIL_YAW);
            event.add(type, ModAttributes.ROUNDS_PER_MINUTE);
            event.add(type, ModAttributes.SILENCE);
            event.add(type, ModAttributes.WEIGHT);
            event.add(type, ModAttributes.BULLET_GUNDAMAGE);
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_PISTOL);
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_RIFLE);
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_SHOTGUN);
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_SNIPER);
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_SMG);
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_LMG);
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_LAUNCHER);
            event.add(type, ModAttributes.BULLET_COUNT);
            event.add(type, ModAttributes.MAGAZINE_CAPACITY);
            event.add(type, ModAttributes.RELOAD_TIME);
            event.add(type, ModAttributes.MELEE_DAMAGE);
            event.add(type, ModAttributes.MELEE_DISTANCE);
        });
    }

    @SubscribeEvent
    public void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.tickCount % CHECK_INTERVAL != 0) return;

        ItemStack gun = entity.getMainHandItem();
        if (!GunEvents.isTaczGun(gun) || !GunLevelManager.hasAnyUpgrade(gun)) return;

        IGunOperator operator = IGunOperator.fromLivingEntity(entity);
        if (operator == null) return;

        applyReloadSpeedBonus(operator, gun);
        applyCacheBonuses(operator, gun);
    }

    private void applyReloadSpeedBonus(IGunOperator operator, ItemStack gun) {
        double reloadBonus = GunLevelManager.getReloadSpeedBonus(gun);
        if (reloadBonus > 0 && operator.getDataHolder().reloadTimestamp > 0) {
            operator.getDataHolder().reloadTimestamp -= (long) (CHECK_INTERVAL * TICK_MS * reloadBonus);
        }
    }

    private void applyCacheBonuses(IGunOperator operator, ItemStack gun) {
        IGun iGun = IGun.getIGunOrNull(gun);
        if (iGun == null) return;

        ResourceLocation gunId = iGun.getGunId(gun);
        TimelessAPI.getCommonGunIndex(gunId).ifPresent(index -> {
            AttachmentCacheProperty cache = new AttachmentCacheProperty();
            cache.eval(gun, index.getGunData());

            applyPenetrationBonus(cache, gun);
            applyFireRateBonus(cache, gun);
            applyRecoilReduction(cache, gun);
            applyWeightReduction(cache, gun);

            operator.updateCacheProperty(cache);
        });
    }

    private void applyPenetrationBonus(AttachmentCacheProperty cache, ItemStack gun) {
        double penBonus = GunLevelManager.getArmorPenetration(gun);
        if (penBonus <= 0) return;

        Float current = cache.getCache(GunProperties.ARMOR_IGNORE);
        float base = current != null ? current : 0.0f;
        cache.setCache(GunProperties.ARMOR_IGNORE, Math.min(1.0f, base + (float) penBonus));
    }

    private void applyFireRateBonus(AttachmentCacheProperty cache, ItemStack gun) {
        double fireRateBonus = GunLevelManager.getFireRateBonus(gun);
        if (fireRateBonus <= 0) return;

        Integer current = cache.getCache(GunProperties.ROUNDS_PER_MINUTE);
        int base = current != null ? current : 1;
        int modified = (int) Math.round(base * (1.0 + fireRateBonus));
        if (modified > base) {
            cache.setCache(GunProperties.ROUNDS_PER_MINUTE, modified);
        }
    }

    private void applyRecoilReduction(AttachmentCacheProperty cache, ItemStack gun) {
        double recoilReduction = GunLevelManager.getRecoilReduction(gun);
        if (recoilReduction <= 0) return;

        ParameterizedCachePair<Float, Float> recoil = cache.getCache(GunProperties.RECOIL);
        if (recoil == null) return;

        float pitchDefault = recoil.left().getDefaultValue();
        float yawDefault = recoil.right().getDefaultValue();

        Modifier mod = new Modifier();
        mod.setPercent(-recoilReduction);
        List<Modifier> modList = List.of(mod);

        cache.setCache(GunProperties.RECOIL, ParameterizedCachePair.of(modList, modList, pitchDefault, yawDefault));
    }

    private void applyWeightReduction(AttachmentCacheProperty cache, ItemStack gun) {
        double reduction = GunLevelManager.getWeightReduction(gun);
        if (reduction <= 0) return;

        Float current = cache.getCache(com.tacz.guns.api.GunProperties.WEIGHT);
        if (current == null) return;
        float reduced = current * (1.0f - (float) reduction);
        cache.setCache(com.tacz.guns.api.GunProperties.WEIGHT, Math.max(reduced, 0.01f));
    }
}
