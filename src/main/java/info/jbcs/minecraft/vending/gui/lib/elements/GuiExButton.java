package info.jbcs.minecraft.vending.gui.lib.elements;

import info.jbcs.minecraft.vending.gui.lib.input.InputMouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;

public class GuiExButton extends GuiElement {
    public boolean disabled;
    public TexturedBox boxDisabled;
    public TexturedBox boxNormal;
    public TexturedBox boxOver;
    protected String caption;
    int u, v, texw, texh;
    int borderTop, borderRight, borderBottom, borderLeft;
    boolean over;

    public GuiExButton(int x, int y, int w, int h, String caption) {
        this(x, y, w, h, caption, "textures/gui/widgets.png");
    }

    public GuiExButton(int x, int y, int w, int h, String caption, String texture) {
        super(x, y, w, h);
        this.caption = I18n.format(caption).trim();
        disabled = false;
        u = 0;
        v = 46;
        texw = 200;
        texh = 20;
        borderTop = 2;
        borderRight = 2;
        borderBottom = 3;
        borderLeft = 2;

        boxDisabled = new TexturedBox(texture, 0, 46, 200, 20, 2, 2, 3, 2);
        boxNormal = new TexturedBox(texture, 0, 66, 200, 20, 2, 2, 3, 2);
        boxOver = new TexturedBox(texture, 0, 86, 200, 20, 2, 2, 3, 2);

    }

    public void onClick() {
    }

    @Override
    public void render() {
        int color = 0xffe0e0e0;
        TexturedBox box = boxDisabled;

        if (!disabled && !over) {
            box = boxNormal;
            color = 0xffffffff;
        } else if (!disabled) {
            box = boxOver;
            color = 0xffffff70;
        }
        FontRenderer fontRenderer = gui.fontRenderer();
        box.render(gui, x, y, w, h);
        gui.drawCenteredString(fontRenderer, caption, x + w / 2, y + h / 4 + 1, color);
    }

    @Override
    public void mouseMove(InputMouseEvent ev) {
        over = isMouseOver(ev);
    }

    @Override
    public void mouseDown(InputMouseEvent ev) {
        if (!isMouseOver(ev)) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        SoundHandler soundHandler = new SoundHandler(mc.getResourceManager(), mc.gameSettings);
        soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        onClick();
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
