package info.jbcs.minecraft.vending.items.wrapper;

import info.jbcs.minecraft.vending.inventory.InventoryVendingStorageAttachment;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

class StorageAttachmentInputInvWrapper extends RangedWrapper {
    StorageAttachmentInputInvWrapper(InventoryVendingStorageAttachment inventoryStorageAttachment) {
        super(new InvWrapper(inventoryStorageAttachment), 27, 36);
    }
}