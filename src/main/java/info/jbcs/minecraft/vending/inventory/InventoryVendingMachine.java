package info.jbcs.minecraft.vending.inventory;

import com.kamildanak.minecraft.foamflower.inventory.InventoryStatic;
import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.init.VendingSoundEvents;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingStorageAttachment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static info.jbcs.minecraft.vending.General.countNotNull;

public class InventoryVendingMachine extends InventoryStatic {
    TileEntityVendingMachine te;

    public InventoryVendingMachine(TileEntityVendingMachine tileEntityVendingMachine) {
        super(14);
        te = tileEntityVendingMachine;
    }

    @Nonnull
    private int[] getSellSlots() {
        if (te.isMultiple()) return new int[]{9, 10, 11, 12};
        return new int[]{9};
    }

    private int getBoughtSlot() {
        return te.isMultiple() ? 13 : 10;
    }

    @Nonnull
    private int[] getInventorySlots() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
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
        itemstack = super.insertItem(itemstack, 0, 8, simulate);
        TileEntityVendingStorageAttachment attachment = getAttachment();
        if (attachment == null) return itemstack;
        return attachment.inventory.insertItemIntoStorage(itemstack, simulate);
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
        TileEntityVendingStorageAttachment attachment = getAttachment();
        ItemStack stackFromMainInventory = extractItem(stack, stack.getCount(), 0, 8, false);
        ItemStack stackFromStorage = ItemStack.EMPTY;
        if (attachment != null) {
            stackFromStorage = attachment.extractItemFromStorage(stack,
                    stack.getCount() - stackFromMainInventory.getCount(), false);
        }
        if (extractItem(stack, stack.getCount() - stackFromMainInventory.getCount() - stackFromStorage.getCount(),
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

    public void vend(InventoryStatic inventory, int fromStartInclusive, int fromEndInclusive,
                     int toStartInclusive, int toEndInclusive) {
        if (te.getWorld().isRemote) return;
        if (te.inventory instanceof InventoryVendingMachineEnderPay) {
            if (((InventoryVendingMachineEnderPay) te.inventory).boughtCreditsSum() > 0) return;
            if (((InventoryVendingMachineEnderPay) te.inventory).soldCreditsSum() > 0) return;
        }
        for (int i = fromStartInclusive; i <= fromEndInclusive; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            NonNullList<ItemStack> vended = NonNullList.create();
            if (vend(inventory, i, stack, vended)) {
                for (ItemStack stack2 : vended) {
                    for (int j = toStartInclusive; j <= toEndInclusive; j++) {
                        if (stack2.isEmpty()) break;
                        stack2 = inventory.insertItem(j, stack2, false);
                    }
                }
                return;
            }
        }
    }

    private boolean vend(InventoryStatic inventory, int i, ItemStack offered, NonNullList<ItemStack> itemsToReturn) {
        ItemStack change = vend(offered, itemsToReturn);
        inventory.setStackInSlot(i, takeItems(offered));
        return !change.isItemEqual(offered) || change.getCount() != offered.getCount();
    }

    private ItemStack vend(ItemStack offered, NonNullList<ItemStack> itemsToReturn) {
        if (!te.isOpen() || !checkIfFits(offered)) {
            return offered;
        }
        itemsToReturn.addAll(giveItems());
        return takeItems(offered);
    }

    public void vend(EntityPlayer entityPlayer) {
        boolean result = vend0(entityPlayer);
        if (Vending.settings.shouldCloseOnSoldOut() && !hasSomethingToSell())
            te.setOpen(false);
        te.getWorld().playSound(null, te.getPos(),
                result ? VendingSoundEvents.PROCESSED : VendingSoundEvents.FORBIDDEN,
                SoundCategory.MASTER, 0.3f, 0.6f);
    }

    public boolean vend0(EntityPlayer entityplayer) {
        if (te.getWorld().isRemote) return false;
        ItemStack offered = entityplayer.inventory.getCurrentItem();

        if (!te.isOpen() || !checkIfFits(offered)) {
            return false;
        }

        dispenseItems(entityplayer);
        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, takeItems(offered));
        return true;
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
}
