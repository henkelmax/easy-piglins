package de.maxhenkel.easypiglins;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.ModTileEntities;
import de.maxhenkel.easypiglins.events.PiglinEvents;
import de.maxhenkel.easypiglins.gui.Containers;
import de.maxhenkel.easypiglins.items.ModItems;
import de.maxhenkel.easypiglins.loottables.ModLootTables;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EasyPiglinsMod.MODID)
@EventBusSubscriber(modid = EasyPiglinsMod.MODID)
public class EasyPiglinsMod {

    public static final String MODID = "easy_piglins";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ServerConfig SERVER_CONFIG;
    public static ClientConfig CLIENT_CONFIG;

    public EasyPiglinsMod(IEventBus eventBus) {
        eventBus.addListener(ModTileEntities::onRegisterCapabilities);

        SERVER_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.SERVER, ServerConfig.class);
        CLIENT_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.CLIENT, ClientConfig.class);

        ModBlocks.init(eventBus);
        ModItems.init(eventBus);
        ModTileEntities.init(eventBus);
        Containers.init(eventBus);
        ModCreativeTabs.init(eventBus);
        ModLootTables.init(eventBus);
    }

    @SubscribeEvent
    static void commonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(new PiglinEvents());
    }

}
