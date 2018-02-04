package info.jbcs.minecraft.vending.inventory;

import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;

public class InventoryVendingMachine extends InventorySerializable {
    private TileEntityVendingMachine te;

    public InventoryVendingMachine(TileEntityVendingMachine tileEntityVendingMachine) {
        super("Vending Machine", false, 14);
        super.addInventoryChangeListener(tileEntityVendingMachine);
        te = tileEntityVendingMachine;
    }

    public boolean isMultiple() {
        return te.isMultiple();
    }

    public boolean hasAttachedStorage() {
        return getAttachedStorage() != null;
    }

    public InventoryVendingStorageAttachment getAttachedStorage() {
        return te.getAttachmentInventory();
    }
}
