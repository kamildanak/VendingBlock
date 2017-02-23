package info.jbcs.minecraft.vending.network.server;

import info.jbcs.minecraft.vending.network.AbstractMessage;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

public class MessageSetLock extends AbstractMessage.AbstractServerMessage<MessageSetLock> {
    private int x, y, z;
    private boolean locked;

    @SuppressWarnings("unused")
    public MessageSetLock() {
    }

    public MessageSetLock(BlockPos pos, Boolean locked) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.locked = locked;
    }

    @Override
    protected void read(PacketBuffer buffer) throws IOException {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        locked = buffer.readBoolean();
    }

    @Override
    protected void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeBoolean(locked);
    }

    @Override
    public void process(EntityPlayer player, Side side) {
        TileEntity tileEntity = player.worldObj.getTileEntity(new BlockPos(x, y, z));
        if (!(tileEntity instanceof TileEntityVendingMachine)) return;
        ((TileEntityVendingMachine) tileEntity).setOpen(!locked);
        ((TileEntityVendingMachine) tileEntity).markBlockForUpdate(new BlockPos(x, y, z));
    }
}
