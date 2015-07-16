package info.jbcs.minecraft.vending.network;

import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MsgWrench extends Message {
    private int				x, y, z;
    private boolean             infinite;
    private String              ownerName;

    public MsgWrench() { }

    public MsgWrench(TileEntity tileEntityVendingMachine, boolean infinite, String ownerName)
    {
        TileEntityVendingMachine entity = (TileEntityVendingMachine) tileEntityVendingMachine;
        BlockPos blockPos = entity.getPos();
        x = blockPos.getX();
        y = blockPos.getY();
        z = blockPos.getZ();
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
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (player.inventory.getCurrentItem() == null || player.inventory.getCurrentItem().getItem() != Vending.itemWrench)
                return null;
            TileEntity tileEntity = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
            if (!(tileEntity instanceof TileEntityVendingMachine))
                return null;
            TileEntityVendingMachine entity = (TileEntityVendingMachine) tileEntity;
            entity.infinite = message.infinite;
            entity.setOwnerName(message.ownerName);
            player.worldObj.markBlockForUpdate(new BlockPos(message.x, message.y, message.z));
            return null;
        }
    }
}
