package de.maxhenkel.easypiglins;

import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ModItemGroups {

    public static final ItemGroup TAB_EASY_PIGLINS = new ItemGroup("easy_piglins") {

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
