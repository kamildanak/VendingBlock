package info.jbcs.minecraft.vending.tileentity;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NotABanknoteException;
import com.kamildanak.minecraft.foamflower.inventory.InventoryStatic;
import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerMultipleVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerVendingMachine;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TileEntityVendingMachine extends TileEntityLockable implements ITickable, ISidedInventory {
    private static final int[] side0 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    public InventoryStatic inventory = new InventoryStatic(14) {
        @Override
        protected void onContentsChanged(int slot) {
            markBlockForUpdate(world, pos);
        }
    };
    IItemHandler itemHandler = new SidedInvWrapper(this, net.minecraft.util.EnumFacing.DOWN);
    private boolean advanced;
    private boolean infinite;
    private boolean multiple;
    private boolean open = true;
    private String ownerName;
    private UUID ownerUUID;

    public TileEntityVendingMachine() {
        advanced = infinite = multiple = false;
        open = true;
        ownerName = "";
        ownerUUID = new UUID(0, 0);
    }

    public TileEntityVendingMachine(boolean advanced, boolean infinite, boolean multiple) {
        this();
        this.advanced = advanced;
        this.infinite = infinite;
        this.multiple = multiple;
    }

    public static void markBlockForUpdate(World world, BlockPos pos) {
        if (world == null) return;
        IBlockState blockState = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, blockState, blockState, 3);
    }

    @Override
    @Nonnull
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return side0;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
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
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

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
    public void update() {

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

    @Deprecated
    public void setOwnerName(String name) {
        ownerName = name;
    }

    public void setOwner(EntityPlayer player) {
        ownerName = player.getName();
        ownerUUID = player.getUniqueID();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        inventory.deserializeNBT(nbttagcompound);
        ownerName = nbttagcompound.getString("owner");
        ownerUUID = nbttagcompound.getUniqueId("ownerUUID");
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
        nbttagcompound.setUniqueId("ownerUUID", ownerUUID);
        nbttagcompound.setBoolean("advanced", advanced);
        nbttagcompound.setBoolean("infinite", infinite);
        nbttagcompound.setBoolean("multiple", multiple);
        nbttagcompound.setBoolean("open", open);
        return super.writeToNBT(nbttagcompound);
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

    @Nonnull
    public NonNullList<ItemStack> getSoldItems() {
        NonNullList<ItemStack> stackNonNullList = NonNullList.create();
        stackNonNullList.add(inventory.getStackInSlot(9));
        if (multiple)
            for (int i = 10; i < 13; i++)
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
        for (int i = 0; i < 9; i++)
            stackNonNullList.add(inventory.getStackInSlot(i));
        return stackNonNullList;
    }

    public void setBoughtItem(ItemStack stack) {
        inventory.setInventorySlotContents(multiple ? 13 : 10, stack);
    }

    public boolean doesStackFit(ItemStack itemstack) {
        for (int i = 0; i < 9; i++) {
            itemstack = inventory.insertItem(i, itemstack, true);
        }
        return itemstack.isEmpty();
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        return super.getCapability(capability, facing);
    }
}

