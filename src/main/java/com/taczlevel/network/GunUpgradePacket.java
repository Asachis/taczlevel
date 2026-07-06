package com.taczlevel.network;

import com.taczlevel.block.entity.CreativeGunUpgradeBlockEntity;
import com.taczlevel.block.entity.GunUpgradeBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GunUpgradePacket {
    private final int containerId;
    private final int optionIndex;
    private final long blockPos;

    public GunUpgradePacket(int containerId, int optionIndex, long blockPos) {
        this.containerId = containerId;
        this.optionIndex = optionIndex;
        this.blockPos = blockPos;
    }

    public GunUpgradePacket(FriendlyByteBuf buf) {
        this.containerId = buf.readInt();
        this.optionIndex = buf.readInt();
        this.blockPos = buf.readLong();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(containerId);
        buf.writeInt(optionIndex);
        buf.writeLong(blockPos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (player.containerMenu.containerId != containerId) return;

            Level level = player.level();
            BlockEntity be = level.getBlockEntity(net.minecraft.core.BlockPos.of(blockPos));
            if (be instanceof GunUpgradeBlockEntity gunBE) {
                gunBE.performUpgrade(optionIndex);
            } else if (be instanceof CreativeGunUpgradeBlockEntity creativeBE) {
                creativeBE.performUpgrade(optionIndex);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
