package info.jbcs.minecraft.vending.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class Message implements IMessage
{
    public abstract void fromBytes(ByteBuf buffer);

    public abstract void toBytes(ByteBuf buffer);
}
