package de.maxhenkel.easypiglins.items;

import de.maxhenkel.corelib.CachedMap;
import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.items.render.PiglinItemRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class PiglinItem extends Item {

    private CachedMap<ItemStack, PiglinEntity> cachedPiglins;
    private String translationKey;

    public PiglinItem() {
        super(new Item.Properties().stacksTo(1).setISTER(() -> PiglinItemRenderer::new));
        cachedPiglins = new CachedMap<>(10_000);
        translationKey = EntityType.PIGLIN.getDescriptionId();
    }

    @Override
    protected String getOrCreateDescriptionId() {
        return translationKey;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = world.getBlockState(blockpos);

            if (!blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos = blockpos.relative(direction);
            }

            PiglinEntity piglin = getPiglin(world, itemstack);

            piglin.setPos(blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5);

            if (world.addFreshEntity(piglin)) {
                itemstack.shrink(1);
            }

            return ActionResultType.CONSUME;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextComponent getName(ItemStack stack) {
        World world = Minecraft.getInstance().level;
        if (world == null) {
            return super.getName(stack);
        } else {
            return getPiglinFast(world, stack).getDisplayName();
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        if (!(entity instanceof PlayerEntity) || world.isClientSide) {
            return;
        }
        if (!Main.SERVER_CONFIG.piglinInventorySounds.get()) {
            return;
        }
        if (world.getGameTime() % 20 != 0) {
            return;
        }
        if (world.random.nextInt(20) == 0) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            playerEntity.playNotifySound(SoundEvents.PIGLIN_AMBIENT, SoundCategory.HOSTILE, 1F, 1F);
        }
    }

    public void setPiglin(ItemStack stack, PiglinEntity piglin) {
        CompoundNBT compound = stack.getOrCreateTagElement("Piglin");
        piglin.addAdditionalSaveData(compound);
    }

    public PiglinEntity getPiglin(World world, ItemStack stack) {
        CompoundNBT compound = stack.getTagElement("Piglin");
        if (compound == null) {
            compound = new CompoundNBT();
        }

        PiglinEntity piglin = new PiglinEntity(EntityType.PIGLIN, world);
        piglin.readAdditionalSaveData(compound);
        piglin.hurtTime = 0;
        piglin.yHeadRot = 0F;
        piglin.yHeadRotO = 0F;
        return piglin;
    }

    public PiglinEntity getPiglinFast(World world, ItemStack stack) {
        return cachedPiglins.get(stack, () -> getPiglin(world, stack));
    }

}
