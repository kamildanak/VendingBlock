package info.jbcs.minecraft.vending.inventory;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public abstract class InventoryStatic implements IInventory {
	public final ItemStack items[];

	public InventoryStatic(int size) {
		items = new ItemStack[size];
	}

	@Override
	public String getName(){
		return null;
	}

	@Override
	public IChatComponent getDisplayName(){
		return null;
	}

	@Override
	public boolean hasCustomName(){
		return true;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	public void onInventoryChanged(int slot) {
	}

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return items[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (items[i] != null) {
			if (items[i].stackSize <= j) {
				ItemStack itemstack = items[i];
				items[i] = null;
				onInventoryChanged();
				onInventoryChanged(i);
				return itemstack;
			}

			ItemStack itemstack1 = items[i].splitStack(j);

			if (items[i].stackSize == 0) {
				items[i] = null;
			}

			onInventoryChanged();
			onInventoryChanged(i);
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		items[i] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}

		onInventoryChanged();
		onInventoryChanged(i);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		NBTTagList nbtTagList = nbtTagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbtTagList.tagCount(); ++i) {
			NBTTagCompound nbtTagCompound1 = nbtTagList.getCompoundTagAt(i);
			int j = nbtTagCompound1.getByte("slot") & 0xff;

			items[j] = ItemStack.loadItemStackFromNBT(nbtTagCompound1);
		}

		onInventoryChanged();
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				continue;
			}

			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setByte("slot", (byte) i);
			items[i].writeToNBT(nbttagcompound1);
			nbttaglist.appendTag(nbttagcompound1);
		}

		nbttagcompound.setTag("Items", nbttaglist);
	}

	private int getFirstEmptyStack(int start, int end) {
		for (int i = start; i <= end; i++) {
			if (items[i] == null) {
				return i;
			}
		}

		return -1;
	}

	private int storeItemStack(ItemStack itemstack, int start, int end) {
		for (int i = start; i <= end; i++) {
			if (items[i] != null &&
					items[i].getItem() == itemstack.getItem() &&
					items[i].isStackable() &&
					items[i].stackSize < items[i].getMaxStackSize() &&
					items[i].stackSize < getInventoryStackLimit() &&
					(!items[i].getHasSubtypes() || items[i].getItemDamage() == itemstack.getItemDamage())) {
                if(items[i].hasTagCompound() || itemstack.hasTagCompound()){
                    if(items[i].hasTagCompound() && itemstack.hasTagCompound()) {
                        if (items[i].getTagCompound().equals(itemstack.getTagCompound())) {
                            return i;
                        }
                    }
                }else {
                    return i;
                }
			}
		}

		return -1;
	}

	private int storePartialItemStack(ItemStack itemstack, int start, int end) {
		Item i = itemstack.getItem();
		int j = itemstack.stackSize;
		int k = storeItemStack(itemstack, start, end);

		if (k < 0) {
			k = getFirstEmptyStack(start, end);
		}

		if (k < 0) {
			return j;
		}

		if (items[k] == null) {
			items[k] = new ItemStack(i, 0, itemstack.getItemDamage());
            if(itemstack.hasTagCompound()) {
                items[k].setTagCompound(itemstack.getTagCompound());
            }
		}

		int l = j;

		if (l > items[k].getMaxStackSize() - items[k].stackSize) {
			l = items[k].getMaxStackSize() - items[k].stackSize;
		}

		if (l > getInventoryStackLimit() - items[k].stackSize) {
			l = getInventoryStackLimit() - items[k].stackSize;
		}

		if (l == 0) {
			return j;
		} else {
			j -= l;
			items[k].stackSize += l;
			items[k].animationsToGo = 5;
			onInventoryChanged();
			onInventoryChanged(k);
			return j;
		}
	}

	public boolean addItemStackToInventory(ItemStack itemstack, int start, int end) {
		if (itemstack == null) {
			return true;
		}
		if (!itemstack.isItemDamaged()) {
			int i;
			do {
				i = itemstack.stackSize;
				itemstack.stackSize = storePartialItemStack(itemstack, start, end);
			} while (itemstack.stackSize > 0 && itemstack.stackSize < i);

			return itemstack.stackSize < i;
		}
		int j = getFirstEmptyStack(start, end);

		if (j >= 0) {
			items[j] = ItemStack.copyItemStack(itemstack);
            if(itemstack.hasTagCompound()){
                items[j].setTagCompound(itemstack.getTagCompound());
            }
			items[j].animationsToGo = 5;
			itemstack.stackSize = 0;
			onInventoryChanged();
			onInventoryChanged(j);
			return true;
		} else {
			return false;
		}
	}

	public boolean addItemStackToInventory(ItemStack itemstack) {
		return addItemStackToInventory(itemstack, 0, items.length - 1);
	}

	public ItemStack takeItems(ItemStack itemStack, int damage, int count) {
		ItemStack res = null;

		for (int i = 0; i < items.length; i++) {
			if (items[i] == null || items[i].getItem() != itemStack.getItem() || items[i].getItemDamage() != damage) {
				continue;
			}

            if(itemStack.hasTagCompound()) {
                if (! itemStack.getTagCompound().equals(items[i].getTagCompound())) {
                    continue;
                }
            }
			if (res == null) {
				res = new ItemStack(itemStack.getItem(), 0, damage);
			}

			while (items[i] != null && res.stackSize < count && items[i].stackSize > 0) {
				res.stackSize++;
				items[i].stackSize--;

				if (items[i].stackSize == 0) {
					items[i] = null;
				}

				onInventoryChanged(i);
			}

			if (res.stackSize >= count) {
				break;
			}
		}

		onInventoryChanged();
		return res;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	//@Override
	public void onInventoryChanged() {
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public int getFieldCount(){
		return 0;
	}

	@Override
	public void setField(int id, int value){

	}

	@Override
	public int getField(int id){
		return 0;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	public boolean isEmpty() {
		for (ItemStack item : items) {
			if (item != null) {
				return false;
			}
		}

		return true;
	}

	public void clear() {
		for (int i = 0; i < items.length; i++) {
			items[i] = null;
		}
	}
	
	public void throwItems(World world, int x, int y, int z){
		if(world.isRemote) return;
		
		for (int i = 0; i < items.length; i++) {
			ItemStack itemstack = items[i];
			if (itemstack == null) continue;
			
			items[i]=null;

			float xx = world.rand.nextFloat() * 0.8F + 0.1F;
			float yy = world.rand.nextFloat() * 0.8F + 0.1F;
			float zz = world.rand.nextFloat() * 0.8F + 0.1F;
			while (itemstack.stackSize > 0) {
				int c = world.rand.nextInt(21) + 10;
				if (c > itemstack.stackSize) {
					c = itemstack.stackSize;
				}

				itemstack.stackSize -= c;
				EntityItem entityitem = new EntityItem(world, x + xx, y + yy, z + zz, new ItemStack(itemstack.getItem(), c, itemstack.getItemDamage()));
				float f3 = 0.05F;
				entityitem.motionX = (float) world.rand.nextGaussian() * f3;
				entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
				world.spawnEntityInWorld(entityitem);
			}
		}

		onInventoryChanged();
	}
}
