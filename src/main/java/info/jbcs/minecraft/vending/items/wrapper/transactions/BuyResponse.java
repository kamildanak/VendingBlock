package info.jbcs.minecraft.vending.items.wrapper.transactions;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BuyResponse {
    public static BuyResponse UNCOMPLETED =
            new BuyResponse(0, null, NonNullList.create(), false);
    private ItemStack change;
    private NonNullList<ItemStack> returnedStacks;
    private boolean transactionCompleted;
    private long creditsToReturn;

    BuyResponse(long creditsToReturn, ItemStack change,
                NonNullList<ItemStack> returnedStacks, boolean transactionCompleted) {
        this.creditsToReturn = creditsToReturn;
        this.change = change;
        this.returnedStacks = returnedStacks;
        this.transactionCompleted = transactionCompleted;
    }

    public long getCreditsToReturn() {
        return creditsToReturn;
    }

    public ItemStack getChange() {
        return change;
    }

    public NonNullList<ItemStack> getReturnedStacks() {
        return returnedStacks;
    }

    public boolean isTransactionCompleted() {
        return transactionCompleted;
    }
}
