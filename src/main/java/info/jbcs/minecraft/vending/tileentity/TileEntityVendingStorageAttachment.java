package info.jbcs.minecraft.vending.tileentity;

import info.jbcs.minecraft.vending.EnderPayApiUtils;
import info.jbcs.minecraft.vending.inventory.ContainerVendingStorageAttachment;
import info.jbcs.minecraft.vending.inventory.InventoryVendingStorageAttachment;
import info.jbcs.minecraft.vending.items.wrapper.IItemHandlerAdvanced;
import info.jbcs.minecraft.vending.items.wrapper.StorageAttachmentInvWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityVendingStorageAttachment extends TileEntityChestLike implements ISidedInventory,
        IInventoryChangedListener {
    private int transferCooldown;
    private int ticksWithNoInventoryChanges;
    private StorageAttachmentInvWrapper inventoryWrapper;

    public TileEntityVendingStorageAttachment() {
        inventory = new InventoryVendingStorageAttachment(this);
        inventory.addInventoryChangeListener(this);
        inventoryWrapper = new StorageAttachmentInvWrapper(inventory);
        transferCooldown = -1;
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return inventory.isItemValidForSlot(index, stack);
    }

    @Override
    @Nonnull
    public String getGuiID() {
        return "Vending:vendingStorageAttachment";
    }

    @Override
    @Nonnull
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
        return new ContainerVendingStorageAttachment(playerInventory, this, haveAccess(playerIn));
    }

    public boolean haveAccess(EntityPlayer entityplayer) {
        TileEntity te = this.world.getTileEntity(this.pos.up());
        if (!(te instanceof TileEntityVendingMachine)) return true;
        TileEntityVendingMachine machine = (TileEntityVendingMachine) te;
        return entityplayer.getDisplayNameString().equals(machine.getOwnerName()) || entityplayer.isCreative();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbttagcompound) {
        return super.writeToNBT(nbttagcompound);
    }

    public void update() {
        super.update();
        if(this.world == null || this.world.isRemote) return;

        if (this.ticksWithNoInventoryChanges < Integer.MAX_VALUE) this.ticksWithNoInventoryChanges++;
        --this.transferCooldown;
        if(this.transferCooldown<0) {
            this.transferCooldown = 0;
        }

        if (this.transferCooldown == 0)
        {
            TileEntityVendingMachine machine = getMachine();
            if (machine==null) return;

            if (machine.vend(inventoryWrapper.getInputWrapper(), inventoryWrapper.getOutputWrapper(), false)) {
                this.markDirty();
                this.transferCooldown = 8;
            } else if (this.ticksWithNoInventoryChanges > 20) {
                IItemHandlerAdvanced input = inventoryWrapper.getInputWrapper();
                IItemHandlerAdvanced output = inventoryWrapper.getOutputWrapper();
                if (output.hasEmptySlots()) {
                    for (int i = 0; i < input.getSlots(); i++) {
                        ItemStack stack = input.getStackInSlot(i);
                        if (!stack.isEmpty() && !machine.getInventoryWrapper().Accepts(stack) &&
                                EnderPayApiUtils.isBanknote(stack)) {
                            output.insertItem(stack, false);
                            input.setStackInSlot(i, ItemStack.EMPTY);
                            return;
                        }
                    }
                    for (int i = 0; i < input.getSlots(); i++) {
                        ItemStack stack = input.getStackInSlot(i);
                        if (!stack.isEmpty() && !machine.getInventoryWrapper().Accepts(stack)) {
                            output.insertItem(stack, false);
                            input.setStackInSlot(i, ItemStack.EMPTY);
                            return;
                        }
                    }
                    for (int i = 0; i < input.getSlots(); i++) {
                        ItemStack stack = input.getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            output.insertItem(stack, false);
                            input.setStackInSlot(i, ItemStack.EMPTY);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Nullable
    private TileEntityVendingMachine getMachine() {
        TileEntity te = this.world.getTileEntity(this.pos.up());
        if (!(te instanceof TileEntityVendingMachine)) return null;
        return (TileEntityVendingMachine) te;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == EnumFacing.DOWN) return (T) inventoryWrapper.getOutputWrapper();
            if (facing != EnumFacing.UP) return (T) inventoryWrapper.getInputWrapper();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<?> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
        return (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY &&
                facing != EnumFacing.UP)
                || super.hasCapability(capability, facing);
    }

    @Override
    public void onInventoryChanged(@Nonnull IInventory inventory) {
        this.ticksWithNoInventoryChanges = 0;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return inventory.getSlotsForFace(side);
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
        return inventory.canInsertItem(index, itemStackIn, direction);
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return inventory.canExtractItem(index, stack, direction);
    }
}


