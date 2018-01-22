package info.jbcs.minecraft.vending.tileentity;

import info.jbcs.minecraft.vending.inventory.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityVendingStorageAttachment extends TileEntityChestLike{
    private int transferCooldown;

    public TileEntityVendingStorageAttachment() {
        inventory = new InventoryVendingStorageAttachment();
        transferCooldown = -1;
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    @Nonnull
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        if (side == EnumFacing.UP){
            return new int[]{};
        }
        if (side == EnumFacing.DOWN)
        {
            return inventory.getOutputSlots();
        }
        return inventory.getInputSlots();
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return inventory.isOutputSlot(index);
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        if(inventory.isOutputSlot(index)) return false;
        TileEntity te = this.world.getTileEntity(this.pos.up());
        if (!(te instanceof TileEntityVendingMachine)) return false;
        TileEntityVendingMachine machine = (TileEntityVendingMachine) te;
        for(ItemStack itemStack : machine.inventory.getBoughtItems())
        {
            if (inventory.isInputSlot(index) && itemStack.isItemEqual(stack)) return true;
        }
        for(ItemStack itemStack : machine.inventory.getSoldItems())
        {
            if (inventory.isInventorySlot(index) && itemStack.isItemEqual(stack)) return true;
        }
        return false;
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

            if (inventory.canStoreItems(machine.inventory.getSoldItems(), inventory.getOutputSlots()) &&
                    inventory.canStoreCredits(inventory.getInputSlots())) {
                machine.inventory.vend(inventory, inventory.getInputSlots(), inventory.getOutputSlots());
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
}


