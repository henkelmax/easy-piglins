package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.corelib.blockentity.IServerTickableBlockEntity;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.gui.BarterSlot;
import de.maxhenkel.easypiglins.inventory.InputOnlyResourceHandler;
import de.maxhenkel.easypiglins.inventory.ListAccessItemStacksResourceHandler;
import de.maxhenkel.easypiglins.inventory.OutputOnlyResourceHandler;
import de.maxhenkel.easypiglins.inventory.ValidateResourceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
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
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.List;

public class BartererTileentity extends PiglinTileentity implements IServerTickableBlockEntity {

    protected ListAccessItemStacksResourceHandler inputInventory;
    protected ListAccessItemStacksResourceHandler outputInventory;
    @Nullable
    protected NonNullList<ItemStack> itemsLeft;

    protected ItemStack barteringItem;
    protected int barteringTimeLeft;

    protected CombinedResourceHandler<ItemResource> itemHandler;

    public BartererTileentity(BlockPos pos, BlockState state) {
        super(ModTileEntities.BARTERER.get(), ModBlocks.BARTERER.get().defaultBlockState(), pos, state);
        inputInventory = new ValidateResourceHandler(4, BarterSlot::isValid);
        outputInventory = new ListAccessItemStacksResourceHandler(4);
        barteringItem = ItemStack.EMPTY;

        itemHandler = new CombinedResourceHandler<>(new InputOnlyResourceHandler(inputInventory), new OutputOnlyResourceHandler(outputInventory));
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
        try (Transaction t = Transaction.open(null)) {
            for (int i = 0; i < inputInventory.size(); i++) {
                ItemResource resource = inputInventory.getResource(i);
                if (resource.toStack().isPiglinCurrency() && inputInventory.getAmountAsInt(i) >= 1) {
                    if (inputInventory.extract(resource, 1, t) >= 1) {
                        t.commit();
                        return resource.toStack();
                    }
                }
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
        try (Transaction t = Transaction.open(null)) {
            for (ItemStack drop : itemsLeft) {
                if (drop.isEmpty()) {
                    continue;
                }
                int inserted = outputInventory.insert(ItemResource.of(drop), drop.getCount(), t);
                if (inserted > 0) {
                    newLeft.add(drop.split(drop.getCount() - inserted));
                }
            }
            t.commit();
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

        ItemUtils.saveInventory(valueOutput.child("InputInventory"), "Items", inputInventory.getRaw());
        ItemUtils.saveInventory(valueOutput.child("OutputInventory"), "Items", outputInventory.getRaw());

        if (itemsLeft != null) {
            ItemUtils.saveItemList(valueOutput, "ItemsLeft", itemsLeft);
        }
        if (!barteringItem.isEmpty()) {
            valueOutput.store("BarteringItem", ItemStack.CODEC, barteringItem);
        }

        valueOutput.putInt("BarteringTimeLeft", barteringTimeLeft);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        inputInventory.getRaw().clear();
        outputInventory.getRaw().clear();
        ItemUtils.readItemList(valueInput.childOrEmpty("InputInventory"), "Items", inputInventory.getRaw());
        ItemUtils.readItemList(valueInput.childOrEmpty("OutputInventory"), "Items", outputInventory.getRaw());

        itemsLeft = ItemUtils.readItemList(valueInput, "ItemsLeft", false);

        barteringItem = valueInput.read("BarteringItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        barteringTimeLeft = valueInput.getIntOr("BarteringTimeLeft", 0);

        super.loadAdditional(valueInput);
    }

    public Container getInputInventory() {
        return new ItemListInventory(inputInventory.getRaw(), this::setChanged);
    }

    public Container getOutputInventory() {
        return new ItemListInventory(outputInventory.getRaw(), this::setChanged);
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return itemHandler;
    }

}
