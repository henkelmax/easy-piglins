package de.maxhenkel.easypiglins.items;

import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

    public static final RegistryObject<PiglinItem> PIGLIN = ITEM_REGISTER.register("piglin", PiglinItem::new);
    public static final RegistryObject<Item> BARTERER = ITEM_REGISTER.register("barterer", () -> ModBlocks.BARTERER.get().toItem());

    public static void init() {
        ITEM_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
