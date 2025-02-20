package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.corelib.blockentity.IServerTickableBlockEntity;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.easypiglins.MultiItemStackHandler;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import de.maxhenkel.easypiglins.gui.BarterSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class BartererTileentity extends PiglinTileentity implements IServerTickableBlockEntity {

    protected NonNullList<ItemStack> inputInventory;
    protected NonNullList<ItemStack> outputInventory;
    @Nullable
    protected NonNullList<ItemStack> itemsLeft;

    protected ItemStack barteringItem;
    protected int barteringTimeLeft;

    protected MultiItemStackHandler itemHandler;
    protected ItemStackHandler outputHandler;

    public BartererTileentity(BlockPos pos, BlockState state) {
        super(ModTileEntities.BARTERER.get(), ModBlocks.BARTERER.get().defaultBlockState(), pos, state);
        inputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
        outputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
        barteringItem = ItemStack.EMPTY;

        itemHandler = new MultiItemStackHandler(inputInventory, outputInventory, BarterSlot::isValid);
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
        LootTable loottable = level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.PIGLIN_BARTERING);
        List<ItemStack> loot = loottable.getRandomItems((new LootParams.Builder((ServerLevel) level)).withParameter(LootContextParams.THIS_ENTITY, piglin).create(LootContextParamSets.PIGLIN_BARTER));
        return NonNullList.copyOf(loot);
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
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);

        compound.put("InputInventory", ContainerHelper.saveAllItems(new CompoundTag(), inputInventory, true, provider));
        compound.put("OutputInventory", ContainerHelper.saveAllItems(new CompoundTag(), outputInventory, true, provider));
        if (itemsLeft != null) {
            ItemUtils.saveItemList(provider, compound, "ItemsLeft", itemsLeft);
        }
        if (!barteringItem.isEmpty()) {
            compound.put("BarteringItem", barteringItem.save(provider, new CompoundTag()));
        }

        compound.putInt("BarteringTimeLeft", barteringTimeLeft);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        inputInventory.clear();
        outputInventory.clear();
        PiglinData.convertInventory(compound.getCompound("InputInventory"), inputInventory, provider);
        PiglinData.convertInventory(compound.getCompound("OutputInventory"), outputInventory, provider);

        if (compound.contains("ItemsLeft")) {
            itemsLeft = ItemUtils.readItemList(provider, compound, "ItemsLeft", false);
        } else {
            itemsLeft = null;
        }
        if (compound.contains("BarteringItem")) {
            barteringItem = ItemStack.parseOptional(provider, compound.getCompound("BarteringItem"));
        } else {
            barteringItem = ItemStack.EMPTY;
        }

        barteringTimeLeft = compound.getInt("BarteringTimeLeft");

        super.loadAdditional(compound, provider);
    }

    public Container getInputInventory() {
        return new ItemListInventory(inputInventory, this::setChanged);
    }

    public Container getOutputInventory() {
        return new ItemListInventory(outputInventory, this::setChanged);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

}
