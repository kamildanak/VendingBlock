package info.jbcs.minecraft.vending.tileentity;

import info.jbcs.minecraft.vending.inventory.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityVendingMachine extends TileEntityLockable implements ISidedInventory {
    private static final int[] side0 = new int[]{};
    //private static final int[] side0 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}; Uncomment to enable hoppers
    public InventoryVendingMachine inventory;
    private IItemHandler itemHandler = new SidedInvWrapper(this, net.minecraft.util.EnumFacing.DOWN);
    private boolean advanced;
    private boolean infinite;
    private boolean multiple;
    private boolean open = true;
    private String ownerName;

    public TileEntityVendingMachine() {
        advanced = infinite = multiple = false;
        open = true;
        ownerName = "";
        inventory = (Loader.isModLoaded("enderpay")) ?
                new InventoryVendingMachineEnderPay(this) :
                new InventoryVendingMachine(this);
    }

    public TileEntityVendingMachine(boolean advanced, boolean infinite, boolean multiple) {
        this();
        this.advanced = advanced;
        this.infinite = infinite;
        this.multiple = multiple;
    }

    @Override
    @Nonnull
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return side0;
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return this.isItemValidForSlot(index, stack);
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSlots() + (advanced ? -1 : 0) + (multiple ? 0 : -3);
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int index) {
        return inventory.getStackInSlot(index);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int index, int count) {
        return inventory.decrStackSize(index, count);
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
        if ((advanced && i == 10) || (advanced && multiple && i == 13)) {
            return;
        }
        inventory.setInventorySlotContents(i, itemstack);
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getSlotLimit(0);
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        return world.getTileEntity(pos) == this &&
                player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {

    }

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return index < 9;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    @Nonnull
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
        if (isAdvanced())
            return new ContainerAdvancedVendingMachine(playerInventory, this);
        if (isMultiple())
            return new ContainerMultipleVendingMachine(playerInventory, this);
        return new ContainerVendingMachine(playerInventory, this);
    }

    @Override
    @Nonnull
    public String getGuiID() {
        return "vending:vendingMachine";
    }

    @Override
    @Nonnull
    public String getName() {
        return "";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String name) {
        ownerName = name;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        inventory.deserializeNBT(nbttagcompound);
        ownerName = nbttagcompound.getString("owner");
        advanced = nbttagcompound.getBoolean("advanced");
        infinite = nbttagcompound.getBoolean("infinite");
        multiple = nbttagcompound.getBoolean("multiple");
        open = !nbttagcompound.hasKey("open") || nbttagcompound.getBoolean("open");
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.merge(inventory.serializeNBT());
        nbttagcompound.setString("owner", ownerName);
        nbttagcompound.setBoolean("advanced", advanced);
        nbttagcompound.setBoolean("infinite", infinite);
        nbttagcompound.setBoolean("multiple", multiple);
        nbttagcompound.setBoolean("open", open);
        return super.writeToNBT(nbttagcompound);
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound updateTag = super.getUpdateTag();
        writeToNBT(updateTag);
        return updateTag;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new SPacketUpdateTileEntity(pos, 1, var1);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        return super.getCapability(capability, facing);
    }
}

