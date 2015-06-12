package info.jbcs.minecraft.vending;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import static java.lang.Math.max;


public class HintGui extends Gui {
    private Minecraft mc;
    public HintGui(Minecraft mc){
        super();
        // We need this to invoke the render engine.
        this.mc = mc;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderInfo(RenderGameOverlayEvent.Post  event){
        if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            //event.setCanceled(true);
            return;
        } else {
            if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
                return;
            }
            EntityPlayer player = mc.thePlayer;
            World world = mc.theWorld;
            MovingObjectPosition mop = General.getMovingObjectPositionFromPlayer(world, player, false);

            if (mop == null) {
                return;
            }

            // if (mop.typeOfHit != EnumMovingObjectType.TILE) {
            //    return;
            //}

            TileEntity te = world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);

            if (te == null) {
                return;
            }

            if (!(te instanceof TileEntityVendingMachine)) {
                return;
            }

            TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) te;
            draw(tileEntity.ownerName, tileEntity.getSoldItem(), tileEntity.getBoughtItem());

            GeneralClient.bind("textures/gui/icons.png");
        }
    }

    RenderItem render = new RenderItem();

    void drawNumberForItem(FontRenderer fontRenderer, ItemStack stack, int ux, int uy) {
        if (stack == null || stack.stackSize < 2) {
            return;
        }

        String line = "" + stack.stackSize;
        int x = ux + 19 - 2 - fontRenderer.getStringWidth(line);
        int y = uy + 6 + 3;
        GL11.glTranslatef(0.0f, 0.0f, 50.0f);
        drawString(fontRenderer, line, x + 1, y + 1, 0x888888);
        drawString(fontRenderer, line, x, y, 0xffffff);
        GL11.glTranslatef(0.0f, 0.0f, -50.0f);
    }

    void draw(String seller, ItemStack sold, ItemStack bought) {
        boolean doubleSize = false;
        if (bought == null && sold == null) {
            return;
        }
        ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenwidth = resolution.getScaledWidth();
        FontRenderer fontRenderer = mc.fontRenderer;
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        int w = 120;
        int def_h = 60;
        int h = def_h;
        int lines_b = 0;
        int lines_s = 0;
        int len_b = 0;
        int len_s = 0;
        int firstImportantLine = 1;
        String tooltip;
        if(bought !=null)
        for(int i=firstImportantLine; i<bought.getTooltip(mc.thePlayer, false).size(); i++){
            tooltip = bought.getTooltip(mc.thePlayer, false).get(i).toString();
            if(! tooltip.isEmpty())lines_b++;
            if(tooltip.length()>len_b)len_b=fontRenderer.getStringWidth(tooltip);
        }
        if(sold !=null)
        for(int i=firstImportantLine; i<sold.getTooltip(mc.thePlayer, false).size(); i++){
            tooltip = sold.getTooltip(mc.thePlayer, false).get(i).toString();
            if(! tooltip.isEmpty())lines_s++;
            if(tooltip.length()>len_s)len_s=fontRenderer.getStringWidth(tooltip);
        }
        if(max(lines_b, lines_s)>0 && bought != null && sold != null) doubleSize = true;
        w = 42 + max(max(76, len_b), max(76, len_s));
        if(doubleSize) w = 63 + max(76, len_b) + max(76, len_s);
        h = h + max(max(lines_b-1, lines_s-1), 0) * 16;

        int centerYOff = -80;
        int cx = width / 2;
        int x = cx - w / 2;
        int y = height / 2 - def_h / 2 + centerYOff;

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, -100.0f);

        GL11.glDisable(GL11.GL_LIGHTING);

        drawGradientRect(x, y, x + w, y + h, 0xc0101010, 0xd0101010);
        drawCenteredString(fontRenderer, seller, cx, y + 8, 0xffffff);

        if (bought != null && sold != null) {
            x += 18;
            y += 26;
            drawString(fontRenderer, StatCollector.translateToLocal("gui.vendingBlock.isselling"), x, y, 0xa0a0a0);
            drawNumberForItem(fontRenderer, sold, x + 46, y - 4);
            if(!doubleSize){
                drawString(fontRenderer, StatCollector.translateToLocal("gui.vendingBlock.for"), x + 14, y + 16, 0xa0a0a0);
                drawNumberForItem(fontRenderer, bought, x + 14 + 18, y + 16 - 4);
                render.renderItemIntoGUI(fontRenderer, mc.renderEngine, bought, x + 14 + 18, y + 16 - 4);
            }else {
                drawString(fontRenderer, StatCollector.translateToLocal("gui.vendingBlock.for"), x + 14*2 + max(76, len_s), y, 0xa0a0a0);
                drawNumberForItem(fontRenderer, bought, x + 18 + 14*2 + max(76, len_s), y - 4);
                String line;
                int omitted = 0;
                for(int i=firstImportantLine; i<sold.getTooltip(mc.thePlayer, false).size(); i++){
                    line = sold.getTooltip(mc.thePlayer, false).get(i).toString();
                    if(!line.isEmpty())
                        drawString(fontRenderer, line, x, y + 16 * (i+1-firstImportantLine-omitted), 0xa0a0a0);
                    else omitted++;
                }
                omitted = 0;
                for(int i=firstImportantLine; i<bought.getTooltip(mc.thePlayer, false).size(); i++){
                    line = bought.getTooltip(mc.thePlayer, false).get(i).toString();
                    if(!line.isEmpty())
                        drawString(fontRenderer, line, x + 14*2 + max(76, len_s), y + 16 * (i+1-firstImportantLine-omitted), 0xa0a0a0);
                    else omitted++;
                }
                render.renderItemIntoGUI(fontRenderer, mc.renderEngine, bought, x + 14 * 2 + 18 + max(76, len_s), y - 4);
            }
            render.renderItemIntoGUI(fontRenderer, mc.renderEngine, sold, x + 46, y - 4);
        } else if (sold != null) {
            x += 18;
            y += 30;
            drawString(fontRenderer, StatCollector.translateToLocal("gui.vendingBlock.isgiving"), x, y, 0xa0a0a0);
            drawString(fontRenderer, StatCollector.translateToLocal("gui.vendingBlock.away"), x + 60, y, 0xa0a0a0);
            drawNumberForItem(fontRenderer, sold, x + 42, y - 4);
            if(lines_s>0){
                String line;
                int omitted = 0;
                for(int i=firstImportantLine; i<sold.getTooltip(mc.thePlayer, false).size(); i++) {
                    line = sold.getTooltip(mc.thePlayer, false).get(i).toString();
                    if(!line.isEmpty())
                        drawString(fontRenderer, line, x, y + 16 * (i+1-firstImportantLine-omitted), 0xa0a0a0);
                    else omitted++;
                }
            }
            render.renderItemIntoGUI(fontRenderer, mc.renderEngine, sold, x + 42, y - 4);
        } else if (bought != null) {
            x += 22;
            y += 30;
            drawString(fontRenderer, StatCollector.translateToLocal("gui.vendingBlock.isaccepting"), x, y, 0xa0a0a0);
            drawNumberForItem(fontRenderer, bought, x + 62, y - 4);
            if(lines_b>0){
                String line;
                int omited = 0;
                for(int i=firstImportantLine; i<bought.getTooltip(mc.thePlayer, false).size(); i++) {
                    line = bought.getTooltip(mc.thePlayer, false).get(i).toString();
                    if(!line.isEmpty())
                        drawString(fontRenderer, line, x, y + 16 * (i+1-firstImportantLine-omited), 0xa0a0a0);
                    else omited++;
                }
            }
            render.renderItemIntoGUI(fontRenderer, mc.renderEngine, bought, x + 62, y - 4);
        }
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
