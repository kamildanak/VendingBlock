package info.jbcs.minecraft.vending.gui.hud;

import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.gui.lib.IGuiWrapper;
import info.jbcs.minecraft.vending.gui.lib.elements.GuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public abstract class HUD extends GuiScreen implements IGuiWrapper {
    private Minecraft mc;

    public HUD(Minecraft mc) {
        this.mc = mc;
    }

    public void render() {
        getRoot().render();
    }

    public abstract GuiElement getRoot();

    @Override
    public FontRenderer fontRenderer() {
        return mc.fontRendererObj;
    }

    @Override
    public void bindTexture(String texture) {
        Utils.bind(texture);
    }

    @Override
    public void drawString(String text, int sx, int sy, int color) {
        mc.fontRendererObj.drawString(text, sx, sy, color);
    }

    @Override
    public void drawTiledRect(int x2, int y, int w2, int h1, int u2, int v1, int texw2, int texh1) {

    }

    public void drawTexturedRectangle(int x, int y, int textureX, int textureY, int width, int height) {
        super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    @Override
    public void drawCenteredString(String caption, int i, int i1, int color) {

    }

    public Minecraft getMinecraft() {
        return mc;
    }

}
