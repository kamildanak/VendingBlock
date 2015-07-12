package info.jbcs.minecraft.vending.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class SlotAdvancedVendingMachine extends Slot {
	public SlotAdvancedVendingMachine(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemstack) {
		super.onPickupFromSlot(player, itemstack);
		player.inventory.setItemStack(null);
		putStack(new ItemStack(itemstack.getItem(), itemstack.stackSize, itemstack.getItemDamage()));
	}

	@Override
	public IIcon getBackgroundIconIndex(){
		if(inventory.getStackInSlot(10) != null){
			return inventory.getStackInSlot(10).getIconIndex();
		}else{
			return null;
		}

	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return false;
	}
}
