package com.taczlevel;

import com.taczlevel.attribute.AttributeHandler;
import com.taczlevel.attribute.ModAttributes;
import com.taczlevel.command.TaczLevelCommand;
import com.taczlevel.config.ModConfig;
import com.taczlevel.event.GunEvents;
import com.taczlevel.event.GunStatsHandler;
import com.taczlevel.event.TAACompatHandler;
import com.taczlevel.network.GunUpgradePayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(TaczLevelMod.MODID)
public class TaczLevelMod {
    public static final String MODID = "taczlevel";

    public TaczLevelMod(IEventBus modBus, ModContainer container) {
        container.registerConfig(Type.COMMON, ModConfig.SPEC);

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenuTypes.MENU_TYPES.register(modBus);
        ModCreativeTab.TABS.register(modBus);
        ModAttributes.ATTRIBUTES.register(modBus);

        modBus.addListener(this::registerPayloads);
        modBus.addListener(this::registerScreens);
        modBus.addListener(AttributeHandler::registerAttributes);

        var neoBus = NeoForge.EVENT_BUS;
        neoBus.register(new GunEvents());
        neoBus.register(new GunStatsHandler());
        neoBus.register(new AttributeHandler());
        if (ModConfig.useEntityAttributes()) {
            neoBus.register(new TAACompatHandler());
        }
        neoBus.addListener(this::registerCommands);

        tryRegisterConfigScreen(container);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(GunUpgradePayload.TYPE, GunUpgradePayload.STREAM_CODEC, GunUpgradePayload::handle);
    }

    private void registerScreens(final net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.GUN_UPGRADE_MENU.get(), com.taczlevel.gui.GunUpgradeScreen::new);
        event.register(ModMenuTypes.CREATIVE_GUN_UPGRADE_MENU.get(), com.taczlevel.gui.CreativeGunUpgradeScreen::new);
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        TaczLevelCommand.register(event.getDispatcher());
    }

    private void tryRegisterConfigScreen(ModContainer container) {
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            container.registerExtensionPoint(
                    net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                    (java.util.function.Supplier<net.neoforged.neoforge.client.gui.IConfigScreenFactory>) () ->
                            (mc, parent) -> com.taczlevel.config.screen.ClothConfigScreen.buildScreen(parent)
            );
        } catch (ClassNotFoundException e) {
            // Cloth Config not installed, no in-game config screen
        }
    }
}
