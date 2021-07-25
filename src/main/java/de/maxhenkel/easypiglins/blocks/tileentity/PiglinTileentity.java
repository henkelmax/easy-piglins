package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.core.BlockPos;
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
            ModItems.PIGLIN.setPiglin(piglin, piglinEntity);
        }
        return piglin;
    }

    public boolean hasPiglin() {
        return !piglin.isEmpty();
    }

    public Piglin getPiglinEntity() {
        if (piglinEntity == null && !piglin.isEmpty()) {
            piglinEntity = ModItems.PIGLIN.getPiglin(level, piglin);
        }
        return piglinEntity;
    }

    public void setPiglin(ItemStack piglin) {
        this.piglin = piglin;

        if (piglin.isEmpty()) {
            piglinEntity = null;
        } else {
            piglinEntity = ModItems.PIGLIN.getPiglin(level, piglin);
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
    public CompoundTag save(CompoundTag compound) {
        if (hasPiglin()) {
            CompoundTag comp = new CompoundTag();
            getPiglin().save(comp);
            compound.put("Piglin", comp);
        }
        return super.save(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        if (compound.contains("Piglin")) {
            CompoundTag comp = compound.getCompound("Piglin");
            piglin = ItemStack.of(comp);
            piglinEntity = null;
        } else {
            removePiglin();
        }
        super.load(compound);
    }

}
