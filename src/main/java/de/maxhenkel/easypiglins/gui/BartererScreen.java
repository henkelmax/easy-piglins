package de.maxhenkel.easypiglins.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BartererScreen extends InputOutputScreen<BartererContainer> {

    public BartererScreen(BartererContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
    }

    @Override
    protected IFormattableTextComponent getTopText() {
        return new TranslationTextComponent("gui.easy_piglins.input_items");
    }

    @Override
    protected IFormattableTextComponent getBottomText() {
        return new TranslationTextComponent("gui.easy_piglins.output_items");
    }

}