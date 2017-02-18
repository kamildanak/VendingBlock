package info.jbcs.minecraft.vending.gui.lib.layouts;

import info.jbcs.minecraft.vending.gui.lib.elements.GuiElement;
import org.lwjgl.opengl.GL11;

public class LinearLayout extends AbstractLayout {
    private int height, width;
    private boolean horizontal;

    public LinearLayout(int x, int y, boolean horizontal) {
        super(x, y, 0, 0);
        this.horizontal = horizontal;
    }

    public void render() {
        //((HUD)gui).drawRect(x, y + 1, x + 1, y+getHeight(), 0xFFFFFF);
        //((HUD)gui).drawRect(x+getWidth(), y + 1, x + getWidth() + 1, y+getHeight(), 0xFFFFFF);
        if (getChildren() == null || hidden) {
            return;
        }

        int offset = 0;
        if (horizontal) {
            for (GuiElement e : getChildren()) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                e.x = x + offset;
                e.y = y;
                if (e.center) e.y = y + getHeight() / 2 - e.getHeight() / 2;
                e.render();
                int w = e.getWidth();
                offset += w + ((w == 0) ? 0 : 6);
            }
        } else {
            for (GuiElement e : getChildren()) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                e.x = x;
                if (e.center) e.x = x + getWidth() / 2 - e.getWidth() / 2;
                e.y = y + offset;
                e.render();
                offset += e.getHeight();
            }
        }

    }

    @Override
    public int getHeight() {
        if (hidden) return 0;
        int s = 0;
        if (horizontal) {
            for (GuiElement guiElement : this.getChildren()) {
                if (s < guiElement.getHeight()) s = guiElement.getHeight();
            }
        } else {
            for (GuiElement guiElement : this.getChildren()) {
                s += guiElement.getHeight();
            }
        }
        return s;
    }

    @Override
    public int getWidth() {
        if (hidden) return 0;
        int s = 0;
        if (!horizontal) {
            for (GuiElement guiElement : this.getChildren()) {
                if (s < guiElement.getWidth()) s = guiElement.getWidth();
            }
        } else {
            for (GuiElement guiElement : this.getChildren()) {
                int w = guiElement.getWidth();
                s += w + ((w == 0) ? 0 : 6);
            }
            s -= 6;
        }
        return s;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }
}
