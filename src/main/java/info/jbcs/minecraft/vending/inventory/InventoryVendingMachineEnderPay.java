package info.jbcs.minecraft.vending.inventory;

import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;

import static info.jbcs.minecraft.vending.General.countNotNull;

public class InventoryVendingMachineEnderPay extends InventoryVendingMachine {
    public InventoryVendingMachineEnderPay(TileEntityVendingMachine tileEntityVendingMachine) {
        super(tileEntityVendingMachine);
    }

    @Optional.Method(modid = "enderpay")
    public long soldCreditsSum() {
        return Utils.originalValueCreditsSum(getSoldItems());
    }

    @Optional.Method(modid = "enderpay")
    public long boughtCreditsSum() {
        return Utils.originalValueCreditsSum(getBoughtItems());
    }

    @Optional.Method(modid = "enderpay")
    public void extractCredits(long amount) {
        if (te.isInfinite()) return;
        long leftToTake = takeCredits(getInventorySlots(), amount);
        if (leftToTake > 0)
        {
            takeCredits(getSellSlots(), leftToTake);
            if (Vending.settings.shouldCloseOnPartialSoldOut()) te.setOpen(false);
        }
    }

    @Optional.Method(modid = "enderpay")
    public long getCurrentValueTotalCreditsSum() {
        return Utils.currentValueCreditsSum(getInventoryItems()) + Utils.currentValueCreditsSum(super.getSoldItems());
    }

    private boolean hasEnoughCredits() {
        if (te.isInfinite() || !Loader.isModLoaded("enderpay")) return true;
        long soldSum = soldCreditsSum();
        long realTotalSum = getCurrentValueTotalCreditsSum();
        return soldSum <= realTotalSum;
    }

    @Override
    public boolean doesNotFit(@Nonnull ItemStack offered) {
        if (!Loader.isModLoaded("enderpay")) return super.doesNotFit(offered);
        NonNullList<ItemStack> soldItems = super.getSoldItems();
        ItemStack bought = super.getBoughtItems().get(0);
        if (Loader.isModLoaded("enderpay")) {
            if (bought.isEmpty() && soldCreditsSum() > 0) return false;
            if (Utils.isBanknote(bought) && boughtCreditsSum() == 0)
                return countNotNull(soldItems) <= 0 && soldCreditsSum() <= 0;
            if (Utils.isFilledBanknote(bought))
                return (boughtCreditsSum() <= 0 || !canStoreCredits(getInventorySlots()));
        }
        return super.doesNotFit(offered);
    }

    @Override
    public BuyResponse vend(BuyRequest request) {
        if (!hasEnoughCredits() || request.getAvailableCredits() < boughtCreditsSum()) {
            return BuyResponse.UNCOMPLETED;
        }
        BuyResponse response = super.vend(request);
        if (!response.isTransactionCompleted()) return response;
        if (!te.isInfinite())
        {
            long creditsDelta = boughtCreditsSum() - soldCreditsSum();
            if (creditsDelta > 0) {
                storeCredits(getInventorySlots(), creditsDelta);
            } else {
                extractCredits(-creditsDelta);
            }
        }

        response.setTakenCredits(boughtCreditsSum());
        response.setReturnedCredits(soldCreditsSum());
        return response;
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getSoldItems() {
        if (!Loader.isModLoaded("enderpay")) return super.getSoldItems();
        return Utils.filterBanknotes(super.getSoldItems());
    }

    @Override
    public boolean hasNothingToSell() {
        return soldCreditsSum() <= 0 && super.hasNothingToSell();
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getBoughtItems() {
        if (!Loader.isModLoaded("enderpay")) return super.getBoughtItems();
        return Utils.filterBanknotes(super.getBoughtItems());
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getSoldItemsWithFilledBanknotes() {
        if (!Loader.isModLoaded("enderpay")) return super.getSoldItems();
        return Utils.filterBlankBanknotes(super.getSoldItems());
    }
}
