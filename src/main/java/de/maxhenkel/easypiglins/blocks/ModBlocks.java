package de.maxhenkel.easypiglins.blocks;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ModBlocks {

    public static final BartererBlock BARTERER = new BartererBlock();

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                BARTERER
        );

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ItemBlockRenderTypes.setRenderLayer(BARTERER, RenderType.cutout());
        }
    }

    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                BARTERER.toItem()
        );
    }

}
