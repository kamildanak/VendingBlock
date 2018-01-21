package info.jbcs.minecraft.vending.inventory;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NoSuchAccountException;
import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.init.VendingSoundEvents;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingStorageAttachment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.IntStream;

import static info.jbcs.minecraft.vending.General.countNotNull;

public class InventoryVendingMachine extends InventoryStaticExtended {
    TileEntityVendingMachine te;

    public InventoryVendingMachine(TileEntityVendingMachine tileEntityVendingMachine) {
        super(14);
        te = tileEntityVendingMachine;
    }

    @Nonnull
    public int[] getSellSlots() {
        if (te.isMultiple()) return new int[]{9, 10, 11, 12};
        return new int[]{9};
    }

    private int getBoughtSlot() {
        return te.isMultiple() ? 13 : 10;
    }

    @Nonnull
    int[] getInventorySlots() {
        if (getAttachment() != null) {
            int firstOfAttachment = 14;
            return IntStream.concat(IntStream.rangeClosed(0, 8),
                    IntStream.rangeClosed(firstOfAttachment, firstOfAttachment+26)).toArray();
        }
        return IntStream.rangeClosed(0, 8).toArray();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= 14) {
            TileEntityVendingStorageAttachment attachment = getAttachment();
            if (attachment != null) return attachment.inventory.getStackInSlot(slot - 14);
        }
        return super.getStackInSlot(slot);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (slot >= 14) {
            TileEntityVendingStorageAttachment attachment = getAttachment();
            if (attachment != null) {
                attachment.inventory.setStackInSlot(slot - 14, stack);
                return;
            }
        }
        super.setStackInSlot(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        Utils.markBlockForUpdate(te.getWorld(), te.getPos());
    }

    @Nonnull
    private NonNullList<ItemStack> getItemsFromSlots(int[] slots) {
        NonNullList<ItemStack> stackNonNullList = NonNullList.create();
        for (int i : slots) {
            stackNonNullList.add(getStackInSlot(i));
        }
        return stackNonNullList;
    }

    @Nonnull
    public NonNullList<ItemStack> getSoldItems() {
        return getItemsFromSlots(getSellSlots());
    }

    @Nonnull
    public NonNullList<ItemStack> getBoughtItems() {
        return getItemsFromSlots(new int[]{getBoughtSlot()});
    }

    @Nonnull
    NonNullList<ItemStack> getInventoryItems() {
        return getItemsFromSlots(getInventorySlots());
    }

    public void setBoughtItem(ItemStack stack) {
        setInventorySlotContents(getBoughtSlot(), stack);
    }

    private boolean doesStackFit(ItemStack itemstack) {
        return insertItem(itemstack, true).isEmpty();
    }

    private ItemStack insertItem(ItemStack itemstack, boolean simulate) {
        return super.insertItem(itemstack, getInventorySlots(), simulate);
    }

    private NonNullList<ItemStack> giveItems() {
        NonNullList<ItemStack> itemsToDispense = NonNullList.create();
        NonNullList<ItemStack> soldItems = getSoldItems();
        if (countNotNull(soldItems) == 0) return itemsToDispense;

        for (ItemStack sold : soldItems) {
            if (sold.isEmpty()) continue;
            ItemStack vended = sold.copy();

            if (!te.isInfinite()) {
                extractItem(sold);
            }
            itemsToDispense.add(vended);
        }
        return itemsToDispense;
    }

    private void extractItem(ItemStack stack) {
        ItemStack extractedItem = extractItem(stack, getInventorySlots(), false);
        if (extractItem(stack, stack.getCount() - extractedItem.getCount(),
                9, 12, false).getCount() > 0 && Vending.settings.shouldCloseOnPartialSoldOut()) {
            te.setOpen(false);
        }
    }

    public boolean hasSomethingToSell() {
        return countNotNull(getSoldItems()) != 0;
    }

    private ItemStack takeItems(@Nonnull ItemStack offered) {
        offered = offered.copy();
        NonNullList<ItemStack> bought = getBoughtItems();
        if (countNotNull(bought) == 0) return offered;
        if (offered.isEmpty()) return offered;
        ItemStack paid = offered.splitStack(bought.get(0).getCount());
        if (offered.getCount() == 0) {
            offered = ItemStack.EMPTY;
        }

        if (!te.isInfinite())
            insertItem(paid, false);
        return offered;
    }

    public boolean checkIfFits(@Nonnull ItemStack offered) {
        NonNullList<ItemStack> soldItems = getSoldItems();
        ItemStack bought = getBoughtItems().get(0);
        if (bought.isEmpty()) return countNotNull(soldItems) > 0;
        return doesStackFit(bought) &&
                !offered.isEmpty() &&
                bought.getItem() == offered.getItem() &&
                bought.getItemDamage() == offered.getItemDamage() &&
                offered.getCount() >= bought.getCount() &&
                Objects.equals(bought.getTagCompound(), offered.getTagCompound());
    }

    public BuyResponse vend(BuyRequest request) {
        ItemStack offered = request.getOfferedStack();
        if (!te.isOpen() || !checkIfFits(offered)) {
            return BuyResponse.UNCOMPLETED;
        }
        return new BuyResponse(0, 0, takeItems(offered), giveItems(), true);
    }

    public void vend(InventoryStaticExtended inventory, int[] fromSlots, int[] toSlots) {
        if (te.getWorld().isRemote) return;
        if (!(inventory.insertItems(getSoldItems(), toSlots, true).getItemsLeft().size()==0)) return;

        NonNullList<ItemStack> itemStacks = Utils.itemStacksFromInventory(inventory, fromSlots);
        long availableFunds = Utils.currentValueCreditsSum(itemStacks);

        for (int i: fromSlots) {
            ItemStack stack = inventory.getStackInSlot(i).copy();
            BuyResponse response = vend(new BuyRequest(availableFunds, stack));
            if (!response.isTransactionCompleted()) continue;
            inventory.setStackInSlot(i, response.getChange());
            if (Loader.isModLoaded("enderpay")) {
                if (response.getCreditsDelta() > 0) {
                    Utils.storeCredits(inventory, fromSlots, response.getCreditsDelta());
                } else {
                    Utils.takeCredits(inventory, fromSlots, -response.getCreditsDelta());
                }
            }
            for (ItemStack itemStack : response.getReturnedStacks()) {
                inventory.insertItem(itemStack, toSlots, false);
            }

            if (Vending.settings.shouldCloseOnSoldOut() && !hasSomethingToSell()) {
                te.setOpen(false);
            }
            return;
        }
    }

    public void vend(EntityPlayer entityPlayer) {
        if (te.getWorld().isRemote) return;
        ItemStack offered = entityPlayer.inventory.getCurrentItem();
        long availableCredits = 0;
        if (Loader.isModLoaded("enderpay")) {
            try {
                availableCredits = EnderPayApi.getBalance(entityPlayer.getUniqueID());
            } catch (NoSuchAccountException ignored) {
            }
        }
        BuyResponse response = vend(new BuyRequest(availableCredits, offered));
        if (response.isTransactionCompleted()) {
            dispenseItems(entityPlayer);
            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, response.getChange());

            if (Loader.isModLoaded("enderpay")) {
                try {
                    EnderPayApi.addToBalance(entityPlayer.getUniqueID(), response.getCreditsDelta());
                } catch (NoSuchAccountException ignored) {
                }
            }
        }
        te.getWorld().playSound(null, te.getPos(),
                response.isTransactionCompleted() ? VendingSoundEvents.PROCESSED : VendingSoundEvents.FORBIDDEN,
                SoundCategory.MASTER, 0.3f, 0.6f);
    }

    private void dispenseItems(EntityPlayer entityPlayer) {
        for (ItemStack vended : this.giveItems()) {
            if (Vending.settings.shouldTransferToInventory() && entityPlayer.inventory.addItemStackToInventory(vended))
                continue;
            Utils.throwItemAtPlayer(entityPlayer, te.getWorld(), te.getPos(), vended);
        }
    }

    @Nonnull
    public NonNullList<ItemStack> getSoldItemsWithFilledBanknotes() {
        return getSoldItems();
    }

    @Nullable
    private TileEntityVendingStorageAttachment getAttachment() {
        TileEntity tileEntity = te.getWorld().getTileEntity(te.getPos().down());
        if (!(tileEntity instanceof TileEntityVendingStorageAttachment)) {
            return null;
        }
        return (TileEntityVendingStorageAttachment) tileEntity;
    }


    @Override
    @Nonnull
    public ItemStack decrStackSize(int index, int amount) {
        if (index >= 14) {
            TileEntityVendingStorageAttachment attachment = getAttachment();
            if (attachment != null) attachment.inventory.decrStackSize(index, amount);
        }
        return ItemStackHelper.getAndSplit(this.stacks, index, amount);
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int index) {
        if (index >= 14) {
            TileEntityVendingStorageAttachment attachment = getAttachment();
            if (attachment != null) attachment.inventory.removeStackFromSlot(index);
        }
        return ItemStackHelper.getAndRemove(this.stacks, index);
    }
}
