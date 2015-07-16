package info.jbcs.minecraft.vending.gui;

import info.jbcs.minecraft.vending.General;
import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.network.MsgAdvVenSetItem;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class GuiAdvancedVendingMachine extends GuiVendingMachine implements IPickBlockHandler {
	ContainerAdvancedVendingMachine container;
	EntityPlayer player;

	public GuiAdvancedVendingMachine(InventoryPlayer inventoryplayer, TileEntityVendingMachine machine) {
		super(new ContainerAdvancedVendingMachine(inventoryplayer, machine));

		container = (ContainerAdvancedVendingMachine) inventorySlots;
		player = inventoryplayer.player;
	}

	@Override
	public void initGui() {
		super.initGui();

		buttonList.clear();
		buttonList.add(new GuiButton(100, guiLeft + 118, guiTop + 58, 50, 20, StatCollector.translateToLocal("gui.vendingBlock.select")));
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton button) {
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
			msg = new MsgAdvVenSetItem(General.getItemId(stack.getItem()), stack.stackSize, stack.getItemDamage());
		}
		Vending.instance.messagePipeline.sendToServer(msg);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int a, int b) {
		super.drawGuiContainerForegroundLayer(a, b);
	}
}
