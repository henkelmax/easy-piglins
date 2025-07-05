package de.maxhenkel.easypiglins.datacomponents;

import com.mojang.serialization.Codec;
import de.maxhenkel.corelib.codec.CodecUtils;
import de.maxhenkel.corelib.codec.ValueInputOutputUtils;
import de.maxhenkel.easypiglins.EasyPiglinsMod;
import de.maxhenkel.easypiglins.items.ModItems;
import de.maxhenkel.easypiglins.items.PiglinItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;

public class PiglinData {

    public static final Codec<PiglinData> CODEC = CompoundTag.CODEC.xmap(PiglinData::of, d -> d.nbt);
    public static final StreamCodec<RegistryFriendlyByteBuf, PiglinData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PiglinData decode(RegistryFriendlyByteBuf buf) {
            return new PiglinData(buf.readNbt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PiglinData piglinData) {
            buf.writeNbt(piglinData.nbt);
        }
    };

    private WeakReference<Piglin> piglinCache = new WeakReference<>(null);
    private final CompoundTag nbt;

    private PiglinData(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public static PiglinData of(CompoundTag nbt) {
        return new PiglinData(nbt.copy());
    }

    public static PiglinData of(Piglin piglin) {
        TagValueOutput valueOutput = ValueInputOutputUtils.createValueOutput(piglin, piglin.registryAccess());
        piglin.addAdditionalSaveData(valueOutput);
        return new PiglinData(ValueInputOutputUtils.toTag(valueOutput));
    }

    @Nullable
    public static PiglinData get(ItemStack stack) {
        if (!(stack.getItem() instanceof PiglinItem)) {
            throw new IllegalArgumentException("Tried to set piglin data to non-piglin item (%s)".formatted(stack.getHoverName().getString()));
        }
        convert(stack);
        return stack.get(ModItems.PIGLIN_DATA_COMPONENT);
    }

    public static PiglinData getOrCreate(ItemStack stack) {
        PiglinData piglinData = get(stack);
        if (piglinData == null) {
            piglinData = setEmptyPiglinTag(stack);
        }
        return piglinData;
    }

    public Piglin getCachePiglin(Level level) {
        Piglin piglin = piglinCache.get();
        if (piglin == null) {
            piglin = createPiglin(level, null);
            piglinCache = new WeakReference<>(piglin);
        }
        return piglin;
    }

    public Piglin createPiglin(Level level, @Nullable ItemStack stack) {
        Piglin v = new Piglin(EntityType.PIGLIN, level);
        v.readAdditionalSaveData(ValueInputOutputUtils.createValueInput(EasyPiglinsMod.MODID, level.registryAccess(), nbt));
        if (stack != null) {
            Component customName = stack.get(DataComponents.CUSTOM_NAME);
            if (customName != null) {
                v.setCustomName(customName);
            }
        }
        v.hurtTime = 0;
        v.yHeadRot = 0F;
        v.yHeadRotO = 0F;
        return v;
    }

    public static Piglin createPiglin(ItemStack stack, Level level) {
        PiglinData data = getOrCreate(stack);
        return data.createPiglin(level, stack);
    }

    public static void applyToItem(ItemStack stack, Piglin piglin) {
        if (stack.isEmpty()) {
            return;
        }
        stack.set(ModItems.PIGLIN_DATA_COMPONENT, PiglinData.of(piglin));
        if (piglin.hasCustomName()) {
            stack.set(DataComponents.CUSTOM_NAME, piglin.getCustomName());
        }
    }

    public static Piglin getCachePiglin(ItemStack stack, Level level) {
        return getOrCreate(stack).getCachePiglin(level);
    }

    public static ItemStack convert(CompoundTag itemCompound) {
        ItemStack stack = CodecUtils.fromNBT(ItemStack.CODEC, itemCompound).orElse(ItemStack.EMPTY);
        if (stack.isEmpty()) {
            return stack;
        }
        if (!(stack.getItem() instanceof PiglinItem)) {
            return stack;
        }
        if (stack.has(ModItems.PIGLIN_DATA_COMPONENT)) {
            return stack;
        }
        Optional<CompoundTag> tagOptional = itemCompound.getCompound("tag");
        if (tagOptional.isEmpty()) {
            return stack;
        }
        CompoundTag tag = tagOptional.get();
        Optional<CompoundTag> piglinTagOptional = tag.getCompound("Piglin");
        if (piglinTagOptional.isEmpty()) {
            return stack;
        }
        CompoundTag piglinTag = piglinTagOptional.get();
        PiglinData piglinData = PiglinData.of(piglinTag);
        stack.set(ModItems.PIGLIN_DATA_COMPONENT, piglinData);
        return stack;
    }

    public static void convert(ItemStack stack) {
        if (!(stack.getItem() instanceof PiglinItem)) {
            return;
        }
        if (stack.has(ModItems.PIGLIN_DATA_COMPONENT)) {
            return;
        }
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            setEmptyPiglinTag(stack);
            return;
        }
        CompoundTag customTag = customData.copyTag();
        Optional<CompoundTag> piglinTagOptional = customTag.getCompound("Piglin");
        if (piglinTagOptional.isEmpty()) {
            setEmptyPiglinTag(stack);
            return;
        }
        CompoundTag piglinTag = piglinTagOptional.get();
        customTag.remove("Piglin");
        if (customTag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customTag));
        }
        PiglinData piglinData = PiglinData.of(piglinTag);
        stack.set(ModItems.PIGLIN_DATA_COMPONENT, piglinData);
    }

    private static PiglinData setEmptyPiglinTag(ItemStack stack) {
        PiglinData piglinData = new PiglinData(new CompoundTag());
        stack.set(ModItems.PIGLIN_DATA_COMPONENT, piglinData);
        return piglinData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PiglinData piglinData = (PiglinData) o;
        return Objects.equals(nbt, piglinData.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nbt);
    }
}
