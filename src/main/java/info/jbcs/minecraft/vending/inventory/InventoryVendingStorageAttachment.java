package info.jbcs.minecraft.vending.inventory;

import com.kamildanak.minecraft.foamflower.inventory.InventoryStatic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryVendingStorageAttachment extends InventoryStatic {
    public InventoryVendingStorageAttachment() {
        super(54);
    }

    public boolean doesStackFit(ItemStack itemstack, int start, int end) {
        for (int i = start; i <= end; i++) {
            itemstack = insertItem(i, itemstack, true);
        }
        return itemstack.isEmpty();
    }

    public boolean doesStacksFit(NonNullList<ItemStack> items, int start, int end) {
        NonNullList<ItemStack> itemStacksToFit = NonNullList.create();
        for (ItemStack itemStack : items)
        {
            boolean added = false;
            for (ItemStack itemStack2 : itemStacksToFit)
            {
                if (itemStack.isItemEqual(itemStack2)) {
                    itemStack2.setCount(itemStack2.getCount() + itemStack.getCount());
                    added = true;
                }
            }
            if(!added) itemStacksToFit.add(itemStack.copy());
        }
        for (ItemStack itemStack2 : itemStacksToFit)
        {
            if (!doesStackFit(itemStack2, start, end)) return false;
        }
        return true;
    }

    public ItemStack insertItemIntoStorage(ItemStack itemstack, boolean simulate) {
        for (int i = 0; i < 27; i++) {
            itemstack = insertItem(i, itemstack, simulate);
        }
        return itemstack;
    }
}
