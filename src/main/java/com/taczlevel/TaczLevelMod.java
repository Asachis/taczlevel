package com.taczlevel;

import com.taczlevel.command.TaczLevelCommand;
import com.taczlevel.config.ModConfig;
import com.taczlevel.event.GunEvents;
import com.taczlevel.event.TAACompatHandler;
import com.taczlevel.network.GunUpgradePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

@Mod(TaczLevelMod.MODID)
public class TaczLevelMod {
    public static final String MODID = "taczlevel";
    private static final String PROTOCOL_VERSION = "1";

    @SuppressWarnings({"deprecation", "removal"})
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public TaczLevelMod() {
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, com.taczlevel.config.ModConfig.SPEC);

        var fmlContext = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = fmlContext.getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModCreativeTab.TABS.register(modEventBus);

        modEventBus.addListener(this::clientSetup);

        var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(new GunEvents());
        forgeBus.register(new TAACompatHandler());
        forgeBus.addListener(this::registerCommands);

        int id = 0;
        CHANNEL.registerMessage(id++, GunUpgradePacket.class,
                GunUpgradePacket::encode,
                GunUpgradePacket::new,
                GunUpgradePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        tryRegisterConfigScreen(fmlContext);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        net.minecraft.client.gui.screens.MenuScreens.register(
                ModMenuTypes.GUN_UPGRADE_MENU.get(),
                com.taczlevel.gui.GunUpgradeScreen::new
        );
        net.minecraft.client.gui.screens.MenuScreens.register(
                ModMenuTypes.CREATIVE_GUN_UPGRADE_MENU.get(),
                com.taczlevel.gui.CreativeGunUpgradeScreen::new
        );
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        TaczLevelCommand.register(event.getDispatcher());
    }

    private void tryRegisterConfigScreen(FMLJavaModLoadingContext ctx) {
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) ->
                            com.taczlevel.config.screen.ClothConfigScreen.buildScreen(parent)));
        } catch (ClassNotFoundException e) {
            // Cloth Config not installed, no in-game config screen
        }
    }
}
