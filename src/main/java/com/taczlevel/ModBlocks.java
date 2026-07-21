package com.taczlevel;

import com.taczlevel.block.CreativeGunUpgradeBlock;
import com.taczlevel.block.GunUpgradeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TaczLevelMod.MODID);

    public static final DeferredBlock<Block> CREATIVE_GUN_UPGRADE_BLOCK = BLOCKS.register("creative_gun_upgrade_block",
            () -> new CreativeGunUpgradeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> GUN_UPGRADE_BLOCK = BLOCKS.register("gun_upgrade_block",
            () -> new GunUpgradeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F).requiresCorrectToolForDrops()));
}
