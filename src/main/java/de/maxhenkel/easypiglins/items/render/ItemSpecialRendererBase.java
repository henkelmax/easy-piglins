package de.maxhenkel.easypiglins.items.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.easypiglins.blocks.tileentity.FakeWorldTileentity;
import de.maxhenkel.easypiglins.datacomponents.PiglinBlockEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ItemSpecialRendererBase<T extends FakeWorldTileentity> implements SpecialModelRenderer<T> {

    protected static final Minecraft minecraft = Minecraft.getInstance();

    protected BlockEntityRenderer<T> renderer;
    protected Supplier<BlockState> blockSupplier;
    protected Supplier<T> blockEntitySupplier;

    public ItemSpecialRendererBase(EntityModelSet modelSet, Supplier<BlockState> blockSupplier, Supplier<T> blockEntitySupplier) {
        this.blockSupplier = blockSupplier;
        this.blockEntitySupplier = blockEntitySupplier;
    }

    @Override
    public void render(@Nullable T blockEntity, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean b) {
        minecraft.getBlockRenderer().renderSingleBlock(blockSupplier.get(), stack, bufferSource, light, overlay);
        if (blockEntity == null) {
            return;
        }
        renderer.render(blockEntity, 0F, stack, bufferSource, light, overlay);
    }

    @Nullable
    @Override
    public T extractArgument(ItemStack stack) {
        return PiglinBlockEntityData.getAndStoreBlockEntity(stack, minecraft.level.registryAccess(), minecraft.level, blockEntitySupplier);
    }
}

