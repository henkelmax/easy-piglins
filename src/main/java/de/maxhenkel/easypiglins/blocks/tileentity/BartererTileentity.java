package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.corelib.blockentity.IServerTickableBlockEntity;
import de.maxhenkel.corelib.codec.ValueInputOutputUtils;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.easypiglins.MultiItemStackHandler;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.gui.BarterSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        CompoundTag inputInv = new CompoundTag();
        ItemUtils.saveInventory(inputInv, "Items", inputInventory);
        ValueInputOutputUtils.setTag(valueOutput, "InputInventory", inputInv);

        CompoundTag outputInv = new CompoundTag();
        ItemUtils.saveInventory(outputInv, "Items", outputInventory);
        ValueInputOutputUtils.setTag(valueOutput, "OutputInventory", outputInv);

        if (itemsLeft != null) {
            CompoundTag il = new CompoundTag();
            ItemUtils.saveItemList(il, "ItemsLeft", itemsLeft);
            valueOutput.store(il);
        }
        if (!barteringItem.isEmpty()) {
            valueOutput.store("BarteringItem", ItemStack.CODEC, barteringItem);
        }

        valueOutput.putInt("BarteringTimeLeft", barteringTimeLeft);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        inputInventory.clear();
        outputInventory.clear();
        ValueInputOutputUtils.getTag(valueInput, "InputInventory").ifPresent(t -> ItemUtils.readItemList(t, "Items", inputInventory));
        ValueInputOutputUtils.getTag(valueInput, "OutputInventory").ifPresent(t -> ItemUtils.readItemList(t, "Items", outputInventory));

        itemsLeft = ValueInputOutputUtils.getTag(valueInput, "ItemsLeft").map(t -> ItemUtils.readItemList(t, "ItemsLeft", false)).orElse(null);

        barteringItem = valueInput.read("BarteringItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        barteringTimeLeft = valueInput.getIntOr("BarteringTimeLeft", 0);

        super.loadAdditional(valueInput);
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
