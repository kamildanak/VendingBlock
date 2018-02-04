package info.jbcs.minecraft.vending.items.wrapper.transactions;

import net.minecraft.item.ItemStack;

public class InsertionResultSingle {
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

    public boolean noItemsLeftToInsert() {
        return getItemsLeft().isEmpty();
    }
}
