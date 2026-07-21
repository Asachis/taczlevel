package com.taczlevel;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TaczLevelMod.MODID);

    public static final DeferredItem<Item> CREATIVE_GUN_UPGRADE_BLOCK_ITEM = ITEMS.register("creative_gun_upgrade_block",
            () -> new BlockItem(ModBlocks.CREATIVE_GUN_UPGRADE_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<Item> GUN_UPGRADE_BLOCK_ITEM = ITEMS.register("gun_upgrade_block",
            () -> new BlockItem(ModBlocks.GUN_UPGRADE_BLOCK.get(), new Item.Properties()));
}
