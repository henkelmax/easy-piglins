package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.corelib.blockentity.IServerTickableBlockEntity;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.easypiglins.MultiItemStackHandler;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.gui.BarterSlot;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class BartererTileentity extends PiglinTileentity implements IServerTickableBlockEntity {

    protected NonNullList<ItemStack> inputInventory;
    protected NonNullList<ItemStack> outputInventory;
    @Nullable
    protected NonNullList<ItemStack> itemsLeft;

    protected ItemStack barteringItem;
    protected int barteringTimeLeft;

    protected LazyOptional<MultiItemStackHandler> itemHandler;
    protected ItemStackHandler outputHandler;

    public BartererTileentity(BlockPos pos, BlockState state) {
        super(ModTileEntities.BARTERER.get(), ModBlocks.BARTERER.get().defaultBlockState(), pos, state);
        inputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
        outputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
        barteringItem = ItemStack.EMPTY;

        itemHandler = LazyOptional.of(() -> new MultiItemStackHandler(inputInventory, outputInventory, BarterSlot::isValid));
        outputHandler = new ItemStackHandler(outputInventory);
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

        if (itemsLeft == null) {
            ItemStack oldBarteringItem = barteringItem;
            barteringItem = removeBarteringItem();
            if (!barteringItem.isEmpty()) {
                if (level.getRandom().nextInt(5) == 0) {
                    BartererBlock.playPiglinSound(level, getBlockPos(), SoundEvents.PIGLIN_ADMIRING_ITEM);
                }
                itemsLeft = generateLoot(p);
                barteringTimeLeft = 120;
                setChanged();
                sync();
            } else {
                if (!oldBarteringItem.equals(barteringItem)) {
                    sync();
                }
            }
        }

        if (barteringTimeLeft <= 0) {
            insertItems();
        } else {
            barteringTimeLeft--;
            setChanged();
        }
    }

    public ItemStack removeBarteringItem() {
        for (ItemStack stack : inputInventory) {
            if (stack.isPiglinCurrency() && stack.getCount() >= 1) {
                return stack.split(1);
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getRenderBarteringItem() {
        return barteringItem;
    }

    private NonNullList<ItemStack> generateLoot(Piglin piglin) {
        LootTable loottable = level.getServer().getLootData().getLootTable(BuiltInLootTables.PIGLIN_BARTERING);
        List<ItemStack> loot = loottable.getRandomItems((new LootParams.Builder((ServerLevel) level)).withParameter(LootContextParams.THIS_ENTITY, piglin).create(LootContextParamSets.PIGLIN_BARTER));
        NonNullList<ItemStack> result = NonNullList.create();
        result.addAll(loot);
        return result;
    }

    private void insertItems() {
        if (itemsLeft == null) {
            return;
        }

        NonNullList<ItemStack> newLeft = NonNullList.create();
        for (ItemStack drop : itemsLeft) {
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                drop = outputHandler.insertItem(i, drop, false);
                if (drop.isEmpty()) {
                    break;
                }
            }
            if (!drop.isEmpty()) {
                newLeft.add(drop);
            }
        }

        if (newLeft.isEmpty()) {
            itemsLeft = null;
            sync();
        } else {
            itemsLeft = newLeft;
        }

        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        compound.put("InputInventory", ContainerHelper.saveAllItems(new CompoundTag(), inputInventory, true));
        compound.put("OutputInventory", ContainerHelper.saveAllItems(new CompoundTag(), outputInventory, true));
        if (itemsLeft != null) {
            ItemUtils.saveItemList(compound, "ItemsLeft", itemsLeft);
        }
        if (!barteringItem.isEmpty()) {
            compound.put("BarteringItem", barteringItem.save(new CompoundTag()));
        }

        compound.putInt("BarteringTimeLeft", barteringTimeLeft);
    }

    @Override
    public void load(CompoundTag compound) {
        inputInventory.clear();
        outputInventory.clear();
        ContainerHelper.loadAllItems(compound.getCompound("InputInventory"), inputInventory);
        ContainerHelper.loadAllItems(compound.getCompound("OutputInventory"), outputInventory);

        if (compound.contains("ItemsLeft")) {
            itemsLeft = ItemUtils.readItemList(compound, "ItemsLeft", false);
        } else {
            itemsLeft = null;
        }
        if (compound.contains("BarteringItem")) {
            barteringItem = ItemStack.of(compound.getCompound("BarteringItem"));
        } else {
            barteringItem = ItemStack.EMPTY;
        }

        barteringTimeLeft = compound.getInt("BarteringTimeLeft");

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
        if (!remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        itemHandler.invalidate();
        super.setRemoved();
    }

}
