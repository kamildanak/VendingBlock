package info.jbcs.minecraft.vending.tileentity;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NotABanknoteException;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;

public class TileEntityVendingMachine extends TileEntity implements IInventory, ISidedInventory {
    private static final int[] side0 = new int[]{};
    private NonNullList<ItemStack> sold = NonNullList.withSize(4, ItemStack.EMPTY);
    private NonNullList<ItemStack> bought = NonNullList.withSize(4, ItemStack.EMPTY);
    private boolean advanced = false;
    private boolean infinite = false;
    private boolean multiple = false;
    public InventoryStatic inventory = new InventoryStatic(14) {
        @Override
        @Nonnull
        public String getName() {
            return "Vending Machine";
        }

        @Override
        public void onInventoryChanged() {
            if (world == null) {
                return;
            }
            for (int i = 0; i < getSoldItems().size(); i++) {
                if (!ItemStack.areItemStacksEqual(sold.get(i), getSoldItems().get(i))) {
                    sold.set(i,getSoldItems().get(i));
                    if (!sold.get(i).isEmpty()) sold.set(i,sold.get(i).copy());
                    markBlockForUpdate(pos);
                }
            }
            for (int i = 0; i < getBoughtItems().size(); i++) {
                if (!ItemStack.areItemStacksEqual(bought.get(i), getBoughtItems().get(i))) {
                    bought.set(i,getBoughtItems().get(i));
                    if (!bought.get(i).isEmpty()) bought.set(i,bought.get(i).copy());
                    markBlockForUpdate(pos);
                }
            }
        }

        @Override
        @Nonnull
        public ItemStack removeStackFromSlot(int i) {
            return ItemStack.EMPTY;
        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean isUsableByPlayer(@Nonnull EntityPlayer entityplayer) {
            return world.getTileEntity(pos) == TileEntityVendingMachine.this && entityplayer.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
        }
    };
    private String ownerName = "";
    private boolean open = true;

    public TileEntityVendingMachine(boolean advanced, boolean infinite, boolean multiple) {
        this.advanced = advanced;
        this.infinite = infinite;
        this.multiple = multiple;
    }

    public TileEntityVendingMachine() {
    }

    public void markBlockForUpdate(BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, blockState, blockState, 3);
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory() + (advanced ? -1 : 0);
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int i) {
        return inventory.getStackInSlot(i);
    }

    @Nonnull
    public NonNullList<ItemStack> getSoldItems() {
        NonNullList<ItemStack> stackNonNullList = NonNullList.create();
        stackNonNullList.add(inventory.getStackInSlot(9));
        if (multiple)
            for(int i=10; i<13;i++)
                stackNonNullList.add(inventory.getStackInSlot(i));
        return stackNonNullList;
    }

    @Nonnull
    public NonNullList<ItemStack> getBoughtItems() {
        NonNullList<ItemStack> stackNonNullList = NonNullList.create();
        stackNonNullList.add(inventory.getStackInSlot(multiple ? 13 : 10));
        return stackNonNullList;
    }

    @Nonnull
    public NonNullList<ItemStack> getInventoryItems() {
        NonNullList<ItemStack> stackNonNullList = NonNullList.create();
        for(int i=0; i<9;i++)
            stackNonNullList.add(inventory.getStackInSlot(i));
        return stackNonNullList;
    }

    public void setBoughtItem(ItemStack stack) {
        inventory.setInventorySlotContents(multiple ? 13 : 10, stack);
    }

    public boolean doesStackFit(ItemStack itemstack) {
        for (int i = 0; i < 9; i++) {
            if (inventory.items.get(i).isEmpty()) {
                return true;
            }

            if (inventory.items.get(i).getItem() != itemstack.getItem() && inventory.items.get(i).isStackable()) {
                continue;
            }

            if (inventory.items.get(i).getCount() + itemstack.getCount() > inventory.items.get(i).getMaxStackSize()) {
                continue;
            }

            if ((inventory.items.get(i).getHasSubtypes() && inventory.items.get(i).getItemDamage() != itemstack.getItemDamage())) {
                continue;
            }

            return true;
        }

        return false;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int i, int j) {
        return inventory.decrStackSize(i, j);
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int i) {
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
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer entityplayer) {
        return inventory.isUsableByPlayer(entityplayer);
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {

    }

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {

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
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        inventory.writeToNBT(nbttagcompound);
        nbttagcompound.setString("owner", ownerName);
        nbttagcompound.setBoolean("advanced", advanced);
        nbttagcompound.setBoolean("infinite", infinite);
        nbttagcompound.setBoolean("multiple", multiple);
        nbttagcompound.setBoolean("open", open);
        return super.writeToNBT(nbttagcompound);
    }


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

    @Override
    public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
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
    @Nonnull
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return side0;
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return false;
    }

    @Override
    @Nonnull
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return inventory.getDisplayName();
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
        return creditsSum(getSoldItems());
    }

    @Optional.Method(modid = "enderpay")
    public long boughtCreditsSum() {
        return creditsSum(getBoughtItems());
    }

    @Optional.Method(modid = "enderpay")
    private long creditsSum(NonNullList<ItemStack> stacks) {
        long sum = 0;
        for (ItemStack itemStack : stacks) {
            if (itemStack.isEmpty()) continue;
            if (EnderPayApi.isValidFilledBanknote(itemStack)) {
                try {
                    sum += EnderPayApi.getBanknoteOriginalValue(itemStack);
                } catch (NotABanknoteException ignored) {
                }
            }
        }
        return sum;
    }

    @Optional.Method(modid = "enderpay")
    private long realCreditsSum(NonNullList<ItemStack> stacks) {
        long sum = 0;
        for (ItemStack itemStack : stacks) {
            if (itemStack.isEmpty()) continue;
            if (EnderPayApi.isValidFilledBanknote(itemStack)) {
                try {
                    sum += EnderPayApi.getBanknoteCurrentValue(itemStack);
                } catch (NotABanknoteException ignored) {
                }
            }
        }
        return sum;
    }

    @Optional.Method(modid = "enderpay")
    public long realInventoryCreditsSum() {
        return realCreditsSum(getInventoryItems());
    }

    @Optional.Method(modid = "enderpay")
    public long realTotalCreditsSum() {
        return realCreditsSum(getInventoryItems()) + realCreditsSum(getSoldItems());
    }

    @Optional.Method(modid = "enderpay")
    public boolean hasPlaceForBanknote() {
        NonNullList<ItemStack> stacks = getInventoryItems();
        for (ItemStack itemStack : stacks) {
            if (itemStack.isEmpty() || EnderPayApi.isFilledBanknote(itemStack)) return true;
        }
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
}


