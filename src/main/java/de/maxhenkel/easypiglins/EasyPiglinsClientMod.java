package de.maxhenkel.easypiglins;

import de.maxhenkel.easypiglins.blocks.tileentity.ModTileEntities;
import de.maxhenkel.easypiglins.blocks.tileentity.render.BartererRenderer;
import de.maxhenkel.easypiglins.events.ModSoundEvents;
import de.maxhenkel.easypiglins.gui.Containers;
import de.maxhenkel.easypiglins.items.render.BartererSpecialRenderer;
import de.maxhenkel.easypiglins.items.render.PiglinSpecialRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = EasyPiglinsMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EasyPiglinsMod.MODID, value = Dist.CLIENT)
public class EasyPiglinsClientMod {

    public EasyPiglinsClientMod(IEventBus eventBus) {
        Containers.initClient(eventBus);
    }

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(ModTileEntities.BARTERER.get(), c -> new BartererRenderer(c.blockRenderDispatcher()));

        NeoForge.EVENT_BUS.register(new ModSoundEvents());
    }

    @SubscribeEvent
    static void registerItemModels(RegisterSpecialModelRendererEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(EasyPiglinsMod.MODID, "barterer"), BartererSpecialRenderer.Unbaked.MAP_CODEC);

        event.register(ResourceLocation.fromNamespaceAndPath(EasyPiglinsMod.MODID, "piglin"), PiglinSpecialRenderer.Unbaked.MAP_CODEC);
    }

}
