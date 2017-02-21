package info.jbcs.minecraft.vending.inventory;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public abstract class InventoryStatic implements IInventory {
    public final NonNullList<ItemStack> items;

    public InventoryStatic(int size) {
        items = NonNullList.withSize(size,ItemStack.EMPTY);
    }

    @Override
    @Nonnull
    public String getName() {
        return "";
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("");
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer entityplayer) {
        return false;
    }

    public void onInventoryChanged(int slot) {
    }

    @Override
    public int getSizeInventory() {
        return items.size();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int i) {
        return items.get(i);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int i, int j) {
        if (!items.get(i).isEmpty()) {
            if (items.get(i).getCount() <= j) {
                ItemStack itemstack = items.get(i);
                items.set(i,ItemStack.EMPTY);
                onInventoryChanged();
                onInventoryChanged(i);
                return itemstack;
            }

            ItemStack itemstack1 = items.get(i).splitStack(j);

            if (items.get(i).getCount() == 0) {
                items.set(i,ItemStack.EMPTY);
            }

            onInventoryChanged();
            onInventoryChanged(i);
            return itemstack1;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
        items.set(i,itemstack);

        if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit()) {
            itemstack.setCount(getInventoryStackLimit());
        }

        onInventoryChanged();
        onInventoryChanged(i);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        NBTTagList nbtTagList = nbtTagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nbtTagList.tagCount(); ++i) {
            NBTTagCompound nbtTagCompound1 = nbtTagList.getCompoundTagAt(i);
            int j = nbtTagCompound1.getByte("slot") & 0xff;

            items.set(j, new ItemStack(nbtTagCompound1));
        }

        onInventoryChanged();
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                continue;
            }

            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("slot", (byte) i);
            items.get(i).writeToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
        }

        nbttagcompound.setTag("Items", nbttaglist);
    }

    private int getFirstEmptyStack(int start, int end) {
        for (int i = start; i <= end; i++) {
            if (items.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private int storeItemStack(ItemStack itemstack, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (!items.get(i).isEmpty() &&
                    items.get(i).getItem() == itemstack.getItem() &&
                    items.get(i).isStackable() &&
                    items.get(i).getCount() < items.get(i).getMaxStackSize() &&
                    items.get(i).getCount() < getInventoryStackLimit() &&
                    (!items.get(i).getHasSubtypes() || items.get(i).getItemDamage() == itemstack.getItemDamage())) {
                if (items.get(i).hasTagCompound() || itemstack.hasTagCompound()) {
                    if (items.get(i).hasTagCompound() && itemstack.hasTagCompound()) {
                        if (items.get(i).getTagCompound().equals(itemstack.getTagCompound())) {
                            return i;
                        }
                    }
                } else {
                    return i;
                }
            }
        }

        return -1;
    }

    private int storePartialItemStack(ItemStack itemstack, int start, int end) {
        Item i = itemstack.getItem();
        int j = itemstack.getCount();
        int k = storeItemStack(itemstack, start, end);

        if (k < 0) {
            k = getFirstEmptyStack(start, end);
        }

        if (k < 0) {
            return j;
        }

        if (items.get(k).isEmpty()) {
            items.set(k,new ItemStack(i, 0, itemstack.getItemDamage()));
            if (itemstack.hasTagCompound()) {
                items.get(k).setTagCompound(itemstack.getTagCompound());
            }
        }

        int l = j;

        if (l > items.get(k).getMaxStackSize() - items.get(k).getCount()) {
            l = items.get(k).getMaxStackSize() - items.get(k).getCount();
        }

        if (l > getInventoryStackLimit() - items.get(k).getCount()) {
            l = getInventoryStackLimit() - items.get(k).getCount();
        }

        if (l == 0) {
            return j;
        } else {
            j -= l;
            items.get(k).setCount(items.get(k).getCount()+1);
            items.get(k).setAnimationsToGo(5);
            onInventoryChanged();
            onInventoryChanged(k);
            return j;
        }
    }

    public boolean addItemStackToInventory(@Nonnull ItemStack itemstack, int start, int end) {
        if (itemstack.isEmpty()) {
            return true;
        }
        if (!itemstack.isItemDamaged()) {
            int i;
            do {
                i = itemstack.getCount();
                itemstack.setCount(storePartialItemStack(itemstack, start, end));
            } while (itemstack.getCount() > 0 && itemstack.getCount() < i);

            return itemstack.getCount() < i;
        }
        int j = getFirstEmptyStack(start, end);

        if (j >= 0) {
            items.set(j,itemstack.copy());
            if (itemstack.hasTagCompound()) {
                items.get(j).setTagCompound(itemstack.getTagCompound());
            }
            items.get(j).setAnimationsToGo(5);
            itemstack.setCount(0);
            onInventoryChanged();
            onInventoryChanged(j);
            return true;
        } else {
            return false;
        }
    }

    public boolean addItemStackToInventory(ItemStack itemstack) {
        return addItemStackToInventory(itemstack, 0, items.size() - 1);
    }

    public ItemStack takeItems(ItemStack itemStack, int damage, int count) {
        ItemStack res = ItemStack.EMPTY;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty() || items.get(i).getItem() != itemStack.getItem() || items.get(i).getItemDamage() != damage) {
                continue;
            }

            if (itemStack.hasTagCompound()) {
                if (!itemStack.getTagCompound().equals(items.get(i).getTagCompound())) {
                    continue;
                }
            }
            if (res.isEmpty()) {
                res = new ItemStack(itemStack.getItem(), 0, damage);
            }

            while (items.get(i) != null && res.getCount() < count && items.get(i).getCount() > 0) {
                res.setCount(res.getCount()+1);
                items.get(i).setCount(items.get(i).getCount()-1);

                if (items.get(i).getCount() == 0) {
                    items.set(i, ItemStack.EMPTY);
                }

                onInventoryChanged(i);
            }

            if (res.getCount() >= count) {
                break;
            }
        }

        onInventoryChanged();
        return res;
    }

    //@Override
    public void onInventoryChanged() {
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {
    }

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
        return true;
    }

    public boolean isEmpty() {
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void clear() {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
    }

    public void throwItems(World world, int x, int y, int z) {
        if (world.isRemote) return;

        for (int i = 0; i < items.size(); i++) {
            ItemStack itemstack = items.get(i);
            if (itemstack.isEmpty()) continue;

            items.set(i, ItemStack.EMPTY);

            float xx = world.rand.nextFloat() * 0.8F + 0.1F;
            float yy = world.rand.nextFloat() * 0.8F + 0.1F;
            float zz = world.rand.nextFloat() * 0.8F + 0.1F;
            while (itemstack.getCount() > 0) {
                int c = world.rand.nextInt(21) + 10;
                if (c > itemstack.getCount()) {
                    c = itemstack.getCount();
                }

                itemstack.setCount(itemstack.getCount()-c);
                EntityItem entityitem = new EntityItem(world, x + xx, y + yy, z + zz, new ItemStack(itemstack.getItem(), c, itemstack.getItemDamage()));
                float f3 = 0.05F;
                entityitem.motionX = (float) world.rand.nextGaussian() * f3;
                entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
                world.spawnEntity(entityitem);
            }
        }

        onInventoryChanged();
    }
}
