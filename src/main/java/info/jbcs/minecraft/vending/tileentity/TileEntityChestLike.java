package info.jbcs.minecraft.vending.tileentity;

import info.jbcs.minecraft.vending.block.BlockVendingStorageAttachment;
import info.jbcs.minecraft.vending.init.VendingSoundEvents;
import info.jbcs.minecraft.vending.inventory.ContainerVendingStorageAttachment;
import info.jbcs.minecraft.vending.inventory.InventoryVendingStorageAttachment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityChestLike extends TileEntityLockable implements ISidedInventory, ITickable {
    private IItemHandler itemHandler = new SidedInvWrapper(this, net.minecraft.util.EnumFacing.DOWN);
    public InventoryVendingStorageAttachment inventory;
    public int numPlayersUsing;
    public float prevLidAngle;
    public float lidAngle;
    private int ticksSinceSync;

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
    public String getName() {
        return "Vending Storage Attachment";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    public void openInventory(EntityPlayer player) {
        if (!player.isSpectator() && this.getBlockType() instanceof BlockVendingStorageAttachment) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
            IBlockState blockState = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, blockState, blockState, 2);
        }
    }

    public void closeInventory(EntityPlayer player) {
        if (!player.isSpectator() && this.getBlockType() instanceof BlockVendingStorageAttachment) {
            --this.numPlayersUsing;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
            IBlockState blockState = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, blockState, blockState, 2);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        inventory.deserializeNBT(nbttagcompound);
        if (nbttagcompound.hasKey("numPlayersUsing"))
            numPlayersUsing = nbttagcompound.getInteger("numPlayersUsing");
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbttagcompound) {
        nbttagcompound.merge(inventory.serializeNBT());
        nbttagcompound.setInteger("numPlayersUsing", numPlayersUsing);
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

    @Override
    public void update() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        ++this.ticksSinceSync;

        if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
            this.numPlayersUsing = 0;
            float f = 5.0F;

            for (EntityPlayer entityplayer :
                    this.world.getEntitiesWithinAABB(EntityPlayer.class,
                            new AxisAlignedBB((double) ((float) i - 5.0F),
                                    (double) ((float) j - 5.0F), (double) ((float) k - 5.0F),
                                    (double) ((float) (i + 1) + 5.0F),
                                    (double) ((float) (j + 1) + 5.0F),
                                    (double) ((float) (k + 1) + 5.0F)))) {
                if (entityplayer.openContainer instanceof ContainerVendingStorageAttachment) {
                    IInventory iinventory = ((ContainerVendingStorageAttachment) entityplayer.openContainer).getStorageInventory();

                    if (iinventory == this) {
                        ++this.numPlayersUsing;
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle;
        float f1 = 0.1F;

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
            double d1 = (double) i + 0.5D;
            double d2 = (double) k + 0.5D;

            this.world.playSound( d1, (double) j + 0.5D, d2,
                    VendingSoundEvents.STORAGE_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F,
                    true);
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
            float f2 = this.lidAngle;

            if (this.numPlayersUsing > 0) {
                this.lidAngle += 0.1F;
            } else {
                this.lidAngle -= 0.1F;
            }

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            float f3 = 0.5F;

            if (this.lidAngle < 0.5F && f2 >= 0.5F) {
                double d3 = (double) i + 0.5D;
                double d0 = (double) k + 0.5D;

                this.world.playSound( d3, (double) j + 0.5D, d0,
                        VendingSoundEvents.STORAGE_CLOSE,
                        SoundCategory.BLOCKS, 0.5F,
                        this.world.rand.nextFloat() * 0.1F + 0.9F, true);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }
    }
}
