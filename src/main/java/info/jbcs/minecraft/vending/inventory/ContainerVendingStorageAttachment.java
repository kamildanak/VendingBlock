package info.jbcs.minecraft.vending.inventory;

import info.jbcs.minecraft.vending.tileentity.TileEntityVendingStorageAttachment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerVendingStorageAttachment extends ContainerTileEntityBig<TileEntityVendingStorageAttachment> {
    private TileEntityVendingStorageAttachment attachment;

    public ContainerVendingStorageAttachment(InventoryPlayer playerInv, TileEntityVendingStorageAttachment attachment,
                                             boolean storageModifiable) {
        super(playerInv, attachment, 8, 0);
        this.attachment = attachment;

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 3; x++) {
                if(storageModifiable) {
                    addSlotToContainer(new Slot(attachment, y + x * 9, x * 18 + 7, 18 + y * 18));
                }
                else {
                    addSlotToContainer(new SlotAdvancedVendingMachine(attachment, y + x * 9, x * 18 + 7, 18 + y * 18));
                }
            }
        }

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(attachment, 3 * 9 + x, 66 + x * 18, 18));
        }

        for (int y = 4; y < 6; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(attachment, y * 9 + x, 66 + x * 18, 18 + (y-2) * 18));
            }
        }
    }

    public IInventory getStorageInventory() {
        return attachment;
    }
}
