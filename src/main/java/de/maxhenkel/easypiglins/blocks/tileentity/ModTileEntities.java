package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.easypiglins.EasyPiglinsMod;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTileEntities {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, EasyPiglinsMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BartererTileentity>> BARTERER = BLOCK_ENTITY_REGISTER.register("barterer", () ->
            new BlockEntityType(BartererTileentity::new, ModBlocks.BARTERER.get())
    );

    public static void init(IEventBus eventBus) {
        BLOCK_ENTITY_REGISTER.register(eventBus);
    }

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BARTERER.get(), (object, context) -> object.getItemHandler());
    }

}
