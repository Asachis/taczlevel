package com.taczlevel.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.taczlevel.config.ModConfig;
import com.taczlevel.data.GunLevelManager;
import com.taczlevel.event.GunEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class TaczLevelCommand {

    private static final String[] OPTIONS = {"reload", "recoil", "pen", "fire_rate", "dummy_ammo", "all"};

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("taczlevel")
                .requires(source -> source.hasPermission(2))

                // help
                .then(Commands.literal("help")
                        .executes(TaczLevelCommand::help))

                // set <reload|recoil|pen|fire_rate|dummy_ammo|all> <level> [player]
                .then(Commands.literal("set")
                        .then(Commands.literal("reload")
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, GunLevelManager.getMaxLevel()))
                                        .executes(ctx -> setLevel(ctx, 0, IntegerArgumentType.getInteger(ctx, "level"), null))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> setLevel(ctx, 0, IntegerArgumentType.getInteger(ctx, "level"),
                                                        EntityArgument.getPlayer(ctx, "player"))))))
                        .then(Commands.literal("recoil")
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, GunLevelManager.getMaxLevel()))
                                        .executes(ctx -> setLevel(ctx, 1, IntegerArgumentType.getInteger(ctx, "level"), null))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> setLevel(ctx, 1, IntegerArgumentType.getInteger(ctx, "level"),
                                                        EntityArgument.getPlayer(ctx, "player"))))))
                        .then(Commands.literal("pen")
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, GunLevelManager.getMaxLevel()))
                                        .executes(ctx -> setLevel(ctx, 2, IntegerArgumentType.getInteger(ctx, "level"), null))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> setLevel(ctx, 2, IntegerArgumentType.getInteger(ctx, "level"),
                                                        EntityArgument.getPlayer(ctx, "player"))))))
                        .then(Commands.literal("fire_rate")
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, GunLevelManager.getMaxFireRateLevel()))
                                        .executes(ctx -> setLevel(ctx, 3, IntegerArgumentType.getInteger(ctx, "level"), null))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> setLevel(ctx, 3, IntegerArgumentType.getInteger(ctx, "level"),
                                                        EntityArgument.getPlayer(ctx, "player"))))))
                        .then(Commands.literal("dummy_ammo")
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, ModConfig.DUMMY_AMMO.maxLevel.get()))
                                        .executes(ctx -> setLevel(ctx, 4, IntegerArgumentType.getInteger(ctx, "level"), null))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> setLevel(ctx, 4, IntegerArgumentType.getInteger(ctx, "level"),
                                                        EntityArgument.getPlayer(ctx, "player"))))))
                        .then(Commands.literal("all")
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, GunLevelManager.getMaxLevel()))
                                        .executes(ctx -> setAllLevels(ctx, null, IntegerArgumentType.getInteger(ctx, "level")))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> setAllLevels(ctx,
                                                        EntityArgument.getPlayer(ctx, "player"),
                                                        IntegerArgumentType.getInteger(ctx, "level")))))))

                // upgrade <reload|recoil|pen|fire_rate|dummy_ammo|all> [player]
                .then(Commands.literal("upgrade")
                        .then(Commands.literal("reload")
                                .executes(ctx -> upgrade(ctx, 0, null))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> upgrade(ctx, 0, EntityArgument.getPlayer(ctx, "player")))))
                        .then(Commands.literal("recoil")
                                .executes(ctx -> upgrade(ctx, 1, null))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> upgrade(ctx, 1, EntityArgument.getPlayer(ctx, "player")))))
                        .then(Commands.literal("pen")
                                .executes(ctx -> upgrade(ctx, 2, null))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> upgrade(ctx, 2, EntityArgument.getPlayer(ctx, "player")))))
                        .then(Commands.literal("fire_rate")
                                .executes(ctx -> upgrade(ctx, 3, null))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> upgrade(ctx, 3, EntityArgument.getPlayer(ctx, "player")))))
                        .then(Commands.literal("dummy_ammo")
                                .executes(ctx -> upgrade(ctx, 4, null))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> upgrade(ctx, 4, EntityArgument.getPlayer(ctx, "player")))))
                        .then(Commands.literal("all")
                                .executes(ctx -> upgradeAll(ctx, null))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> upgradeAll(ctx, EntityArgument.getPlayer(ctx, "player"))))))

                // get [player]
                .then(Commands.literal("get")
                        .executes(ctx -> getLevels(ctx, null))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> getLevels(ctx, EntityArgument.getPlayer(ctx, "player")))))

                // autoUpgrade <true|false>
                .then(Commands.literal("autoUpgrade")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(TaczLevelCommand::setAutoUpgrade)))
        );
    }

    private static ServerPlayer resolvePlayer(CommandContext<CommandSourceStack> ctx, ServerPlayer target)
            throws CommandSyntaxException {
        return target != null ? target : ctx.getSource().getPlayerOrException();
    }

    private static ItemStack getGun(ServerPlayer player) {
        return player.getMainHandItem();
    }

    // === Help ===

    private static int help(CommandContext<CommandSourceStack> ctx) {
        var src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("§6=== TaczLevel Commands ==="), false);
        src.sendSuccess(() -> Component.literal("§e/taczlevel help§r - Show this help"), false);
        src.sendSuccess(() -> Component.literal("§e/taczlevel set <option> <level> [player]§r - Set an option's level"), false);
        src.sendSuccess(() -> Component.literal("§e/taczlevel upgrade <option> [player]§r - Upgrade an option by 1 level"), false);
        src.sendSuccess(() -> Component.literal("§e/taczlevel get [player]§r - View current levels"), false);
        src.sendSuccess(() -> Component.literal("§e/taczlevel autoUpgrade <true/false>§r - Toggle auto-upgrade on new guns"), false);
        src.sendSuccess(() -> Component.literal("§7Options: reload, recoil, pen, fire_rate, dummy_ammo, all"), false);
        src.sendSuccess(() -> Component.literal("§7[player] defaults to sender if omitted"), false);
        return 1;
    }

    private static int setAutoUpgrade(CommandContext<CommandSourceStack> ctx) {
        boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
        ModConfig.GAME_RULES.startWithUpgrades.set(enabled);
        ctx.getSource().sendSuccess(() -> Component.literal(
                "§eAuto-upgrade " + (enabled ? "§aenabled" : "§cdisabled") + "§r"), true);
        return 1;
    }

    // === Set ===

    private static int setLevel(CommandContext<CommandSourceStack> ctx, int option, int level,
                                ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, target);
        ItemStack gun = getGun(player);
        if (!GunEvents.isTaczGun(gun)) {
            ctx.getSource().sendFailure(Component.translatable("command.taczlevel.not_tacz_gun"));
            return 0;
        }
        switch (option) {
            case 0 -> {
                GunLevelManager.setReloadLevel(gun, level);
                ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.set_reload",
                        player.getName(), level), true);
            }
            case 1 -> {
                GunLevelManager.setRecoilLevel(gun, level);
                ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.set_recoil",
                        player.getName(), level), true);
            }
            case 2 -> {
                GunLevelManager.setPenLevel(gun, level);
                ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.set_pen",
                        player.getName(), level), true);
            }
            case 3 -> {
                GunLevelManager.setFireRateLevel(gun, level);
                ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.set_fire_rate",
                        player.getName(), level), true);
            }
            case 4 -> {
                GunLevelManager.setDummyAmmoLevel(gun, level);
                ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.set_dummy_ammo",
                        player.getName(), level), true);
            }
        }
        GunLevelManager.clearActiveSlot(gun);
        return 1;
    }

    private static int setAllLevels(CommandContext<CommandSourceStack> ctx, ServerPlayer target,
                                    int level) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, target);
        ItemStack gun = getGun(player);
        if (!GunEvents.isTaczGun(gun)) {
            ctx.getSource().sendFailure(Component.translatable("command.taczlevel.not_tacz_gun"));
            return 0;
        }
        GunLevelManager.setReloadLevel(gun, level);
        GunLevelManager.setRecoilLevel(gun, level);
        GunLevelManager.setPenLevel(gun, level);
        GunLevelManager.setFireRateLevel(gun, level);
        GunLevelManager.setDummyAmmoLevel(gun, level);
        GunLevelManager.clearActiveSlot(gun);
        ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.set_all", player.getName(), level), true);
        return 1;
    }

    // === Upgrade ===

    private static int upgrade(CommandContext<CommandSourceStack> ctx, int option,
                               ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, target);
        ItemStack gun = getGun(player);
        if (!GunEvents.isTaczGun(gun)) {
            ctx.getSource().sendFailure(Component.translatable("command.taczlevel.not_tacz_gun"));
            return 0;
        }
        switch (option) {
            case 0 -> {
                if (GunLevelManager.getReloadLevel(gun) >= GunLevelManager.getMaxLevel()) {
                    ctx.getSource().sendFailure(Component.translatable("command.taczlevel.max_level"));
                    return 0;
                }
                GunLevelManager.upgradeReload(gun);
                ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.upgraded_reload",
                        player.getName(), GunLevelManager.getReloadLevel(gun)), true);
            }
            case 1 -> {
                if (GunLevelManager.getRecoilLevel(gun) >= GunLevelManager.getMaxLevel()) {
                    ctx.getSource().sendFailure(Component.translatable("command.taczlevel.max_level"));
                    return 0;
                }
                GunLevelManager.upgradeRecoil(gun);
                ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.upgraded_recoil",
                        player.getName(), GunLevelManager.getRecoilLevel(gun)), true);
            }
            case 2 -> {
                if (GunLevelManager.getPenLevel(gun) >= GunLevelManager.getMaxLevel()) {
                    ctx.getSource().sendFailure(Component.translatable("command.taczlevel.max_level"));
                    return 0;
                }
                GunLevelManager.upgradePen(gun);
                ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.upgraded_pen",
                        player.getName(), GunLevelManager.getPenLevel(gun)), true);
            }
            case 3 -> {
                if (GunLevelManager.getFireRateLevel(gun) >= GunLevelManager.getMaxFireRateLevel()) {
                    ctx.getSource().sendFailure(Component.translatable("command.taczlevel.max_level"));
                    return 0;
                }
                GunLevelManager.upgradeFireRate(gun);
                ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.upgraded_fire_rate",
                        player.getName(), GunLevelManager.getFireRateLevel(gun)), true);
            }
            case 4 -> {
                if (GunLevelManager.getDummyAmmoLevel(gun) >= ModConfig.DUMMY_AMMO.maxLevel.get()) {
                    ctx.getSource().sendFailure(Component.translatable("command.taczlevel.max_level"));
                    return 0;
                }
                GunLevelManager.upgradeDummyAmmo(gun);
                ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.upgraded_dummy_ammo",
                        player.getName(), GunLevelManager.getDummyAmmoLevel(gun)), true);
            }
        }
        return 1;
    }

    private static int upgradeAll(CommandContext<CommandSourceStack> ctx,
                                  ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, target);
        ItemStack gun = getGun(player);
        if (!GunEvents.isTaczGun(gun)) {
            ctx.getSource().sendFailure(Component.translatable("command.taczlevel.not_tacz_gun"));
            return 0;
        }
        boolean upgraded = false;
        if (GunLevelManager.getReloadLevel(gun) < GunLevelManager.getMaxLevel()) {
            GunLevelManager.upgradeReload(gun);
            upgraded = true;
        }
        if (GunLevelManager.getRecoilLevel(gun) < GunLevelManager.getMaxLevel()) {
            GunLevelManager.upgradeRecoil(gun);
            upgraded = true;
        }
        if (GunLevelManager.getPenLevel(gun) < GunLevelManager.getMaxLevel()) {
            GunLevelManager.upgradePen(gun);
            upgraded = true;
        }
        if (GunLevelManager.getFireRateLevel(gun) < GunLevelManager.getMaxFireRateLevel()) {
            GunLevelManager.upgradeFireRate(gun);
            upgraded = true;
        }
        if (GunLevelManager.getDummyAmmoLevel(gun) < ModConfig.DUMMY_AMMO.maxLevel.get()) {
            GunLevelManager.upgradeDummyAmmo(gun);
            upgraded = true;
        }
        if (!upgraded) {
            ctx.getSource().sendFailure(Component.translatable("command.taczlevel.max_level"));
            return 0;
        }
        ctx.getSource().sendSuccess(() -> Component.translatable("command.taczlevel.upgraded_all", player.getName()), true);
        return 1;
    }

    // === Get ===

    private static int getLevels(CommandContext<CommandSourceStack> ctx,
                                 ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, target);
        ItemStack gun = getGun(player);
        if (!GunEvents.isTaczGun(gun) || !GunLevelManager.hasAnyUpgrade(gun)) {
            ctx.getSource().sendFailure(Component.translatable("command.taczlevel.not_tacz_gun"));
            return 0;
        }
        int r = GunLevelManager.getReloadLevel(gun);
        int rec = GunLevelManager.getRecoilLevel(gun);
        int p = GunLevelManager.getPenLevel(gun);
        int f = GunLevelManager.getFireRateLevel(gun);
        int d = GunLevelManager.getDummyAmmoLevel(gun);
        int exp = GunLevelManager.getExp(gun);
        int next = GunLevelManager.getExpToNext(gun);

        var src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("§6=== ").append(player.getName()).append(" §6==="), false);
        src.sendSuccess(() -> Component.translatable("gui.taczlevel.tooltip_reload",
                String.format("%.0f", GunLevelManager.getReloadSpeedBonus(gun) * 100), r,
                GunLevelManager.getMaxLevel()), false);
        src.sendSuccess(() -> Component.translatable("gui.taczlevel.tooltip_recoil",
                String.format("%.0f", GunLevelManager.getRecoilReduction(gun) * 100), rec,
                GunLevelManager.getMaxLevel()), false);
        src.sendSuccess(() -> Component.translatable("gui.taczlevel.tooltip_pen",
                String.format("%.0f", GunLevelManager.getArmorPenetration(gun) * 100), p,
                GunLevelManager.getMaxLevel()), false);
        src.sendSuccess(() -> Component.translatable("gui.taczlevel.tooltip_fire_rate",
                String.format("%.0f", GunLevelManager.getFireRateBonus(gun) * 100), f,
                GunLevelManager.getMaxFireRateLevel()), false);
        src.sendSuccess(() -> Component.translatable("gui.taczlevel.tooltip_dummy_ammo",
                GunLevelManager.getDummyAmmoMaxPool(gun), d,
                ModConfig.DUMMY_AMMO.maxLevel.get()), false);
        if (next > 0) {
            src.sendSuccess(() -> Component.translatable("gui.taczlevel.tooltip_exp", exp, next), false);
        }
        return 1;
    }
}
