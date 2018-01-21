package info.jbcs.minecraft.vending.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class AbstractInventoryExtended extends AbstractInventory{
    public InsertionResultMultiple insertItems(NonNullList<ItemStack> items, int[] slots, boolean simulate) {
        NonNullList<ItemStack> itemStacksThatDidNotFit = NonNullList.create();
        items = joinItems(items);
        int emptySlots = 0;
        int emptySlotsUsed = 0;
        for (int i: slots) {
            if (getStackInSlot(i).isEmpty()) emptySlots++;
        }
        for (ItemStack itemStack2 : items) {
            InsertionResultSingle insertionResultSingle = insertItem(itemStack2, slots, emptySlots, simulate);
            emptySlots -= insertionResultSingle.emptySlotsUsed;
            emptySlotsUsed += insertionResultSingle.emptySlotsUsed;
            if (!insertionResultSingle.itemsLeft.isEmpty()) {
                itemStacksThatDidNotFit.addAll(splitItemsStack(insertionResultSingle.itemsLeft));
            }
        }
        return new InsertionResultMultiple(itemStacksThatDidNotFit, emptySlotsUsed);
    }

    private NonNullList<ItemStack> splitItemsStack(ItemStack stack) {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        for(int i = stack.getCount(); i>0; i++) {
            if(stack.getCount() <= 0) return itemStacks;
            itemStacks.add(stack.splitStack(stack.getMaxStackSize()));
        }
        return itemStacks;
    }

    private NonNullList<ItemStack> joinItems(NonNullList<ItemStack> items)
    {
        NonNullList<ItemStack> joinedItems = NonNullList.create();
        for (ItemStack itemStack : items) {
            boolean added = false;
            for (ItemStack itemStack2 : joinedItems) {
                if (itemStack.isItemEqual(itemStack2)) {
                    itemStack2.setCount(itemStack2.getCount() + itemStack.getCount());
                    added = true;
                }
            }
            if (!added) joinedItems.add(itemStack.copy());
        }
        return joinedItems;
    }

    public InsertionResultSingle insertItem(ItemStack paid, int[] inventorySlots, int emptySlots, boolean simulate) {
        int emptySlotsUsed = 0;
        for (int i: inventorySlots) {
            if (paid.isEmpty()) return new InsertionResultSingle(paid, 0);
            if(!this.getStackInSlot(i).isEmpty()){
                paid = this.insertItem(i, paid, simulate);
            }
        }
        for (int i: inventorySlots) {
            if(emptySlots <= 0 || paid.isEmpty()) return new InsertionResultSingle(paid, emptySlotsUsed);
            if (this.getStackInSlot(i).isEmpty()) {
                emptySlotsUsed++;
                paid = this.insertItem(i, paid, simulate);
                emptySlots--;
            }
        }
        return new InsertionResultSingle(paid, emptySlotsUsed);
    }

    public ItemStack insertItem(ItemStack itemstack, int[] inventorySlots, boolean simulate) {
        return insertItem(itemstack, inventorySlots, Integer.MAX_VALUE, simulate).itemsLeft;
    }

    private class InsertionResultSingle {
        private int emptySlotsUsed;
        private ItemStack itemsLeft;

        public InsertionResultSingle(ItemStack itemsLeft, int emptySlotsUsed) {
            this.emptySlotsUsed = emptySlotsUsed;
            this.itemsLeft = itemsLeft;
        }

        public int getEmptySlotsUsed() {
            return emptySlotsUsed;
        }

        public ItemStack getItemsLeft() {
            return itemsLeft;
        }
    }

    public class InsertionResultMultiple {
        private int emptySlotsUsed;
        private NonNullList<ItemStack> itemsLeft;

        public InsertionResultMultiple(NonNullList<ItemStack> itemsLeft, int emptySlotsUsed) {
            this.emptySlotsUsed = emptySlotsUsed;
            this.itemsLeft = itemsLeft;
        }

        public int getEmptySlotsUsed() {
            return emptySlotsUsed;
        }

        public NonNullList<ItemStack> getItemsLeft() {
            return itemsLeft;
        }
    }

    public ItemStack extractItem(ItemStack stack, int[] inventorySlots, boolean simulate) {
        int count = stack.getCount();
        int expected = count;
        for (int i : inventorySlots) {
            if (stack.isItemEqual(this.extractItem(i, count, true))) {
                count -= this.extractItem(i, count, simulate).getCount();
            }
        }
        return ItemHandlerHelper.copyStackWithSize(stack, expected - count);
    }
}
