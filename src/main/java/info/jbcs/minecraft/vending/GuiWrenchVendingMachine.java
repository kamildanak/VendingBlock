package info.jbcs.minecraft.vending;

import info.jbcs.minecraft.gui.GuiEdit;
import info.jbcs.minecraft.gui.GuiExButton;
import info.jbcs.minecraft.gui.GuiLabel;
import info.jbcs.minecraft.gui.GuiScreenPlus;
import info.jbcs.minecraft.utilities.packets.PacketData;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiWrenchVendingMachine extends GuiScreenPlus {
	GuiEdit ownerNameEdit;
	GuiExButton infiniteButton;
	TileEntityVendingMachine entity;

	boolean infinite;

	public GuiWrenchVendingMachine(World world, int x, int y, int z, EntityPlayer entityplayer) {
		super(166, 120, "vending:textures/wrench-gui.png");
		
		addChild(new GuiLabel(9, 9, "Vending block settings"));
		addChild(new GuiLabel(9, 29, "Owner name:"));
		
		addChild(ownerNameEdit = new GuiEdit(16, 43, 138, 13));
		
		addChild(infiniteButton = new GuiExButton(9, 64, 148, 20, "") {
			@Override
			public void onClick() {
				infinite = !infinite;
				caption = "Infinite: " + (infinite ? "YES" : "NO");
			}
		});
		
		addChild(new GuiExButton(9, 91, 148, 20, "Apply") {
			@Override
			public void onClick() {
				Packets.wrench.sendToServer(new PacketData() {
					@Override
					public void data(DataOutputStream stream) throws IOException {
						stream.writeInt(entity.xCoord);
						stream.writeInt(entity.yCoord);
						stream.writeInt(entity.zCoord);
						stream.writeBoolean(infinite);
						Packet.writeString(ownerNameEdit.getText(), stream);
					}
				});
				mc.thePlayer.closeScreen();
			}
		});
		
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityVendingMachine)) {
			return;
		}

		entity = (TileEntityVendingMachine) tileEntity;
		ownerNameEdit.setText(entity.ownerName);
		infinite = !entity.infinite;
		infiniteButton.onClick();
	}
}
