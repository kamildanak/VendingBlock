package info.jbcs.minecraft.vending.gui;

import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.inventory.ContainerVendingStorageAttachment;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingStorageAttachment;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import org.lwjgl.opengl.GL11;

public class GuiVendingStorageAttachment  extends GuiContainer {
    private TileEntityVendingStorageAttachment storageAttachment;
    private EntityPlayer player;

    public GuiVendingStorageAttachment(EntityPlayer player, TileEntityVendingStorageAttachment attachment) {
        super(new ContainerVendingStorageAttachment(player.inventory, attachment, attachment.haveAccess(player)));
        storageAttachment = attachment;
        this.player = player;
        xSize = 232;
        ySize = 188;
    }

    public GuiVendingStorageAttachment(Container c, TileEntityVendingStorageAttachment attachment) {
        super(c);
        storageAttachment = attachment;
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.clear();
        storageAttachment.openInventory(player);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int a, int b) {
        FontRenderer fontRenderer = this.fontRenderer;
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("gui.vendingBlock.storage").trim(), 7, 6, 0x404040);
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("gui.vendingBlock.buy_buffer").trim(), 66, 6, 0x404040);
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("gui.vendingBlock.sell_buffer").trim(), 66, 42, 0x404040);
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("gui.vendingBlock.inventory").trim(), 66, 96, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Utils.bind("vending:textures/vending-storage-gui.png");
        drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
    }

    @Override
    public void onGuiClosed() {
        storageAttachment.closeInventory(player);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
