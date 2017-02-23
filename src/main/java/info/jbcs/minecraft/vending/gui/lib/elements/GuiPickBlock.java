package info.jbcs.minecraft.vending.gui.lib.elements;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

import info.jbcs.minecraft.vending.gui.GuiAdvancedVendingMachine;
import info.jbcs.minecraft.vending.gui.lib.input.IPickBlockHandler;
import info.jbcs.minecraft.vending.inventory.ContainerPickBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiPickBlock extends InventoryEffectRenderer
{
    /** The location of the creative inventory tabs texture */
    private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    /** Currently selected creative inventory tab index. */
    private static int selectedTabIndex = CreativeTabs.BUILDING_BLOCKS.getTabIndex();
    /** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
    private float currentScroll;
    /** True if the scrollbar is being dragged */
    private boolean isScrolling;
    /** True if the left mouse button was held down last time drawScreen was called. */
    private boolean wasClicking;
    private GuiTextField searchField;
    private boolean clearSearch;
    private static int tabPage = 0;
    private int maxPages = 0;
    private GuiScreen parent;

    private ContainerPickBlock containterPickBlock;

    public GuiPickBlock(EntityPlayer player, ItemStack stack, GuiAdvancedVendingMachine guiAdvancedVendingMachine)
    {
        super(new ContainerPickBlock());
        this.containterPickBlock = (ContainerPickBlock) this.inventorySlots;
        this.allowUserInput = true;
        this.ySize = 136;
        this.xSize = 195;
        containterPickBlock.resultSlot.putStack(stack);
        parent = guiAdvancedVendingMachine;
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(@Nullable Slot slotIn, int slotId, int mouseButton, ClickType type)
    {

        this.clearSearch = true;
        if (this.inventorySlots != null)
        {
            ItemStack itemStack3 = slotIn == null ? ItemStack.EMPTY : this.inventorySlots.getSlot(slotIn.slotNumber).getStack();
            ItemStack itemStack4 = containterPickBlock.resultSlot.getStack().copy();
            if(itemStack3.isItemEqual(itemStack4) && itemStack4.getCount() < itemStack4.getMaxStackSize())
                itemStack4.setCount(itemStack4.getCount()+1);
            else
                itemStack4 = itemStack3;
            containterPickBlock.resultSlot.putStack(itemStack4);
        }
    }

    protected void updateActivePotionEffects()
    {
        int i = this.guiLeft;
        super.updateActivePotionEffects();

        if (this.searchField != null && this.guiLeft != i)
        {
            this.searchField.xPosition = this.guiLeft + 82;
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.searchField = new GuiTextField(0, this.fontRendererObj, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRendererObj.FONT_HEIGHT);
        this.searchField.setMaxStringLength(15);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setVisible(false);
        this.searchField.setTextColor(16777215);
        int i = selectedTabIndex;
        selectedTabIndex = -1;
        this.setCurrentCreativeTab(CreativeTabs.CREATIVE_TAB_ARRAY[i]);
        int tabCount = CreativeTabs.CREATIVE_TAB_ARRAY.length;
        if (tabCount > 12)
        {
            buttonList.add(new GuiButton(101, guiLeft,              guiTop - 50, 20, 20, "<"));
            buttonList.add(new GuiButton(102, guiLeft + xSize - 20, guiTop - 50, 20, 20, ">"));
            maxPages = ((tabCount - 12) / 10) + 1;
        }
        buttonList.add(new GuiButton(100, guiLeft + 34, guiTop + 109, 70, 20, net.minecraft.client.resources.I18n.format("gui.vendingBlock.select").trim()));
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex].hasSearchBar())
        {
            if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat))
            {
                this.setCurrentCreativeTab(CreativeTabs.SEARCH);
            }
            else
            {
                super.keyTyped(typedChar, keyCode);
            }
        }
        else
        {
            if (this.clearSearch)
            {
                this.clearSearch = false;
                this.searchField.setText("");
            }

            if (!this.checkHotbarKeys(keyCode))
            {
                if (this.searchField.textboxKeyTyped(typedChar, keyCode))
                {
                    this.updateCreativeSearch();
                }
                else
                {
                    super.keyTyped(typedChar, keyCode);
                }
            }
        }
    }

    private void updateCreativeSearch()
    {
        ContainerPickBlock guicontainercreative$containercreative = (ContainerPickBlock)this.inventorySlots;
        guicontainercreative$containercreative.itemList.clear();

        CreativeTabs tab = CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex];
        if (tab.hasSearchBar() && tab != CreativeTabs.SEARCH)
        {
            tab.displayAllRelevantItems(guicontainercreative$containercreative.itemList);
            updateFilteredItems(guicontainercreative$containercreative);
            return;
        }

        for (Item item : Item.REGISTRY)
        {
            if (item != null && item.getCreativeTab() != null)
            {
                item.getSubItems(item, (CreativeTabs)null, guicontainercreative$containercreative.itemList);
            }
        }
        updateFilteredItems(guicontainercreative$containercreative);
    }

    //split from above for custom search tabs
    private void updateFilteredItems(ContainerPickBlock guicontainercreative$containercreative)
    {
        if (CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex] == CreativeTabs.SEARCH) // FORGE: Only add enchanted books to the regular search
            for (Enchantment enchantment : Enchantment.REGISTRY)
            {
                if (enchantment != null && enchantment.type != null)
                {
                    Items.ENCHANTED_BOOK.getAll(enchantment, guicontainercreative$containercreative.itemList);
                }
            }
        Iterator<ItemStack> iterator = guicontainercreative$containercreative.itemList.iterator();
        String s1 = this.searchField.getText().toLowerCase(Locale.ROOT);

        while (iterator.hasNext())
        {
            ItemStack itemstack = (ItemStack)iterator.next();
            boolean flag = false;

            for (String s : itemstack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips))
            {
                if (TextFormatting.getTextWithoutFormattingCodes(s).toLowerCase(Locale.ROOT).contains(s1))
                {
                    flag = true;
                    break;
                }
            }

            if (!flag)
            {
                iterator.remove();
            }
        }

        this.currentScroll = 0.0F;
        guicontainercreative$containercreative.scrollTo(0.0F);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the itemList)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        CreativeTabs creativetabs = CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex];

        if (creativetabs != null && creativetabs.drawInForegroundOfTab())
        {
            GlStateManager.disableBlend();
            this.fontRendererObj.drawString(I18n.format(creativetabs.getTranslatedTabLabel(), new Object[0]), 8, 6, 4210752);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;

            for (CreativeTabs creativetabs : CreativeTabs.CREATIVE_TAB_ARRAY)
            {
                if (this.isMouseOverTab(creativetabs, i, j))
                {
                    return;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (state == 0)
        {
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;

            for (CreativeTabs creativetabs : CreativeTabs.CREATIVE_TAB_ARRAY)
            {
                if (creativetabs != null && this.isMouseOverTab(creativetabs, i, j) && creativetabs!= CreativeTabs.INVENTORY)
                {
                    this.setCurrentCreativeTab(creativetabs);
                    return;
                }
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * returns (if you are not on the inventoryTab) and (the flag isn't set) and (you have more than 1 page of itemList)
     */
    private boolean needsScrollBars()
    {
        if (CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex] == null) return false;
        return selectedTabIndex != CreativeTabs.INVENTORY.getTabIndex() && CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex].shouldHidePlayerInventory() && ((ContainerPickBlock)this.inventorySlots).canScroll();
    }

    /**
     * Sets the current creative tab, restructuring the GUI as needed.
     */
    private void setCurrentCreativeTab(CreativeTabs tab)
    {
        if (tab == null) return;
        int i = selectedTabIndex;
        selectedTabIndex = tab.getTabIndex();
        ContainerPickBlock guicontainercreative$containercreative = (ContainerPickBlock)this.inventorySlots;
        this.dragSplittingSlots.clear();
        guicontainercreative$containercreative.itemList.clear();
        tab.displayAllRelevantItems(guicontainercreative$containercreative.itemList);

        if (this.searchField != null)
        {
            if (tab.hasSearchBar())
            {
                this.searchField.setVisible(true);
                this.searchField.setCanLoseFocus(false);
                this.searchField.setFocused(true);
                this.searchField.setText("");
                this.searchField.width = tab.getSearchbarWidth();
                this.searchField.xPosition = this.guiLeft + (82 /*default left*/ + 89 /*default width*/) - this.searchField.width;
                this.updateCreativeSearch();
            }
            else
            {
                this.searchField.setVisible(false);
                this.searchField.setCanLoseFocus(true);
                this.searchField.setFocused(false);
            }
        }

        this.currentScroll = 0.0F;
        guicontainercreative$containercreative.scrollTo(0.0F);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0 && this.needsScrollBars())
        {
            int j = (((ContainerPickBlock)this.inventorySlots).itemList.size() + 9 - 1) / 9 - 5;

            if (i > 0)
            {
                i = 1;
            }

            if (i < 0)
            {
                i = -1;
            }

            this.currentScroll = (float)((double)this.currentScroll - (double)i / (double)j);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            ((ContainerPickBlock)this.inventorySlots).scrollTo(this.currentScroll);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        boolean flag = Mouse.isButtonDown(0);
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;

        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
        {
            this.isScrolling = this.needsScrollBars();
        }

        if (!flag)
        {
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling)
        {
            this.currentScroll = ((float)(mouseY - l) - 7.5F) / ((float)(j1 - l) - 15.0F);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            ((ContainerPickBlock)this.inventorySlots).scrollTo(this.currentScroll);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        int start = tabPage * 10;
        int end = Math.min(CreativeTabs.CREATIVE_TAB_ARRAY.length, ((tabPage + 1) * 10) + 2);
        if (tabPage != 0) start += 2;
        boolean rendered = false;

        for (CreativeTabs creativetabs : java.util.Arrays.copyOfRange(CreativeTabs.CREATIVE_TAB_ARRAY,start,end))
        {
            if (creativetabs == null) continue;
            if (creativetabs == CreativeTabs.INVENTORY) continue;
            if (this.renderCreativeInventoryHoveringText(creativetabs, mouseX, mouseY))
            {
                rendered = true;
                break;
            }
        }

        if (maxPages != 0)
        {
            String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
            int width = fontRendererObj.getStringWidth(page);
            GlStateManager.disableLighting();
            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            fontRendererObj.drawString(page, guiLeft + (xSize / 2) - (width / 2), guiTop - 44, -1);
            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
    }

    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        if (selectedTabIndex == CreativeTabs.SEARCH.getTabIndex())
        {
            List<String> list = stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips);
            CreativeTabs creativetabs = stack.getItem().getCreativeTab();

            if (creativetabs == null && stack.getItem() == Items.ENCHANTED_BOOK)
            {
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);

                if (map.size() == 1)
                {
                    Enchantment enchantment = (Enchantment)map.keySet().iterator().next();

                    for (CreativeTabs creativetabs1 : CreativeTabs.CREATIVE_TAB_ARRAY)
                    {
                        if (creativetabs1.hasRelevantEnchantmentType(enchantment.type))
                        {
                            creativetabs = creativetabs1;
                            break;
                        }
                    }
                }
            }

            if (creativetabs != null)
            {
                list.add(1, "" + TextFormatting.BOLD + TextFormatting.BLUE + I18n.format(creativetabs.getTranslatedTabLabel(), new Object[0]));
            }

            for (int i = 0; i < list.size(); ++i)
            {
                if (i == 0)
                {
                    list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
                }
                else
                {
                    list.set(i, TextFormatting.GRAY + (String)list.get(i));
                }
            }

            net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
            this.drawHoveringText(list, x, y);
            net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
        }
        else
        {
            super.renderToolTip(stack, x, y);
        }
    }

    /**
     * Draws the background layer of this container (behind the itemList).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        CreativeTabs creativetabs = CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex];

        int start = tabPage * 10;
        int end = Math.min(CreativeTabs.CREATIVE_TAB_ARRAY.length, ((tabPage + 1) * 10 + 2));
        if (tabPage != 0) start += 2;

        for (CreativeTabs creativetabs1 : java.util.Arrays.copyOfRange(CreativeTabs.CREATIVE_TAB_ARRAY,start,end))
        {
            this.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);

            if (creativetabs1 == null) continue;
            if (creativetabs1==CreativeTabs.INVENTORY) continue;
            if (creativetabs1.getTabIndex() != selectedTabIndex)
            {
                this.drawTab(creativetabs1);
            }
        }

        if (tabPage != 0)
        {
            if (creativetabs != CreativeTabs.SEARCH)
            {
                this.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
                drawTab(CreativeTabs.SEARCH);
            }
        }

        this.mc.getTextureManager().bindTexture(new ResourceLocation("vending:textures/gui/pickblock/tab_" + creativetabs.getBackgroundImageName()));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.searchField.drawTextBox();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.guiLeft + 175;
        int j = this.guiTop + 18;
        int k = j + 112;
        this.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);

        if (creativetabs.shouldHidePlayerInventory())
        {
            this.drawTexturedModalRect(i, j + (int)((float)(k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
        }

        if (creativetabs == null || creativetabs.getTabPage() != tabPage)
        {
            if (creativetabs != CreativeTabs.SEARCH && creativetabs != CreativeTabs.INVENTORY)
            {
                return;
            }
        }

        this.drawTab(creativetabs);
    }

    /**
     * Checks if the mouse is over the given tab. Returns true if so.
     */
    protected boolean isMouseOverTab(CreativeTabs tab, int mouseX, int mouseY)
    {
        if (tab.getTabPage() != tabPage)
        {
            if (tab != CreativeTabs.SEARCH && tab != CreativeTabs.INVENTORY)
            {
                return false;
            }
        }

        int i = tab.getTabColumn();
        int j = 28 * i;
        int k = 0;

        if (i == 5)
        {
            j = this.xSize - 28 + 2;
        }
        else if (i > 0)
        {
            j += i;
        }

        if (tab.isTabInFirstRow())
        {
            k = k - 32;
        }
        else
        {
            k = k + this.ySize;
        }

        return mouseX >= j && mouseX <= j + 28 && mouseY >= k && mouseY <= k + 32;
    }

    /**
     * Renders the creative inventory hovering text if mouse is over it. Returns true if did render or false otherwise.
     * Params: current creative tab to be checked, current mouse x position, current mouse y position.
     */
    protected boolean renderCreativeInventoryHoveringText(CreativeTabs tab, int mouseX, int mouseY)
    {
        int i = tab.getTabColumn();
        int j = 28 * i;
        int k = 0;

        if (i == 5)
        {
            j = this.xSize - 28 + 2;
        }
        else if (i > 0)
        {
            j += i;
        }

        if (tab.isTabInFirstRow())
        {
            k = k - 32;
        }
        else
        {
            k = k + this.ySize;
        }

        if (this.isPointInRegion(j + 3, k + 3, 23, 27, mouseX, mouseY))
        {
            if(tab==CreativeTabs.INVENTORY) return false;
            this.drawCreativeTabHoveringText(I18n.format(tab.getTranslatedTabLabel(), new Object[0]), mouseX, mouseY);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Draws the given tab and its background, deciding whether to highlight the tab or not based off of the selected
     * index.
     */
    protected void drawTab(CreativeTabs tab)
    {
        if(tab == CreativeTabs.INVENTORY) return;
        boolean flag = tab.getTabIndex() == selectedTabIndex;
        boolean flag1 = tab.isTabInFirstRow();
        int i = tab.getTabColumn();
        int j = i * 28;
        int k = 0;
        int l = this.guiLeft + 28 * i;
        int i1 = this.guiTop;
        int j1 = 32;

        if (flag)
        {
            k += 32;
        }

        if (i == 5)
        {
            l = this.guiLeft + this.xSize - 28;
        }
        else if (i > 0)
        {
            l += i;
        }

        if (flag1)
        {
            i1 = i1 - 28;
        }
        else
        {
            k += 64;
            i1 = i1 + (this.ySize - 4);
        }

        GlStateManager.disableLighting();
        GlStateManager.color(1F, 1F, 1F); //Forge: Reset color in case Items change it.
        GlStateManager.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
        this.drawTexturedModalRect(l, i1, j, k, 28, 32);
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        l = l + 6;
        i1 = i1 + 8 + (flag1 ? 1 : -1);
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        ItemStack itemstack = tab.getIconItemStack();
        this.itemRender.renderItemAndEffectIntoGUI(itemstack, l, i1);
        this.itemRender.renderItemOverlays(this.fontRendererObj, itemstack, l, i1);
        GlStateManager.disableLighting();
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    /**
     * Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 100:
                ItemStack stack = containterPickBlock.resultSlot.getStack();

                if (parent instanceof IPickBlockHandler) {
                    ((IPickBlockHandler) parent).blockPicked(stack);
                }

                Minecraft.getMinecraft().displayGuiScreen(parent);
                break;
        }
    }
}
