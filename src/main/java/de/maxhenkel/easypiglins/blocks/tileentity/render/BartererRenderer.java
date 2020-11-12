package de.maxhenkel.easypiglins.blocks.tileentity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3f;

public class BartererRenderer extends TileEntityRenderer<BartererTileentity> {

    private Minecraft minecraft;
    private PiglinRenderer renderer;

    public BartererRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(BartererTileentity barterer, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.push();

        if (renderer == null) {
            renderer = new PiglinRenderer(minecraft.getRenderManager(), false);
        }

        Direction direction = Direction.SOUTH;
        if (!barterer.isFakeWorld()) {
            direction = barterer.getBlockState().get(BartererBlock.FACING);
        }

        PiglinEntity piglin = barterer.getPiglinEntity();
        if (piglin != null) {
            matrixStack.push();
            piglin.setHeldItem(Hand.OFF_HAND, barterer.getBarteringItem());
            matrixStack.translate(0.5D, 1D / 16D, 0.5D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-direction.getHorizontalAngle()));
            matrixStack.translate(0D, 0D, -4D / 16D);
            matrixStack.scale(0.45F, 0.45F, 0.45F);
            renderer.render(piglin, 0F, 1F, matrixStack, buffer, combinedLightIn);
            matrixStack.pop();
        }

        matrixStack.pop();
    }

}
