package de.maxhenkel.easypiglins.items;

import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(BuiltInRegistries.ITEM, Main.MODID);

    public static final DeferredHolder<Item, PiglinItem> PIGLIN = ITEM_REGISTER.register("piglin", PiglinItem::new);
    public static final DeferredHolder<Item, Item> BARTERER = ITEM_REGISTER.register("barterer", () -> ModBlocks.BARTERER.get().toItem());

    public static void init() {
        ITEM_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
