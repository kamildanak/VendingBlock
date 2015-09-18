package info.jbcs.minecraft.vending.block;

import info.jbcs.minecraft.vending.General;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static info.jbcs.minecraft.vending.General.countNotNull;

public class BlockVendingMachine extends BlockContainer {
	public static final PropertyEnum SUPPORT = PropertyEnum.create("support", EnumSupports.class);
	boolean isAdvanced, isMultiple;
	private String name;

	public BlockVendingMachine(boolean advanced, boolean multiple, String name) {
		super(Material.glass);
		setUnlocalizedName(name);
		this.name=name;
		this.setDefaultState(this.blockState.getBaseState().withProperty(SUPPORT, EnumSupports.STONE));
		GameRegistry.registerBlock(this, name);

		setStepSound(Block.soundTypeGlass);
		setCreativeTab(Vending.tabVending);

		setHardness(0.3F);
		setResistance(6000000.0F);
		setBlockUnbreakable();

		setStepSound(Block.soundTypeGlass);

		setBlockBounds(0.0625f, 0.125f, 0.0625f, 0.9375f, 0.9375f, 0.9375f);

		isAdvanced=advanced;
		isMultiple=multiple;
	}

	public int getRenderType()
	{
		return 3;
	}

	public boolean isFullCube()
	{
		return false;
	}

	public EnumWorldBlockLayer getBlockLayer()
	{
		return EnumWorldBlockLayer.CUTOUT;
	}

	public String getName()
	{
		return name;
	}

	public String getLocalizedName()
	{
		return StatCollector.translateToLocal("tile." + getName() + ".name");
	}

	void vend(World world, BlockPos blockPos, EntityPlayer entityplayer){
		TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos);
		if (tileEntity == null)
			return;

		ItemStack[] soldItems = tileEntity.getSoldItems();
		ItemStack bought = tileEntity.getBoughtItems()[0];
		ItemStack offered = entityplayer.inventory.getCurrentItem();

		boolean fits = true;

		if (bought == null) {
			offered = null;
			if (soldItems == null)
				fits = false;
		} else {
			if(! tileEntity.doesStackFit(bought))
				fits = false;
			else if (offered == null)
				fits = false;
			else if (bought.getItem() != offered.getItem())
				fits = false;
			else if (bought.getItemDamage() != offered.getItemDamage())
				fits = false;
			else if (offered.stackSize < bought.stackSize)
				fits = false;
            else if(bought.hasTagCompound() || offered.hasTagCompound()){
                if(bought.hasTagCompound() && offered.hasTagCompound()) {
                    if (!bought.getTagCompound().equals(offered.getTagCompound())) {
                        fits = false;
                    }
                }else {
                    fits = false;
                }
            }
		}

		if (fits) {
			if (!world.isRemote) {
				if (countNotNull(soldItems) != 0) {
					for(ItemStack sold: soldItems) {
						if(sold==null) continue;
						NBTTagCompound tag = new NBTTagCompound();
						sold.writeToNBT(tag);
						ItemStack vended = ItemStack.loadItemStackFromNBT(tag);

						if (!tileEntity.infinite) {
							tileEntity.inventory.takeItems(sold, sold.getItemDamage(), sold.stackSize);
						}

						EntityItem entityitem = new EntityItem(world, blockPos.getX() + 0.5, blockPos.getY() + 1.2, blockPos.getZ() + 0.5, vended);
						General.propelTowards(entityitem, entityplayer, 0.2);
						entityitem.motionY = 0.2;
						entityitem.setPickupDelay(10);
						world.spawnEntityInWorld(entityitem);
					}
				}

				world.playSoundEffect(blockPos.getX(), blockPos.getY(), blockPos.getZ(), "vending:cha-ching", 0.3f, 0.6f);

				if (offered != null) {
					ItemStack paid = offered.splitStack(bought.stackSize);
					if(offered.stackSize==0){
						entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
					}

					if(! tileEntity.infinite)
						tileEntity.inventory.addItemStackToInventory(paid, 0, 8);
				}

				if(! tileEntity.infinite)
					tileEntity.inventory.onInventoryChanged();
			}
		} else {
			world.playSoundEffect(blockPos.getX(), blockPos.getY(), blockPos.getZ(), "vending:forbidden", 1.0f, 1.0f);
		}
	}

	@Override
	public void onBlockClicked(World world, BlockPos blockPos, EntityPlayer entityplayer) {
		TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos);
		if (tileEntity == null)
			return;

		if (! entityplayer.getDisplayNameString().equals(tileEntity.getOwnerName()) || !tileEntity.inventory.isEmpty()){
			vend(world, blockPos, entityplayer);
			return;
		}

		dropBlockAsItem(world, blockPos, world.getBlockState(blockPos), 0);
		world.setBlockToAir(blockPos);
		world.playSoundEffect(blockPos.getX(), blockPos.getY(), blockPos.getZ(), "vending:cha-ching", 0.3f, 0.6f);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState state, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY, float hitZ){
		TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getTileEntity(blockPos);
		if (tileEntity == null)
			return false;

		if(entityPlayer.inventory.getCurrentItem()!=null && entityPlayer.inventory.getCurrentItem().getItem()==Vending.itemWrench){
			Vending.guiWrench.open(entityPlayer, world, blockPos);
			return true;
		}

		if (entityPlayer.getDisplayNameString().equals(tileEntity.getOwnerName()) && !entityPlayer.isSneaking()) {
			Vending.guiVending.open(entityPlayer, world, blockPos);

			return true;
		}

		if (entityPlayer.capabilities.isCreativeMode && !entityPlayer.isSneaking()) {
			Vending.guiVending.open(entityPlayer, world, blockPos);

			return true;
		}

		vend(world, blockPos, entityPlayer);

		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos blockPos, IBlockState state, EntityLivingBase entityLiving, ItemStack stack){
		world.setBlockState(blockPos, getStateFromMeta(stack.getMetadata()));
		TileEntityVendingMachine e = new TileEntityVendingMachine();
		e.advanced=isAdvanced;
		e.multiple=isMultiple;

		if (entityLiving != null) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			e.setOwnerName(player.getDisplayNameString());
			world.setTileEntity(blockPos, e);
		}
	}


	@Override
	public TileEntity createNewTileEntity(World var1, int metadata) {
		TileEntityVendingMachine e=new TileEntityVendingMachine();
		e.advanced=isAdvanced;
		e.multiple=isMultiple;

		return e;
	}

	@Override
	public void breakBlock(World world, BlockPos blockPos, IBlockState state) {
		TileEntityVendingMachine tileEntityChest = (TileEntityVendingMachine) world.getTileEntity(blockPos);

		if (tileEntityChest == null)
			return;

		for (int l = 0; l < tileEntityChest.getSizeInventory(); l++) {
			ItemStack itemstack = tileEntityChest.getStackInSlot(l);
			if (itemstack == null)
				continue;
			if (l==10 && tileEntityChest.advanced)
				continue;

			float f = world.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
			while (itemstack.stackSize > 0) {
				int i1 = world.rand.nextInt(21) + 10;
				if (i1 > itemstack.stackSize) {
					i1 = itemstack.stackSize;
				}

				itemstack.stackSize -= i1;
				NBTTagCompound tag = new NBTTagCompound();
				itemstack.writeToNBT(tag);
				ItemStack toSpawn = ItemStack.loadItemStackFromNBT(tag);
				toSpawn.stackSize = i1;
				EntityItem entityitem = new EntityItem(world, blockPos.getX() + f, blockPos.getY() + f1, blockPos.getZ() + f2, toSpawn);
				float f3 = 0.05F;
				entityitem.motionX = (float) world.rand.nextGaussian() * f3;
				entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
				world.spawnEntityInWorld(entityitem);
			}
		}

		super.breakBlock(world, blockPos, state);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List list) {
		for (int i = 0; i < EnumSupports.length; ++i) list.add(new ItemStack(item, 1, i));
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(SUPPORT, EnumSupports.byMetadata(meta));
	}

	public int getMetaFromState(IBlockState state)
	{
		return ((EnumSupports)state.getValue(SUPPORT)).getMetadata();
	}

	protected BlockState createBlockState()
	{
		return new BlockState(this, SUPPORT);
	}
}
