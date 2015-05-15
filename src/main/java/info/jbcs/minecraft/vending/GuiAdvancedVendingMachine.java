package info.jbcs.minecraft.vending;

import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
		buttonList.add(new GuiButton(100, guiLeft + 118, guiTop + 58, 50, 20, StatCollector.translateToLocal("vendingButton.select")));
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
		int type = 1;
		ByteBuf buffer = Unpooled.buffer();
		buffer.writeInt(type);

		if (stack == null) {
			buffer.writeInt(0);
		} else {
			buffer.writeInt(General.getItemId(stack.getItem()));
			buffer.writeInt(stack.stackSize);
			buffer.writeInt(stack.getItemDamage());
		}
		FMLProxyPacket packet = new FMLProxyPacket(buffer.copy(), "Vending");

		Vending.Channel.sendToServer(packet);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int a, int b) {
		super.drawGuiContainerForegroundLayer(a, b);
	}
}
