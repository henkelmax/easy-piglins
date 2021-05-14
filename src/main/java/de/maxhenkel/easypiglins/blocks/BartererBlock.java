package de.maxhenkel.easypiglins.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.ModItemGroups;
import de.maxhenkel.easypiglins.blocks.tileentity.BartererTileentity;
import de.maxhenkel.easypiglins.gui.BartererContainer;
import de.maxhenkel.easypiglins.items.PiglinItem;
import de.maxhenkel.easypiglins.items.render.BartererItemRenderer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BartererBlock extends HorizontalRotatableBlock implements ITileEntityProvider, IItemBlock {

    public BartererBlock() {
        super(Properties.of(Material.METAL).strength(2.5F).sound(SoundType.METAL).noOcclusion().lightLevel(value -> 15));
        setRegistryName(new ResourceLocation(Main.MODID, "barterer"));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_EASY_PIGLINS).setISTER(() -> BartererItemRenderer::new)).setRegistryName(getRegistryName());
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if (!(tileEntity instanceof BartererTileentity)) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }
        BartererTileentity barterer = (BartererTileentity) tileEntity;
        if (!barterer.hasPiglin() && heldItem.getItem() instanceof PiglinItem) {
            barterer.setPiglin(heldItem.copy());
            ItemUtils.decrItemStack(heldItem, player);
            playPiglinSound(worldIn, pos, SoundEvents.PIGLIN_ADMIRING_ITEM);
            return ActionResultType.SUCCESS;
        } else if (player.isShiftKeyDown() && barterer.hasPiglin()) {
            ItemStack stack = barterer.removePiglin();
            if (heldItem.isEmpty()) {
                player.setItemInHand(handIn, stack);
            } else {
                if (!player.inventory.add(stack)) {
                    Direction direction = state.getValue(BartererBlock.FACING);
                    InventoryHelper.dropItemStack(worldIn, direction.getStepX() + pos.getX() + 0.5D, pos.getY() + 0.5D, direction.getStepZ() + pos.getZ() + 0.5D, stack);
                }
            }
            playPiglinSound(worldIn, pos, SoundEvents.PIGLIN_JEALOUS);
            return ActionResultType.SUCCESS;
        } else {
            player.openMenu(new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent(state.getBlock().getDescriptionId());
                }

                @Nullable
                @Override
                public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
                    return new BartererContainer(id, playerInventory, barterer.getInputInventory(), barterer.getOutputInventory());
                }
            });
            return ActionResultType.SUCCESS;
        }
    }

    public static void playPiglinSound(World world, BlockPos pos, SoundEvent soundEvent) {
        world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, soundEvent, SoundCategory.NEUTRAL, 1F, 1F);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new BartererTileentity();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getShadeBrightness(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1F;
    }

}
