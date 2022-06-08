package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.render.BartererRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTileEntities {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Main.MODID);

    public static final RegistryObject<BlockEntityType<BartererTileentity>> BARTERER = BLOCK_ENTITY_REGISTER.register("barterer", () ->
            BlockEntityType.Builder.of(BartererTileentity::new, ModBlocks.BARTERER.get()).build(null)
    );

    public static void init() {
        BLOCK_ENTITY_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        BlockEntityRenderers.register(ModTileEntities.BARTERER.get(), BartererRenderer::new);
    }

}
