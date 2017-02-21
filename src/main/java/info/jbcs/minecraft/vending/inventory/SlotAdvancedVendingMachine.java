package info.jbcs.minecraft.vending.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotAdvancedVendingMachine extends Slot {
    public SlotAdvancedVendingMachine(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    @Nonnull
    public ItemStack onTake(EntityPlayer player, @Nonnull ItemStack itemstack) {
        super.onTake(player, itemstack);
        player.inventory.setItemStack(ItemStack.EMPTY);
        putStack(new ItemStack(itemstack.getItem(), itemstack.getCount(), itemstack.getItemDamage()));
        return ItemStack.EMPTY;
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
