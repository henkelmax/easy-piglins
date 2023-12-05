package de.maxhenkel.easypiglins;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.ModTileEntities;
import de.maxhenkel.easypiglins.events.PiglinEvents;
import de.maxhenkel.easypiglins.gui.Containers;
import de.maxhenkel.easypiglins.items.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "easy_piglins";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ServerConfig SERVER_CONFIG;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModTileEntities::onRegisterCapabilities);

        SERVER_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.SERVER, ServerConfig.class);

        if (FMLEnvironment.dist.isClient()) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::clientSetup);
        }

        ModBlocks.init();
        ModItems.init();
        ModTileEntities.init();
        Containers.init();
        ModCreativeTabs.init();
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(new PiglinEvents());
    }

    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        ModTileEntities.clientSetup();
        Containers.clientSetup();
    }

}
