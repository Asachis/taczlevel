package com.taczlevel;

import com.taczlevel.attribute.AttributeHandler;
import com.taczlevel.attribute.ModAttributes;
import com.taczlevel.command.TaczLevelCommand;
import com.taczlevel.config.ModConfig;
import com.taczlevel.event.GunEvents;
import com.taczlevel.event.GunStatsHandler;
import com.taczlevel.event.TAACompatHandler;
import com.taczlevel.network.NetworkHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.RegisterEvent;
import java.util.function.BiFunction;


@Mod(TaczLevelMod.MODID)
public class TaczLevelMod {
    public static final String MODID = "taczlevel";

    @SuppressWarnings({"deprecation", "removal"})
    public TaczLevelMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenuTypes.MENU_TYPES.register(modBus);
        ModCreativeTab.TABS.register(modBus);
        ModAttributes.ATTRIBUTES.register(modBus);
        modBus.addListener(ModAttributes::registerTaaFallbackAttributes);

        NetworkHandler.register();
        modBus.addListener(this::registerScreens);
        modBus.addListener(AttributeHandler::registerAttributes);

        var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(new GunEvents());
        forgeBus.register(new GunStatsHandler());
        forgeBus.register(new AttributeHandler());
        forgeBus.register(new TAACompatHandler());
        forgeBus.addListener(this::registerCommands);

        if (FMLLoader.getDist() == Dist.CLIENT) {
            ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                    (BiFunction<net.minecraft.client.Minecraft, Screen, Screen>)
                        (mc, screen) -> com.taczlevel.config.screen.ClothConfigScreen.buildScreen(screen)
                )
            );
        }
    }

    @SuppressWarnings({"deprecation", "removal"})
    public static ResourceLocation location(String path) {
        return new ResourceLocation(MODID, path);
    }

    private void registerScreens(final net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.GUN_UPGRADE_MENU.get(), com.taczlevel.gui.GunUpgradeScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.CREATIVE_GUN_UPGRADE_MENU.get(), com.taczlevel.gui.CreativeGunUpgradeScreen::new);
        });
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        TaczLevelCommand.register(event.getDispatcher());
    }

}
