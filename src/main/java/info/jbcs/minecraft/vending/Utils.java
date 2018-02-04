package info.jbcs.minecraft.vending;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    public static void markBlockForUpdate(World world, BlockPos pos) {
        if (world == null) return;
        IBlockState blockState = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, blockState, blockState, 3);
    }

    public static NonNullList<ItemStack> splitItemsStack(ItemStack stack) {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        for(int i = stack.getCount(); i>0; i++) {
            if(stack.getCount() <= 0) return itemStacks;
            itemStacks.add(stack.splitStack(stack.getMaxStackSize()));
        }
        return itemStacks;
    }

    public static NonNullList<ItemStack> joinItems(NonNullList<ItemStack> items)
    {
        NonNullList<ItemStack> joinedItems = NonNullList.create();
        for (ItemStack itemStack : items) {
            boolean added = false;
            for (ItemStack itemStack2 : joinedItems) {
                if (itemStack.isItemEqual(itemStack2)) {
                    itemStack2.setCount(itemStack2.getCount() + itemStack.getCount());
                    added = true;
                }
            }
            if (!added) joinedItems.add(itemStack.copy());
        }
        return joinedItems;
    }
}
