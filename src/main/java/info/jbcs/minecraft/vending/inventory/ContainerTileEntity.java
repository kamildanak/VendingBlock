package info.jbcs.minecraft.vending.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class ContainerTileEntity<T extends TileEntity & IInventory> extends Container {
    public final IInventory playerInventory;
    public final T entity;
    public int playerSlotsCount;

    public ContainerTileEntity(IInventory playerInv, T tileEntity, int startX, int startY) {
        playerInventory = playerInv;
        entity = tileEntity;

        for (int k = 0; k < 3; k++) {
            for (int j1 = 0; j1 < 9; j1++) {
                addSlotToContainer(new Slot(playerInv, j1 + k * 9 + 9, startX + j1 * 18, startY + k * 18));
            }
        }

        for (int l = 0; l < 9; l++) {
            addSlotToContainer(new Slot(playerInv, l, startX + l * 18, startY + 142 - 84));
        }

        playerSlotsCount = inventorySlots.size();
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) {
        return entity.isUsableByPlayer(entityplayer);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(i);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (i < playerSlotsCount) {
                if (!mergeItemStack(itemstack1, playerSlotsCount, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!mergeItemStack(itemstack1, 0, playerSlotsCount, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
