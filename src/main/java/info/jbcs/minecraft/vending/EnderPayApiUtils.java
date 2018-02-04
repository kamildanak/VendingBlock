package info.jbcs.minecraft.vending;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NoSuchAccountException;
import com.kamildanak.minecraft.enderpay.api.NotABanknoteException;
import info.jbcs.minecraft.vending.forge.LoaderWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Optional;

public class EnderPayApiUtils {
    public static ItemStack getBanknote(long value) {
        if (LoaderWrapper.isEnderPayLoaded()) return getBanknoteOptional(value);
        return ItemStack.EMPTY;
    }

    @Optional.Method(modid = "enderpay")
    private static ItemStack getBanknoteOptional(long value) {
        return EnderPayApi.getBanknote(value);
    }

    public static void addToBalance(EntityPlayer entityPlayer, long creditsToReturn) {
        if (LoaderWrapper.isEnderPayLoaded()) addToBalanceOptional(entityPlayer, creditsToReturn);
    }

    @Optional.Method(modid = "enderpay")
    private static void addToBalanceOptional(EntityPlayer entityPlayer, long creditsToReturn) {
        try {
            EnderPayApi.addToBalance(entityPlayer.getUniqueID(), creditsToReturn);
        } catch (NoSuchAccountException ignored) {
        }
    }

    public static String getCurrencyName(long amount) {
        if (LoaderWrapper.isEnderPayLoaded()) return getCurrencyNameOptional(amount);
        return "";
    }

    @Optional.Method(modid = "enderpay")
    private static String getCurrencyNameOptional(long amount) {
        return EnderPayApi.getCurrencyName(amount);
    }

    public static boolean isFilledBanknote(ItemStack stack) {
        return LoaderWrapper.isEnderPayLoaded() && isFilledBanknoteOptional(stack);
    }

    @Optional.Method(modid = "enderpay")
    private static boolean isFilledBanknoteOptional(ItemStack stack) {
        return EnderPayApi.isValidFilledBanknote(stack);
    }


    @Optional.Method(modid = "enderpay")
    private static boolean isBanknoteOptional(ItemStack itemStack) {
        return EnderPayApi.isBlankBanknote(itemStack) || EnderPayApi.isFilledBanknote(itemStack);
    }

    public static NonNullList<ItemStack> filterBanknotes(NonNullList<ItemStack> soldItems) {
        if (!LoaderWrapper.isEnderPayLoaded()) return soldItems;
        for (int i = 0; i < soldItems.size(); i++) {
            if (isBanknoteOptional(soldItems.get(i))) soldItems.set(i, ItemStack.EMPTY);
        }
        return soldItems;
    }

    public static NonNullList<ItemStack> filterBlankBanknotes(NonNullList<ItemStack> soldItems) {
        if (!LoaderWrapper.isEnderPayLoaded()) return soldItems;
        for (int i = 0; i < soldItems.size(); i++) {
            if (isBanknoteOptional(soldItems.get(i)) && !isFilledBanknoteOptional(soldItems.get(i)))
                soldItems.set(i, ItemStack.EMPTY);
        }
        return soldItems;
    }


    public static long originalValueCreditsSum(NonNullList<ItemStack> stacks) {
        if (LoaderWrapper.isEnderPayLoaded()) return originalValueCreditsSumOptional(stacks);
        return 0;
    }

    @Optional.Method(modid = "enderpay")
    private static long originalValueCreditsSumOptional(NonNullList<ItemStack> stacks) {
        long sum = 0;
        for (ItemStack itemStack : stacks) {
            if (itemStack.isEmpty()) continue;
            if (EnderPayApi.isValidFilledBanknote(itemStack)) {
                try {
                    sum += EnderPayApi.getBanknoteOriginalValue(itemStack);
                } catch (NotABanknoteException ignored) {
                }
            }
        }
        return sum;
    }

    public static long currentValueCreditsSum(NonNullList<ItemStack> stacks) {
        if (LoaderWrapper.isEnderPayLoaded()) return currentValueCreditsSumOptional(stacks);
        return 0;
    }

    @Optional.Method(modid = "enderpay")
    private static long currentValueCreditsSumOptional(NonNullList<ItemStack> stacks) {
        long sum = 0;
        for (ItemStack itemStack : stacks) {
            if (itemStack.isEmpty()) continue;
            if (EnderPayApi.isValidFilledBanknote(itemStack)) {
                try {
                    sum += EnderPayApi.getBanknoteCurrentValue(itemStack);
                } catch (NotABanknoteException ignored) {
                }
            }
        }
        return sum;
    }

    public static NonNullList<ItemStack> filterFilledBanknotes(NonNullList<ItemStack> itemStacks) {
        if (!LoaderWrapper.isEnderPayLoaded()) return itemStacks;
        for (int i = 0; i < itemStacks.size(); i++) {
            if (isFilledBanknoteOptional(itemStacks.get(i))) itemStacks.set(i, ItemStack.EMPTY);
        }
        return itemStacks;
    }

    public static long getAvailableCredits(EntityPlayer entityPlayer) {
        if (LoaderWrapper.isEnderPayLoaded()) return getAvailableCreditsOptional(entityPlayer);
        return 0;
    }

    @Optional.Method(modid = "enderpay")
    private static long getAvailableCreditsOptional(EntityPlayer entityPlayer) {
        try {
            return EnderPayApi.getBalance(entityPlayer.getUniqueID());
        } catch (NoSuchAccountException ignored) {
        }
        return 0;
    }

    public static long getBanknoteOriginalValue(ItemStack stack) {
        if (LoaderWrapper.isEnderPayLoaded()) return getBanknoteOriginalValueOptional(stack);
        return 0;
    }


    @Optional.Method(modid = "enderpay")
    private static long getBanknoteOriginalValueOptional(ItemStack stack) {
        try {
            return EnderPayApi.getBanknoteOriginalValue(stack);
        } catch (NotABanknoteException ignored) {
        }
        return 0;
    }

    public static boolean isBanknote(ItemStack itemStack) {
        return LoaderWrapper.isEnderPayLoaded() && isBanknoteOptional(itemStack);
    }
}
