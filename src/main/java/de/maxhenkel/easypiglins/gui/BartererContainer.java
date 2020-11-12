package de.maxhenkel.easypiglins.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class BartererContainer extends InputOutputContainer {

    public BartererContainer(int id, PlayerInventory playerInventory, IInventory inputInventory, IInventory outputInventory) {
        super(Containers.BREEDER_CONTAINER, id, playerInventory, inputInventory, outputInventory);
    }

    public BartererContainer(int id, PlayerInventory playerInventory) {
        super(Containers.BREEDER_CONTAINER, id, playerInventory);
    }

    @Override
    public Slot getInputSlot(IInventory inventory, int id, int x, int y) {
        return new BarterSlot(inventory, id, x, y);
    }

}
