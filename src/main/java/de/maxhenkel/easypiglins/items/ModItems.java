package de.maxhenkel.easypiglins.items;

import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    private static final DeferredRegister.Items ITEM_REGISTER = DeferredRegister.createItems(Main.MODID);

    public static final DeferredHolder<Item, PiglinItem> PIGLIN = ITEM_REGISTER.registerItem("piglin", PiglinItem::new);
    public static final DeferredHolder<Item, BlockItem> BARTERER = ITEM_REGISTER.registerSimpleBlockItem(ModBlocks.BARTERER);

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Main.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PiglinData>> PIGLIN_DATA_COMPONENT = DATA_COMPONENT_TYPE_REGISTER.register("piglin", () -> DataComponentType.<PiglinData>builder().persistent(PiglinData.CODEC).networkSynchronized(PiglinData.STREAM_CODEC).build());

    public static void init(IEventBus eventBus) {
        ITEM_REGISTER.register(eventBus);
        DATA_COMPONENT_TYPE_REGISTER.register(eventBus);
    }

}
