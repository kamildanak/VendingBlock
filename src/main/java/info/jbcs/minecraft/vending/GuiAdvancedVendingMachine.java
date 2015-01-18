package info.jbcs.minecraft.vending;

import info.jbcs.minecraft.gui.GuiPickBlock;
import info.jbcs.minecraft.gui.IPickBlockHandler;
import info.jbcs.minecraft.utilities.packets.PacketData;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

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
		buttonList.add(new GuiButton(100, guiLeft + 118, guiTop + 58, 50, 20, "Select..."));
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 100) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiPickBlock(player, container.entity.getBoughtItem(), this));
		}
	}

	@Override
	public void blockPicked(final ItemStack stack) {
		Packets.advancedMachine.sendToServer(new PacketData() {
			@Override
			public void data(DataOutputStream stream) throws IOException {
				if (stack == null) {
					stream.writeInt(0);
				} else {
					stream.writeInt(stack.itemID);
					stream.writeInt(stack.stackSize);
					stream.writeInt(stack.getItemDamage());
				}
			}
		});
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int a, int b) {
		super.drawGuiContainerForegroundLayer(a, b);
	}
}
