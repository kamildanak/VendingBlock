package info.jbcs.minecraft.vending.inventory;

import info.jbcs.minecraft.vending.gui.lib.elements.GuiPickBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContainerPickBlock extends Container {
    public NonNullList<ItemStack> itemList = NonNullList.create();
    public GuiPickBlock gui;
    public int width = 9;
    public int height = 5;
    public SlotPickBlock resultSlot;
    public InventoryStatic inventory = new InventoryStatic(width * height + 1) {
        @Override
        @Nonnull
        public ItemStack removeStackFromSlot(int i) {
            return ItemStack.EMPTY;
        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
            return false;
        }
    };

    public ContainerPickBlock() {
        Set<ResourceLocation> itemReg = Item.REGISTRY.getKeys();
        List<ResourceLocation> itemArray = new ArrayList<>();
        itemArray.addAll(itemReg);

        for (ResourceLocation itemName : itemArray) {
            Item item = Item.REGISTRY.getObject(itemName);

            if (item != null && item.getCreativeTab() != null) {
                item.getSubItems(item.getCreativeTab(), itemList);
            }
        }

        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                addSlotToContainer(new SlotPickBlock(this, index++, 9 + x * 18, 18 + y * 18));
            }
        }

        //noinspection UnusedAssignment
        addSlotToContainer(resultSlot = new SlotPickBlock(this, index++, 9, 112));
        scrollTo(0);
    }

    public void scrollTo(float offset) {
        int columnsNotFitting = itemList.size() / width - height + 1;

        if (columnsNotFitting < 0) {
            columnsNotFitting = 0;
        }

        int columnOffset = (int) (offset * columnsNotFitting + 0.5D);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int index = x + (y + columnOffset) * width;

                if (index >= 0 && index < itemList.size()) {
                    inventory.setInventorySlotContents(x + y * width, itemList.get(index));
                } else {
                    inventory.setInventorySlotContents(x + y * width, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) {
        return false;
    }

    public boolean canScroll() {
        return true;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
    }
}
