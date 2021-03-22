package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.render.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModTileEntities {

    public static TileEntityType<BartererTileentity> BARTERER;

    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        BARTERER = TileEntityType.Builder.of(BartererTileentity::new, ModBlocks.BARTERER).build(null);
        BARTERER.setRegistryName(new ResourceLocation(Main.MODID, "barterer"));
        event.getRegistry().register(BARTERER);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.BARTERER, BartererRenderer::new);
    }

}
