package info.jbcs.minecraft.vending.gui.lib.elements;

public class GuiLabelCarousel extends GuiLabelMultiline {
    private String[] captions = new String[0];

    public GuiLabelCarousel(int x, int y, int w, int h, String caption, int color) {
        super(x, y, w, h, caption, color);
    }

    public GuiLabelCarousel(int x, int y, String caption, int color) {
        super(x, y, caption, color);
    }

    public GuiLabelCarousel(int x, int y, int w, int h, String caption) {
        super(x, y, w, h, caption);
    }

    public GuiLabelCarousel(int x, int y, String caption) {
        super(x, y, caption);
    }

    @Override
    public void render() {
        if (hidden) return;
        if (captions.length == 0) return;
        int offset = 0;
        String caption = captions[((int) gui.getMinecraft().thePlayer.worldObj.getWorldTime() / 50) % captions.length];
        if (caption == null) return;
        for (String s : caption.split("\n")) {
            if (s.trim().length() == 0) continue;
            gui.drawString(s, x, y + offset, 0xffffff);
            offset += gui.fontRenderer().FONT_HEIGHT;
        }
    }

    @Override
    public int getHeight() {
        if (hidden) return 0;
        if (gui.fontRenderer() == null) return 0;
        int max = 0;
        for (String caption : captions) {
            int c = 0;
            for (String s : caption.split("\n"))
                if (s.trim().length() != 0) c++;
            if (max < c) max = c;
        }
        return max * gui.fontRenderer().FONT_HEIGHT;
    }

    @Override
    public int getWidth() {
        if (hidden) return 0;
        int max = 0;
        for (String caption : captions) {
            if (caption == null) continue;
            for (String s : caption.split("\n")) {
                if (max < gui.fontRenderer().getStringWidth(s)) max = gui.fontRenderer().getStringWidth(s);
            }
        }
        return max;
    }

    public void setCaption(String[] captions) {
        this.captions = captions;
    }
}
