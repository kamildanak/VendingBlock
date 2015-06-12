package info.jbcs.minecraft.vending;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class GuiWrenchVendingMachine extends GuiScreenPlus {
	GuiEdit ownerNameEdit;
	GuiExButton infiniteButton;
	TileEntityVendingMachine entity;

	boolean infinite;

	public GuiWrenchVendingMachine(World world, int x, int y, int z, EntityPlayer entityplayer) {
		super(166, 120, "vending:textures/wrench-gui.png");
		
		addChild(new GuiLabel(9, 9, StatCollector.translateToLocal("gui.vendingBlock.settings")));
		addChild(new GuiLabel(9, 29, StatCollector.translateToLocal("gui.vendingBlock.owner")));
		addChild(ownerNameEdit = new GuiEdit(16, 43, 138, 13));
		addChild(infiniteButton = new GuiExButton(9, 64, 148, 20, "") {
			@Override
			public void onClick() {
				infinite = !infinite;
				caption = StatCollector.translateToLocal("gui.vendingBlock.infinite") + ": " + (infinite ? StatCollector.translateToLocal("gui.vendingBlock.yes") : StatCollector.translateToLocal("gui.vendingBlock.no"));
			}
		});

		addChild(new GuiExButton(9, 91, 148, 20, StatCollector.translateToLocal("gui.vendingBlock.apply")) {
			@Override
			public void onClick() {
				int type = 2;
				ByteBuf buffer = Unpooled.buffer();
				buffer.writeInt(type);
				buffer.writeInt(entity.xCoord);
				buffer.writeInt(entity.yCoord);
				buffer.writeInt(entity.zCoord);
				buffer.writeBoolean(infinite);
				ByteBufUtils.writeUTF8String(buffer, ownerNameEdit.getText());
				FMLProxyPacket packet = new FMLProxyPacket(buffer.copy(), "Vending");

				Vending.Channel.sendToServer(packet);
				mc.thePlayer.closeScreen();
			}
		});
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityVendingMachine)) {
			return;
		}

		entity = (TileEntityVendingMachine) tileEntity;
		ownerNameEdit.setText(entity.ownerName);
		infinite = !entity.infinite;
		infiniteButton.onClick();
	}
}
