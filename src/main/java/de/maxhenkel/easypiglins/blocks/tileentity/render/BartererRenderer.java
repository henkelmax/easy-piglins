package de.maxhenkel.easypiglins.blocks.tileentity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.client.RenderUtils;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

public class BartererRenderer extends TileEntityRenderer<BartererTileentity> {

    private Minecraft minecraft;
    private PiglinRenderer renderer;

    public BartererRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(BartererTileentity barterer, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        renderBlock(matrixStack, buffer, combinedLightIn, combinedOverlayIn);
        renderWithoutBlock(barterer, partialTicks, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
    }

    public void renderWithoutBlock(BartererTileentity barterer, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.pushPose();

        renderBlock(matrixStack, buffer, combinedLightIn, combinedOverlayIn);

        if (renderer == null) {
            renderer = new PiglinRenderer(minecraft.getEntityRenderDispatcher(), false);
        }

        Direction direction = Direction.SOUTH;
        if (!barterer.isFakeWorld()) {
            direction = barterer.getBlockState().getValue(BartererBlock.FACING);
        }

        PiglinEntity piglin = barterer.getPiglinEntity();
        if (piglin != null) {
            matrixStack.pushPose();
            piglin.setItemInHand(Hand.OFF_HAND, barterer.getBarteringItem());
            matrixStack.translate(0.5D, 1D / 16D, 0.5D);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-direction.toYRot()));
            matrixStack.translate(0D, 0D, -4D / 16D);
            matrixStack.scale(0.45F, 0.45F, 0.45F);
            renderer.render(piglin, 0F, 1F, matrixStack, buffer, combinedLightIn);
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

    protected void renderBlock(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        BlockState state = ModBlocks.BARTERER.defaultBlockState();
        int color = minecraft.getBlockColors().getColor(state, null, null, 0);
        BlockRendererDispatcher dispatcher = minecraft.getBlockRenderer();
        dispatcher.getModelRenderer().renderModel(matrixStack.last(), buffer.getBuffer(RenderType.cutoutMipped()), state, dispatcher.getBlockModel(state), RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
    }

}
