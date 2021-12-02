package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.corelib.blockentity.IServerTickableBlockEntity;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class BartererTileentity extends PiglinTileentity implements IServerTickableBlockEntity {

    private NonNullList<ItemStack> inputInventory;
    private NonNullList<ItemStack> outputInventory;

    public BartererTileentity(BlockPos pos, BlockState state) {
        super(ModTileEntities.BARTERER, ModBlocks.BARTERER.defaultBlockState(), pos, state);
        inputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
        outputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public void tickServer() {
        Piglin p = getPiglinEntity();
        if (p == null) {
            return;
        }

        if (level.getGameTime() % 20 == 0 && level.random.nextInt(40) == 0) {
            BartererBlock.playPiglinSound(level, worldPosition, SoundEvents.PIGLIN_AMBIENT);
        }

        if (level.getGameTime() % 120 == 0) {
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

    private void addLoot(Piglin piglin) {
        LootTable loottable = level.getServer().getLootTables().get(BuiltInLootTables.PIGLIN_BARTERING);
        List<ItemStack> loot = loottable.getRandomItems((new LootContext.Builder((ServerLevel) level)).withParameter(LootContextParams.THIS_ENTITY, piglin).withRandom(level.random).create(LootContextParamSets.PIGLIN_BARTER));
        if (level.getRandom().nextInt(5) == 0) {
            BartererBlock.playPiglinSound(level, getBlockPos(), SoundEvents.PIGLIN_ADMIRING_ITEM);
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
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        compound.put("InputInventory", ContainerHelper.saveAllItems(new CompoundTag(), inputInventory, true));
        compound.put("OutputInventory", ContainerHelper.saveAllItems(new CompoundTag(), outputInventory, true));
    }

    @Override
    public void load(CompoundTag compound) {
        inputInventory.clear();
        outputInventory.clear();
        ContainerHelper.loadAllItems(compound.getCompound("InputInventory"), inputInventory);
        ContainerHelper.loadAllItems(compound.getCompound("OutputInventory"), outputInventory);
        super.load(compound);
    }

    public Container getInputInventory() {
        return new ItemListInventory(inputInventory, this::setChanged);
    }

    public Container getOutputInventory() {
        return new ItemListInventory(outputInventory, this::setChanged);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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
