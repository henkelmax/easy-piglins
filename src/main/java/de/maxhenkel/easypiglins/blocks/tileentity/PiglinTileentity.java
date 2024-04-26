package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
            CompoundTag comp = new CompoundTag();
            getPiglin().save(provider, comp);
            compound.put("Piglin", comp);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        if (compound.contains("Piglin")) {
            CompoundTag comp = compound.getCompound("Piglin");
            piglin = PiglinData.convert(provider, comp);
            piglinEntity = null;
        } else {
            removePiglin();
        }
        super.loadAdditional(compound, provider);
    }

}
