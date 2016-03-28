package info.jbcs.minecraft.vending.network;

import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerTileEntity;
import info.jbcs.minecraft.vending.inventory.ContainerVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MsgSetLock extends Message
{
    private int x,y,z;
    private boolean locked;

    public MsgSetLock(){}
    public MsgSetLock(BlockPos pos, Boolean locked) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.locked = locked;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        locked = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(locked);
    }

    public static class Handler implements IMessageHandler<MsgSetLock, IMessage> {

        @Override
        public IMessage onMessage(MsgSetLock message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            TileEntity tileEntity = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
            if(!(tileEntity instanceof TileEntityVendingMachine)) return null;
            ((TileEntityVendingMachine) tileEntity).setOpen(!message.locked);
            ((TileEntityVendingMachine) tileEntity).markBlockForUpdate(new BlockPos(message.x, message.y, message.z));
            return null;
        }
    }

}
