package info.jbcs.minecraft.vending.gui;

import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.gui.lib.GuiScreenPlus;
import info.jbcs.minecraft.vending.gui.lib.IGuiWrapper;
import info.jbcs.minecraft.vending.gui.lib.elements.GuiEdit;
import info.jbcs.minecraft.vending.gui.lib.elements.GuiExButton;
import info.jbcs.minecraft.vending.gui.lib.elements.GuiLabel;
import info.jbcs.minecraft.vending.network.MsgWrench;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiWrenchVendingMachine extends GuiScreenPlus implements IGuiWrapper {
    private GuiEdit ownerNameEdit;
    private GuiExButton infiniteButton;
    private TileEntityVendingMachine entity;

    private boolean infinite;

    public GuiWrenchVendingMachine(World world, BlockPos blockPos, EntityPlayer entityplayer) {
        super(166, 120, "vending:textures/wrench-gui.png");

        addChild(new GuiLabel(9, 9, "gui.vendingBlock.settings"));
        addChild(new GuiLabel(9, 29, "gui.vendingBlock.owner"));
        addChild(ownerNameEdit = new GuiEdit(16, 43, 138, 13));
        addChild(infiniteButton = new GuiExButton(9, 64, 148, 20, "") {
            @Override
            public void onClick() {
                infinite = !infinite;
                setCaption(I18n.format("gui.vendingBlock.infinite").trim() + ": " +
                        (infinite ? I18n.format("gui.vendingBlock.yes").trim() :
                                I18n.format("gui.vendingBlock.no").trim()));
            }
        });

        addChild(new GuiExButton(9, 91, 148, 20, "gui.vendingBlock.apply") {
            @Override
            public void onClick() {
                MsgWrench msg = new MsgWrench(entity, infinite, ownerNameEdit.getText());
                Vending.instance.messagePipeline.sendToServer(msg);
                mc.thePlayer.closeScreen();
            }
        });

        TileEntity tileEntity = world.getTileEntity(blockPos);
        if (!(tileEntity instanceof TileEntityVendingMachine)) {
            return;
        }

        entity = (TileEntityVendingMachine) tileEntity;
        ownerNameEdit.setText(entity.getOwnerName());
        infinite = !entity.isInfinite();
        infiniteButton.onClick();
    }
}
