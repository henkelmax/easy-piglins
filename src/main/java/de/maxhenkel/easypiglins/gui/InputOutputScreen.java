package de.maxhenkel.easypiglins.gui;

import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.easypiglins.EasyPiglinsMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class InputOutputScreen<T extends AbstractContainerMenu> extends ScreenBase<T> {

    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(EasyPiglinsMod.MODID, "textures/gui/container/input_output.png");

    private Inventory playerInventory;

    public InputOutputScreen(T container, Inventory playerInventory, Component name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 176;
        imageHeight = 164;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        drawCentered(guiGraphics, getTopText(), 9, FONT_COLOR);
        drawCentered(guiGraphics, getBottomText(), 40, FONT_COLOR);
        guiGraphics.drawString(font, playerInventory.getDisplayName().getVisualOrderText(), 8, imageHeight - 96 + 3, FONT_COLOR, false);
    }

    protected void drawCentered(GuiGraphics guiGraphics, MutableComponent text, int y, int color) {
        int width = font.width(text);
        guiGraphics.drawString(font, text.getVisualOrderText(), imageWidth / 2 - width / 2, y, color, false);
    }

    protected abstract MutableComponent getTopText();

    protected abstract MutableComponent getBottomText();

}