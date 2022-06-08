package de.maxhenkel.easypiglins.items.render;

import de.maxhenkel.easypiglins.blocks.ModBlocks;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import de.maxhenkel.easypiglins.blocks.tileentity.render.BartererRenderer;
import net.minecraft.core.BlockPos;

public class BartererItemRenderer extends BlockItemRendererBase<BartererRenderer, BartererTileentity> {

    public BartererItemRenderer() {
        super(BartererRenderer::new, () -> new BartererTileentity(BlockPos.ZERO, ModBlocks.BARTERER.get().defaultBlockState()));
    }

}