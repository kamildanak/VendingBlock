package info.jbcs.minecraft.vending.gui.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public interface IGuiWrapper {
    FontRenderer fontRenderer();

    void drawString(String text, int sx, int sy, int color);

    void bindTexture(String texture);

    void drawTexturedModalRect(int x, int y, int u1, int v1, int w1, int h1);

    void drawTiledRect(int x2, int y, int w2, int h1, int u2, int v1, int texw2, int texh1);

    void drawCenteredString(FontRenderer fontRenderer, String caption, int i, int i1, int color);

    Minecraft getMinecraft();
}
