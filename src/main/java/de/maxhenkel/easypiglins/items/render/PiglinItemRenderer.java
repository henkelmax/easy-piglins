package de.maxhenkel.easypiglins.items.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class PiglinItemRenderer extends ItemStackTileEntityRenderer {

    private Minecraft minecraft;
    private PiglinRenderer renderer;

    public PiglinItemRenderer() {
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (renderer == null) {
            renderer = new PiglinRenderer(minecraft.getEntityRenderDispatcher(), false);
        }
        renderer.render(ModItems.PIGLIN.getPiglinFast(minecraft.level, itemStackIn), 0F, 1F, matrixStackIn, bufferIn, combinedLightIn);
    }

}
