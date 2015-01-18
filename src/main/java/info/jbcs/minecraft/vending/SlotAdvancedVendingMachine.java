package info.jbcs.minecraft.vending;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotAdvancedVendingMachine extends Slot {
	public SlotAdvancedVendingMachine(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemstack) {
		super.onPickupFromSlot(player, itemstack);
		player.inventory.setItemStack(null);
		putStack(new ItemStack(itemstack.itemID, itemstack.stackSize, itemstack.getItemDamage()));
	}
}
