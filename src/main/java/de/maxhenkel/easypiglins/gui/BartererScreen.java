package de.maxhenkel.easypiglins.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;

public class BartererScreen extends InputOutputScreen<BartererContainer> {

    public BartererScreen(BartererContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
    }

    @Override
    protected MutableComponent getTopText() {
        return new TranslatableComponent("gui.easy_piglins.input_items");
    }

    @Override
    protected MutableComponent getBottomText() {
        return new TranslatableComponent("gui.easy_piglins.output_items");
    }

}