package de.maxhenkel.easypiglins.items.render;

import com.mojang.serialization.MapCodec;
import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import de.maxhenkel.easypiglins.blocks.tileentity.render.BartererRenderState;
import de.maxhenkel.easypiglins.blocks.tileentity.render.BartererRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BartererSpecialRenderer extends ItemSpecialRendererBase<BartererTileentity, BartererRenderState> {

    public BartererSpecialRenderer(Supplier<BlockState> blockSupplier) {
        super(blockSupplier, BartererTileentity.class);
        renderer = new BartererRenderer();
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
            return new BartererSpecialRenderer(() -> ModBlocks.BARTERER.get().defaultBlockState());
        }
    }
}

