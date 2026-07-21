package com.taczlevel.network;

import com.taczlevel.TaczLevelMod;
import com.taczlevel.block.entity.CreativeGunUpgradeBlockEntity;
import com.taczlevel.block.entity.GunUpgradeBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GunUpgradePayload(int containerId, int optionIndex, BlockPos blockPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GunUpgradePayload> TYPE =
            new CustomPacketPayload.Type<>(TaczLevelMod.location("gun_upgrade"));

    public static final StreamCodec<ByteBuf, GunUpgradePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, GunUpgradePayload::containerId,
            ByteBufCodecs.INT, GunUpgradePayload::optionIndex,
            BlockPos.STREAM_CODEC, GunUpgradePayload::blockPos,
            GunUpgradePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GunUpgradePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (player.containerMenu.containerId != payload.containerId) return;

                Level level = player.level();
                BlockEntity be = level.getBlockEntity(payload.blockPos);
                if (be instanceof GunUpgradeBlockEntity gunBE) {
                    gunBE.performUpgrade(payload.optionIndex);
                } else if (be instanceof CreativeGunUpgradeBlockEntity creativeBE) {
                    creativeBE.performUpgrade(payload.optionIndex);
                }
            }
        });
    }
}
