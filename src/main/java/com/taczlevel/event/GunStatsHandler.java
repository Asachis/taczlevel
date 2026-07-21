package com.taczlevel.event;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.taczlevel.attribute.ModAttributes;
import com.taczlevel.data.GunLevelManager;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;

public class GunStatsHandler {

    @SubscribeEvent
    public void onAttachmentProperty(AttachmentPropertyEvent event) {
        var gun = event.getGunItem();
        if (!GunEvents.isTaczGun(gun) || !GunLevelManager.hasAnyUpgrade(gun)) return;

        var cache = event.getCacheProperty();
        LivingEntity shooter = event.getShooter();

        double penBonus = GunLevelManager.getArmorPenetration(gun);
        if (penBonus > 0) {
            Float current = cache.getCache(GunProperties.ARMOR_IGNORE);
            float base = current != null ? current : 0.0f;
            cache.setCache(GunProperties.ARMOR_IGNORE, base + (float) penBonus);
        }

        double fireRateBonus = GunLevelManager.getFireRateBonus(gun);
        if (fireRateBonus > 0) {
            Integer current = cache.getCache(GunProperties.ROUNDS_PER_MINUTE);
            int base = current != null ? current : 0;
            int modified = (int) Math.round(base * (1.0 + fireRateBonus));
            if (modified > base) {
                cache.setCache(GunProperties.ROUNDS_PER_MINUTE, modified);
            }
        }

        if (shooter != null) {
            double reloadAttr = shooter.getAttributeValue(ModAttributes.RELOAD_TIME.getDelegate());
            if (reloadAttr > 0 && Math.abs(reloadAttr - 1.0) > 0.001) {
                reloadAttr = Math.max(0.1, reloadAttr);
                Float current = cache.getCache(GunProperties.AMMO_SPEED);
                float base = current != null ? current : 1.0f;
                cache.setCache(GunProperties.AMMO_SPEED, base * (float) reloadAttr);
            }

            double headshotAttr = shooter.getAttributeValue(ModAttributes.HEADSHOT_MULTIPLIER.getDelegate());
            if (headshotAttr > 0 && Math.abs(headshotAttr - 1.0) > 0.001) {
                Float current = cache.getCache(GunProperties.HEADSHOT_MULTIPLIER);
                float base = current != null ? current : 1.0f;
                cache.setCache(GunProperties.HEADSHOT_MULTIPLIER, base * (float) headshotAttr);
            }

            double knockbackAttr = shooter.getAttributeValue(ModAttributes.KNOCKBACK.getDelegate());
            if (knockbackAttr > 0 && Math.abs(knockbackAttr - 1.0) > 0.001) {
                Float current = cache.getCache(GunProperties.KNOCKBACK);
                float base = current != null ? current : 1.0f;
                cache.setCache(GunProperties.KNOCKBACK, base * (float) knockbackAttr);
            }

            double pierceAttr = shooter.getAttributeValue(ModAttributes.PIERCE.getDelegate());
            if (pierceAttr > 0 && Math.abs(pierceAttr - 1.0) > 0.001) {
                Integer current = cache.getCache(GunProperties.PIERCE);
                int base = current != null ? current : 0;
                cache.setCache(GunProperties.PIERCE, Math.max(0, (int) Math.round(base * pierceAttr)));
            }

            double effectiveRangeAttr = shooter.getAttributeValue(ModAttributes.EFFECTIVE_RANGE.getDelegate());
            if (effectiveRangeAttr > 0 && Math.abs(effectiveRangeAttr - 1.0) > 0.001) {
                Float current = cache.getCache(GunProperties.EFFECTIVE_RANGE);
                float base = current != null ? current : 1.0f;
                cache.setCache(GunProperties.EFFECTIVE_RANGE, base * (float) effectiveRangeAttr);
            }
        }
    }
}
