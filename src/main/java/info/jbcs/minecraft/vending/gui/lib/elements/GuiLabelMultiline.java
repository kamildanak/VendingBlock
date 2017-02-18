package info.jbcs.minecraft.vending.gui.lib.elements;

public class GuiLabelMultiline extends GuiLabel {
    public GuiLabelMultiline(int x, int y, int w, int h, String caption, int color) {
        super(x, y, w, h, caption, color);
    }

    public GuiLabelMultiline(int x, int y, String caption, int color) {
        super(x, y, caption, color);
    }

    public GuiLabelMultiline(int x, int y, int w, int h, String caption) {
        super(x, y, w, h, caption);
    }

    public GuiLabelMultiline(int x, int y, String caption) {
        super(x, y, caption);
    }

    @Override
    public void render() {
        if (hidden) return;
        int offset = 1;
        for (String s : getCaption().split("\n")) {
            gui.drawString(s, x, y + offset, super.getColor());
            offset += gui.fontRenderer().FONT_HEIGHT;
        }
    }

    @Override
    public int getHeight() {
        if (hidden) return 0;
        if (gui.fontRenderer() != null) {
            int c = 0;
            for (String s : getCaption().split("\n"))
                if (s.trim().length() != 0) c++;
            return c;
        }
        return 0;
    }

    @Override
    public int getWidth() {
        if (hidden) return 0;
        int max = 0;
        for (String s : getCaption().split("\n")) {
            if (max < gui.fontRenderer().getStringWidth(s)) max = gui.fontRenderer().getStringWidth(s);
        }
        return max;
    }
}
