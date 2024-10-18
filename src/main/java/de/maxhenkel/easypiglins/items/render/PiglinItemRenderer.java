package de.maxhenkel.easypiglins.items.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.client.ItemRenderer;
import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PiglinItemRenderer extends ItemRenderer {

    private PiglinRenderer renderer;
    private PiglinRenderState piglinRenderState;

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext itemDisplayContext, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Piglin cachePiglin = PiglinData.getCachePiglin(itemStackIn, minecraft.level);
        if (renderer == null) {
            renderer = (PiglinRenderer) minecraft.getEntityRenderDispatcher().getRenderer(cachePiglin);
        }
        if (piglinRenderState == null) {
            piglinRenderState = renderer.createRenderState();
        }
        renderer.extractRenderState(cachePiglin, piglinRenderState, 0F);
        renderer.render(piglinRenderState, matrixStackIn, bufferIn, combinedLightIn);
    }

}
