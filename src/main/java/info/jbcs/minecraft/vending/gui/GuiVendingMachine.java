package info.jbcs.minecraft.vending.gui;

import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.inventory.ContainerVendingMachine;
import info.jbcs.minecraft.vending.network.PacketDispatcher;
import info.jbcs.minecraft.vending.network.server.MessageSetLock;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLockIconButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import org.lwjgl.opengl.GL11;

public class GuiVendingMachine extends GuiContainer {
    private GuiLockIconButton guiLockIconButton;
    private TileEntityVendingMachine tileEntityVendingMachine;

    public GuiVendingMachine(InventoryPlayer inventoryplayer, TileEntityVendingMachine machine) {
        super(new ContainerVendingMachine(inventoryplayer, machine));
        tileEntityVendingMachine = machine;
    }

    public GuiVendingMachine(Container c, TileEntityVendingMachine machine) {
        super(c);
        tileEntityVendingMachine = machine;
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.clear();
        buttonList.add(guiLockIconButton = new GuiLockIconButton(107, guiLeft + 7, guiTop + 63));
        guiLockIconButton.setLocked(!tileEntityVendingMachine.isOpen());
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 107) {
            boolean lock = tileEntityVendingMachine.isOpen();
            MessageSetLock msg = new MessageSetLock(tileEntityVendingMachine.getPos(), lock);

            PacketDispatcher.sendToServer(msg);
            guiLockIconButton.setLocked(lock);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int a, int b) {
        FontRenderer fontRenderer = this.fontRendererObj;
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("gui.vendingBlock.storage").trim(), 69, 6, 0x404040);
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("gui.vendingBlock.selling").trim(), 18, 20, 0x404040);
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("gui.vendingBlock.buying").trim(), 126, 20, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Utils.bind("vending:textures/vending-gui.png");
        drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
    }

    @Override
    public void onGuiClosed() {
    }
}
