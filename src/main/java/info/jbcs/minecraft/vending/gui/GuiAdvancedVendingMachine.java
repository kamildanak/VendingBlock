package info.jbcs.minecraft.vending.gui;

import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.gui.lib.elements.GuiPickBlock;
import info.jbcs.minecraft.vending.gui.lib.input.IPickBlockHandler;
import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.network.MsgAdvVenSetItem;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GuiAdvancedVendingMachine extends GuiVendingMachine implements IPickBlockHandler {
    private ContainerAdvancedVendingMachine container;
    private EntityPlayer player;

    public GuiAdvancedVendingMachine(InventoryPlayer inventoryplayer, TileEntityVendingMachine machine) {
        super(new ContainerAdvancedVendingMachine(inventoryplayer, machine), machine);

        container = (ContainerAdvancedVendingMachine) inventorySlots;
        player = inventoryplayer.player;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(100, guiLeft + 118, guiTop + 58, 50, 20,
                I18n.format("gui.vendingBlock.select").trim()));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button.id == 100) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiPickBlock(player, container.entity.getBoughtItems()[0], this));
        }
    }

    @Override
    public void blockPicked(final ItemStack stack) {
        MsgAdvVenSetItem msg;
        if (stack == null) {
            msg = new MsgAdvVenSetItem(0, 0, 0);
        } else {
            msg = new MsgAdvVenSetItem(Item.getIdFromItem(stack.getItem()), stack.stackSize, stack.getItemDamage());
        }
        Vending.instance.messagePipeline.sendToServer(msg);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int a, int b) {
        super.drawGuiContainerForegroundLayer(a, b);
    }
}
