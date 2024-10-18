package de.maxhenkel.easypiglins.events;

import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class PiglinEvents {

    @SubscribeEvent
    public void onClick(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Piglin)) {
            return;
        }

        Piglin piglin = (Piglin) event.getTarget();
        Player player = event.getEntity();

        if (!(player.level() instanceof ServerLevel serverLevel) || !player.isShiftKeyDown() || piglin.isBaby()) {
            return;
        }

        if (!piglin.isAlive()) {
            return;
        }

        PiglinAi.angerNearbyPiglins(serverLevel, player, true);

        if (!PiglinAi.isWearingSafeArmor(player) || !piglin.getBrain().isActive(Activity.IDLE)) {
            piglin.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, player.getUUID(), 600L);
            player.displayClientMessage(Component.translatable("message.easy_piglins.cant_pick_up"), true);
            return;
        }

        ItemStack stack = new ItemStack(ModItems.PIGLIN.get());

        PiglinData.applyToItem(stack, piglin);

        if (player.getInventory().add(stack)) {
            piglin.discard();
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

}
