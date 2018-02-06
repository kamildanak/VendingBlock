package info.jbcs.minecraft.vending.items.wrapper.transactions;

import info.jbcs.minecraft.vending.Vending;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class BuyRequest {
    private long availableCredits;
    private ItemStack offeredStack;
    private boolean simulate;

    BuyRequest(long availableCredits, ItemStack offeredStack, boolean simulate) {
        this.availableCredits = availableCredits;
        this.offeredStack = offeredStack;
        this.simulate = simulate;
    }

    @Nonnull
    public BuyResponse Visit(VendingMachineInvWrapper vendingMachineInvWrapper) {
        if (!(vendingMachineInvWrapper.getVendingStatus(availableCredits, offeredStack) == VendingStatus.OPEN)) {
            return BuyResponse.UNCOMPLETED;
        }
        offeredStack = offeredStack.copy();
        BuyResponse response = new BuyResponse(
                vendingMachineInvWrapper.getCreditsToReturn(),
                splitOffered(offeredStack, vendingMachineInvWrapper.getBoughtItemWithoutFilledBanknotes()),
                vendingMachineInvWrapper.getSoldItemsWithoutFilledBanknotes(), true);

        if (!simulate && !vendingMachineInvWrapper.isInfinite()) {
            vendingMachineInvWrapper.getStorageHandler().insertItem(vendingMachineInvWrapper.getBoughtItemWithoutFilledBanknotes(), false);
            vendingMachineInvWrapper.storeCredits(vendingMachineInvWrapper.getCreditsToTake(), false);
            vendingMachineInvWrapper.extractItems(vendingMachineInvWrapper.getSoldItemsWithoutFilledBanknotes());
            if (Vending.settings.shouldCloseOnSoldOut() && vendingMachineInvWrapper.hasNothingToSell()) {
                vendingMachineInvWrapper.setOpen(false);
            }
        }
        return response;
    }

    private ItemStack splitOffered(@Nonnull ItemStack offered, @Nonnull ItemStack bought) {
        offered = offered.copy();
        if (offered.isEmpty() || bought.isEmpty()) return offered;
        offered.splitStack(bought.getCount());
        if (offered.getCount() == 0) offered = ItemStack.EMPTY;
        return offered;
    }
}
