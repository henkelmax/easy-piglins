package de.maxhenkel.easypiglins.blocks.tileentity.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.core.Direction;

public class BartererRenderState extends BlockEntityRenderState {

    public Direction direction;
    public boolean renderPiglin;
    public PiglinRenderState piglinRenderState;

}
