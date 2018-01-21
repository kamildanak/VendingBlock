package info.jbcs.minecraft.vending.inventory;

import com.kamildanak.minecraft.foamflower.inventory.InventoryStatic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class InventoryStaticExtended extends InventoryStatic {
    public InventoryStaticExtended(int size) {
        super(size);
    }

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

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.getStackInSlot(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.getStackInSlot(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.setStackInSlot(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
            }
            return existing;
        } else {
            if (!simulate) {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }
}










