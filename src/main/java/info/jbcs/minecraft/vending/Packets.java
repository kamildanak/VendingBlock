package info.jbcs.minecraft.vending;

import info.jbcs.minecraft.utilities.packets.PacketHandler;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;

public class Packets {

	static PacketHandler advancedMachine = new PacketHandler("Advanced Vending Block set vended item") {
		@Override
		public void onData(DataInputStream stream, EntityPlayer player) throws IOException {
			Container con = player.openContainer;
			if (con == null || !(con instanceof ContainerAdvancedVendingMachine))
				return;
			ContainerAdvancedVendingMachine container = (ContainerAdvancedVendingMachine) con;

			int id, count = 0, damage = 0;

			if ((id = stream.readInt()) != 0) {
				count = stream.readInt();
				damage = stream.readInt();
			}

			container.entity.setBoughtItem(id == 0 ? null : new ItemStack(id, count, damage));
		}
	};

	static PacketHandler wrench = new PacketHandler("Vending Block wrench edit") {
		@Override
		public void onData(DataInputStream stream, EntityPlayer player) throws IOException {
			if (player.inventory.getCurrentItem() == null || player.inventory.getCurrentItem().itemID != Vending.itemWrench.itemID)
				return;

			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();

			TileEntity tileEntity = player.worldObj.getBlockTileEntity(x, y, z);

			if (!(tileEntity instanceof TileEntityVendingMachine))
				return;

			TileEntityVendingMachine entity = (TileEntityVendingMachine) tileEntity;

			entity.infinite = stream.readBoolean();
			entity.ownerName = Packet.readString(stream, 32);

			player.worldObj.markBlockForUpdate(x, y, z);
		}
	};

}
