package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class BartererTileentity extends PiglinTileentity implements ITickableTileEntity {

    private NonNullList<ItemStack> inputInventory;
    private NonNullList<ItemStack> outputInventory;

    public BartererTileentity() {
        super(ModTileEntities.BARTERER);
        inputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
        outputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }
        PiglinEntity p = getPiglinEntity();
        if (p == null) {
            return;
        }

        if (world.getGameTime() % 20 == 0 && world.rand.nextInt(40) == 0) {
            BartererBlock.playPiglinSound(world, getPos(), SoundEvents.ENTITY_PIGLIN_AMBIENT);
        }

        if (world.getGameTime() % 120 == 0) {
            if (removeBarteringItem()) {
                addLoot(p);
            }
            sync();
        }
    }

    public boolean removeBarteringItem() {
        for (ItemStack stack : inputInventory) {
            if (stack.isPiglinCurrency() && stack.getCount() >= 1) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public ItemStack getBarteringItem() {
        for (ItemStack stack : inputInventory) {
            if (stack.isPiglinCurrency() && stack.getCount() >= 1) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private void addLoot(PiglinEntity piglin) {
        LootTable loottable = world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.PIGLIN_BARTERING);
        List<ItemStack> loot = loottable.generate((new LootContext.Builder((ServerWorld) world)).withParameter(LootParameters.THIS_ENTITY, piglin).withRandom(world.rand).build(LootParameterSets.field_237453_h_));
        if (world.getRandom().nextInt(5) == 0) {
            BartererBlock.playPiglinSound(world, getPos(), SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM);
        }
        IItemHandlerModifiable itemHandler = getOutputInventoryItemHandler();
        for (ItemStack drop : loot) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                drop = itemHandler.insertItem(i, drop, false);
                if (drop.isEmpty()) {
                    break;
                }
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("InputInventory", ItemStackHelper.saveAllItems(new CompoundNBT(), inputInventory, true));
        compound.put("OutputInventory", ItemStackHelper.saveAllItems(new CompoundNBT(), outputInventory, true));
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        inputInventory.clear();
        outputInventory.clear();
        ItemStackHelper.loadAllItems(compound.getCompound("InputInventory"), inputInventory);
        ItemStackHelper.loadAllItems(compound.getCompound("OutputInventory"), outputInventory);
        super.read(state, compound);
    }

    public IInventory getInputInventory() {
        return new ItemListInventory(inputInventory, this::markDirty);
    }

    public IInventory getOutputInventory() {
        return new ItemListInventory(outputInventory, this::markDirty);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side.equals(Direction.DOWN)) {
                return LazyOptional.of(this::getOutputInventoryItemHandler).cast();
            } else {
                return LazyOptional.of(this::getInputInventoryItemHandler).cast();
            }

        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable foodInventoryHandler;

    public IItemHandlerModifiable getInputInventoryItemHandler() {
        if (foodInventoryHandler == null) {
            foodInventoryHandler = new ItemStackHandler(inputInventory);
        }
        return foodInventoryHandler;
    }

    private IItemHandlerModifiable outputInventoryHandler;

    public IItemHandlerModifiable getOutputInventoryItemHandler() {
        if (outputInventoryHandler == null) {
            outputInventoryHandler = new ItemStackHandler(outputInventory);
        }
        return outputInventoryHandler;
    }

}
