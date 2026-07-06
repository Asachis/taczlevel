package com.taczlevel;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TaczLevelMod.MODID);

    public static final RegistryObject<Item> CREATIVE_GUN_UPGRADE_BLOCK_ITEM = ITEMS.register("creative_gun_upgrade_block",
            () -> new BlockItem(ModBlocks.CREATIVE_GUN_UPGRADE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> GUN_UPGRADE_BLOCK_ITEM = ITEMS.register("gun_upgrade_block",
            () -> new BlockItem(ModBlocks.GUN_UPGRADE_BLOCK.get(), new Item.Properties()));
}
