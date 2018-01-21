package info.jbcs.minecraft.vending.inventory;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NoSuchAccountException;
import com.kamildanak.minecraft.enderpay.api.NotABanknoteException;
import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.entity.player.EntityPlayer;
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
        return Utils.originalValueCreditsSum(super.getSoldItems());
    }

    @Optional.Method(modid = "enderpay")
    public long boughtCreditsSum() {
        return Utils.originalValueCreditsSum(super.getBoughtItems());
    }


    @Optional.Method(modid = "enderpay")
    public long takeCredits(EntityPlayer entityplayer) {
        try {
            long amount = EnderPayApi.getBanknoteOriginalValue(super.getBoughtItems().get(0));
            EnderPayApi.addToBalance(entityplayer.getUniqueID(), -amount);
            return amount;
        } catch (NoSuchAccountException | NotABanknoteException ignored) {
        }
        return 0;
    }

    @Optional.Method(modid = "enderpay")
    public void giveCredits(long amount) {
        if (te.isInfinite()) return;
        long leftToTake = Utils.takeCredits(this, getInventorySlots(), amount);
        if (leftToTake > 0)
        {
            Utils.takeCredits(this, getSellSlots(), leftToTake);
            if (Vending.settings.shouldCloseOnPartialSoldOut()) te.setOpen(false);
        }
    }

    @Optional.Method(modid = "enderpay")
    public long getCurrentValueInventoryCreditsSum() {
        return Utils.currentValueCreditsSum(getInventoryItems());
    }

    @Optional.Method(modid = "enderpay")
    public long getCurrentValueTotalCreditsSum() {
        return Utils.currentValueCreditsSum(getInventoryItems()) + Utils.currentValueCreditsSum(super.getSoldItems());
    }

    @Optional.Method(modid = "enderpay")
    public boolean hasBanknoteInStorage() {
        NonNullList<ItemStack> stacks = getInventoryItems();
        for (ItemStack itemStack : stacks) {
            if (Utils.isBanknote(itemStack)) return true;
        }
        return false;
    }

    private boolean hasEnoughCredits() {
        if (te.isInfinite() || !Loader.isModLoaded("enderpay")) return true;
        long soldSum = soldCreditsSum();
        long realTotalSum = getCurrentValueTotalCreditsSum();
        return soldSum <= realTotalSum;
    }

    @Override
    public boolean checkIfFits(@Nonnull ItemStack offered) {
        if (!Loader.isModLoaded("enderpay")) return super.checkIfFits(offered);
        NonNullList<ItemStack> soldItems = super.getSoldItems();
        ItemStack bought = super.getBoughtItems().get(0);
        if (Loader.isModLoaded("enderpay")) {
            if (bought.isEmpty() && soldCreditsSum() > 0) return true;
            if (Utils.isBanknote(bought) && boughtCreditsSum() == 0)
                return countNotNull(soldItems) > 0 || soldCreditsSum() > 0;
            if (Utils.isFilledBanknote(bought))
                return (boughtCreditsSum() > 0 && hasBanknoteInStorage() &&
                        Utils.hasPlaceForBanknote(getInventoryItems()));
        }
        return super.checkIfFits(offered);
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
                Utils.storeCredits(this, getInventorySlots(), creditsDelta);
            } else {
                giveCredits(-creditsDelta);
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
    public boolean hasSomethingToSell() {
        return soldCreditsSum() > 0 || super.hasSomethingToSell();
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
