package net.darkhax.mobbanners.block;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.block.BlockTileEntity;
import net.darkhax.bookshelf.block.IColorfulBlock;
import net.darkhax.bookshelf.data.Blockstates;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.bookshelf.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBannerBase extends BlockTileEntity implements IColorfulBlock {

    protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0625, 0, 0.969, 0.9375, 1, 1);
    protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0625, 0, 0, 0.9375, 1, 0.031);
    protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(1, 0, 0.0625, 0.969, 1, 0.9375);
    protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0, 0, 0.0625, 0.031, 1, 0.9375);

    public BlockBannerBase () {

        super(Material.CLOTH);
        this.setDefaultState(this.blockState.getBaseState().withProperty(Blockstates.HORIZONTAL, EnumFacing.NORTH));
        this.setHardness(1.0F);
        this.setSoundType(SoundType.CLOTH);
        this.disableStats();
    }

    @Override
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {

        final EnumFacing enumfacing = state.getValue(Blockstates.HORIZONTAL);

        switch (enumfacing) {

            case EAST:
                return AABB_EAST;
            case WEST:
                return AABB_WEST;
            case SOUTH:
                return AABB_SOUTH;
            case NORTH:
                return AABB_NORTH;
            default:
                return AABB_NORTH;
        }
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox (IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {

        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube (IBlockState state) {

        return false;
    }

    @Override
    public boolean isFullCube (IBlockState state) {

        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer () {

        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getStateFromMeta (int meta) {

        EnumFacing enumfacing = EnumFacing.byIndex(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(Blockstates.HORIZONTAL, enumfacing);
    }

    @Override
    public int getMetaFromState (IBlockState state) {

        return state.getValue(Blockstates.HORIZONTAL).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState () {

        return new BlockStateContainer(this, Blockstates.HORIZONTAL);
    }

    @Override
    public IBlockState getStateForPlacement (World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {

        return this.getDefaultState().withProperty(Blockstates.HORIZONTAL, facing);
    }

    @Override
    public void neighborChanged (IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {

        final EnumFacing face = state.getValue(Blockstates.HORIZONTAL);

        if (!this.canPlaceBlockOnSide(worldIn, pos, face)) {

            StackUtils.dropStackInWorld(worldIn, pos, this.getStack(worldIn, pos));
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean canPlaceBlockOnSide (World worldIn, BlockPos pos, EnumFacing side) {

        if (WorldUtils.isHorizontal(side)) {

            final boolean isTargetSolid = worldIn.getBlockState(pos.offset(side.getOpposite())).getBlockFaceShape(worldIn, pos.offset(side), side) == BlockFaceShape.SOLID;
            final boolean replaceTop = super.canPlaceBlockOnSide(worldIn, pos, side) || worldIn.getBlockState(pos).getBlock() == this;
            final boolean replaceBot = super.canPlaceBlockOnSide(worldIn, pos.down(), side);

            return isTargetSolid && replaceTop && replaceBot;
        }

        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape (IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {

        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public ItemStack getPickBlock (IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {

        return this.getStack(world, pos);
    }

    @Override
    public void harvestBlock (World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {

        super.harvestBlock(worldIn, player, pos, state, te, stack);

        // Ensure block is broken after being harvested.
        worldIn.setBlockToAir(pos);
    }

    @Override
    public boolean removedByPlayer (IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {

        // Delay removing block if it can be harvested.
        return willHarvest ? true : super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void getDrops (NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

        drops.add(this.getStack(world, pos));
    }

    public abstract ItemStack getStack (IBlockAccess world, BlockPos pos);
}