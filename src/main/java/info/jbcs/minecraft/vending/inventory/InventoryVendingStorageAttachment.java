package info.jbcs.minecraft.vending.inventory;

import java.util.stream.IntStream;

public class InventoryVendingStorageAttachment extends InventoryStaticExtended {
    private static int[] storageSlots = IntStream.rangeClosed(0, 26).toArray();
    private static int[] inputSlots = IntStream.rangeClosed(27, 35).toArray();
    private static int[] outputSlots = IntStream.rangeClosed(36, 53).toArray();

    public InventoryVendingStorageAttachment() {
        super(54);
    }

    public int[] getStorageSlots() {
        return storageSlots;
    }

    public int[] getInputSlots() {
        return inputSlots;
    }

    public int[] getOutputSlots() {
        return outputSlots;
    }

    public boolean isOutputSlot(int index) {
        return index>=36 && index<=53;
    }

    public boolean isInputSlot(int index) {
        return index>=27 && index<=35;
    }

    public boolean isInventorySlot(int index) {
        return index>=0 && index<=26;
    }
}
