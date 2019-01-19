package info.jbcs.minecraft.vending.gui;

import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.network.MsgWrench;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class GuiWrenchVendingMachine extends GuiScreenPlus {
	GuiEdit ownerNameEdit;
	GuiExButton infiniteButton;
	TileEntityVendingMachine entity;

	boolean infinite;

	public GuiWrenchVendingMachine(World world, BlockPos blockPos, EntityPlayer entityplayer) {
		super(166, 120, "vending:textures/wrench-gui.png");
		
		addChild(new GuiLabel(9, 9, I18n.translateToLocal("gui.vendingBlock.settings")));
		addChild(new GuiLabel(9, 29, I18n.translateToLocal("gui.vendingBlock.owner")));
		addChild(ownerNameEdit = new GuiEdit(16, 43, 138, 13));
		addChild(infiniteButton = new GuiExButton(9, 64, 148, 20, "") {
			@Override
			public void onClick() {
				infinite = !infinite;
				caption = I18n.translateToLocal("gui.vendingBlock.infinite") + ": " + (infinite ? I18n.translateToLocal("gui.vendingBlock.yes") : I18n.translateToLocal("gui.vendingBlock.no"));
			}
		});

		addChild(new GuiExButton(9, 91, 148, 20, I18n.translateToLocal("gui.vendingBlock.apply")) {
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
		infinite = !entity.infinite;
		infiniteButton.onClick();
	}
}
