package info.jbcs.minecraft.vending;

import info.jbcs.minecraft.utilities.GeneralClient;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.lwjgl.opengl.GL11;

public class GuiVendingMachine extends GuiContainer {
	public GuiVendingMachine(InventoryPlayer inventoryplayer, TileEntityVendingMachine machine) {
		super(new ContainerVendingMachine(inventoryplayer, machine));
	}

	public GuiVendingMachine(Container c) {
		super(c);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int a, int b) {
		fontRenderer.drawString("Storage", 69, 6, 0x404040);
		fontRenderer.drawString("Selling", 18, 20, 0x404040);
		fontRenderer.drawString("Buying", 126, 20, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GeneralClient.bind("vending:textures/vending-gui.png");
		drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
	}

	@Override
	public void onGuiClosed() {
	}
}
