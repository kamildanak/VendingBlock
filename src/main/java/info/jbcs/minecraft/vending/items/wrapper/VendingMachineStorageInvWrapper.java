package info.jbcs.minecraft.vending.items.wrapper;

import info.jbcs.minecraft.vending.inventory.InventoryVendingMachine;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class VendingMachineStorageInvWrapper extends RangedWrapper {
    public VendingMachineStorageInvWrapper(InventoryVendingMachine inventory) {
        super(new InvWrapper(inventory), 0, 9);
    }
}
