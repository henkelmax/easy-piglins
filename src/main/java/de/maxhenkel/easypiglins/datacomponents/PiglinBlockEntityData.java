package de.maxhenkel.easypiglins.datacomponents;

import de.maxhenkel.easypiglins.blocks.tileentity.FakeWorldTileentity;
import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

public class PiglinBlockEntityData {

    public static final StreamCodec<RegistryFriendlyByteBuf, PiglinBlockEntityData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PiglinBlockEntityData decode(RegistryFriendlyByteBuf buf) {
            return new PiglinBlockEntityData(buf.readNbt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PiglinBlockEntityData be) {
            buf.writeNbt(be.nbt);
        }
    };

    @Nullable
    private FakeWorldTileentity cache;
    private final CompoundTag nbt;

    private PiglinBlockEntityData(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public CompoundTag copy() {
        return nbt.copy();
    }

    public static PiglinBlockEntityData of(CompoundTag nbt) {
        return new PiglinBlockEntityData(nbt.copy());
    }

    @Nullable
    public static PiglinBlockEntityData get(ItemStack stack) {
        return stack.get(ModItems.BLOCK_ENTITY_DATA_COMPONENT);
    }

    public <T extends FakeWorldTileentity> T getBlockEntity(HolderLookup.Provider provider, @Nullable Level level, Supplier<T> blockEntitySupplier) {
        if (cache == null) {
            cache = blockEntitySupplier.get();
            cache.setFakeWorld(level);
            cache.loadCustomOnly(nbt, provider);
        }
        if (level != null && !cache.isFakeWorld()) {
            cache.setFakeWorld(level);
        }
        return (T) cache;
    }

    public static <T extends FakeWorldTileentity> T getAndStoreBlockEntity(ItemStack stack, HolderLookup.Provider provider, @Nullable Level level, Supplier<T> blockEntitySupplier) {
        PiglinBlockEntityData data = get(stack);
        if (data == null) {
            CustomData beData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
            data = new PiglinBlockEntityData(beData.copyTag());
            stack.set(ModItems.BLOCK_ENTITY_DATA_COMPONENT, data);
        }
        return data.getBlockEntity(provider, level, blockEntitySupplier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PiglinBlockEntityData be = (PiglinBlockEntityData) o;
        return Objects.equals(nbt, be.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nbt);
    }

}
