package com.taczlevel.network;

import com.taczlevel.TaczLevelMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@SuppressWarnings({"deprecation", "removal"})
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TaczLevelMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, GunUpgradePacket.class,
                GunUpgradePacket::encode,
                GunUpgradePacket::decode,
                GunUpgradePacket::handle);
    }
}
