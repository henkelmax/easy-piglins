package de.maxhenkel.easypiglins.gui;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class BartererContainer extends InputOutputContainer {

    public BartererContainer(int id, Inventory playerInventory, Container inputInventory, Container outputInventory) {
        super(Containers.BREEDER_CONTAINER, id, playerInventory, inputInventory, outputInventory);
    }

    public BartererContainer(int id, Inventory playerInventory) {
        super(Containers.BREEDER_CONTAINER, id, playerInventory);
    }

    @Override
    public Slot getInputSlot(Container inventory, int id, int x, int y) {
        return new BarterSlot(inventory, id, x, y);
    }

}
