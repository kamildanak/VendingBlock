package info.jbcs.minecraft.vending.init;

import net.minecraft.item.Item;

public class VendingItems {
    public static final Item ITEM_WRENCH;
    static final Item[] ITEMS;

    static {
        ITEM_WRENCH = new Item().setRegistryName("vendingMachineWrench").setUnlocalizedName("vendingMachineWrench");
        ITEM_WRENCH.setContainerItem(ITEM_WRENCH);
        ITEMS = new Item[]{ITEM_WRENCH};
    }
}
