package de.maxhenkel.easypiglins.items.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.client.ItemRenderer;
import de.maxhenkel.corelib.client.RendererProviders;
import de.maxhenkel.easypiglins.blocks.tileentity.FakeWorldTileentity;
import de.maxhenkel.easypiglins.datacomponents.PiglinBlockEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockItemRendererBase<T extends BlockEntityRenderer<U>, U extends FakeWorldTileentity> extends ItemRenderer {

    private Function<BlockEntityRendererProvider.Context, T> rendererSupplier;
    private Supplier<U> tileEntitySupplier;
    private T renderer;
    private Minecraft minecraft;

    public BlockItemRendererBase(Function<BlockEntityRendererProvider.Context, T> rendererSupplier, Supplier<U> tileentitySupplier) {
        this.rendererSupplier = rendererSupplier;
        this.tileEntitySupplier = tileentitySupplier;
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if (renderer == null) {
            renderer = rendererSupplier.apply(RendererProviders.createBlockEntityRendererContext());
        }

        U blockEntity = PiglinBlockEntityData.getAndStoreBlockEntity(itemStack, minecraft.level.registryAccess(), minecraft.level, tileEntitySupplier);
        renderer.render(blockEntity, 0F, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
    }
}
