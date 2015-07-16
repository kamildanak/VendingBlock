package info.jbcs.minecraft.vending.gui;

import info.jbcs.minecraft.vending.GeneralClient;
import info.jbcs.minecraft.vending.inventory.ContainerVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.StatCollector;
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
		FontRenderer fontRenderer = this.fontRendererObj;
		fontRenderer.drawString(StatCollector.translateToLocal("gui.vendingBlock.storage"), 69, 6, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("gui.vendingBlock.selling"), 18, 20, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("gui.vendingBlock.buying"), 126, 20, 0x404040);
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
