package info.jbcs.minecraft.vending.gui;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.foamflower.gui.GuiScreenPlus;
import com.kamildanak.minecraft.foamflower.gui.elements.GuiEditBigInteger;
import com.kamildanak.minecraft.foamflower.gui.elements.GuiExButton;
import com.kamildanak.minecraft.foamflower.gui.elements.GuiLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.math.BigInteger;

public class GuiPickBanknoteValue extends GuiScreenPlus {
    private GuiEditBigInteger editBigInteger;

    public GuiPickBanknoteValue(EntityPlayer player, GuiAdvancedVendingMachine guiAdvancedVendingMachine)
    {
        super(146, 65,"vending:textures/banknote-gui.png");
        addChild(new GuiLabel(9, 9, "gui.vendingBlock.number_of_credits"));
        addChild(editBigInteger = new GuiEditBigInteger(9, 21, 127, 10,
                BigInteger.ZERO, BigInteger.valueOf(Long.MAX_VALUE)));
        addChild(new GuiExButton(9, 36, 60, 20, "gui.vendingBlock.ok")
        {
            @Override
            public void onClick() {
                guiAdvancedVendingMachine.blockPicked(EnderPayApi.getBanknote(editBigInteger.getValue().longValue()));
                Minecraft.getMinecraft().displayGuiScreen(guiAdvancedVendingMachine);
            }
        });
        addChild(new GuiExButton(76, 36, 60, 20, "gui.vendingBlock.cancel"){
            @Override
            public void onClick() {
                Minecraft.getMinecraft().displayGuiScreen(guiAdvancedVendingMachine);
            }
        });
    }
}
