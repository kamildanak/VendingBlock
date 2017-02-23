package info.jbcs.minecraft.vending.network.server;

import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.network.AbstractMessage;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

public class MessageWrench extends AbstractMessage.AbstractServerMessage<MessageWrench> {
    private int x, y, z;
    private boolean infinite;
    private String ownerName;

    @SuppressWarnings("unused")
    public MessageWrench() {
    }

    public MessageWrench(TileEntity tileEntityVendingMachine, boolean infinite, String ownerName) {
        TileEntityVendingMachine entity = (TileEntityVendingMachine) tileEntityVendingMachine;
        BlockPos blockPos = entity.getPos();
        x = blockPos.getX();
        y = blockPos.getY();
        z = blockPos.getZ();
        this.infinite = infinite;
        this.ownerName = ownerName;
    }

    @Override
    protected void read(PacketBuffer buffer) throws IOException {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        infinite = buffer.readBoolean();
        ownerName = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    protected void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeBoolean(infinite);
        ByteBufUtils.writeUTF8String(buffer, ownerName);
    }

    @Override
    public void process(EntityPlayer player, Side side) {
        if (player.inventory.getCurrentItem() == null || player.inventory.getCurrentItem().getItem() != Vending.itemWrench)
            return;
        TileEntity tileEntity = player.worldObj.getTileEntity(new BlockPos(x, y, z));
        if (!(tileEntity instanceof TileEntityVendingMachine))
            return;
        TileEntityVendingMachine entity = (TileEntityVendingMachine) tileEntity;
        entity.setInfinite(infinite);
        entity.setOwnerName(ownerName);
        entity.markBlockForUpdate(new BlockPos(x, y, z));
    }
}
