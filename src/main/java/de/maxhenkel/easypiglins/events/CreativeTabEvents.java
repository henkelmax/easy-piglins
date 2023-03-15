package de.maxhenkel.easypiglins.events;

import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CreativeTabEvents {

    public static CreativeModeTab TAB_EASY_PIGLINS;

    @SubscribeEvent
    public static void onCreativeModeTabRegister(CreativeModeTabEvent.Register event) {
        TAB_EASY_PIGLINS = event.registerCreativeModeTab(new ResourceLocation(Main.MODID, "easy_piglins"), builder -> {
            builder
                    .icon(() -> new ItemStack(ModItems.PIGLIN.get()))
                    .displayItems((features, output) -> {
                        output.accept(new ItemStack(ModBlocks.BARTERER.get()));
                        output.accept(new ItemStack(ModItems.PIGLIN.get()));
                    })
                    .title(Component.translatable("itemGroup.easy_piglins"))
                    .build();
        });
    }

}
