package de.maxhenkel.easypiglins.events;

import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PiglinEvents {

    @SubscribeEvent
    public void onClick(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof PiglinEntity)) {
            return;
        }

        PiglinEntity piglin = (PiglinEntity) event.getTarget();
        PlayerEntity player = event.getPlayer();

        if (player.world.isRemote || !player.isSneaking() || piglin.isChild()) {
            return;
        }

        if (piglin.removed) {
            event.setCancellationResult(ActionResultType.FAIL);
            event.setCanceled(true);
            return;
        }

        ItemStack stack = new ItemStack(ModItems.PIGLIN);

        ModItems.PIGLIN.setPiglin(stack, piglin);

        if (player.inventory.addItemStackToInventory(stack)) {
            piglin.remove();
        }
        event.setCancellationResult(ActionResultType.SUCCESS);
        event.setCanceled(true);
    }

}
