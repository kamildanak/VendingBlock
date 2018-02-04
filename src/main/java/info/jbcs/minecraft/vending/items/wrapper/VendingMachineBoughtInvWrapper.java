package info.jbcs.minecraft.vending.items.wrapper;

import info.jbcs.minecraft.vending.inventory.InventoryVendingMachine;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class VendingMachineBoughtInvWrapper extends MixedInvWrapper {
    private IItemHandlerModifiable itemHandlerStandard;
    private IItemHandlerModifiable itemHandlerMultiple;
    private InventoryVendingMachine inventoryVendingMachine;

    public VendingMachineBoughtInvWrapper(InventoryVendingMachine inventory) {
        itemHandlerStandard = new SingleInvWrapper(new InvWrapper(inventory), 10);
        itemHandlerMultiple = new SingleInvWrapper(new InvWrapper(inventory), 13);
        inventoryVendingMachine = inventory;
    }

    @Override
    @Nonnull
    public IItemHandlerModifiable getItemHandler() {
        if (inventoryVendingMachine.isMultiple()) return itemHandlerMultiple;
        return itemHandlerStandard;
    }
}
