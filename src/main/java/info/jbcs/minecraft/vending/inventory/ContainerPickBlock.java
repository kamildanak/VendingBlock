package info.jbcs.minecraft.vending.inventory;

import info.jbcs.minecraft.vending.gui.GuiPickBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContainerPickBlock extends Container
{
    public ArrayList<ItemStack> items = new ArrayList<ItemStack>();
    public GuiPickBlock gui;
    EntityPlayer player;
    public int width = 9;
    public int height = 7;

    public SlotPickBlock resultSlot;

    public ContainerPickBlock(EntityPlayer p)
    {
        Set itemReg = GameData.getItemRegistry().getKeys();
        List<ResourceLocation> itemList = new ArrayList<ResourceLocation>();
        itemList.addAll(itemReg);

        for(ResourceLocation itemName: itemList){
            Item item = GameData.getItemRegistry().getObject(itemName);

            if (item != null && item.getCreativeTab() != null)
            {
                item.getSubItems(item, null, items);
            }
        }

        int index = 0;

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                addSlotToContainer(new SlotPickBlock(this, index++, 9 + x * 18, 18 + y * 18));
            }
        }

        addSlotToContainer(resultSlot = new SlotPickBlock(this, index++, 18, 153));
        player = p;
        scrollTo(0);
    }

    public InventoryStatic inventory = new InventoryStatic(width * height + 1)
    {
        @Override
        public void markDirty() {

        }

        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemstack)
        {
            return false;
        }
    };

    public void scrollTo(float offset)
    {
        int columnsNotFitting = items.size() / width - height + 1;

        if (columnsNotFitting < 0)
        {
            columnsNotFitting = 0;
        }

        int columnOffset = (int)(offset * columnsNotFitting + 0.5D);

        for (int y = 0; y < height; ++y)
        {
            for (int x = 0; x < width; ++x)
            {
                int index = x + (y + columnOffset) * width;

                if (index >= 0 && index < items.size())
                {
                    inventory.setInventorySlotContents(x + y * width, items.get(index));
                }
                else
                {
                    inventory.setInventorySlotContents(x + y * width, null);
                }
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        SlotPickBlock slot = (SlotPickBlock) this.inventorySlots.get(index);
        return slot.transferStackInSlot(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }
}
