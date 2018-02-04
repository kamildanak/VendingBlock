package info.jbcs.minecraft.vending.items.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public interface IItemHandlerAdvanced extends IItemHandlerModifiable {
    boolean containsItem(ItemStack stack);

    @Nonnull
    NonNullList<ItemStack> getItemStacks();

    @Nonnull
    NonNullList<ItemStack> getItemStacksWithoutFilledBanknotes();

    long getCreditsSum(boolean subtractTax);

    long storeCredits(long creditsDelta, boolean simulate);

    @Nonnull
    NonNullList<ItemStack> insertItems(NonNullList<ItemStack> itemStacks, boolean simulate);

    @Nonnull
    ItemStack insertItem(ItemStack itemStack, boolean simulate);

    @Nonnull
    ItemStack extractItem(ItemStack itemStack, boolean simulate);

    boolean hasBanknote();
}
