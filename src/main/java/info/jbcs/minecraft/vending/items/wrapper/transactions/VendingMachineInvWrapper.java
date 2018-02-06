package info.jbcs.minecraft.vending.items.wrapper.transactions;

import info.jbcs.minecraft.vending.EnderPayApiUtils;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.forge.LoaderWrapper;
import info.jbcs.minecraft.vending.inventory.InventoryVendingMachine;
import info.jbcs.minecraft.vending.items.wrapper.*;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;


public class VendingMachineInvWrapper {
    private InventoryVendingMachine inventory;
    private TileEntityVendingMachine machine;
    private IItemHandlerAdvanced soldHandler;
    private IItemHandlerAdvanced boughtHandler;


    public VendingMachineInvWrapper(TileEntityVendingMachine machine, InventoryVendingMachine inventory) {
        soldHandler = new AdvancedInventoryWrapper(new VendingMachineSoldInvWrapper(inventory));
        boughtHandler = new AdvancedInventoryWrapper(new VendingMachineBoughtInvWrapper(inventory));
        this.inventory = inventory;
        this.machine = machine;
    }

    public IItemHandlerAdvanced getSoldHandler() {
        return soldHandler;
    }

    public IItemHandlerAdvanced getStorageHandler() {
        if (inventory.hasAttachedStorage()) {
            return new AdvancedInventoryWrapper(new VendingMachineCombinedInvWrapper(
                    new VendingMachineStorageInvWrapper(inventory),
                    new StorageAttachmentStorageInvWrapper(inventory.getAttachedStorage())
            ));
        }
        return new AdvancedInventoryWrapper(new VendingMachineStorageInvWrapper(inventory));
    }

    public boolean Accepts(ItemStack stack) {
        return !EnderPayApiUtils.isFilledBanknote(stack) && boughtHandler.containsItem(stack);
    }

    public boolean Vends(ItemStack stack) {
        return !EnderPayApiUtils.isFilledBanknote(stack) && soldHandler.containsItem(stack);
    }

    public boolean isInfinite() {
        return machine.isInfinite();
    }

    boolean hasNothingToSell() {
        boolean noItemsToSell = AdvancedItemHandlerHelper.countNotNull(getSoldItemsWithoutFilledBanknotes()) == 0;
        if (!LoaderWrapper.isEnderPayLoaded()) return noItemsToSell;
        return noItemsToSell && soldHandler.getCreditsSum(false) == 0;
    }

    boolean hasNothingToBuy() {
        boolean noItemsToBuy = AdvancedItemHandlerHelper.countNotNull(boughtHandler.getItemStacksWithoutFilledBanknotes()) == 0;
        if (!LoaderWrapper.isEnderPayLoaded()) return noItemsToBuy;
        return noItemsToBuy && boughtHandler.getCreditsSum(false) == 0;
    }

    private void extractItem(ItemStack stack) {
        ItemStack extractedItem = getStorageHandler().extractItem(stack, false);
        ItemStack leftToExtract = stack.copy();
        leftToExtract.setCount(stack.getCount() - extractedItem.getCount());
        if (leftToExtract.getCount() > 0) {
            soldHandler.extractItem(leftToExtract, false);
            if (Vending.settings.shouldCloseOnPartialSoldOut()) machine.setOpen(false);
        }
    }

    public void extractItems(NonNullList<ItemStack> soldItems) {
        for (ItemStack item : soldItems) {
            extractItem(item);
        }
    }

    long storeCredits(long amount, boolean simulate) {
        if (!LoaderWrapper.isEnderPayLoaded()) return 0;
        if (machine.isInfinite()) return 0;
        long leftToTake = getStorageHandler().storeCredits(amount, simulate);
        if (leftToTake > 0) {
            leftToTake = soldHandler.storeCredits(-leftToTake, simulate);
            if (Vending.settings.shouldCloseOnPartialSoldOut()) machine.setOpen(false);
            return leftToTake;
        }
        return 0;
    }

    public long soldCreditsSum(boolean subtractTax) {
        return this.soldHandler.getCreditsSum(subtractTax);
    }

    public long getTotalAvailableCreditSums(boolean subtractTax) {
        return this.soldHandler.getCreditsSum(subtractTax) + this.getStorageHandler().getCreditsSum(subtractTax);
    }

    public long boughtCreditsSum(boolean subtractTax) {
        return this.boughtHandler.getCreditsSum(subtractTax);
    }

    public boolean hasBanknote() {
        return getStorageHandler().hasBanknote();
    }

    public NonNullList<ItemStack> getSoldItems() {
        return this.soldHandler.getItemStacks();
    }

    public NonNullList<ItemStack> getSoldItemsWithoutFilledBanknotes() {
        return this.soldHandler.getItemStacksWithoutFilledBanknotes();
    }

    public ItemStack getBoughtItemWithoutFilledBanknotes() {
        ItemStack itemStack = this.boughtHandler.getStackInSlot(0);
        if (!LoaderWrapper.isEnderPayLoaded() || !EnderPayApiUtils.isFilledBanknote(itemStack)) return itemStack;
        return ItemStack.EMPTY;
    }

    public ItemStack getBoughtItem() {
        return this.boughtHandler.getStackInSlot(0);
    }

    public void setBoughtItem(ItemStack boughtItem) {
        this.boughtHandler.setStackInSlot(0, boughtItem);
    }

    public NonNullList<ItemStack> getInventoryItems() {
        return getStorageHandler().getItemStacks();
    }

    private boolean hasEnoughCreditsToReturn() {
        if (machine.isInfinite() || !LoaderWrapper.isEnderPayLoaded()) return true;
        long soldSum = soldHandler.getCreditsSum(false);
        long realTotalSum = getTotalAvailableCreditSums(true);
        return soldSum <= realTotalSum;
    }

    @Nonnull
    public BuyResponse Accept(BuyRequest request) {
        return request.Visit(this);
    }

    public VendingStatus getVendingStatus() {
        if (!machine.isOpen()) return VendingStatus.CLOSED;
        if (hasNothingToSell() && hasNothingToBuy()) return VendingStatus.NOTHING_TO_TRADE;
        if (!machine.isInfinite()) {
            if (!hasEnoughCreditsToReturn()) return VendingStatus.NOT_ENOUGH_CREDITS_IN_STORAGE;
            if (boughtCreditsSum(false) > 0) {
                if (storeCredits(getCreditsToTake(), true) != 0) {
                    if (getStorageHandler().hasBanknote()) return VendingStatus.NO_BANKNOTE_IN_INVENTORY;
                    return VendingStatus.NOT_ENOUGH_SPACE_TO_STORE_ITEMS;
                }
            } else {
                if (!getStorageHandler().insertItem(getBoughtItem(), true).isEmpty())
                    return VendingStatus.NOT_ENOUGH_SPACE_TO_STORE_ITEMS;
            }

        }
        return VendingStatus.OPEN;
    }

    public long getCreditsToReturn() {
        return soldCreditsSum(false) - boughtCreditsSum(false);
    }


    public long getCreditsToTake() {
        return -getCreditsToReturn();
    }

    public VendingStatus getVendingStatus(long offeredCredits, ItemStack offeredStack) {
        VendingStatus status = getVendingStatus();
        if (status != VendingStatus.OPEN) return status;
        if (getBoughtItemWithoutFilledBanknotes() != ItemStack.EMPTY) {
            if (!AdvancedItemHandlerHelper.areStacksEqualIgnoreCount(getBoughtItem(), offeredStack))
                return VendingStatus.WRONG_ITEM_OFFERED_TYPE;
            if (offeredStack.getCount() < getBoughtItem().getCount()) return VendingStatus.NOT_ENOUGH_ITEM_OFFERED;
        }
        if (offeredCredits < boughtCreditsSum(false)) return VendingStatus.NOT_ENOUGH_CREDITS_OFFERED;
        return VendingStatus.OPEN;
    }

    public void setOpen(boolean open) {
        this.machine.setOpen(open);
    }
}