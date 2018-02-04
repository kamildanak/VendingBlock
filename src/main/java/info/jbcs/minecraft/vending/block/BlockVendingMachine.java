package info.jbcs.minecraft.vending.block;

import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.init.VendingItems;
import info.jbcs.minecraft.vending.init.VendingSoundEvents;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class BlockVendingMachine extends BlockContainer {
    private static final PropertyEnum<EnumSupports> SUPPORT =
            PropertyEnum.create("support", EnumSupports.class);
    private boolean isAdvanced, isMultiple, isOpen;
    private String name;

    public BlockVendingMachine(boolean advanced, boolean multiple, String name) {
        super(Material.GLASS);
        setProperties();

        isAdvanced = advanced;
        isMultiple = multiple;
        isOpen = true;

        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SUPPORT, EnumSupports.STONE));
    }

    private void setProperties() {
        setSoundType(SoundType.GLASS);
        setHardness(0.3F);
        setResistance(6000000.0F);
        setBlockUnbreakable();
        setCreativeTab(Vending.tabVending);
    }


    @Override
    public void onBlockClicked(World world, BlockPos blockPos, EntityPlayer entityplayer) {
        TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos);
        if (tileEntity == null)
            return;

        if (!entityplayer.getDisplayNameString().equals(tileEntity.getOwnerName()) || !tileEntity.isEmpty()) {
            tileEntity.vend(entityplayer, false);
            return;
        }

        dropBlockAsItem(world, blockPos, world.getBlockState(blockPos), 0);
        world.setBlockToAir(blockPos);

        world.playSound(entityplayer, blockPos, VendingSoundEvents.PROCESSED,
                SoundCategory.MASTER, 0.3f, 0.6f);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState state, EntityPlayer entityPlayer,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos);
        if (tileEntity == null)
            return false;

        if (!entityPlayer.inventory.getCurrentItem().isEmpty() &&
                entityPlayer.inventory.getCurrentItem().getItem() == VendingItems.ITEM_WRENCH) {
            Vending.guiWrench.open(entityPlayer, world, blockPos);
            return true;
        }

        if ((entityPlayer.getDisplayNameString().equals(tileEntity.getOwnerName()) && !entityPlayer.isSneaking()) ||
                entityPlayer.capabilities.isCreativeMode && !entityPlayer.isSneaking()) {
            Vending.guiVending.open(entityPlayer, world, blockPos);
            return true;
        }

        tileEntity.vend(entityPlayer, false);
        tileEntity.markDirty();
        Utils.markBlockForUpdate(world, blockPos);

        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
        if (!worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)) return false;
        if (!Vending.settings.isPlacingNextToDoorsBlocked()) return true;
        for (int x = -1; x < 2; x++)
            for (int z = -1; z < 2; z++)
                if (worldIn.getBlockState(pos.add(x, 0, z)).getBlock() instanceof BlockDoor) return false;
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, IBlockState state, EntityLivingBase entityLiving, @Nonnull ItemStack stack) {
        world.setBlockState(blockPos, getStateFromMeta(stack.getMetadata()));
        TileEntityVendingMachine e;
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("preConfigured") && tagCompound.getBoolean("preConfigured"))
        {
            e = new TileEntityVendingMachine(isAdvanced, tagCompound.getBoolean("infinite"), isMultiple);
            e.setOpen(true);
            e.setOwnerName(tagCompound.getString("ownerName"));
            world.setTileEntity(blockPos, e);
            return;
        } else
        {
            e = new TileEntityVendingMachine(isAdvanced, false, isMultiple);
        }
        e.setOpen(isOpen);

        if (entityLiving != null) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            e.setOwnerName(player.getName());
            world.setTileEntity(blockPos, e);
        }
    }

    @Override
    @Nonnull
    public TileEntity createNewTileEntity(@Nonnull World var1, int metadata) {
        TileEntityVendingMachine e = new TileEntityVendingMachine(isAdvanced, false, isMultiple);
        e.setOpen(isOpen);

        return e;
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

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tabs, NonNullList<ItemStack> list) {
        for (int i = 0; i < EnumSupports.length; ++i) list.add(new ItemStack(this, 1, i));
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(SUPPORT, EnumSupports.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(SUPPORT).getMetadata();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SUPPORT);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0625f, 0.125f, 0.0625f, 0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Nonnull
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public String getName() {
        return name;
    }
}
