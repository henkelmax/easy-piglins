package de.maxhenkel.easypiglins.blocks.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.corelib.client.RenderUtils;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class BartererRenderer implements BlockEntityRenderer<BartererTileentity> {

    private final Minecraft minecraft;
    protected EntityModelSet entityModelSet;
    private PiglinRenderer renderer;
    private PiglinRenderState piglinRenderState;

    public BartererRenderer(EntityModelSet entityModelSet) {
        this.entityModelSet = entityModelSet;
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

        Direction direction = Direction.SOUTH;
        if (!barterer.isFakeWorld()) {
            direction = barterer.getBlockState().getValue(BartererBlock.FACING);
        }

        Piglin piglin = barterer.getPiglinEntity();
        if (piglin != null) {
            if (renderer == null) {
                renderer = (PiglinRenderer) minecraft.getEntityRenderDispatcher().getRenderer(piglin);
            }
            if (piglinRenderState == null) {
                piglinRenderState = renderer.createRenderState();
            }
            piglin.setItemInHand(InteractionHand.OFF_HAND, barterer.getRenderBarteringItem());
            renderer.extractRenderState(piglin, piglinRenderState, 0F);
            matrixStack.pushPose();
            matrixStack.translate(0.5D, 1D / 16D, 0.5D);
            matrixStack.mulPose(Axis.YP.rotationDegrees(-direction.toYRot()));
            matrixStack.translate(0D, 0D, -4D / 16D);
            matrixStack.scale(0.45F, 0.45F, 0.45F);
            renderer.render(piglinRenderState, matrixStack, buffer, combinedLightIn);
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

}
