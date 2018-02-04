package info.jbcs.minecraft.vending.items.wrapper.transactions;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

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

    public boolean noItemsLeftToInsert() {
        return getItemsLeft().isEmpty();
    }
}
