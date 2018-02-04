package info.jbcs.minecraft.vending.inventory;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class InventorySerializable extends InventoryBasic {
    InventorySerializable(String title, boolean customName, int slotCount) {
        super(title, customName, slotCount);
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.clear();
        NBTTagList tagList = compound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getByte("slot") & 0xff;

            if (slot < getSizeInventory()) {
                setInventorySlotContents(slot, new ItemStack(itemTags));
            }
        }

        if (compound.hasKey("CustomName", 8)) {
            setCustomName(compound.getString("CustomName"));
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            if (!getStackInSlot(i).isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("slot", (byte) i);
                getStackInSlot(i).writeToNBT(itemTag);
                nbtTagList.appendTag(itemTag);
            }
        }
        compound.setTag("Items", nbtTagList);
        compound.setInteger("Size", getSizeInventory()); //TODO: remove with next Minecraft update

        if (this.hasCustomName()) {
            compound.setString("CustomName", this.getName());
        }

        return compound;
    }
}
