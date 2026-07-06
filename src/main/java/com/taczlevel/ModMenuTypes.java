package com.taczlevel;

import com.taczlevel.gui.CreativeGunUpgradeMenu;
import com.taczlevel.gui.GunUpgradeMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TaczLevelMod.MODID);

    public static final RegistryObject<MenuType<CreativeGunUpgradeMenu>> CREATIVE_GUN_UPGRADE_MENU = MENU_TYPES.register("creative_gun_upgrade_menu",
            () -> IForgeMenuType.create(CreativeGunUpgradeMenu::new));

    public static final RegistryObject<MenuType<GunUpgradeMenu>> GUN_UPGRADE_MENU = MENU_TYPES.register("gun_upgrade_menu",
            () -> IForgeMenuType.create(GunUpgradeMenu::new));
}
