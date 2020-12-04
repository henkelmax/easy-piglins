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
        super(new Item.Properties().maxStackSize(1).setISTER(() -> PiglinItemRenderer::new));
        cachedPiglins = new CachedMap<>(10_000);
        translationKey = EntityType.PIGLIN.getTranslationKey();
    }

    @Override
    protected String getDefaultTranslationKey() {
        return translationKey;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            ItemStack itemstack = context.getItem();
            BlockPos blockpos = context.getPos();
            Direction direction = context.getFace();
            BlockState blockstate = world.getBlockState(blockpos);

            if (!blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos = blockpos.offset(direction);
            }

            PiglinEntity piglin = getPiglin(world, itemstack);

            piglin.setPosition(blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5);

            if (world.addEntity(piglin)) {
                itemstack.shrink(1);
            }

            return ActionResultType.CONSUME;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        World world = Minecraft.getInstance().world;
        if (world == null) {
            return super.getDisplayName(stack);
        } else {
            return getPiglinFast(world, stack).getDisplayName();
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        if (!(entity instanceof PlayerEntity) || world.isRemote) {
            return;
        }
        if (!Main.SERVER_CONFIG.piglinInventorySounds.get()) {
            return;
        }
        if (world.getGameTime() % 20 != 0) {
            return;
        }
        if (world.rand.nextInt(20) == 0) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            playerEntity.playSound(SoundEvents.ENTITY_PIGLIN_AMBIENT, SoundCategory.HOSTILE, 1F, 1F);
        }
    }

    public void setPiglin(ItemStack stack, PiglinEntity piglin) {
        CompoundNBT compound = stack.getOrCreateChildTag("Piglin");
        piglin.writeAdditional(compound);
    }

    public PiglinEntity getPiglin(World world, ItemStack stack) {
        CompoundNBT compound = stack.getChildTag("Piglin");
        if (compound == null) {
            compound = new CompoundNBT();
        }

        PiglinEntity piglin = new PiglinEntity(EntityType.PIGLIN, world);
        piglin.readAdditional(compound);
        piglin.hurtTime = 0;
        return piglin;
    }

    public PiglinEntity getPiglinFast(World world, ItemStack stack) {
        return cachedPiglins.get(stack, () -> getPiglin(world, stack));
    }

}
