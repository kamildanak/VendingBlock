package info.jbcs.minecraft.vending.inventory;

import com.kamildanak.minecraft.foamflower.inventory.InventoryStatic;
import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.init.VendingSoundEvents;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nonnull;
import java.util.Objects;

import static info.jbcs.minecraft.vending.General.countNotNull;

public class InventoryVendingMachine extends InventoryStatic {
    TileEntityVendingMachine te;

    public InventoryVendingMachine(TileEntityVendingMachine tileEntityVendingMachine) {
        super(14);
        te = tileEntityVendingMachine;
    }

    @Override
    protected void onContentsChanged(int slot) {
        Utils.markBlockForUpdate(te.getWorld(), te.getPos());
    }

    @Nonnull
    public NonNullList<ItemStack> getSoldItems() {
        NonNullList<ItemStack> stackNonNullList = NonNullList.create();
        stackNonNullList.add(getStackInSlot(9));
        if (te.isMultiple())
            for (int i = 10; i < 13; i++)
                stackNonNullList.add(getStackInSlot(i));
        return stackNonNullList;
    }

    @Nonnull
    public NonNullList<ItemStack> getBoughtItems() {
        NonNullList<ItemStack> stackNonNullList = NonNullList.create();
        stackNonNullList.add(getStackInSlot(te.isMultiple() ? 13 : 10));
        return stackNonNullList;
    }

    @Nonnull
    public NonNullList<ItemStack> getInventoryItems() {
        NonNullList<ItemStack> stackNonNullList = NonNullList.create();
        for (int i = 0; i < 9; i++)
            stackNonNullList.add(getStackInSlot(i));
        return stackNonNullList;
    }


    public void setBoughtItem(ItemStack stack) {
        setInventorySlotContents(te.isMultiple() ? 13 : 10, stack);
    }

    public boolean doesStackFit(ItemStack itemstack) {
        for (int i = 0; i < 9; i++) {
            itemstack = insertItem(i, itemstack, true);
        }
        return itemstack.isEmpty();
    }

    public void giveItems(EntityPlayer entityPlayer) {
        NonNullList<ItemStack> soldItems = getSoldItems();
        if (countNotNull(soldItems) == 0) return;
        for (ItemStack sold : soldItems) {
            if (sold.isEmpty()) continue;
            ItemStack vended = sold.copy();

            if (!te.isInfinite()) {
                ItemStack stackFromMainInventory = extractItem(sold, sold.getCount(),
                        0, 8, false);
                if (extractItem(sold, sold.getCount() - stackFromMainInventory.getCount(),
                        9, 12, false).getCount() > 0 && Vending.settings.shouldCloseOnPartialSoldOut()) {
                    te.setOpen(false);
                }
            }

            if (Vending.settings.shouldTransferToInventory() && entityPlayer.inventory.addItemStackToInventory(vended))
                continue;
            Utils.throwItemAtPlayer(entityPlayer, te.getWorld(), te.getPos(), vended);
        }
    }

    public boolean hasSomethingToSell() {
        return countNotNull(getSoldItems()) != 0;
    }

    public void takeItems(EntityPlayer entityplayer, @Nonnull ItemStack offered) {
        NonNullList<ItemStack> bought = getBoughtItems();
        if (countNotNull(bought) == 0) return;
        if (offered.isEmpty()) return;
        ItemStack paid = offered.splitStack(bought.get(0).getCount());
        if (offered.getCount() == 0) {
            entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, ItemStack.EMPTY);
        }

        if (!te.isInfinite())
            insertItem(paid, 0, 8, false);
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

        giveItems(entityplayer);
        takeItems(entityplayer, offered);
        return true;
    }

    @Nonnull
    public NonNullList<ItemStack> getSoldItemsWithFilledBanknotes() {
        return getSoldItems();
    }
}
