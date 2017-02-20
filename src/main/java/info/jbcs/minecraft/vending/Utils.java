package info.jbcs.minecraft.vending;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        world.spawnEntityInWorld(entityitem);
    }

    @Optional.Method(modid = "enderpay")
    public static boolean isBanknote(ItemStack itemStack) {
        return EnderPayApi.isBlankBanknote(itemStack) || EnderPayApi.isFilledBanknote(itemStack);
    }

    @Optional.Method(modid = "enderpay")
    public static boolean isFilledBanknote(ItemStack bought) {
        return EnderPayApi.isFilledBanknote(bought);
    }
}
