package info.jbcs.minecraft.vending.tileentity;

import com.kamildanak.minecraft.enderpay.item.ItemFilledBanknote;
import info.jbcs.minecraft.vending.inventory.InventoryStatic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Optional;

public class TileEntityVendingMachine extends TileEntity implements IInventory, ISidedInventory {
    private static final int[] side0 = new int[]{};
    public ItemStack[] sold = {null, null, null, null};
    public ItemStack[] bought = {null, null, null, null};
    public boolean advanced = false;
    public boolean infinite = false;
    public boolean multiple = false;
    public InventoryStatic inventory = new InventoryStatic(14) {
        @Override
        public String getName() {
            return "Vending Machine";
        }

        @Override
        public void onInventoryChanged() {
            if (worldObj == null) {
                return;
            }
            for (int i = 0; i < getSoldItems().length; i++) {
                if (!ItemStack.areItemStacksEqual(sold[i], getSoldItems()[i])) {
                    sold[i] = getSoldItems()[i];
                    if (sold[i] != null) sold[i] = sold[i].copy();
                    markBlockForUpdate(pos);
                }
            }
            for (int i = 0; i < getBoughtItems().length; i++) {
                if (!ItemStack.areItemStacksEqual(sold[i], getBoughtItems()[i])) {
                    bought[i] = getBoughtItems()[i];
                    if (bought[i] != null) bought[i] = bought[i].copy();
                    markBlockForUpdate(pos);
                }
            }
        }

        @Override
        public ItemStack removeStackFromSlot(int i) {
            return null;
        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer entityplayer) {
            return worldObj.getTileEntity(pos) == TileEntityVendingMachine.this && entityplayer.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
        }
    };
    private String ownerName = "";
    private boolean open = true;

    public void markBlockForUpdate(BlockPos pos) {
        IBlockState blockState = worldObj.getBlockState(pos);
        worldObj.notifyBlockUpdate(pos, blockState, blockState, 3);
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory() + (advanced ? -1 : 0);
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return inventory.getStackInSlot(i);
    }

    public ItemStack[] getSoldItems() {
        if (multiple)
            return new ItemStack[]{inventory.getStackInSlot(9), inventory.getStackInSlot(10),
                    inventory.getStackInSlot(11), inventory.getStackInSlot(12)};
        return new ItemStack[]{inventory.getStackInSlot(9)};
    }

    public ItemStack[] getBoughtItems() {
        return new ItemStack[]{inventory.getStackInSlot(multiple ? 13 : 10)};
    }

    public void setBoughtItem(ItemStack stack) {
        inventory.setInventorySlotContents(multiple ? 13 : 10, stack);
    }

    public boolean doesStackFit(ItemStack itemstack) {
        for (int i = 0; i < 9; i++) {
            if (inventory.items[i] == null) {
                return true;
            }

            if (inventory.items[i].getItem() != itemstack.getItem() && inventory.items[i].isStackable()) {
                continue;
            }

            if (inventory.items[i].stackSize + itemstack.stackSize > inventory.items[i].getMaxStackSize()) {
                continue;
            }

            if ((inventory.items[i].getHasSubtypes() && inventory.items[i].getItemDamage() != itemstack.getItemDamage())) {
                continue;
            }

            return true;
        }

        return false;
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return inventory.decrStackSize(i, j);
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        if ((advanced && i == 10) || (advanced && multiple && i == 13)) {
            return;
        }
        inventory.setInventorySlotContents(i, itemstack);
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return inventory.isUseableByPlayer(entityplayer);
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        inventory.clear();
        inventory.readFromNBT(nbttagcompound);
        ownerName = nbttagcompound.getString("owner");
        advanced = nbttagcompound.getBoolean("advanced");
        infinite = nbttagcompound.getBoolean("infinite");
        multiple = nbttagcompound.getBoolean("multiple");
        open = !nbttagcompound.hasKey("open") || nbttagcompound.getBoolean("open");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        inventory.writeToNBT(nbttagcompound);
        nbttagcompound.setString("owner", ownerName);
        nbttagcompound.setBoolean("advanced", advanced);
        nbttagcompound.setBoolean("infinite", infinite);
        nbttagcompound.setBoolean("multiple", multiple);
        nbttagcompound.setBoolean("open", open);
        return super.writeToNBT(nbttagcompound);
    }


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

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return !((!multiple && i == 100) || (advanced && multiple && i == 13));
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

    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side0;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String name) {
        ownerName = name;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Optional.Method(modid = "enderpay")
    public long soldCreditsSum() {
        return creditsSum(sold);
    }

    @Optional.Method(modid = "enderpay")
    public long boughtCreditsSum() {
        return creditsSum(bought);
    }

    @Optional.Method(modid = "enderpay")
    private long creditsSum(ItemStack[] stacks) {
        long sum = 0;
        for (ItemStack itemStack : stacks) {
            if (itemStack == null) continue;
            if (itemStack.getItem() instanceof ItemFilledBanknote) {
                sum += itemStack.getTagCompound().getLong("Amount");
            }
        }
        return sum;
    }
}


