package com.taczlevel;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TaczLevelMod.MODID);

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.taczlevel"))
                    .icon(() -> new ItemStack(ModItems.GUN_UPGRADE_BLOCK_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.GUN_UPGRADE_BLOCK_ITEM.get());
                        output.accept(ModItems.CREATIVE_GUN_UPGRADE_BLOCK_ITEM.get());
                    })
                    .build()
    );
}
