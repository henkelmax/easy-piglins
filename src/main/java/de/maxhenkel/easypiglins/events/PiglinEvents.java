package de.maxhenkel.easypiglins.events;

import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.TranslationTextComponent;
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

        if (player.level.isClientSide || !player.isShiftKeyDown() || piglin.isBaby()) {
            return;
        }

        if (piglin.removed) {
            return;
        }

        PiglinTasks.angerNearbyPiglins(player, true);

        if (!PiglinTasks.isWearingGold(player) || !piglin.getBrain().isActive(Activity.IDLE)) {
            piglin.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, player.getUUID(), 600L);
            player.displayClientMessage(new TranslationTextComponent("message.easy_piglins.cant_pick_up"), true);
            return;
        }

        ItemStack stack = new ItemStack(ModItems.PIGLIN);

        ModItems.PIGLIN.setPiglin(stack, piglin);

        if (player.inventory.add(stack)) {
            piglin.remove();
        }
        event.setCancellationResult(ActionResultType.SUCCESS);
        event.setCanceled(true);
    }

}
