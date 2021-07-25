package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.render.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

public class ModTileEntities {

    public static BlockEntityType<BartererTileentity> BARTERER;

    public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        BARTERER = BlockEntityType.Builder.of(BartererTileentity::new, ModBlocks.BARTERER).build(null);
        BARTERER.setRegistryName(new ResourceLocation(Main.MODID, "barterer"));
        event.getRegistry().register(BARTERER);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        BlockEntityRenderers.register(ModTileEntities.BARTERER, BartererRenderer::new);
    }

}
