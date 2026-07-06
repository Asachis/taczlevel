package com.taczlevel.event;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.taczlevel.data.GunLevelManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GunStatsHandler {

    @SubscribeEvent
    public void onAttachmentProperty(AttachmentPropertyEvent event) {
        var gun = event.getGunItem();
        if (!GunEvents.isTaczGun(gun) || !GunLevelManager.hasAnyUpgrade(gun)) return;

        var cache = event.getCacheProperty();

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
    }
}