package de.maxhenkel.easypiglins;

import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {

    public static final CreativeModeTab TAB_EASY_PIGLINS = new CreativeModeTab("easy_piglins") {

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.PIGLIN);
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> list) {
            super.fillItemList(list);
            list.add(new ItemStack(ModItems.PIGLIN));
        }

    };

}
