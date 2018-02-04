package info.jbcs.minecraft.vending.items.wrapper;

import info.jbcs.minecraft.vending.inventory.InventoryVendingMachine;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;

public class VendingMachineSoldInvWrapper extends MixedInvWrapper {
    private IItemHandlerModifiable itemHandlerStandard;
    private IItemHandlerModifiable itemHandlerMultiple;
    private InventoryVendingMachine inventoryVendingMachine;

    public VendingMachineSoldInvWrapper(InventoryVendingMachine inventory) {
        itemHandlerStandard = new RangedWrapper(new InvWrapper(inventory), 9, 10);
        itemHandlerMultiple = new RangedWrapper(new InvWrapper(inventory), 9, 13);
        inventoryVendingMachine = inventory;
    }

    @Override
    @Nonnull
    public IItemHandlerModifiable getItemHandler() {
        if (inventoryVendingMachine.isMultiple()) return itemHandlerMultiple;
        return itemHandlerStandard;
    }
}
