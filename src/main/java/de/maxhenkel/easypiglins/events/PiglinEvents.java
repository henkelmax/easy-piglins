package de.maxhenkel.easypiglins.events;

import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PiglinEvents {

    @SubscribeEvent
    public void onClick(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Piglin)) {
            return;
        }

        Piglin piglin = (Piglin) event.getTarget();
        Player player = event.getEntity();

        if (player.level().isClientSide || !player.isShiftKeyDown() || piglin.isBaby()) {
            return;
        }

        if (!piglin.isAlive()) {
            return;
        }

        PiglinAi.angerNearbyPiglins(player, true);

        if (!PiglinAi.isWearingGold(player) || !piglin.getBrain().isActive(Activity.IDLE)) {
            piglin.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, player.getUUID(), 600L);
            player.displayClientMessage(Component.translatable("message.easy_piglins.cant_pick_up"), true);
            return;
        }

        ItemStack stack = new ItemStack(ModItems.PIGLIN.get());

        ModItems.PIGLIN.get().setPiglin(stack, piglin);

        if (player.getInventory().add(stack)) {
            piglin.discard();
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

}
