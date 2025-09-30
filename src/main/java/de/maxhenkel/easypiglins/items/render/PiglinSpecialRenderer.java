package de.maxhenkel.easypiglins.items.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public class PiglinSpecialRenderer implements SpecialModelRenderer<PiglinRenderState> {

    protected static final Minecraft minecraft = Minecraft.getInstance();

    @Nullable
    private PiglinRenderer renderer;
    private final CameraRenderState cameraRenderState;

    public PiglinSpecialRenderer() {
        cameraRenderState = new CameraRenderState();
    }

    @Override
    public void submit(@Nullable PiglinRenderState state, ItemDisplayContext context, PoseStack stack, SubmitNodeCollector collector, int light, int overlay, boolean b, int i) {
        if (state == null) {
            return;
        }
        PiglinRenderer piglinRenderer = getRenderer();
        state.lightCoords = light;
        piglinRenderer.submit(state, stack, collector, cameraRenderState);
    }

    @Override
    public void getExtents(Set<Vector3f> vecs) {

    }

    @Nullable
    @Override
    public PiglinRenderState extractArgument(ItemStack stack) {
        Piglin cachePiglin = PiglinData.getCachePiglin(stack, minecraft.level);
        if (renderer == null) {
            renderer = (PiglinRenderer) minecraft.getEntityRenderDispatcher().getRenderer(cachePiglin);
        }
        PiglinRenderState piglinRenderState = renderer.createRenderState();
        renderer.extractRenderState(cachePiglin, piglinRenderState, 0F);
        return piglinRenderState;
    }

    private PiglinRenderer getRenderer() {
        if (renderer == null) {
            renderer = createPiglinRenderer();
        }
        return renderer;
    }

    public static PiglinRenderer createPiglinRenderer() {
        return (PiglinRenderer) minecraft.getEntityRenderDispatcher().renderers.get(EntityType.PIGLIN);
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        public Unbaked() {

        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nullable
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new PiglinSpecialRenderer();
        }
    }

}

