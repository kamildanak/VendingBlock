package info.jbcs.minecraft.vending.items.wrapper;

import com.kamildanak.minecraft.enderpay.EnderPay;
import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NotABanknoteException;
import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.forge.LoaderWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class AdvancedInventoryWrapper extends DummyInventoryWrapper implements IItemHandlerAdvanced {
    public AdvancedInventoryWrapper(IItemHandlerModifiable itemHandlerModifiable) {
        super(itemHandlerModifiable);
    }

    public long storeCredits(long credits, boolean simulate) {
        if (!LoaderWrapper.isEnderPayLoaded()) return 0;
        return storeCreditsOptional(credits, simulate);
    }

    @Optional.Method(modid = "enderpay")
    private long takeCreditsOptional(long credits, boolean simulate) {
        if (credits < 0) return storeCreditsOptional(-credits, simulate);
        // Count banknotes, their current value and remove them from inventory
        int banknotes = 0;
        long creditsSum = 0;
        for (int i = 0; i < getSlots(); i++) {
            if (Utils.isBanknote(getStackInSlot(i))) {
                banknotes += getStackInSlot(i).getCount();
                try {
                    creditsSum += EnderPayApi.getBanknoteCurrentValue(getStackInSlot(i));
                } catch (NotABanknoteException ignored) {
                }
                if (!simulate) setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        if (simulate) return Math.max(0, -(creditsSum - credits));
        // Put one filled banknote, and multiple blank
        for (int i = 0; i < getSlots(); i++) {
            if (getStackInSlot(i).isEmpty()) {
                long toStoreBack = creditsSum - credits;
                if (toStoreBack > 0) {
                    setStackInSlot(i, EnderPayApi.getBanknote(toStoreBack));
                    banknotes--;
                }
                storeBlankBanknotes(banknotes);
                if (toStoreBack > 0) return 0;
                return -toStoreBack;
            }
        }
        return creditsSum;
    }


    @Optional.Method(modid = "enderpay")
    private long storeCreditsOptional(long credits, boolean simulate) {
        if (credits < 0) return takeCreditsOptional(-credits, simulate);
        if (!canStoreCredits()) return credits;
        if (simulate) return 0;
        int banknotes = 0;
        try {
            for (int i = 0; i < getSlots(); i++) {
                ItemStack itemStack = getStackInSlot(i);
                if (Utils.isBanknote(itemStack)) {
                    if (itemStack.getCount() == 1) {
                        setStackInSlot(i,
                                EnderPayApi.getBanknote(credits + EnderPayApi.getBanknoteCurrentValue(itemStack)));

                        return 0;
                    }
                    banknotes += itemStack.getCount();
                }
            }
            for (int i = 0; i < getSlots(); i++) {
                if (Utils.isBanknote(getStackInSlot(i))) {
                    setStackInSlot(i, ItemStack.EMPTY);
                }
            }
            for (int i = 0; i < getSlots(); i++) {
                if (getStackInSlot(i).isEmpty()) {
                    setStackInSlot(i, EnderPayApi.getBanknote(credits));
                    banknotes--;
                    break;
                }
            }
            storeBlankBanknotes(banknotes);
            return 0;
        } catch (NotABanknoteException ignored) {
        }
        return credits;
    }

    @Optional.Method(modid = "enderpay")
    private boolean canStoreCredits() {
        if (!hasBanknote()) return false;
        int spacesForBanknotes = 0;
        int banknotes = 0;
        for (int i = 0; i < getSlots(); i++) {
            ItemStack itemStack = getStackInSlot(i);
            if (Utils.isBanknote(itemStack) && itemStack.getCount() == 1) return true;
            if (itemStack.isEmpty()) spacesForBanknotes++;
            if (Utils.isBanknote(itemStack)) {
                banknotes += itemStack.getCount();
                spacesForBanknotes++;
            }
        }
        return (banknotes - 2) / 64 < spacesForBanknotes - 1;
    }

    @Optional.Method(modid = "enderpay")
    public boolean hasBanknote() {
        for (int i = 0; i < getSlots(); i++) {
            if (Utils.isBanknote(getStackInSlot(i))) return true;
        }
        return false;
    }

    @Optional.Method(modid = "enderpay")
    private void storeBlankBanknotes(int banknotes) {
        for (int i = 0; i < getSlots(); i++) {
            if (getStackInSlot(i).isEmpty()) {
                if (banknotes <= 0) break;
                int m = Math.min(banknotes, 64);
                banknotes -= m;
                setStackInSlot(i, new ItemStack(EnderPay.itemBlankBanknote, m));
            }
        }
    }

    @Override
    public boolean containsItem(ItemStack stack) {
        for (int i = 0; i < getSlots(); i++) {
            if (getStackInSlot(i).isItemEqual(stack)) return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getItemStacks() {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        for (int i = 0; i < getSlots(); i++) {
            itemStacks.add(getStackInSlot(i).copy());
        }
        return itemStacks;
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getItemStacksWithoutFilledBanknotes() {
        if (!LoaderWrapper.isEnderPayLoaded()) return getItemStacks();
        return Utils.filterFilledBanknotes(getItemStacks());
    }

    @Override
    public long getCreditsSum(boolean subtractTax) {
        if (!LoaderWrapper.isEnderPayLoaded()) return 0;
        return subtractTax ? Utils.currentValueCreditsSum(getItemStacks()) : Utils.originalValueCreditsSum(getItemStacks());
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> insertItems(NonNullList<ItemStack> itemStacks, boolean simulate) {
        return AdvancedItemHandlerHelper.insertItems(this, itemStacks, simulate);
    }

    /**
     * Inserts an ItemStack into any  and return the remainder.
     * The ItemStack should not be modified in this function!
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param itemStack ItemStack to insert.
     * @param simulate  If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return ItemStack.EMPTY).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    @Nonnull
    @Override
    public ItemStack insertItem(ItemStack itemStack, boolean simulate) {
        itemStack = itemStack.copy();
        for (int i = 0; i < getSlots(); i++) {
            itemStack = insertItem(i, itemStack, simulate);
        }
        return itemStack;
    }

    /**
     * Extracts an ItemStack from the given slot. The returned value must be null
     * if nothing is extracted, otherwise it's stack size must not be greater than amount or the
     * itemstacks getMaxStackSize().
     *
     * @param itemStack ItemStack to extract.
     * @param simulate  If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be ItemStack.EMPTY, if nothing can be extracted
     **/
    @Nonnull
    @Override
    public ItemStack extractItem(ItemStack itemStack, boolean simulate) {
        int count = itemStack.getCount();
        int extracted = 0;
        for (int i = 0; i < getSlots(); i++) {
            if (itemStack.isItemEqual(this.getStackInSlot(i))) {
                extracted += this.extractItem(i, count - extracted, simulate).getCount();
            }
        }
        return AdvancedItemHandlerHelper.copyStackWithSize(itemStack, extracted);
    }

}
