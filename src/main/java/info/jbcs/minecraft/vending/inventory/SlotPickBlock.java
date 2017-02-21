package info.jbcs.minecraft.vending.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotPickBlock extends Slot {
    ContainerPickBlock container;

    public SlotPickBlock(ContainerPickBlock c, int index, int x, int y) {
        super(c.inventory, index, x, y);
        container = c;
    }

    void click(EntityPlayer player, @Nonnull ItemStack itemstack, int count) {
        player.inventory.setItemStack(ItemStack.EMPTY);

        if (itemstack.isEmpty()) {
            return;
        }

        if (container.gui == null) {
            return;
        }

        putStack(new ItemStack(itemstack.getItem(), itemstack.getCount(), itemstack.getItemDamage()));
        int newSize;

        if (container.resultSlot == this) {
            newSize = itemstack.getCount() - count;
        } else {
            newSize = itemstack.getCount();
            ItemStack otherstack = container.resultSlot.getStack();

            if (!otherstack.isEmpty() && otherstack.getItem() == itemstack.getItem() && otherstack.getItemDamage() == itemstack.getItemDamage()) {
                newSize = otherstack.getCount() + count;
            } else {
                newSize = count;
            }
        }

        if (newSize > 64) {
            newSize = 64;
        }

        container.resultSlot.putStack(newSize <= 0 ? ItemStack.EMPTY : new ItemStack(itemstack.getItem(), newSize, itemstack.getItemDamage()));
    }

    @Override
    @Nonnull
    public ItemStack onTake(EntityPlayer player, @Nonnull ItemStack itemstack) {
        super.onTake(player, itemstack);
        click(player, itemstack, 1);
        return itemstack;
    }

    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player) {
        click(player, getStack(), 64);
        return ItemStack.EMPTY;
    }
}
