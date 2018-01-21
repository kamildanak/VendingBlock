package info.jbcs.minecraft.vending.inventory;

import net.minecraft.item.ItemStack;

public class BuyRequest {
    private long availableCredits;
    private ItemStack offeredStack;

    public BuyRequest(long availableCredits, ItemStack offeredStack) {
        this.availableCredits = availableCredits;
        this.offeredStack = offeredStack;
    }

    public long getAvailableCredits() {
        return availableCredits;
    }

    public ItemStack getOfferedStack() {
        return offeredStack;
    }
}
