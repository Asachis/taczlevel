package com.taczlevel.event;

import com.tacz.guns.api.item.IGun;
import com.taczlevel.config.ModConfig;
import com.taczlevel.data.GunLevelManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class GunEvents {

    public static boolean isTaczGun(ItemStack stack) {
        return IGun.getIGunOrNull(stack) != null;
    }

    private Player getPlayerSource(DamageSource source) {
        Entity entity = source.getEntity();
        if (entity instanceof Player player) {
            return player;
        }
        if (entity instanceof Projectile proj && proj.getOwner() instanceof Player player) {
            return player;
        }
        Entity direct = source.getDirectEntity();
        if (direct instanceof Projectile proj && proj.getOwner() instanceof Player player) {
            return player;
        }
        return null;
    }

    private void notifyOptionLevelUp(Player player, int leveledMask, ItemStack gun) {
        if (leveledMask == 0) return;

        for (int i = 0; i < 4; i++) {
            if ((leveledMask & (1 << i)) != 0) {
                int newLevel = GunLevelManager.getLevel(gun, i);
                String pos = ModConfig.OPTION_NOTIFICATION.getPosition(i);
                if ("none".equals(pos)) continue;

                String prefix = "";
                double size = ModConfig.OPTION_NOTIFICATION.getSize(i);
                if (size >= 1.5) {
                    prefix = "§l";
                } else if (size <= 0.7) {
                    prefix = "§7";
                }

                Component msg = Component.literal(prefix).append(
                        Component.translatable("message.taczlevel.option_level_up",
                                Component.translatable(GunLevelManager.getOptionNameKey(i)), newLevel));

                switch (pos) {
                    case "chat" -> player.sendSystemMessage(msg);
                    case "both" -> {
                        player.displayClientMessage(msg, true);
                        player.sendSystemMessage(msg);
                    }
                    default -> player.displayClientMessage(msg, true);
                }
            }
        }

        if (ModConfig.SOUND.enabled.get()) {
            float vol = ModConfig.SOUND.volume.get().floatValue();
            float pitch = ModConfig.SOUND.pitch.get().floatValue();
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, vol, pitch);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        Player player = getPlayerSource(event.getSource());
        if (player == null) return;
        ItemStack gun = player.getMainHandItem();
        if (isTaczGun(gun) && GunLevelManager.hasAnyUpgrade(gun)) {
            int exp = getExpForMob(event.getEntity());
            if (exp > 0) {
                int result;
                if (GunLevelManager.getActiveSlot(gun) >= 0) {
                    result = GunLevelManager.addExpToActive(gun, exp);
                } else {
                    result = GunLevelManager.addExp(gun, exp);
                }
                if (result != 0) {
                    notifyOptionLevelUp(player, result, gun);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        Player player = getPlayerSource(event.getSource());
        if (player == null) return;
        ItemStack gun = player.getMainHandItem();
        if (!isTaczGun(gun)) return;

        if (GunLevelManager.hasAnyUpgrade(gun)) {
            float dmg = event.getAmount();
            if (dmg > 0.0f) {
                int dmgExp = Math.max(1, (int) (dmg * ModConfig.XP.damageExpMultiplier.get().floatValue()));
                int result;
                if (GunLevelManager.getActiveSlot(gun) >= 0) {
                    result = GunLevelManager.addExpToActive(gun, dmgExp);
                } else {
                    result = GunLevelManager.addExp(gun, dmgExp);
                }
                if (result != 0) {
                    notifyOptionLevelUp(player, result, gun);
                }
            }
        }
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!isTaczGun(stack)) return;
        if (!GunLevelManager.hasAnyUpgrade(stack)) return;

        List<Component> tooltip = event.getToolTip();
        tooltip.add(Component.empty());

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("gui.taczlevel.tooltip_collapsed"));
            return;
        }

        tooltip.add(Component.translatable("gui.taczlevel.tooltip_header").withStyle(ChatFormatting.GOLD));

        int reloadLvl = GunLevelManager.getReloadLevel(stack);
        int recoilLvl = GunLevelManager.getRecoilLevel(stack);
        int penLvl = GunLevelManager.getPenLevel(stack);
        int fireRateLvl = GunLevelManager.getFireRateLevel(stack);
        int overallLvl = GunLevelManager.getOverallLevel(stack);

        if (overallLvl > 0) {
            tooltip.add(Component.translatable("gui.taczlevel.tooltip_level", overallLvl)
                    .withStyle(ChatFormatting.AQUA));
        }

        int[][] stats = {
            {reloadLvl, GunLevelManager.getMaxLevel()},
            {recoilLvl, GunLevelManager.getMaxLevel()},
            {penLvl, GunLevelManager.getMaxLevel()},
            {fireRateLvl, GunLevelManager.getMaxFireRateLevel()}
        };
        String[] keys = {"gui.taczlevel.tooltip_reload", "gui.taczlevel.tooltip_recoil",
                "gui.taczlevel.tooltip_pen", "gui.taczlevel.tooltip_fire_rate"};

        for (int i = 0; i < 4; i++) {
            int lvl = stats[i][0];
            int max = stats[i][1];
            if (lvl > 0) {
                double pct = GunLevelManager.getStatPercentage(i, lvl);
                boolean maxed = lvl >= max;
                ChatFormatting color = maxed ? ChatFormatting.GOLD : ChatFormatting.GREEN;
                tooltip.add(Component.translatable(keys[i], String.format("%.0f", pct), lvl, max)
                        .withStyle(color));
            }
        }

        if (overallLvl > 0) {
            int exp = GunLevelManager.getExp(stack);
            int needed = GunLevelManager.getExpToNext(stack);
            if (needed > 0) {
                tooltip.add(Component.translatable("gui.taczlevel.tooltip_exp", exp, needed)
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @SubscribeEvent
    public void onItemEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!ModConfig.GAME_RULES.startWithUpgrades.get()) return;
        if (!(event.getEntity() instanceof ItemEntity item)) return;
        ItemStack stack = item.getItem();
        if (isTaczGun(stack)) {
            GunLevelManager.initDefaultUpgrades(stack);
        }
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!ModConfig.GAME_RULES.startWithUpgrades.get()) return;
        ItemStack stack = event.getCrafting();
        if (isTaczGun(stack)) {
            GunLevelManager.initDefaultUpgrades(stack);
        }
    }

    private int getExpForMob(LivingEntity entity) {
        if (entity instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon) return ModConfig.XP.dragonExp.get();
        if (entity instanceof net.minecraft.world.entity.boss.wither.WitherBoss) return ModConfig.XP.witherExp.get();
        if (entity instanceof net.minecraft.world.entity.monster.Monster) {
            float health = entity.getMaxHealth();
            if (health >= 100) return ModConfig.XP.bossExp.get();
            if (health >= 40) return ModConfig.XP.eliteExp.get();
            return Math.max(ModConfig.XP.normalExp.get(), (int) (health / 2));
        }
        if (entity instanceof net.minecraft.world.entity.NeutralMob) {
            return ModConfig.XP.neutralExp.get();
        }
        if (entity instanceof net.minecraft.world.entity.animal.Animal) {
            return ModConfig.XP.animalExp.get();
        }
        if (!(entity instanceof net.minecraft.world.entity.player.Player)) {
            return ModConfig.XP.otherExp.get();
        }
        return 0;
    }
}
