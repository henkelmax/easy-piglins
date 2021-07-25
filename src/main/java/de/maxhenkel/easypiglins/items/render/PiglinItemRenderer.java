package de.maxhenkel.easypiglins.items.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.easypiglins.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.world.item.ItemStack;

public class PiglinItemRenderer extends BlockEntityWithoutLevelRenderer {

    private Minecraft minecraft;
    private PiglinRenderer renderer;

    public PiglinItemRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet entityModelSet) {
        super(renderDispatcher, entityModelSet);
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (renderer == null) {
            renderer = new PiglinRenderer(new EntityRendererProvider.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getResourceManager(), minecraft.getEntityModels(), minecraft.font), ModelLayers.PIGLIN, ModelLayers.PIGLIN_INNER_ARMOR, ModelLayers.PIGLIN_OUTER_ARMOR, false);
        }
        renderer.render(ModItems.PIGLIN.getPiglinFast(minecraft.level, itemStackIn), 0F, 1F, matrixStackIn, bufferIn, combinedLightIn);
    }

}
