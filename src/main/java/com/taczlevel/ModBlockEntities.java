package com.taczlevel;

import com.taczlevel.block.entity.CreativeGunUpgradeBlockEntity;
import com.taczlevel.block.entity.GunUpgradeBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TaczLevelMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeGunUpgradeBlockEntity>> CREATIVE_GUN_UPGRADE_BE = BLOCK_ENTITIES.register("creative_gun_upgrade_block",
            () -> BlockEntityType.Builder.of(CreativeGunUpgradeBlockEntity::new, ModBlocks.CREATIVE_GUN_UPGRADE_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GunUpgradeBlockEntity>> GUN_UPGRADE_BE = BLOCK_ENTITIES.register("gun_upgrade_block",
            () -> BlockEntityType.Builder.of(GunUpgradeBlockEntity::new, ModBlocks.GUN_UPGRADE_BLOCK.get()).build(null));
}
