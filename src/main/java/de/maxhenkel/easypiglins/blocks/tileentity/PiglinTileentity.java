package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class PiglinTileentity extends FakeWorldTileentity {

    private ItemStack piglin;
    private Piglin piglinEntity;

    public PiglinTileentity(BlockEntityType<?> type, BlockState defaultState, BlockPos pos, BlockState state) {
        super(type, defaultState, pos, state);
        piglin = ItemStack.EMPTY;
    }

    public ItemStack getPiglin() {
        if (piglinEntity != null) {
            PiglinData.applyToItem(piglin, piglinEntity);
        }
        return piglin;
    }

    public boolean hasPiglin() {
        return !piglin.isEmpty();
    }

    public Piglin getPiglinEntity() {
        if (piglinEntity == null && !piglin.isEmpty()) {
            piglinEntity = PiglinData.createPiglin(piglin, level);
        }
        return piglinEntity;
    }

    public void setPiglin(ItemStack piglin) {
        this.piglin = piglin;

        if (piglin.isEmpty()) {
            piglinEntity = null;
        } else {
            piglinEntity = PiglinData.createPiglin(piglin, level);
            onAddPiglin(piglinEntity);
        }
        setChanged();
        sync();
    }

    protected void onAddPiglin(Piglin piglin) {

    }

    public ItemStack removePiglin() {
        ItemStack v = getPiglin();
        setPiglin(ItemStack.EMPTY);
        return v;
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);

        if (hasPiglin()) {
            ItemStack piglinItem = getPiglin();
            if (!piglinItem.isEmpty()) {
                compound.put("Piglin", piglinItem.save(provider));
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        Optional<ItemStack> optionalPiglinItem = compound.getCompound("Piglin").map(t -> PiglinData.convert(provider, t));
        if (optionalPiglinItem.isPresent()) {
            piglin = optionalPiglinItem.get();
            piglinEntity = null;
        } else {
            removePiglin();
        }
        super.loadAdditional(compound, provider);
    }

}
