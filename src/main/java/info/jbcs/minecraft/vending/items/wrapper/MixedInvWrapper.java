package info.jbcs.minecraft.vending.items.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public abstract class MixedInvWrapper implements IItemHandlerModifiable {
    @Nonnull
    public abstract IItemHandlerModifiable getItemHandler();

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        getItemHandler().setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return getItemHandler().getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return getItemHandler().getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return getItemHandler().insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return getItemHandler().extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return getItemHandler().getSlotLimit(slot);
    }
}
