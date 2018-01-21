package info.jbcs.minecraft.vending.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BuyResponse {
    private long takenCredits;
    private long returnedCredits;
    private ItemStack change;
    private NonNullList<ItemStack> returnedStacks;
    private boolean transactionCompleted;
    public static BuyResponse UNCOMPLETED =
            new BuyResponse(0,0,null, NonNullList.create(), false);

    public BuyResponse(long takenCredits, long returnedCredits, ItemStack change,
                       NonNullList<ItemStack> returnedStacks, boolean transactionCompleted) {
        this.takenCredits = takenCredits;
        this.returnedCredits = returnedCredits;
        this.change = change;
        this.returnedStacks = returnedStacks;
        this.transactionCompleted = transactionCompleted;
    }

    public long getTakenCredits() {
        return takenCredits;
    }

    public long getReturnedCredits() {
        return returnedCredits;
    }

    public long getCreditsDelta() {
        return returnedCredits - takenCredits;
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

    public void setTakenCredits(long takenCredits) {
        this.takenCredits = takenCredits;
    }

    public void setReturnedCredits(long returnedCredits) {
        this.returnedCredits = returnedCredits;
    }
}
