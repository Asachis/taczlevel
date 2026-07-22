package com.taczlevel.network;

import com.taczlevel.block.entity.CreativeGunUpgradeBlockEntity;
import com.taczlevel.block.entity.GunUpgradeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GunUpgradePacket {
    private final int containerId;
    private final int optionIndex;
    private final BlockPos blockPos;

    public GunUpgradePacket(int containerId, int optionIndex, BlockPos blockPos) {
        this.containerId = containerId;
        this.optionIndex = optionIndex;
        this.blockPos = blockPos;
    }

    public static void encode(GunUpgradePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.containerId);
        buf.writeInt(msg.optionIndex);
        buf.writeBlockPos(msg.blockPos);
    }

    public static GunUpgradePacket decode(FriendlyByteBuf buf) {
        return new GunUpgradePacket(buf.readInt(), buf.readInt(), buf.readBlockPos());
    }

    public static void handle(GunUpgradePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || player.containerMenu.containerId != msg.containerId) return;

            Level level = player.level();
            BlockEntity be = level.getBlockEntity(msg.blockPos);
            if (be instanceof GunUpgradeBlockEntity gunBE) {
                gunBE.performUpgrade(msg.optionIndex);
            } else if (be instanceof CreativeGunUpgradeBlockEntity creativeBE) {
                creativeBE.performUpgrade(msg.optionIndex);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
