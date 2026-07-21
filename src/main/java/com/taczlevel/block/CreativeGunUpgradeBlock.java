package com.taczlevel.block;

import com.mojang.serialization.MapCodec;
import com.taczlevel.block.entity.CreativeGunUpgradeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CreativeGunUpgradeBlock extends BaseEntityBlock {
    public static final MapCodec<CreativeGunUpgradeBlock> CODEC = simpleCodec(CreativeGunUpgradeBlock::new);

    public CreativeGunUpgradeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends CreativeGunUpgradeBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeGunUpgradeBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CreativeGunUpgradeBlockEntity gunBE) {
                player.openMenu(gunBE, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CreativeGunUpgradeBlockEntity gunBE) {
                gunBE.drops();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
