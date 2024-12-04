package de.maxhenkel.easypiglins.items.render;

import com.mojang.serialization.MapCodec;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import de.maxhenkel.easypiglins.blocks.tileentity.render.BartererRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class BartererSpecialRenderer extends ItemSpecialRendererBase<BartererTileentity> {

    public BartererSpecialRenderer(EntityModelSet modelSet, Supplier<BlockState> blockSupplier, Supplier<BartererTileentity> blockEntitySupplier) {
        super(modelSet, blockSupplier, blockEntitySupplier);
        renderer = new BartererRenderer(modelSet);
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
            return new BartererSpecialRenderer(modelSet, () -> ModBlocks.BARTERER.get().defaultBlockState(), () -> new BartererTileentity(BlockPos.ZERO, ModBlocks.BARTERER.get().defaultBlockState()));
        }
    }
}

