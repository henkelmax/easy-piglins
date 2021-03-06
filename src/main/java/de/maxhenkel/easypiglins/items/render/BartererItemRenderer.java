package de.maxhenkel.easypiglins.items.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.CachedMap;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import de.maxhenkel.easypiglins.blocks.tileentity.render.BartererRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.model.data.EmptyModelData;

public class BartererItemRenderer extends ItemStackTileEntityRenderer {

    private BartererRenderer renderer;
    private Minecraft minecraft;

    private CachedMap<ItemStack, BartererTileentity> cachedMap;

    public BartererItemRenderer() {
        cachedMap = new CachedMap<>(10_000L);
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (renderer == null) {
            renderer = new BartererRenderer(TileEntityRendererDispatcher.instance);
        }

        BlockState traderBlock = ModBlocks.BARTERER.defaultBlockState();
        BlockRendererDispatcher dispatcher = minecraft.getBlockRenderer();
        dispatcher.getModelRenderer().renderModel(matrixStack.last(), buffer.getBuffer(RenderType.cutoutMipped()), traderBlock, dispatcher.getBlockModel(traderBlock), 0, 0, 0, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);

        CompoundNBT blockEntityTag = itemStack.getTagElement("BlockEntityTag");
        if (blockEntityTag == null) {
            return;
        }

        BartererTileentity trader = cachedMap.get(itemStack, () -> {
            BartererTileentity bartererTileentity = new BartererTileentity();
            bartererTileentity.setFakeWorld(minecraft.level);
            bartererTileentity.load(null, blockEntityTag);
            return bartererTileentity;
        });
        renderer.renderWithoutBlock(trader, 0F, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
    }

}
