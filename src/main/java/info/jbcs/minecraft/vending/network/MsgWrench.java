package info.jbcs.minecraft.vending.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import info.jbcs.minecraft.vending.TileEntityVendingMachine;
import info.jbcs.minecraft.vending.Vending;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.swing.text.html.parser.Entity;

public class MsgWrench extends Message {
    private int				x, y, z;
    private boolean             infinite;
    private String              ownerName;

    public MsgWrench() { }

    @SuppressWarnings("unchecked")
    public MsgWrench(TileEntity tileEntityVendingMachine, boolean infinite, String ownerName)
    {
        TileEntityVendingMachine entity = (TileEntityVendingMachine) tileEntityVendingMachine;
        x = entity.xCoord;
        y = entity.yCoord;
        z = entity.zCoord;
        this.infinite = infinite;
        this.ownerName = ownerName;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        infinite = buf.readBoolean();
        ownerName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(infinite);
        ByteBufUtils.writeUTF8String(buf, ownerName);
    }

    public static class Handler implements IMessageHandler<MsgWrench, IMessage> {

        @Override
        public IMessage onMessage(MsgWrench message, MessageContext ctx) {
            System.out.println("MESSAGE GOT");
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (player.inventory.getCurrentItem() == null || player.inventory.getCurrentItem().getItem() != Vending.itemWrench)
                return null;
            System.out.println("Processing");
            TileEntity tileEntity = player.worldObj.getTileEntity(message.x, message.y, message.z);
            if (!(tileEntity instanceof TileEntityVendingMachine))
                return null;
            TileEntityVendingMachine entity = (TileEntityVendingMachine) tileEntity;
            entity.infinite = message.infinite;
            entity.ownerName = message.ownerName;
            player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
            System.out.println("Processed");
            return null;
        }
    }
}
