package de.maxhenkel.easypiglins.items.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.client.ItemRenderer;
import de.maxhenkel.corelib.client.RendererProviders;
import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PiglinItemRenderer extends ItemRenderer {

    private PiglinRenderer renderer;

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext itemDisplayContext, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (renderer == null) {
            renderer = new PiglinRenderer(RendererProviders.createEntityRendererContext(), ModelLayers.PIGLIN, ModelLayers.PIGLIN_INNER_ARMOR, ModelLayers.PIGLIN_OUTER_ARMOR, false);
        }
        Piglin cachePiglin = PiglinData.getCachePiglin(itemStackIn, minecraft.level);
        renderer.render(cachePiglin, 0F, 1F, matrixStackIn, bufferIn, combinedLightIn);
    }

}
