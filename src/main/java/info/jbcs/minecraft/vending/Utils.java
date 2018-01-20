package info.jbcs.minecraft.vending;

import com.kamildanak.minecraft.enderpay.EnderPay;
import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NoSuchAccountException;
import com.kamildanak.minecraft.enderpay.api.NotABanknoteException;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@SuppressWarnings({"Duplicates"})
public class Utils {
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    private static HashMap<String, ResourceLocation> resources = new HashMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    // http://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java
    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static void bind(String textureName) {
        ResourceLocation res = resources.get(textureName);

        if (res == null) {
            res = new ResourceLocation(textureName);
            resources.put(textureName, res);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(res);
    }

    public static void throwItemAtPlayer(EntityPlayer entityPlayer, World world, BlockPos blockPos, ItemStack vended) {
        EntityItem entityitem = new EntityItem(world, blockPos.getX() + 0.5, blockPos.getY() + 1.2, blockPos.getZ() + 0.5, vended);
        General.propelTowards(entityitem, entityPlayer, 0.2);
        entityitem.motionY = 0.2;
        entityitem.setPickupDelay(10);
        world.spawnEntity(entityitem);
    }

    @Optional.Method(modid = "enderpay")
    public static boolean isBanknote(ItemStack itemStack) {
        return EnderPayApi.isBlankBanknote(itemStack) || EnderPayApi.isFilledBanknote(itemStack);
    }

    @Optional.Method(modid = "enderpay")
    public static boolean isFilledBanknote(ItemStack bought) {
        return EnderPayApi.isFilledBanknote(bought);
    }

    @Optional.Method(modid = "enderpay")
    public static boolean checkIfPlayerHasEnoughCredits(EntityPlayer entityPlayer, long requiredAmount) {
        try {
            return EnderPayApi.getBalance(entityPlayer.getUniqueID()) >= requiredAmount;
        } catch (NoSuchAccountException ignored) {
            return false;
        }
    }

    public static void markBlockForUpdate(World world, BlockPos pos) {
        if (world == null) return;
        IBlockState blockState = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, blockState, blockState, 3);
    }

    public static NonNullList<ItemStack> filterBanknotes(NonNullList<ItemStack> soldItems) {
        if (!Loader.isModLoaded("enderpay")) return soldItems;
        for (int i = 0; i < soldItems.size(); i++) {
            if (Utils.isBanknote(soldItems.get(i))) soldItems.set(i, ItemStack.EMPTY);
        }
        return soldItems;
    }

    public static NonNullList<ItemStack> filterBlankBanknotes(NonNullList<ItemStack> soldItems) {
        if (!Loader.isModLoaded("enderpay")) return soldItems;
        for (int i = 0; i < soldItems.size(); i++) {
            if (Utils.isBanknote(soldItems.get(i)) && !Utils.isFilledBanknote(soldItems.get(i)))
                soldItems.set(i, ItemStack.EMPTY);
        }
        return soldItems;
    }


    @Optional.Method(modid = "enderpay")
    public static boolean hasPlaceForBanknote(NonNullList<ItemStack> stacks) {
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
    public static void storeBanknote(IInventory inventory, int start, int end, ItemStack banknote) {
        int banknotes = 0;
        try {
            for (int i = start; i <= end; i++) {
                ItemStack itemStack = inventory.getStackInSlot(i);
                if (Utils.isBanknote(itemStack)) {
                    if (itemStack.getCount() == 1) {
                        inventory.setInventorySlotContents(i,
                                EnderPayApi.getBanknote(EnderPayApi.getBanknoteCurrentValue(banknote) +
                                        EnderPayApi.getBanknoteCurrentValue(itemStack)));

                        return;
                    }
                    banknotes += itemStack.getCount();
                }
            }
            for (int i = start; i <= end; i++) {
                if (Utils.isBanknote(inventory.getStackInSlot(i))) {
                    inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
            for (int i = start; i <= end; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    inventory.setInventorySlotContents(i,EnderPayApi.getBanknote(EnderPayApi.getBanknoteCurrentValue(banknote)));
                    banknotes--;
                    break;
                }
            }
            storeBlankBanknotes(inventory, start, end, banknotes);
        } catch (NotABanknoteException ignored) {
        }
    }

    @Optional.Method(modid = "enderpay")
    public static void storeBlankBanknotes(IInventory inventory, int start, int end, int banknotes) {
        for (int i = start; i <= end; i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                if(banknotes<=0) break;
                int m = Math.min(banknotes, 64);
                banknotes -= m;
                inventory.setInventorySlotContents(i, new ItemStack(EnderPay.itemBlankBanknote, m));
            }
        }
    }

    @Optional.Method(modid = "enderpay")
    public static long originalValueCreditsSum(NonNullList<ItemStack> stacks) {
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

    @Optional.Method(modid = "enderpay")
    public static long currentValueCreditsSum(NonNullList<ItemStack> stacks) {
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
}
