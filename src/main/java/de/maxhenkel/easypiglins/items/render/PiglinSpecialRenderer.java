package de.maxhenkel.easypiglins.items.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class PiglinSpecialRenderer implements SpecialModelRenderer<PiglinRenderState> {

    protected static final Minecraft minecraft = Minecraft.getInstance();

    private PiglinRenderer renderer;

    public PiglinSpecialRenderer(EntityModelSet modelSet) {

    }

    @Override
    public void render(@Nullable PiglinRenderState piglinRenderState, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean b) {
        if (piglinRenderState == null) {
            return;
        }
        getRenderer().render(piglinRenderState, stack, bufferSource, light);
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

    @OnlyIn(Dist.CLIENT)
    public static class Unbaked implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        public Unbaked() {

        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
            return new PiglinSpecialRenderer(modelSet);
        }
    }

}

