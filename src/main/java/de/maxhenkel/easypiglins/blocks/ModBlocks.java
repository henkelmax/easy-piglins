package de.maxhenkel.easypiglins.blocks;

import de.maxhenkel.easypiglins.Main;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);

    public static final RegistryObject<BartererBlock> BARTERER = BLOCK_REGISTER.register("barterer", BartererBlock::new);

    public static void clientSetup() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ItemBlockRenderTypes.setRenderLayer(BARTERER.get(), RenderType.cutout());
        }
    }

    public static void init() {
        BLOCK_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
