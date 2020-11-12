package de.maxhenkel.easypiglins.items;

import de.maxhenkel.easypiglins.Main;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

public class ModItems {

    public static PiglinItem PIGLIN = new PiglinItem();

    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                PIGLIN.setRegistryName(Main.MODID, "piglin")
        );
    }

}
