package info.jbcs.minecraft.vending.gui.lib.elements;

import info.jbcs.minecraft.vending.gui.lib.input.InputKeyboardEvent;
import info.jbcs.minecraft.vending.gui.lib.input.InputMouseEvent;
import net.minecraft.client.gui.GuiTextField;

public class GuiEdit extends GuiElement {
    GuiTextField field;
    private String tempString = "";

    public GuiEdit(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public void onAdded() {
        field = new GuiTextField(0, gui.fontRenderer(), x, y, w, h);
        setText(tempString);
    }

    public String getText() {
        if (field == null) {
            return tempString;
        } else {
            return field.getText();
        }
    }

    public void setText(String text) {
        if (field == null) {
            tempString = text;
        } else {
            field.setText(text);
        }
    }

    @Override
    public void render() {
        field.drawTextBox();
    }

    @Override
    public void mouseDown(InputMouseEvent ev) {
        field.mouseClicked(ev.x, ev.y, ev.button);

        if (isMouseOver(ev)) {
            ev.handled = true;
        }
    }

    @Override
    public void keyPressed(InputKeyboardEvent ev) {
        if (!field.isFocused()) return;

        field.textboxKeyTyped(ev.character, ev.key);
        ev.handled = true;
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
}
