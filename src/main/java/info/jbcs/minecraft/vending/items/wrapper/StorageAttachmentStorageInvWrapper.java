package info.jbcs.minecraft.vending.items.wrapper;

import info.jbcs.minecraft.vending.inventory.InventoryVendingStorageAttachment;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class StorageAttachmentStorageInvWrapper extends RangedWrapper {
    public StorageAttachmentStorageInvWrapper(InventoryVendingStorageAttachment inventoryStorageAttachment) {
        super(new InvWrapper(inventoryStorageAttachment), 0, 27);
    }
}
