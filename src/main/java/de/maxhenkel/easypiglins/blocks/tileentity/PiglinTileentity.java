package de.maxhenkel.easypiglins.blocks.tileentity;

import de.maxhenkel.corelib.codec.ValueInputOutputUtils;
import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        if (hasPiglin()) {
            ItemStack piglinItem = getPiglin();
            if (!piglinItem.isEmpty()) {
                valueOutput.store("Piglin", ItemStack.CODEC, piglinItem);
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        Optional<ItemStack> optionalPiglinItem = ValueInputOutputUtils.getTag(valueInput, "Piglin").map(PiglinData::convert);
        if (optionalPiglinItem.isPresent()) {
            piglin = optionalPiglinItem.get();
            piglinEntity = null;
        } else {
            removePiglin();
        }
        super.loadAdditional(valueInput);
    }

}
