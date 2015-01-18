package info.jbcs.minecraft.vending;

import info.jbcs.minecraft.utilities.General;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockVendingMachine extends BlockContainer {
	Block[] supportBlocks;
	boolean isAdvanced;

	Icon iconTop, iconSide;

	public BlockVendingMachine(int id, Block[] supports,boolean advanced) {
		super(id,Material.glass);	

		supportBlocks = supports;

		setStepSound(soundGlassFootstep);
		setCreativeTab(Vending.tabVending);
		
		setHardness(0.3F);
		setResistance(6000000.0F);
		setBlockUnbreakable();
		
		setStepSound(Block.soundGlassFootstep);

		setBlockBounds(0.0625f, 0.125f, 0.0625f, 0.9375f, 0.9375f, 0.9375f);
		
		isAdvanced=advanced;
	}

	void vend(World world, int i, int j, int k, EntityPlayer entityplayer){
		TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getBlockTileEntity(i, j, k);
		if (tileEntity == null)
			return;

		ItemStack sold = tileEntity.getSoldItem();
		ItemStack bought = tileEntity.getBoughtItem();
		ItemStack offered = entityplayer.inventory.getCurrentItem();

		boolean fits = true;

		if (bought == null) {
			offered = null;
			if (sold == null)
				fits = false;
		} else {
			if(! tileEntity.doesStackFit(bought))
				fits = false;
			else if (offered == null)
				fits = false;
			else if (bought.itemID != offered.itemID)
				fits = false;
			else if (bought.getItemDamage() != offered.getItemDamage())
				fits = false;
			else if (offered.stackSize < bought.stackSize)
				fits = false;
		}

		if (fits) {
			if (!world.isRemote) {
				if (sold != null) {
					NBTTagCompound tag=new NBTTagCompound();
					sold.writeToNBT(tag);
					ItemStack vended = ItemStack.loadItemStackFromNBT(tag);
					
					if(! tileEntity.infinite)
						tileEntity.inventory.takeItems(sold.itemID, sold.getItemDamage(), sold.stackSize);
					
					EntityItem entityitem = new EntityItem(world, i + 0.5, j + 1.2, k + 0.5, vended);
					General.propelTowards(entityitem, entityplayer, 0.2);
					entityitem.motionY = 0.2;
					entityitem.delayBeforeCanPickup = 10;
					world.spawnEntityInWorld(entityitem);
				}

				world.playSoundEffect(i, j, k, "random.cha-ching", 0.3f, 0.6f);

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
			world.playSoundEffect(i, j, k, "random.forbidden", 1.0f, 1.0f);
		}
	}

	@Override
	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
		TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getBlockTileEntity(i, j, k);
		if (tileEntity == null)
			return;

		if (! entityplayer.username.equals(tileEntity.ownerName) || ! tileEntity.inventory.isEmpty()){
			vend(world, i, j, k, entityplayer);
			return;
		}

		dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
		world.setBlock(i, j, k, 0);
		world.playSoundEffect(i, j, k, "random.cha-ching", 0.3f, 0.6f);
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int a, float b, float x, float y) {
		TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) world.getBlockTileEntity(i, j, k);
		if (tileEntity == null)
			return false;

		if(entityplayer.inventory.getCurrentItem()!=null && entityplayer.inventory.getCurrentItem().itemID==Vending.itemWrench.itemID){
			Vending.guiWrench.open(entityplayer, world, i, j, k);
			return true;
		}
		
		if (entityplayer.username.equals(tileEntity.ownerName) && !entityplayer.isSneaking()) {
			Vending.guiVending.open(entityplayer, world, i, j, k);
			
			return true;
		}
		
		if (entityplayer.capabilities.isCreativeMode && !entityplayer.isSneaking()) {
			Vending.guiVending.open(entityplayer, world, i, j, k);
			
			return true;
		}

		vend(world, i, j, k, entityplayer);

		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
		TileEntityVendingMachine e = new TileEntityVendingMachine();
		e.advanced=isAdvanced;

		if (entityliving instanceof EntityLivingBase) {
			EntityPlayer player = (EntityPlayer) entityliving;
			e.ownerName = player.username;
			world.setBlockTileEntity(i, j, k, e);
		}
	}

	@Override
	public int getRenderBlockPass() {
		return 0;
	}

	@Override
	public Icon getIcon(int side, int meta) {
		return side < 2 ? iconTop : iconSide;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		TileEntityVendingMachine e=new TileEntityVendingMachine();
		e.advanced=isAdvanced;
		
		return e;
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, int a, int b) {
		TileEntityVendingMachine tileentitychest = (TileEntityVendingMachine) world.getBlockTileEntity(i, j, k);

		if (!(tileentitychest instanceof TileEntityVendingMachine))
			return;

		
		
		for (int l = 0; l < tileentitychest.getSizeInventory(); l++) {
			ItemStack itemstack = tileentitychest.getStackInSlot(l);
			if (itemstack == null)
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
				EntityItem entityitem = new EntityItem(world, i + f, j + f1, k + f2, new ItemStack(itemstack.itemID, i1, itemstack.getItemDamage()));
				float f3 = 0.05F;
				entityitem.motionX = (float) world.rand.nextGaussian() * f3;
				entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
				world.spawnEntityInWorld(entityitem);
			}
		}

		super.breakBlock(world, i, j, k, a, b);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public void registerIcons(IconRegister register) {
		iconTop = register.registerIcon("Vending:vendor-top");
		iconSide = register.registerIcon("Vending:vendor-side");
	}

	@Override
	public int getRenderType() {
		return BlockVendingMachineRenderer.id;
	}

	@Override
	public void getSubBlocks(int id, CreativeTabs par2CreativeTabs, List list) {
		for (int i = 0; i < supportBlocks.length; ++i) {
			list.add(new ItemStack(id, 1, i));
		}
	}
}
