package info.jbcs.minecraft.vending.items.wrapper;

import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.items.wrapper.transactions.InsertionResultMultiple;
import info.jbcs.minecraft.vending.items.wrapper.transactions.InsertionResultSingle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class AdvancedItemHandlerHelper extends ItemHandlerHelper {

    public static int countNotNull(@Nonnull NonNullList<ItemStack> itemStacks) {
        int counter = 0;
        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty()) counter++;
        }
        return counter;
    }

    @Nonnull
    public static NonNullList<ItemStack> insertItems(@Nonnull IItemHandlerModifiable itemHandler,
                                                     NonNullList<ItemStack> itemStacks, boolean simulate) {
        return insertItemsPrivate(itemHandler, itemStacks, simulate).getItemsLeft();
    }

    private static InsertionResultMultiple insertItemsPrivate(@Nonnull IItemHandlerModifiable itemHandler,
                                                              NonNullList<ItemStack> items, boolean simulate) {
        NonNullList<ItemStack> itemStacksThatDidNotFit = NonNullList.create();
        items = Utils.joinItems(items);
        int emptySlots = getEmptyCount(itemHandler);
        int emptySlotsUsed = 0;

        for (ItemStack itemStack2 : items) {
            InsertionResultSingle insertionResultSingle = insertItem(itemHandler, itemStack2, emptySlots, simulate);
            emptySlots -= insertionResultSingle.getEmptySlotsUsed();
            emptySlotsUsed += insertionResultSingle.getEmptySlotsUsed();
            if (!insertionResultSingle.noItemsLeftToInsert()) {
                itemStacksThatDidNotFit.addAll(Utils.splitItemsStack(insertionResultSingle.getItemsLeft()));
            }
        }
        return new InsertionResultMultiple(itemStacksThatDidNotFit, emptySlotsUsed);
    }

    private static InsertionResultSingle insertItem(@Nonnull IItemHandlerModifiable itemHandler,
                                                    ItemStack itemStack, int emptySlots, boolean simulate) {
        int emptySlotsUsed = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (itemStack.isEmpty()) return new InsertionResultSingle(itemStack, 0);
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                itemStack = itemHandler.insertItem(i, itemStack, simulate);
            }
        }
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (emptySlots <= 0 || itemStack.isEmpty()) return new InsertionResultSingle(itemStack, emptySlotsUsed);
            if (itemHandler.getStackInSlot(i).isEmpty()) {
                emptySlotsUsed++;
                itemStack = itemHandler.insertItem(i, itemStack, simulate);
                emptySlots--;
            }
        }
        return new InsertionResultSingle(itemStack, emptySlotsUsed);
    }

    private static int getEmptyCount(@Nonnull IItemHandlerModifiable itemHandler) {
        int emptySlots = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (itemHandler.getStackInSlot(i).isEmpty()) emptySlots++;
        }
        return emptySlots;
    }

    public static boolean areStacksEqualIgnoreCount(ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty() && stack2.isEmpty()) return true;
        if (stack1.getCount() == 0 || stack2.getCount() == 0) return false;
        stack1 = stack1.copy();
        stack2 = stack2.copy();
        stack1.setCount(1);
        stack2.setCount(1);
        return ItemStack.areItemStacksEqual(stack1, stack2);
    }
}
