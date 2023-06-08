package de.maxhenkel.easypiglins;

import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MODID);

    public static final RegistryObject<CreativeModeTab> TAB_EASY_PIGLINS = TAB_REGISTER.register("easy_piglins", () -> {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(ModItems.PIGLIN.get()))
                .displayItems((features, output) -> {
                    output.accept(new ItemStack(ModBlocks.BARTERER.get()));
                    output.accept(new ItemStack(ModItems.PIGLIN.get()));
                })
                .title(Component.translatable("itemGroup.easy_piglins"))
                .build();
    });

    public static void init() {
        TAB_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
