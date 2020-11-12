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
        super(Properties.create(Material.IRON).hardnessAndResistance(2.5F).sound(SoundType.METAL).notSolid().setLightLevel(value -> 15));
        setRegistryName(new ResourceLocation(Main.MODID, "barterer"));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().group(ModItemGroups.TAB_EASY_PIGLINS).setISTER(() -> BartererItemRenderer::new)).setRegistryName(getRegistryName());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(handIn);
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof BartererTileentity)) {
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        }
        BartererTileentity barterer = (BartererTileentity) tileEntity;
        if (!barterer.hasPiglin() && heldItem.getItem() instanceof PiglinItem) {
            barterer.setPiglin(heldItem.copy());
            ItemUtils.decrItemStack(heldItem, player);
            playPiglinSound(worldIn, pos, SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM);
            return ActionResultType.SUCCESS;
        } else if (player.isSneaking() && barterer.hasPiglin()) {
            ItemStack stack = barterer.removePiglin();
            if (heldItem.isEmpty()) {
                player.setHeldItem(handIn, stack);
            } else {
                if (!player.inventory.addItemStackToInventory(stack)) {
                    Direction direction = state.get(BartererBlock.FACING);
                    InventoryHelper.spawnItemStack(worldIn, direction.getXOffset() + pos.getX() + 0.5D, pos.getY() + 0.5D, direction.getZOffset() + pos.getZ() + 0.5D, stack);
                }
            }
            playPiglinSound(worldIn, pos, SoundEvents.ENTITY_PIGLIN_JEALOUS);
            return ActionResultType.SUCCESS;
        } else {
            player.openContainer(new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent(state.getBlock().getTranslationKey());
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
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new BartererTileentity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1F;
    }

}
