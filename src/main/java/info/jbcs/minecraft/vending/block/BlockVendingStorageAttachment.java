package info.jbcs.minecraft.vending.block;

import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingStorageAttachment;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockVendingStorageAttachment extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockVendingStorageAttachment(String name, Material material) {
        super(material);
        setProperties();
        setRegistryName(name);
        setUnlocalizedName(name);
        this.setDefaultState(this.blockState.getBaseState());
    }

    private void setProperties() {
        setSoundType(SoundType.METAL);
        setHardness(0.3F);
        setResistance(6000000.0F);
        setBlockUnbreakable();
        setCreativeTab(Vending.tabVending);
    }


    @Override
    public void onBlockClicked(World world, BlockPos blockPos, EntityPlayer entityplayer) {
        TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos.up());
        if (tileEntity == null) {
            destroy(world, blockPos);
            return;
        }

        if (!entityplayer.getDisplayNameString().equals(tileEntity.getOwnerName()) || !tileEntity.inventory.isEmpty()) {
            return;
        }
        destroy(world, blockPos);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!isBlocked(worldIn, pos))
            Vending.guiStorage.open(playerIn, worldIn, pos);
        return true;
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                ItemStack stack) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(
                MathHelper.floor((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
        state = state.withProperty(FACING, enumfacing);
        worldIn.setBlockState(pos, state, 3);
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int i) {
        return new TileEntityVendingStorageAttachment();
    }

    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    private boolean isBlocked(World worldIn, BlockPos pos) {
        IBlockState blockSafeState = worldIn.getBlockState(pos);
        return !worldIn.getBlockState(pos.offset(blockSafeState.getValue(FACING))).getMaterial().isReplaceable() ||
                !worldIn.getBlockState(pos.offset(blockSafeState.getValue(FACING)).offset(blockSafeState.getValue(FACING).rotateY())).getMaterial().isReplaceable();
    }

    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Nonnull
    public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Nonnull
    public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Nonnull
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    private void destroy(World world, BlockPos pos) {
        dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        world.setBlockToAir(pos);
    }

}
