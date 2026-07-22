package com.taczlevel;

import com.taczlevel.block.CreativeGunUpgradeBlock;
import com.taczlevel.block.GunUpgradeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TaczLevelMod.MODID);

    public static final RegistryObject<Block> CREATIVE_GUN_UPGRADE_BLOCK = BLOCKS.register("creative_gun_upgrade_block",
            () -> new CreativeGunUpgradeBlock(BlockBehaviour.Properties.of().strength(-1.0f, 3600000.0f).noLootTable()));

    public static final RegistryObject<Block> GUN_UPGRADE_BLOCK = BLOCKS.register("gun_upgrade_block",
            () -> new GunUpgradeBlock(BlockBehaviour.Properties.of().strength(3.5f).noOcclusion()));
}
