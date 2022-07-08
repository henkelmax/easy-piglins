package de.maxhenkel.easypiglins.blocks.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.maxhenkel.corelib.client.RenderUtils;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class BartererRenderer implements BlockEntityRenderer<BartererTileentity> {

    private Minecraft minecraft;
    protected BlockEntityRendererProvider.Context context;
    private PiglinRenderer renderer;

    public BartererRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(BartererTileentity barterer, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        renderBlock(matrixStack, buffer, combinedLightIn, combinedOverlayIn);
        renderWithoutBlock(barterer, partialTicks, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
    }

    public void renderWithoutBlock(BartererTileentity barterer, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.pushPose();

        renderBlock(matrixStack, buffer, combinedLightIn, combinedOverlayIn);

        if (renderer == null) {
            renderer = new PiglinRenderer(getEntityRenderer(), ModelLayers.PIGLIN, ModelLayers.PIGLIN_INNER_ARMOR, ModelLayers.PIGLIN_OUTER_ARMOR, false);
        }

        Direction direction = Direction.SOUTH;
        if (!barterer.isFakeWorld()) {
            direction = barterer.getBlockState().getValue(BartererBlock.FACING);
        }

        Piglin piglin = barterer.getPiglinEntity();
        if (piglin != null) {
            matrixStack.pushPose();
            piglin.setItemInHand(InteractionHand.OFF_HAND, barterer.getBarteringItem());
            matrixStack.translate(0.5D, 1D / 16D, 0.5D);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-direction.toYRot()));
            matrixStack.translate(0D, 0D, -4D / 16D);
            matrixStack.scale(0.45F, 0.45F, 0.45F);
            renderer.render(piglin, 0F, 1F, matrixStack, buffer, combinedLightIn);
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

    protected void renderBlock(PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        BlockState state = ModBlocks.BARTERER.get().defaultBlockState();
        int color = minecraft.getBlockColors().getColor(state, null, null, 0);
        BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
        dispatcher.getModelRenderer().renderModel(matrixStack.last(), buffer.getBuffer(RenderType.cutoutMipped()), state, dispatcher.getBlockModel(state), RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.cutoutMipped());
    }

    public EntityRendererProvider.Context getEntityRenderer() {
        return new EntityRendererProvider.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderer(), minecraft.gameRenderer.itemInHandRenderer, minecraft.getResourceManager(), minecraft.getEntityModels(), minecraft.font);
    }

}
