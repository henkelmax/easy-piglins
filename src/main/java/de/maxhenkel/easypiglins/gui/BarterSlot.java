package de.maxhenkel.easypiglins.gui;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BarterSlot extends Slot {

    public BarterSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return isValid(stack);
    }

    public static boolean isValid(ItemStack stack) {
        return stack.isPiglinCurrency();
    }

}
