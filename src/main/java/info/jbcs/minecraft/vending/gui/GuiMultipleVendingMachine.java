package info.jbcs.minecraft.vending.gui;

import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.inventory.ContainerMultipleVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GuiMultipleVendingMachine extends GuiVendingMachine {
    private ContainerMultipleVendingMachine container;
    private EntityPlayer player;

    public GuiMultipleVendingMachine(InventoryPlayer inventoryplayer, TileEntityVendingMachine machine) {
        super(new ContainerMultipleVendingMachine(inventoryplayer, machine), machine);
        container = (ContainerMultipleVendingMachine) inventorySlots;
        player = inventoryplayer.player;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int a, int b) {
        FontRenderer fontRenderer = this.fontRendererObj;
        fontRenderer.drawString(I18n.format("gui.vendingBlock.storage").trim(), 69, 6, 0x404040);
        fontRenderer.drawString(I18n.format("gui.vendingBlock.selling").trim(), 15, 15, 0x404040);
        fontRenderer.drawString(I18n.format("gui.vendingBlock.buying").trim(), 126, 20, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Utils.bind("vending:textures/vending-multiple-gui.png");
        drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
    }

}
