package info.jbcs.minecraft.vending.inventory;

import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingStorageAttachment;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

public class InventoryVendingStorageAttachment extends InventorySerializable implements ISidedInventory {
    private static int[] storageSlots = IntStream.rangeClosed(0, 26).toArray();
    private static int[] inputSlots = IntStream.rangeClosed(27, 35).toArray();
    private static int[] outputSlots = IntStream.rangeClosed(36, 53).toArray();
    private TileEntityVendingStorageAttachment teSA;

    public InventoryVendingStorageAttachment(TileEntityVendingStorageAttachment te) {
        super("Vending Machine Attachment", false, 54);
        this.teSA = te;
    }

    private boolean isOutputSlot(int index) {
        return index>=36 && index<=53;
    }

    private boolean isInputSlot(int index) {
        return index>=27 && index<=35;
    }

    private boolean isInventorySlot(int index) {
        return index>=0 && index<=26;
    }

    @Override
    @Nonnull
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        if (side == EnumFacing.UP) {
            return new int[]{};
        }
        if (side == EnumFacing.DOWN) {
            return outputSlots;
        }
        return inputSlots;
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return isOutputSlot(index);
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        if (isOutputSlot(index)) return true;
        TileEntity te = teSA.getWorld().getTileEntity(teSA.getPos().up());
        if (!(te instanceof TileEntityVendingMachine)) return false;
        TileEntityVendingMachine machine = (TileEntityVendingMachine) te;
        return isInputSlot(index) && machine.getInventoryWrapper().Accepts(stack) ||
                isInventorySlot(index) && machine.getInventoryWrapper().Vends(stack);
    }
}
