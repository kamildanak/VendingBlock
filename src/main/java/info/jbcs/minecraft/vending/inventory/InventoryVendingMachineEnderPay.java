package info.jbcs.minecraft.vending.inventory;

import com.kamildanak.minecraft.enderpay.EnderPay;
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
        return creditsSum(super.getSoldItems());
    }

    @Optional.Method(modid = "enderpay")
    public long boughtCreditsSum() {
        return creditsSum(super.getBoughtItems());
    }


    @Optional.Method(modid = "enderpay")
    private long creditsSum(NonNullList<ItemStack> stacks) {
        long sum = 0;
        for (ItemStack itemStack : stacks) {
            if (itemStack.isEmpty()) continue;
            if (EnderPayApi.isValidFilledBanknote(itemStack)) {
                try {
                    sum += EnderPayApi.getBanknoteOriginalValue(itemStack);
                } catch (NotABanknoteException ignored) {
                }
            }
        }
        return sum;
    }

    @Optional.Method(modid = "enderpay")
    private long realCreditsSum(NonNullList<ItemStack> stacks) {
        long sum = 0;
        for (ItemStack itemStack : stacks) {
            if (itemStack.isEmpty()) continue;
            if (EnderPayApi.isValidFilledBanknote(itemStack)) {
                try {
                    sum += EnderPayApi.getBanknoteCurrentValue(itemStack);
                } catch (NotABanknoteException ignored) {
                }
            }
        }
        return sum;
    }

    @Optional.Method(modid = "enderpay")
    public ItemStack takeCredits(EntityPlayer entityplayer) {
        try {
            long amount = EnderPayApi.getBanknoteOriginalValue(super.getBoughtItems().get(0));
            EnderPayApi.addToBalance(entityplayer.getUniqueID(), -amount);
            return EnderPayApi.getBanknote(amount);
        } catch (NoSuchAccountException | NotABanknoteException ignored) {
        }
        return ItemStack.EMPTY;
    }

    @Optional.Method(modid = "enderpay")
    public void giveCredits(EntityPlayer entityplayer) {
        try {
            long soldAmount = soldCreditsSum();
            EnderPayApi.addToBalance(entityplayer.getUniqueID(), soldAmount);
            if (te.isInfinite()) return;
            long inventorySum = realInventoryCreditsSum();
            long totalSum = realTotalCreditsSum();
            int banknotes = 0;
            for (int i = 0; i < 9; i++) {
                if (Utils.isBanknote(te.inventory.getStackInSlot(i))) {
                    banknotes+=te.inventory.getStackInSlot(i).getCount();
                    te.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
            if (inventorySum >= soldAmount) {
                for (int i = 0; i < 9; i++) {
                    if (te.inventory.getStackInSlot(i).isEmpty()) {
                        if (inventorySum - soldAmount > 0)
                        {
                            te.inventory.setInventorySlotContents(i, EnderPayApi.getBanknote(inventorySum - soldAmount));
                            banknotes--;
                        }
                        break;
                    }
                }
            } else {
                if (te.isMultiple()) {
                    for (int i = 9; i < 13; i++) {
                        if (Utils.isFilledBanknote(te.inventory.getStackInSlot(i))) {
                            te.inventory.setInventorySlotContents(i,
                                    new ItemStack(EnderPay.itemBlankBanknote, 1));
                        }
                    }
                    for (int i = 9; i < 13; i++) {
                        if (Utils.isBanknote(te.inventory.getStackInSlot(i)) && te.inventory.getStackInSlot(i).getCount() == 1) {
                            if (totalSum - soldAmount > 0)
                                te.inventory.setInventorySlotContents(i, EnderPayApi.getBanknote(totalSum - soldAmount));
                            break;
                        }
                    }
                } else {
                    te.inventory.setInventorySlotContents(9,
                            totalSum - soldAmount > 0 ? EnderPayApi.getBanknote(totalSum - soldAmount) :
                                    new ItemStack(EnderPay.itemBlankBanknote, 1));
                }
                if (Vending.settings.shouldCloseOnPartialSoldOut()) te.setOpen(false);
            }
            storeBlankBanknotes(banknotes);
        } catch (NoSuchAccountException e) {
            e.printStackTrace();
        }
    }

    @Optional.Method(modid = "enderpay")
    public long realInventoryCreditsSum() {
        return realCreditsSum(getInventoryItems());
    }

    @Optional.Method(modid = "enderpay")
    public long realTotalCreditsSum() {
        return realCreditsSum(getInventoryItems()) + realCreditsSum(super.getSoldItems());
    }

    @Optional.Method(modid = "enderpay")
    public boolean hasBanknoteInStorage() {
        NonNullList<ItemStack> stacks = getInventoryItems();
        for (ItemStack itemStack : stacks) {
            if (Utils.isBanknote(itemStack)) return true;
        }
        return false;
    }

    public boolean checkIfPlayerHasEnoughCreditsForMachine(EntityPlayer entityPlayer) {
        if (!Utils.isBanknote(super.getBoughtItems().get(0))) return true;
        return Utils.checkIfPlayerHasEnoughCredits(entityPlayer, boughtCreditsSum());
    }

    private boolean hasEnoughCredits() {
        if (te.isInfinite() || !Loader.isModLoaded("enderpay")) return true;
        long soldSum = soldCreditsSum();
        long realTotalSum = realTotalCreditsSum();
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
                return (boughtCreditsSum() > 0 && Utils.hasPlaceForBanknote(getInventoryItems()));
        }
        return super.checkIfFits(offered);
    }

    @Override
    public boolean vend0(EntityPlayer entityplayer) {
        if (!hasEnoughCredits() || !checkIfPlayerHasEnoughCreditsForMachine(entityplayer)) {
            return false;
        }
        if (!super.vend0(entityplayer))
            return false;

        if (Loader.isModLoaded("enderpay")) {
            if (soldCreditsSum() > 0)
                giveCredits(entityplayer);
            if (boughtCreditsSum() > 0) {
                ItemStack takenBanknote = takeCredits(entityplayer);
                if (!te.isInfinite()) storeBanknote(takenBanknote);
            }
        }
        return true;
    }

    private void storeBanknote(ItemStack takenBanknote) {
        Utils.storeBanknote(this, 0,8, takenBanknote);
    }

    private void storeBlankBanknotes(int banknotes) {
        Utils.storeBlankBanknotes(this, 0,8, banknotes);
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
