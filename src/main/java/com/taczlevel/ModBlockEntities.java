package com.taczlevel;

import com.taczlevel.block.entity.CreativeGunUpgradeBlockEntity;
import com.taczlevel.block.entity.GunUpgradeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TaczLevelMod.MODID);

    public static final RegistryObject<BlockEntityType<CreativeGunUpgradeBlockEntity>> CREATIVE_GUN_UPGRADE_BE = BLOCK_ENTITIES.register("creative_gun_upgrade_block",
            () -> BlockEntityType.Builder.of(CreativeGunUpgradeBlockEntity::new, ModBlocks.CREATIVE_GUN_UPGRADE_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<GunUpgradeBlockEntity>> GUN_UPGRADE_BE = BLOCK_ENTITIES.register("gun_upgrade_block",
            () -> BlockEntityType.Builder.of(GunUpgradeBlockEntity::new, ModBlocks.GUN_UPGRADE_BLOCK.get()).build(null));
}
