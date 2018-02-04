package info.jbcs.minecraft.vending.items.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class DummyInventoryWrapper implements IItemHandlerModifiable {
    private IItemHandlerModifiable itemHandlerModifiable;

    DummyInventoryWrapper(IItemHandlerModifiable itemHandlerModifiable) {
        this.itemHandlerModifiable = itemHandlerModifiable;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        itemHandlerModifiable.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return itemHandlerModifiable.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return itemHandlerModifiable.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return itemHandlerModifiable.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return itemHandlerModifiable.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return itemHandlerModifiable.getSlotLimit(slot);
    }
}
