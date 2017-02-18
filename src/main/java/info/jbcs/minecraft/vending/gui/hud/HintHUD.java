package info.jbcs.minecraft.vending.gui.hud;

import com.kamildanak.minecraft.enderpay.EnderPay;
import com.kamildanak.minecraft.enderpay.item.ItemFilledBanknote;
import info.jbcs.minecraft.vending.General;
import info.jbcs.minecraft.vending.Utils;
import info.jbcs.minecraft.vending.gui.lib.elements.GuiElement;
import info.jbcs.minecraft.vending.gui.lib.elements.GuiItemsList;
import info.jbcs.minecraft.vending.gui.lib.elements.GuiLabel;
import info.jbcs.minecraft.vending.gui.lib.elements.GuiLabelCarousel;
import info.jbcs.minecraft.vending.gui.lib.layouts.CenteredLayout;
import info.jbcs.minecraft.vending.gui.lib.layouts.LinearLayout;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Vector;

import static info.jbcs.minecraft.vending.General.countNotNull;


public class HintHUD extends HUD {
    private Minecraft mc;
    private GuiElement root;
    private LinearLayout layout;
    private LinearLayout boughtAndSold;
    private LinearLayout bought;
    private LinearLayout sold;
    private LinearLayout boughtItems;
    private LinearLayout soldItems;
    private GuiLabel labelBoughtCredits;
    private GuiLabel labelSoldCredits;
    private GuiLabel labelSeller;
    private GuiLabel labelClosed;
    private GuiLabelCarousel labelBoughtDesc;
    private GuiLabelCarousel labelSoldDesc;
    private GuiItemsList soldItemList;
    private GuiItemsList boughtItemList;

    public HintHUD(Minecraft mc) {
        super(mc);
        this.mc = mc;
        ScaledResolution resolution = new ScaledResolution(mc);
        root = new CenteredLayout(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), true, false);
        root.gui = this;
        root.addChild(layout = new LinearLayout(0, 0, false));
        layout.addChild(labelSeller = new GuiLabel(0, 0, "<seller>", 0xffffff));
        layout.addChild(labelClosed = new GuiLabel(0, 0, "gui.vendingBlock.closed", 0xa0a0a0));

        layout.addChild(boughtAndSold = new LinearLayout(0, 0, false));
        boughtAndSold.addChild(sold = new LinearLayout(0, 0, false));
        boughtAndSold.addChild(bought = new LinearLayout(0, 0, false));

        sold.addChild(soldItems = new LinearLayout(0, 0, true));
        sold.addChild(labelSoldCredits = new GuiLabel(0, 0, "<sCredits>", 0xffffff));
        soldItems.addChild(new GuiLabel(0, 0, "gui.vendingBlock.isSelling", 0xffffff));
        soldItems.addChild(soldItemList = new GuiItemsList(0, 0, 0, 0));
        sold.addChild(labelSoldDesc = new GuiLabelCarousel(0, 0, "", 0xffffff));

        bought.addChild(boughtItems = new LinearLayout(0, 0, true));
        bought.addChild(labelBoughtCredits = new GuiLabel(0, 0, "<bCredits>", 0xffffff));
        boughtItems.addChild(new GuiLabel(0, 0, "gui.vendingBlock.for", 0xffffff));
        boughtItems.addChild(boughtItemList = new GuiItemsList(0, 0, 0, 0));
        bought.addChild(labelBoughtDesc = new GuiLabelCarousel(0, 0, "", 0xffffff));
    }

    @Override
    public GuiElement getRoot() {
        return root;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    @SuppressWarnings("unused")
    public void onRenderInfo(RenderGameOverlayEvent.Post event) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

        if (mc == null || mc.thePlayer == null || mc.theWorld == null) return;
        RayTraceResult mop = General.getMovingObjectPositionFromPlayer(mc.theWorld, mc.thePlayer, false);
        if (mop == null) return;
        TileEntity te = mc.theWorld.getTileEntity(mop.getBlockPos());
        if (te == null) return;
        if (!(te instanceof TileEntityVendingMachine)) return;

        boughtAndSold.setHorizontal(mc.thePlayer.isSneaking());
        drawGradientRect(layout.x - 6, layout.y - 5,
                layout.x + layout.getWidth() + 6, layout.y + layout.getHeight() + 5 - 2,
                0xc0101010, 0xd0101010);

        TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) te;

        root.y = 15;
        labelSeller.setCaption(tileEntity.getOwnerName());
        labelSeller.center = true;

        labelClosed.hidden = tileEntity.isOpen();
        boughtAndSold.hidden = !tileEntity.isOpen();

        labelSeller.hidden = false;
        labelSeller.center = false;
        //labelClosed.hidden = true;
        //boughtAndSold.hidden = true;

        ItemStack[] soldItemStacks;
        ItemStack[] boughtItemStacks;
        soldItemStacks = tileEntity.getSoldItems().clone();
        boughtItemStacks = tileEntity.getBoughtItems().clone();
        if (Loader.isModLoaded("enderpay")) {
            for (int i = 0; i < soldItemStacks.length; i++) {
                if (isBanknote(soldItemStacks[i])) soldItemStacks[i] = null;
            }
            for (int i = 0; i < boughtItemStacks.length; i++) {
                if (isBanknote(boughtItemStacks[i])) boughtItemStacks[i] = null;
            }

            String label = countNotNull(soldItemStacks) == 0 ? "gui.vendingBlock.isSelling" : "gui.vendingBlock.and";
            long amount = tileEntity.soldCreditsSum();
            String amountStr = I18n.format(label).trim() + " " + Utils.format(amount) + getCurrencyName(amount);
            labelSoldCredits.setCaption(amountStr);
            labelSoldCredits.hidden = amount == 0;

            label = countNotNull(boughtItemStacks) == 0 ? "gui.vendingBlock.for" : "gui.vendingBlock.and";
            amount = tileEntity.boughtCreditsSum();
            amountStr = I18n.format(label).trim() + " " + Utils.format(amount) + getCurrencyName(amount);
            labelBoughtCredits.setCaption(amountStr);
            labelBoughtCredits.hidden = amount == 0;

        }

        soldItems.hidden = countNotNull(soldItemStacks) == 0;
        boughtItems.hidden = countNotNull(boughtItemStacks) == 0;
        soldItemList.setItems(soldItemStacks);
        boughtItemList.setItems(boughtItemStacks);
        String tooltip;
        labelSoldDesc.hidden = !mc.thePlayer.isSneaking();
        labelBoughtDesc.hidden = !mc.thePlayer.isSneaking();
        if (mc.thePlayer.isSneaking()) {
            labelBoughtDesc.setCaption(getTooltips(boughtItemStacks));
            labelSoldDesc.setCaption(getTooltips(soldItemStacks));
        }


        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.render();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Utils.bind("textures/gui/icons.png");
    }

    private String[] getTooltips(ItemStack[] itemStacks) {
        Vector<String> tooltips = new Vector<>();
        for (ItemStack stack : itemStacks) {
            if (stack == null) continue;
            String tooltip = "";
            for (int i = 0; i < stack.getTooltip(mc.thePlayer, false).size(); i++) {
                if (i != 0) tooltip += "\n";
                tooltip += stack.getTooltip(mc.thePlayer, false).get(i);
            }
            tooltips.add(tooltip);
        }
        return tooltips.toArray(new String[tooltips.size()]);
    }

    private String getCurrencyName(long amount) {
        if (amount == 1) return EnderPay.currencyNameSingular;
        return EnderPay.currencyNameMultiple;
    }

    @Optional.Method(modid = "enderpay")
    public boolean isBanknote(ItemStack itemStack) {
        if (itemStack == null) return false;
        return itemStack.getItem() instanceof ItemFilledBanknote;
    }
}
