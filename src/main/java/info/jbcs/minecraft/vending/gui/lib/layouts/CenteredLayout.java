package info.jbcs.minecraft.vending.gui.lib.layouts;

import info.jbcs.minecraft.vending.gui.lib.elements.GuiElement;
import org.lwjgl.opengl.GL11;

public class CenteredLayout extends AbstractLayout {
    private boolean horizontal;
    private boolean vertical;
    private int cX, cY;

    public CenteredLayout(int x, int y, int w, int h, boolean horizontal, boolean vertical) {
        super(x, y, w, h);
        this.horizontal = horizontal;
        this.vertical = vertical;
        cX = (x + w) / 2;
        cY = (y + h) / 2;
    }

    @Override
    public int getHeight() {
        if (hidden) return 0;
        return h;
    }

    @Override
    public int getWidth() {
        if (hidden) return 0;
        return w;
    }

    public void render() {
        cX = (w) / 2;
        cY = (h) / 2;
        if (getChildren() == null || hidden) {
            return;
        }
        for (GuiElement e : getChildren()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (horizontal)
                e.x = cX - e.getWidth() / 2;
            else
                e.x = x;
            if (vertical)
                e.y = cY - e.getHeight() / 2;
            else
                e.y = y;
            e.render();
        }
    }
}
