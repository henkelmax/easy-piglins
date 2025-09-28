package de.maxhenkel.easypiglins.blocks.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.easypiglins.blocks.BartererBlock;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BartererRenderer implements BlockEntityRenderer<BartererTileentity, BartererRenderState> {

    private final Minecraft minecraft;
    private final BlockRenderDispatcher blockRenderer;
    private PiglinRenderer renderer;

    public BartererRenderer(BlockRenderDispatcher blockRenderer) {
        minecraft = Minecraft.getInstance();
        this.blockRenderer = blockRenderer;
    }

    @Override
    public BartererRenderState createRenderState() {
        return new BartererRenderState();
    }

    @Override
    public void extractRenderState(BartererTileentity barterer, BartererRenderState renderState, float partialTicks, Vec3 vec3, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(barterer, renderState, partialTicks, vec3, crumblingOverlay);
        renderState.direction = Direction.SOUTH;
        if (!barterer.isFakeWorld()) {
            renderState.direction = barterer.getBlockState().getValue(BartererBlock.FACING);
        } else {
            renderState.lightCoords = 0xF000F0;
        }

        Piglin piglin = barterer.getPiglinEntity();
        if (piglin != null) {
            renderState.renderPiglin = true;
            if (renderer == null) {
                renderer = (PiglinRenderer) minecraft.getEntityRenderDispatcher().getRenderer(piglin);
            }
            if (renderState.piglinRenderState == null) {
                renderState.piglinRenderState = renderer.createRenderState();
            }
            piglin.setItemInHand(InteractionHand.OFF_HAND, barterer.getRenderBarteringItem());
            renderer.extractRenderState(piglin, renderState.piglinRenderState, 0F);
            renderState.piglinRenderState.lightCoords = renderState.lightCoords;
        } else {
            renderState.renderPiglin = false;
        }
    }

    @Override
    public void submit(BartererRenderState renderState, PoseStack stack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        stack.pushPose();

        // See ItemFrameRenderer
        submitNodeCollector.submitBlockModel(
                stack,
                RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS),
                blockRenderer.getBlockModel(renderState.blockState),
                1F,
                1F,
                1F,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );

        if (renderState.renderPiglin) {
            stack.pushPose();
            stack.translate(0.5D, 1D / 16D, 0.5D);
            stack.mulPose(Axis.YP.rotationDegrees(-renderState.direction.toYRot()));
            stack.translate(0D, 0D, -4D / 16D);
            stack.scale(0.45F, 0.45F, 0.45F);
            renderer.submit(renderState.piglinRenderState, stack, submitNodeCollector, cameraRenderState);
            stack.popPose();
        }

        stack.popPose();
    }

}
