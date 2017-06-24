package info.jbcs.minecraft.vending.gui.lib;

import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.gui.lib.elements.GuiElement;
import info.jbcs.minecraft.vending.gui.lib.input.InputKeyboardEvent;
import info.jbcs.minecraft.vending.gui.lib.input.InputMouseEvent;
import info.jbcs.minecraft.vending.gui.lib.layouts.AbsoluteLayout;
import info.jbcs.minecraft.vending.inventory.DummyContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiScreenPlus extends GuiContainer implements IGuiWrapper {
    public int screenW;
    public int screenH;
    public int screenX;
    public int screenY;

    public GuiElement root;

    String backgroundTexture;
    InputMouseEvent mouseEvent = new InputMouseEvent();
    int oldX = -1;
    int oldY = -1;
    boolean[] downButtons = new boolean[12];
    InputKeyboardEvent keyboardEvent = new InputKeyboardEvent();

    public GuiScreenPlus(Container container, int w, int h, String backgroundTexture) {
        super(container);
        root = new AbsoluteLayout(0, 0);
        root.gui = this;
        this.screenW = w;
        this.screenH = h;
        this.backgroundTexture = backgroundTexture;
    }

    public GuiScreenPlus(int w, int h, String backgroundTexture) {
        this(new DummyContainer(), w, h, backgroundTexture);
    }

    @Override
    public void initGui() {
        xSize = screenW;
        ySize = screenH;
        super.initGui();
        screenX = guiLeft;
        screenY = guiTop;
        root.onAdded();

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void handleInput() throws IOException {
        while (Mouse.next()) {
            this.handleMouseInput();
        }

        while (Keyboard.next()) {
            this.handleKeyboardInput();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        mouseEvent.handled = false;
        mouseEvent.x = Mouse.getEventX() * width / mc.displayWidth - this.screenX;
        mouseEvent.y = height - Mouse.getEventY() * height / mc.displayHeight - 1 - this.screenY;

        if (oldX == -1) {
            oldX = mouseEvent.x;
            oldY = mouseEvent.y;
        }

        mouseEvent.dx = mouseEvent.x - oldX;
        mouseEvent.dy = mouseEvent.y - oldY;
        oldX = mouseEvent.x;
        oldY = mouseEvent.y;
        mouseEvent.down = Mouse.getEventButtonState();
        mouseEvent.button = Mouse.getEventButton();
        mouseEvent.wheel = Mouse.getEventDWheel();

        if (mouseEvent.wheel != 0) {
            if (mouseEvent.wheel < 0) {
                mouseEvent.wheel = -1;
            } else {
                mouseEvent.wheel = 1;
            }

            root.mouseWheel(mouseEvent);
        } else if (mouseEvent.button >= 0 && mouseEvent.button < downButtons.length) {
            if (downButtons[mouseEvent.button] != mouseEvent.down) {
                downButtons[mouseEvent.button] = mouseEvent.down;

                if (mouseEvent.down) {
                    root.mouseDown(mouseEvent);
                } else {
                    root.mouseUp(mouseEvent);
                }
            } else if (mouseEvent.dx != 0 || mouseEvent.dy != 0) {
                root.mouseMove(mouseEvent);
            }
        } else if (mouseEvent.dx != 0 || mouseEvent.dy != 0) {
            root.mouseMove(mouseEvent);
        }

        if (!mouseEvent.handled) {
            super.handleMouseInput();
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        keyboardEvent.handled = false;

        if (Keyboard.getEventKeyState()) {
            keyboardEvent.key = Keyboard.getEventKey();
            keyboardEvent.character = Keyboard.getEventCharacter();

            switch (keyboardEvent.key) {
                case 1:
                    break;

                default:
                    root.keyPressed(keyboardEvent);
            }
        }

        if (!keyboardEvent.handled) {
            super.handleKeyboardInput();
        }
    }

    public void close() {
        mc.displayGuiScreen(null);
        mc.setIngameFocus();
    }

    protected void addChild(GuiElement e) {
        root.addChild(e);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int bx, int by) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(backgroundTexture);
        drawTexturedModalRect(screenX, screenY, 0, 0, screenW, screenH);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int fx, int fy) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        root.render();
    }

    public void drawString(String text, int sx, int sy, int color) {
        FontRenderer fontRenderer = this.fontRenderer;
        fontRenderer.drawString(text, sx, sy, color);
    }

    public void drawCenteredString(String text, int sx, int sy, int color) {
        FontRenderer fontRenderer = this.fontRenderer;
        fontRenderer.drawString(text, sx - fontRenderer.getStringWidth(text) / 2, sy - fontRenderer.FONT_HEIGHT / 2, color);
    }

    public void drawStringWithShadow(String text, int sx, int sy, int color) {
        FontRenderer fontRenderer = this.fontRenderer;
        fontRenderer.drawStringWithShadow(text, sx, sy, color);
    }

    public void drawCenteredStringWithShadow(String text, int sx, int sy, int color) {
        FontRenderer fontRenderer = this.fontRenderer;
        fontRenderer.drawStringWithShadow(text, sx - fontRenderer.getStringWidth(text) / 2, sy - fontRenderer.FONT_HEIGHT / 2, color);
    }

    public FontRenderer fontRenderer() {
        return mc.fontRenderer;
    }

    protected void drawRect(int gx, int gy, int gw, int gh, int c1, int c2) {
        drawGradientRect(gx, gy, gx + gw, gy + gh, c1, c2);
    }

    public void drawTiledRect(int rx, int ry, int rw, int rh, int u, int v, int tw, int th) {
        if (rw == 0 || rh == 0 || tw == 0 || th == 0) return;

        for (int y = 0; y < rh; y += th) {
            for (int x = 0; x < rw; x += tw) {
                int qw = tw;

                if (x + qw > rw) {
                    qw = rw - x;
                }

                int qh = th;

                if (y + qh > rh) {
                    qh = rh - y;
                }

                int x1 = rx + x;
                int w = x + qw;
                int y1 = ry + y;
                int h = y + qh;
                drawTexturedModalRect(x1, y1, u, v, w, h);
            }
        }
    }

    public void bindTexture(String tex) {
        Utils.bind(tex);
    }

    public Minecraft getMinecraft() {
        return mc;
    }

    public void drawTexturedRectangle(int x, int y, int textureX, int textureY, int width, int height) {
        super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }
}
