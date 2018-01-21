package info.jbcs.minecraft.vending.tileentity;

import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.inventory.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

public class TileEntityVendingStorageAttachment extends TileEntityChestLike{
    private int transferCooldown;

    public TileEntityVendingStorageAttachment() {
        inventory = new InventoryVendingStorageAttachment();
        transferCooldown = -1;
    }

    @Override
    public int getSizeInventory() {
        return 54;
    }

    @Override
    @Nonnull
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        if (side == EnumFacing.UP){
            return new int[]{};
        }
        if (side == EnumFacing.DOWN)
        {
            return new int[]{36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,53};
        }
        return new int[]{27,28,29,30,31,32,33,34,35};
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return index>35;
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        if(index>35) return false;
        TileEntity te = this.world.getTileEntity(this.pos.up());
        if (!(te instanceof TileEntityVendingMachine)) return false;
        TileEntityVendingMachine machine = (TileEntityVendingMachine) te;
        for(ItemStack itemStack : machine.inventory.getBoughtItems())
        {
            if (index>26 && itemStack.isItemEqual(stack)) return true;
        }
        for(ItemStack itemStack : machine.inventory.getSoldItems())
        {
            if (index<27 && itemStack.isItemEqual(stack)) return true;
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
        --this.transferCooldown;
        if(this.transferCooldown<0) {
            this.transferCooldown = 0;
        }

        if (this.transferCooldown == 0 && this.world != null && !this.world.isRemote)
        {
            TileEntity te = this.world.getTileEntity(this.pos.up());
            if (!(te instanceof TileEntityVendingMachine)) return;
            TileEntityVendingMachine machine = (TileEntityVendingMachine) te;
            InsertionResultMultiple insertionResultMultiple = inventory.insertItems(
                    machine.inventory.getSoldItems(), IntStream.rangeClosed(36, 53).toArray(),true);

            if (insertionResultMultiple.getItemsLeft().size()==0 &&
                    inventory.canStoreCredits(Utils.itemStacksFromInventory(inventory, IntStream.rangeClosed(27, 35).toArray()))) {
                machine.inventory.vend(inventory, IntStream.rangeClosed(27, 35).toArray(),
                        IntStream.rangeClosed(36, 53).toArray());
                this.markDirty();
                this.transferCooldown = 8;
            }
        }
    }
}


