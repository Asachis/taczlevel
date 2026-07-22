package com.taczlevel.attribute;

import com.tacz.guns.api.entity.IGunOperator;
import com.taczlevel.config.ModConfig;
import com.taczlevel.data.GunLevelManager;
import com.taczlevel.event.GunEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeHandler {

    private static final long TICK_MS = 50L;
    private static final int CHECK_INTERVAL = 10;
    private static final double MAX_ELAPSED_MULTIPLIER = 3.0;

    private static final String[] TAA_FALLBACK_NAMES = {
        "reload_time", "recoil", "armor_ignore", "rounds_per_minute", "weight", "move_speed"
    };

    public static void registerAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(type -> {
            event.add(type, ModAttributes.ADS_TIME.get());
            event.add(type, ModAttributes.AMMO_SPEED.get());
            event.add(type, ModAttributes.ARMOR_IGNORE.get());
            event.add(type, ModAttributes.EFFECTIVE_RANGE.get());
            event.add(type, ModAttributes.EXPLOSION_RADIUS.get());
            event.add(type, ModAttributes.EXPLOSION_DAMAGE.get());
            event.add(type, ModAttributes.EXPLOSION_KNOCKBACK.get());
            event.add(type, ModAttributes.EXPLOSION_DESTROY_BLOCK.get());
            event.add(type, ModAttributes.EXPLOSION_DELAY.get());
            event.add(type, ModAttributes.EXPLOSION_ENABLED.get());
            event.add(type, ModAttributes.MOVE_SPEED.get());
            event.add(type, ModAttributes.HEADSHOT_MULTIPLIER.get());
            event.add(type, ModAttributes.IGNITE.get());
            event.add(type, ModAttributes.INACCURACY.get());
            event.add(type, ModAttributes.INACCURACY_STAND.get());
            event.add(type, ModAttributes.INACCURACY_MOVE.get());
            event.add(type, ModAttributes.INACCURACY_SNEAK.get());
            event.add(type, ModAttributes.INACCURACY_LIE.get());
            event.add(type, ModAttributes.INACCURACY_AIM.get());
            event.add(type, ModAttributes.KNOCKBACK.get());
            event.add(type, ModAttributes.PIERCE.get());
            event.add(type, ModAttributes.RECOIL.get());
            event.add(type, ModAttributes.RECOIL_PITCH.get());
            event.add(type, ModAttributes.RECOIL_YAW.get());
            event.add(type, ModAttributes.ROUNDS_PER_MINUTE.get());
            event.add(type, ModAttributes.SILENCE.get());
            event.add(type, ModAttributes.WEIGHT.get());
            event.add(type, ModAttributes.BULLET_GUNDAMAGE.get());
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_PISTOL.get());
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_RIFLE.get());
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get());
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_SNIPER.get());
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_SMG.get());
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_LMG.get());
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get());
            event.add(type, ModAttributes.BULLET_COUNT.get());
            event.add(type, ModAttributes.MAGAZINE_CAPACITY.get());
            event.add(type, ModAttributes.RELOAD_TIME.get());
            event.add(type, ModAttributes.MELEE_DAMAGE.get());
            event.add(type, ModAttributes.MELEE_DISTANCE.get());

            if (!ModList.get().isLoaded("taa")) {
                for (String name : TAA_FALLBACK_NAMES) {
                    Attribute fallback = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("taa", name));
                    if (fallback != null) {
                        event.add(type, fallback);
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (ModConfig.GAME_RULES.useTaaAttributes.get()) return;
        if (!ModConfig.GAME_RULES.useTaczLevelAttributes.get()) return;

        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (entity.tickCount % CHECK_INTERVAL != 0) return;

        ItemStack gun = entity.getMainHandItem();
        if (!GunEvents.isTaczGun(gun) || !GunLevelManager.hasAnyUpgrade(gun)) return;

        if (!(entity instanceof IGunOperator operator)) return;

        applyReloadSpeedBonus(operator, gun);
    }

    private void applyReloadSpeedBonus(IGunOperator operator, ItemStack gun) {
        double reloadBonus = GunLevelManager.getReloadSpeedBonus(gun);
        if (reloadBonus <= 0) return;
        long ts = operator.getDataHolder().reloadTimestamp;
        if (ts <= 0) return;

        // reloadTimestamp is System.currentTimeMillis() when the reload stage started.
        // To accelerate reload, we reduce the timestamp so that elapsed time appears larger.
        // elapsed = now - timestamp is the natural progress.
        // We add bonus * elapsed extra progress, making total progress = elapsed * (1 + bonus).
        long now = System.currentTimeMillis();
        long elapsed = now - ts;
        if (elapsed <= 0) return;

        long extra = (long) (elapsed * reloadBonus);
        // Cap to prevent reducing the timestamp past the current reload stage start
        long maxExtra = (long) (elapsed * MAX_ELAPSED_MULTIPLIER);
        operator.getDataHolder().reloadTimestamp = ts - Math.min(extra, maxExtra);
    }
}
