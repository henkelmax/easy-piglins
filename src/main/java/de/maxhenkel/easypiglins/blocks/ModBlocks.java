package de.maxhenkel.easypiglins.blocks;

import de.maxhenkel.easypiglins.EasyPiglinsMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    private static final DeferredRegister.Blocks BLOCK_REGISTER = DeferredRegister.createBlocks(EasyPiglinsMod.MODID);

    public static final DeferredHolder<Block, BartererBlock> BARTERER = BLOCK_REGISTER.registerBlock("barterer", BartererBlock::new, BlockBehaviour.Properties.of());

    public static void init(IEventBus eventBus) {
        BLOCK_REGISTER.register(eventBus);
    }

}
