package info.jbcs.minecraft.vending;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ReadOnlyByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraft.tileentity.TileEntity;

import java.io.IOException;

public class ServerPacketHandler {

	@SubscribeEvent
	public void onServerPacket(ServerCustomPacketEvent event) {
		EntityPlayerMP player = ((NetHandlerPlayServer)event.handler).playerEntity;
		ByteBuf bbis = new ReadOnlyByteBuf(event.packet.payload());
		int type = bbis.readInt();

		//"Advanced Vending Block set vended item"
		if(type == 1){
			Container con = player.openContainer;
			if (con == null || !(con instanceof ContainerAdvancedVendingMachine))
				return;
			ContainerAdvancedVendingMachine container = (ContainerAdvancedVendingMachine) con;

			int id, count = 0, damage = 0;

			if ((id = bbis.readInt()) != 0) {
				count = bbis.readInt();
				damage = bbis.readInt();
			}

			container.entity.setBoughtItem(id == 0 ? null : new ItemStack(Item.getItemById(id), count, damage));
		}
		//Vending Block wrench edit
		if(type == 2) {
			if (player.inventory.getCurrentItem() == null || player.inventory.getCurrentItem().getItem() != Vending.itemWrench)
				return;

			int x = bbis.readInt();
			int y = bbis.readInt();
			int z = bbis.readInt();
			boolean infinite = bbis.readBoolean();
			String username = ByteBufUtils.readUTF8String(bbis);

			TileEntity tileEntity = player.worldObj.getTileEntity(x, y, z);

			if (!(tileEntity instanceof TileEntityVendingMachine))
				return;

			TileEntityVendingMachine entity = (TileEntityVendingMachine) tileEntity;

			entity.infinite = infinite;
			entity.ownerName = username;

			player.worldObj.markBlockForUpdate(x, y, z);
		}
	}
}
