package info.jbcs.minecraft.vending.gui;

import com.kamildanak.minecraft.foamflower.gui.elements.GuiPickBlock;
import com.kamildanak.minecraft.foamflower.gui.input.IPickBlockHandler;
import info.jbcs.minecraft.vending.EnderPayApiUtils;
import info.jbcs.minecraft.vending.forge.LoaderWrapper;
import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.network.PacketDispatcher;
import info.jbcs.minecraft.vending.network.server.MessageAdvVenSetBanknote;
import info.jbcs.minecraft.vending.network.server.MessageAdvVenSetItem;
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

    GuiAdvancedVendingMachine(InventoryPlayer inventoryplayer, TileEntityVendingMachine machine) {
        super(new ContainerAdvancedVendingMachine(inventoryplayer, machine), machine);

        container = (ContainerAdvancedVendingMachine) inventorySlots;
        player = inventoryplayer.player;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(100, guiLeft + 118, guiTop + 58, 50, 20,
                I18n.format("gui.vendingBlock.select").trim()));

        if (LoaderWrapper.isEnderPayLoaded()) {
            buttonList.add(new GuiButton(101, guiLeft + 158, guiTop + 4, 15, 20,
                    "$"));
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button.id == 100) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiPickBlock(player,
                    container.entity.getInventoryWrapper().getBoughtItem(), this));
        }
        else if (button.id == 101) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiPickBanknoteValue(player,this,
                    container.entity.getInventoryWrapper().getBoughtItem()));
        }
    }

    @Override
    public void blockPicked(final ItemStack stack) {
        if (EnderPayApiUtils.isFilledBanknote(stack))
        {
            PacketDispatcher.sendToServer(new MessageAdvVenSetBanknote(EnderPayApiUtils.getBanknoteOriginalValue(stack)));
            return;
        }
        MessageAdvVenSetItem msg;
        if (stack.isEmpty()) {
            msg = new MessageAdvVenSetItem(0, 0, 0);
        } else {
            msg = new MessageAdvVenSetItem(Item.getIdFromItem(stack.getItem()), stack.getCount(), stack.getItemDamage());
        }
        PacketDispatcher.sendToServer(msg);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int a, int b) {
        super.drawGuiContainerForegroundLayer(a, b);
    }
}
