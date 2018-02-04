package info.jbcs.minecraft.vending.tileentity;

import info.jbcs.minecraft.vending.inventory.ContainerVendingStorageAttachment;
import info.jbcs.minecraft.vending.inventory.InventoryVendingStorageAttachment;
import info.jbcs.minecraft.vending.items.wrapper.StorageAttachmentInvWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityVendingStorageAttachment extends TileEntityChestLike{
    private int transferCooldown;
    private StorageAttachmentInvWrapper inventoryWrapper;

    public TileEntityVendingStorageAttachment() {
        inventory = new InventoryVendingStorageAttachment(this);
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
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) inventoryWrapper;
        return super.getCapability(capability, facing);
    }
}


