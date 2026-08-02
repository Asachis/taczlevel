package com.taczlevel.event;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.tacz.guns.resource.pojo.data.gun.MoveSpeed;
import com.taczlevel.config.ModConfig;
import com.taczlevel.data.GunLevelManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class GunStatsHandler {

    @SubscribeEvent
    public void onAttachmentProperty(AttachmentPropertyEvent event) {
        if (!ModConfig.GAME_RULES.useTaczLevelAttributes.get()) return;
        ItemStack gun = event.getGunItem();
        if (!GunLevelManager.hasAnyUpgrade(gun)) return;

        AttachmentCacheProperty cache = event.getCacheProperty();

        applyFireRateBonus(cache, gun);
        applyRecoilReduction(cache, gun);
        applyPenetrationBonus(cache, gun);
        applyWeightReduction(cache, gun);
    }

    private void applyFireRateBonus(AttachmentCacheProperty cache, ItemStack gun) {
        double bonus = GunLevelManager.getFireRateBonus(gun);
        if (bonus <= 0) return;

        Integer current = cache.getCache(GunProperties.ROUNDS_PER_MINUTE);
        int base = current != null ? current : 1;
        int modified = (int) Math.round(base * (1.0 + bonus));
        if (modified > base) {
            cache.setCache(GunProperties.ROUNDS_PER_MINUTE, modified);
        }
    }

    private void applyRecoilReduction(AttachmentCacheProperty cache, ItemStack gun) {
        double reduction = GunLevelManager.getRecoilReduction(gun);
        if (reduction <= 0) return;

        ParameterizedCachePair<Float, Float> recoil = cache.getCache(GunProperties.RECOIL);
        if (recoil == null) return;

        float pitchDefault = recoil.left().getDefaultValue();
        float yawDefault = recoil.right().getDefaultValue();

        Modifier mod = new Modifier();
        mod.setPercent(-reduction);
        List<Modifier> modList = List.of(mod);

        cache.setCache(GunProperties.RECOIL, ParameterizedCachePair.of(modList, modList, pitchDefault, yawDefault));
    }

    private void applyPenetrationBonus(AttachmentCacheProperty cache, ItemStack gun) {
        double bonus = GunLevelManager.getArmorPenetration(gun);
        if (bonus <= 0) return;

        Float current = cache.getCache(GunProperties.ARMOR_IGNORE);
        float base = current != null ? current : 0.0f;
        cache.setCache(GunProperties.ARMOR_IGNORE, Math.min(1.0f, base + (float) bonus));
    }

    private void applyWeightReduction(AttachmentCacheProperty cache, ItemStack gun) {
        double reduction = GunLevelManager.getWeightReduction(gun);
        if (reduction <= 0) return;

        Float current = cache.getCache(GunProperties.WEIGHT);
        float base = current != null ? current : 1.0f;
        float weightCut = (float) Math.min(reduction, 1.0);
        float reduced = base * (1.0f - weightCut);
        cache.setCache(GunProperties.WEIGHT, Math.max(reduced, 0.01f));

        if (reduction > 1.0) {
            double excess = reduction - 1.0;
            MoveSpeed speed = cache.getCache(GunProperties.MOVE_SPEED);
            if (speed != null) {
                float bonus = (float) (excess * 0.2);
                cache.setCache(GunProperties.MOVE_SPEED,
                    new MoveSpeed(speed.getBaseMultiplier() + bonus, speed.getAimMultiplier() + bonus, speed.getReloadMultiplier() + bonus));
            }
        }
    }
}
