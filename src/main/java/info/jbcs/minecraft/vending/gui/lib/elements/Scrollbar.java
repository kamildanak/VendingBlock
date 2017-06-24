package info.jbcs.minecraft.vending.gui.lib.elements;

import info.jbcs.minecraft.vending.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

public abstract class Scrollbar extends GuiButton {
    public boolean active;

    public float offset;
    public float step;

    boolean dragged;

    int elementHeight = 15;

    public Scrollbar(int id, int x, int y, int w, int h, String string) {
        super(id, x, y, w, h, string);
        offset = 0;
        step = 0.025f;
        height = h;
        active = true;
        dragged = false;
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of
     * MouseListener.mousePressed(MouseEvent e).
     */
    @Override
    public boolean mousePressed(Minecraft mc, int x, int y) {
        if (x < this.x || x >= this.x + width) {
            return false;
        }

        if (y < this.y || y >= this.y + height) {
            return false;
        }

        if (active) {
            dragged = true;
        }

        return true;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (dragged) {
            float initialOffset = offset;
            int off = y - this.y - elementHeight / 2;
            offset = 1.0f * off / (height - elementHeight);

            if (offset < 0) {
                offset = 0;
            }

            if (offset > 1) {
                offset = 1;
            }

            if (initialOffset != offset) {
                onScrolled(offset);
            }
        }

        int bottom = this.y + height;
        Utils.bind("textures/gui/container/creative_inventory/tabs.png");
        drawTexturedModalRect(this.x, this.y + (int) ((height - elementHeight) * offset), active ? 232 : 244, 0, 12, elementHeight);
    }

    public void handleMouseInput() {
        if (Mouse.getEventButton() == 0 && !Mouse.getEventButtonState()) {
            dragged = false;
        }

        if (!active) {
            return;
        }

        float initialOffset = offset;
        int direction = Mouse.getEventDWheel();

        if (direction != 0) {
            offset += direction > 0 ? -step : step;
        }

        if (offset < 0) {
            offset = 0;
        }

        if (offset > 1) {
            offset = 1;
        }

        if (initialOffset != offset) {
            onScrolled(offset);
        }
    }

    public abstract void onScrolled(float off);
}
