package info.jbcs.minecraft.vending.inventory;

import com.kamildanak.minecraft.enderpay.EnderPay;
import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NotABanknoteException;
import info.jbcs.minecraft.vending.Utils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class AbstractInventoryExtended extends AbstractInventory{
    private int getEmptyCount(int[] slots) {
        int emptySlots = 0;
        for (int i: slots) {
            if (getStackInSlot(i).isEmpty()) emptySlots++;
        }
        return emptySlots;
    }

    public InsertionResultMultiple insertItems(NonNullList<ItemStack> items, int[] slots, boolean simulate) {
        NonNullList<ItemStack> itemStacksThatDidNotFit = NonNullList.create();
        items = Utils.joinItems(items);
        int emptySlots = getEmptyCount(slots);
        int emptySlotsUsed = 0;

        for (ItemStack itemStack2 : items) {
            InsertionResultSingle insertionResultSingle = insertItem(itemStack2, slots, emptySlots, simulate);
            emptySlots -= insertionResultSingle.getEmptySlotsUsed();
            emptySlotsUsed += insertionResultSingle.getEmptySlotsUsed();
            if (!insertionResultSingle.noItemsLeftToInsert()) {
                itemStacksThatDidNotFit.addAll(Utils.splitItemsStack(insertionResultSingle.getItemsLeft()));
            }
        }
        return new InsertionResultMultiple(itemStacksThatDidNotFit, emptySlotsUsed);
    }

    private InsertionResultSingle insertItem(ItemStack paid, int[] inventorySlots, int emptySlots, boolean simulate) {
        int emptySlotsUsed = 0;
        for (int i: inventorySlots) {
            if (paid.isEmpty()) return new InsertionResultSingle(paid, 0);
            if(!this.getStackInSlot(i).isEmpty()){
                paid = this.insertItem(i, paid, simulate);
            }
        }
        for (int i: inventorySlots) {
            if(emptySlots <= 0 || paid.isEmpty()) return new InsertionResultSingle(paid, emptySlotsUsed);
            if (this.getStackInSlot(i).isEmpty()) {
                emptySlotsUsed++;
                paid = this.insertItem(i, paid, simulate);
                emptySlots--;
            }
        }
        return new InsertionResultSingle(paid, emptySlotsUsed);
    }

    public InsertionResultSingle insertItem(ItemStack itemstack, int[] slots, boolean simulate) {
        int emptySlots = getEmptyCount(slots);
        return insertItem(itemstack, slots, emptySlots, simulate);
    }

    public ItemStack extractItem(ItemStack stack, int[] inventorySlots, boolean simulate) {
        int count = stack.getCount();
        int expected = count;
        for (int i : inventorySlots) {
            if (stack.isItemEqual(this.extractItem(i, count, true))) {
                count -= this.extractItem(i, count, simulate).getCount();
            }
        }
        return ItemHandlerHelper.copyStackWithSize(stack, expected - count);
    }





    @Optional.Method(modid = "enderpay")
    public boolean canStoreCredits(NonNullList<ItemStack> stacks) {
        int spacesForBanknotes = 0;
        int banknotes = 0;
        for (ItemStack itemStack : stacks) {
            if (Utils.isBanknote(itemStack) && itemStack.getCount()==1) return true;
            if (itemStack.isEmpty()) spacesForBanknotes++;
            if (Utils.isBanknote(itemStack))
            {
                banknotes += itemStack.getCount();
                spacesForBanknotes++;
            }
        }
        return (banknotes-2)/64 < spacesForBanknotes - 1;
    }

    @Optional.Method(modid = "enderpay")
    public void storeCredits(int[] slots, long credits) {
        int banknotes = 0;
        try {
            for (int i: slots) {
                ItemStack itemStack = getStackInSlot(i);
                if (Utils.isBanknote(itemStack)) {
                    if (itemStack.getCount() == 1) {
                        setInventorySlotContents(i,
                                EnderPayApi.getBanknote(credits + EnderPayApi.getBanknoteCurrentValue(itemStack)));

                        return;
                    }
                    banknotes += itemStack.getCount();
                }
            }
            for (int i: slots) {
                if (Utils.isBanknote(getStackInSlot(i))) {
                    setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
            for (int i: slots) {
                if (getStackInSlot(i).isEmpty()) {
                    setInventorySlotContents(i,EnderPayApi.getBanknote(credits));
                    banknotes--;
                    break;
                }
            }
            storeBlankBanknotes(slots, banknotes);
        } catch (NotABanknoteException ignored) {
        }
    }

    @Optional.Method(modid = "enderpay")
    private void storeBlankBanknotes(int[] slots, int banknotes) {
        for (int i: slots) {
            if (getStackInSlot(i).isEmpty()) {
                if(banknotes<=0) break;
                int m = Math.min(banknotes, 64);
                banknotes -= m;
                setInventorySlotContents(i, new ItemStack(EnderPay.itemBlankBanknote, m));
            }
        }
    }

    @Optional.Method(modid = "enderpay")
    public long takeCredits(int[] slots, long credits){
        int banknotes = 0;
        long creditsSum = 0;
        for (int i: slots) {
            if (Utils.isBanknote(getStackInSlot(i))) {
                banknotes += getStackInSlot(i).getCount();
                try {
                    creditsSum += EnderPayApi.getBanknoteCurrentValue(getStackInSlot(i));
                } catch (NotABanknoteException ignored) {
                }
                setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
        for (int i: slots) {
            if (getStackInSlot(i).isEmpty()) {
                long toStoreBack = creditsSum - credits;
                if (toStoreBack > 0)
                {
                    setInventorySlotContents(i, EnderPayApi.getBanknote(toStoreBack));
                    banknotes--;
                }
                storeBlankBanknotes(slots, banknotes);
                if (toStoreBack > 0) return 0;
                return -toStoreBack;
            }
        }
        return creditsSum;
    }
}
