package info.jbcs.minecraft.vending.items.wrapper.transactions;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NoSuchAccountException;
import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.forge.LoaderWrapper;
import info.jbcs.minecraft.vending.items.wrapper.AdvancedInventoryWrapper;
import info.jbcs.minecraft.vending.items.wrapper.AdvancedItemHandlerHelper;
import info.jbcs.minecraft.vending.items.wrapper.IItemHandlerAdvanced;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

public class VendingHelper {
    public static boolean vend(TileEntityVendingMachine machine,
                               IItemHandlerAdvanced input, IItemHandlerAdvanced output, boolean simulate) {
        VendingMachineInvWrapper machineInvWrapper = machine.getInventoryWrapper();
        long availableCredits = input.getCreditsSum(true);
        if (AdvancedItemHandlerHelper.countNotNull(output.insertItems(machineInvWrapper.getSoldItems(), true)) != 0)
            return false;
        if (input.storeCredits(machineInvWrapper.getCreditsToReturn(), true) != 0) return false;
        for (int i = 0; i < input.getSlots(); i++) {
            BuyResponse response = machineInvWrapper.Accept(
                    new BuyRequest(availableCredits, input.getStackInSlot(i).copy(), simulate));
            if (response.isTransactionCompleted()) {
                input.setStackInSlot(i, response.getChange());
                input.storeCredits(response.getCreditsToReturn(), false);
                output.insertItems(response.getReturnedStacks(), false);
                return true;
            }
        }
        return false;
    }

    public static boolean vend(TileEntityVendingMachine machine, EntityPlayer entityPlayer, boolean simulate) {
        VendingMachineInvWrapper machineInvWrapper = machine.getInventoryWrapper();
        ItemStack offeredItem = entityPlayer.inventory.getCurrentItem();
        long availableCredits = Utils.getAvailableCredits(entityPlayer);
        IItemHandlerAdvanced playerInventory = new AdvancedInventoryWrapper(new PlayerMainInvWrapper(entityPlayer.inventory));
        if (Vending.settings.shouldTransferToInventory()) {
            if (AdvancedItemHandlerHelper.countNotNull(playerInventory.insertItems(machineInvWrapper.getSoldItems(), true)) != 0)
                return false;
        }
        BuyRequest buyRequest = new BuyRequest(availableCredits, offeredItem, simulate);
        BuyResponse response = machineInvWrapper.Accept(buyRequest);

        if (response.isTransactionCompleted()) {
            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, response.getChange());
            if (Vending.settings.shouldTransferToInventory()) {
                playerInventory.insertItems(response.getReturnedStacks(), false);
            } else {
                dispenseItems(machine, response.getReturnedStacks(), entityPlayer);
            }
            if (LoaderWrapper.isEnderPayLoaded()) {
                try {
                    EnderPayApi.addToBalance(entityPlayer.getUniqueID(), response.getCreditsToReturn());
                } catch (NoSuchAccountException ignored) {
                }
            }
            return true;
        }
        return false;
    }

    private static void dispenseItems(TileEntityVendingMachine machine,
                                      NonNullList<ItemStack> stacks, EntityPlayer entityPlayer) {
        for (ItemStack vended : stacks) {
            Utils.throwItemAtPlayer(entityPlayer, machine.getWorld(), machine.getPos(), vended);
        }
    }
}
