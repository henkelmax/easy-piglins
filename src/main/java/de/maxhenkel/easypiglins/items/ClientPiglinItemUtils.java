package de.maxhenkel.easypiglins.items;

import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ClientPiglinItemUtils {

    @Nullable
    public static Component getClientName(ItemStack stack) {
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            return PiglinData.getCachePiglin(stack, world).getDisplayName();
        }
        return null;
    }

}
