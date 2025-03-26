package de.maxhenkel.easypiglins.items;

import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class PiglinItem extends Item {

    public PiglinItem(Properties properties) {
        super(properties.stacksTo(1).component(DataComponents.ITEM_NAME, Component.translatable(EntityType.PIGLIN.getDescriptionId())));

        DispenserBlock.registerBehavior(this, (source, stack) -> {
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            BlockPos blockpos = source.pos().relative(direction);
            Level world = source.level();
            Piglin piglin = PiglinData.createPiglin(stack, world);
            piglin.snapTo(blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D, direction.toYRot(), 0F);
            world.addFreshEntity(piglin);
            stack.shrink(1);
            return stack;
        });
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = world.getBlockState(blockpos);

            if (!blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos = blockpos.relative(direction);
            }

            Piglin piglin = PiglinData.createPiglin(itemstack, world);

            piglin.setPos(blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5);

            if (world.addFreshEntity(piglin)) {
                itemstack.shrink(1);
            }

            return InteractionResult.CONSUME;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getName(ItemStack stack) {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return super.getName(stack);
        } else {
            return PiglinData.getCachePiglin(stack, world).getDisplayName();
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (!(entity instanceof Player player)) {
            return;
        }
        if (!Main.SERVER_CONFIG.piglinInventorySounds.get()) {
            return;
        }
        if (level.getGameTime() % 20 != 0) {
            return;
        }
        if (level.random.nextInt(20) == 0) {
            player.playNotifySound(SoundEvents.PIGLIN_AMBIENT, SoundSource.HOSTILE, 1F, 1F);
        }
    }
}
