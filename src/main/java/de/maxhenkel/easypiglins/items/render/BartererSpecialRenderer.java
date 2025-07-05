package de.maxhenkel.easypiglins.items.render;

import com.mojang.serialization.MapCodec;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import de.maxhenkel.easypiglins.blocks.tileentity.render.BartererRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class BartererSpecialRenderer extends ItemSpecialRendererBase<BartererTileentity> {

    public BartererSpecialRenderer(EntityModelSet modelSet, Supplier<BlockState> blockSupplier) {
        super(modelSet, blockSupplier, BartererTileentity.class);
        renderer = new BartererRenderer(modelSet);
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
        public SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
            return new BartererSpecialRenderer(modelSet, () -> ModBlocks.BARTERER.get().defaultBlockState());
        }
    }
}

