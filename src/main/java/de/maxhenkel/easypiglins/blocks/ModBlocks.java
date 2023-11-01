package de.maxhenkel.easypiglins.blocks;

import de.maxhenkel.easypiglins.Main;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ModBlocks {

    private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);

    public static final RegistryObject<BartererBlock> BARTERER = BLOCK_REGISTER.register("barterer", BartererBlock::new);

    public static void init() {
        BLOCK_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
