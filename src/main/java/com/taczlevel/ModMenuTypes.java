package com.taczlevel;

import com.taczlevel.gui.CreativeGunUpgradeMenu;
import com.taczlevel.gui.GunUpgradeMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, TaczLevelMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CreativeGunUpgradeMenu>> CREATIVE_GUN_UPGRADE_MENU = MENU_TYPES.register("creative_gun_upgrade_menu",
            () -> IMenuTypeExtension.create(CreativeGunUpgradeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<GunUpgradeMenu>> GUN_UPGRADE_MENU = MENU_TYPES.register("gun_upgrade_menu",
            () -> IMenuTypeExtension.create(GunUpgradeMenu::new));
}
