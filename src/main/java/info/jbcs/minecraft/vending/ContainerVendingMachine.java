package info.jbcs.minecraft.vending;

import info.jbcs.minecraft.utilities.ContainerTileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerVendingMachine extends ContainerTileEntity<TileEntityVendingMachine> {
	public ContainerVendingMachine(IInventory playerInv, TileEntityVendingMachine machine) {
		super(playerInv, machine, 8, 84);

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlotToContainer(new Slot(machine, y * 3 + x, 62 + x * 18, 17 + y * 18));
			}
		}

		addSlotToContainer(new Slot(machine, 9, 26, 35));
		addSlotToContainer(new Slot(machine, 10, 134, 35));
	}
}
