package info.jbcs.minecraft.vending.inventory;

import info.jbcs.minecraft.vending.items.wrapper.AdvancedInventoryWrapper;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

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

    private boolean isAdvanced() {
        return te.isAdvanced();
    }

    @Override
    public boolean isEmpty() {
        if (isAdvanced() && isMultiple())
            return (new AdvancedInventoryWrapper(new RangedWrapper(new InvWrapper(this), 0, 13))).isEmpty();
        if (isAdvanced())
            return (new AdvancedInventoryWrapper(new RangedWrapper(new InvWrapper(this), 0, 10))).isEmpty();
        return super.isEmpty();
    }

    public boolean hasAttachedStorage() {
        return getAttachedStorage() != null;
    }

    public InventoryVendingStorageAttachment getAttachedStorage() {
        return te.getAttachmentInventory();
    }
}
