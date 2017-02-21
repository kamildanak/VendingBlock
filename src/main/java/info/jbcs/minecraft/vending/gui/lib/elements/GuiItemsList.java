package info.jbcs.minecraft.vending.gui.lib.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.lwjgl.opengl.GL11;

public class GuiItemsList extends GuiElement {

    private NonNullList<ItemStack> items;

    public GuiItemsList(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public void render() {

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, -100.0f);
        GL11.glDisable(GL11.GL_LIGHTING);
        drawItemsWithLabel(gui.fontRenderer(), x, y, items);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public int getHeight() {
        if (hidden || items == null) return 0;
        return 12 + 2;
    }

    @Override
    public int getWidth() {
        if (hidden || items == null) return 0;
        int c = 0;
        for (ItemStack itemStack : items) {
            if (itemStack.isEmpty()) continue;
            c++;
        }
        return 18 * c;
    }

    private void drawNumberForItem(FontRenderer fontRenderer, ItemStack stack, int ux, int uy) {
        if (stack.isEmpty() || stack.getCount() < 2) {
            return;
        }

        String line = "" + stack.getCount();
        int x = ux + 19 - 2 - fontRenderer.getStringWidth(line);
        int y = uy + 6 + 3;
        GL11.glTranslatef(0.0f, 0.0f, 500.0f);
        gui.drawString(line, x + 1, y + 1, 0x888888);
        gui.drawString(line, x, y, 0xffffff);
        GL11.glTranslatef(0.0f, 0.0f, -500.0f);
    }

    private void drawItemsWithLabel(FontRenderer fontRenderer, int x, int y, NonNullList<ItemStack> itemStacks) {
        w = 0;
        for (ItemStack itemStack : itemStacks) {
            if (itemStack.isEmpty()) continue;
            this.renderItemIntoGUI(itemStack, x + w, y - 4);
            drawNumberForItem(fontRenderer, itemStack, x + w, y - 4);
            w += 18;
        }
    }


    private void renderItemIntoGUI(ItemStack stack, int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemModelMesher itemModelMesher = mc.getRenderItem().getItemModelMesher();
        GuiRenderItem guiRenderItem = new GuiRenderItem(mc.getTextureManager(), itemModelMesher, new ItemColors());
        guiRenderItem.renderItemAndEffectIntoGUI(stack, x, y);
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }
}
