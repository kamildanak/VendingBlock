package info.jbcs.minecraft.vending.network.server;

import info.jbcs.minecraft.vending.init.VendingBlocks;
import info.jbcs.minecraft.vending.init.VendingItems;
import info.jbcs.minecraft.vending.network.AbstractMessage;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

public class MessageWrenchToInventory extends AbstractMessage.AbstractServerMessage<MessageWrenchToInventory> {
    private int x, y, z;
    private boolean infinite;
    private String ownerName;

    @SuppressWarnings("unused")
    public MessageWrenchToInventory() {
    }

    public MessageWrenchToInventory(TileEntity tileEntityVendingMachine, boolean infinite, String ownerName) {
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
        if (player.inventory.getCurrentItem().isEmpty() || player.inventory.getCurrentItem().getItem() != VendingItems.ITEM_WRENCH)
            return;
        TileEntity tileEntity = player.world.getTileEntity(new BlockPos(x, y, z));
        if (!(tileEntity instanceof TileEntityVendingMachine))
            return;
        TileEntityVendingMachine entity = (TileEntityVendingMachine) tileEntity;
        ItemStack itemStack;
        if (entity.isMultiple())
            itemStack = new ItemStack(VendingBlocks.BLOCK_VENDING_MACHINE_MULTIPLE, 1, tileEntity.getBlockMetadata());
        else if (entity.isAdvanced())
            itemStack = new ItemStack(VendingBlocks.BLOCK_VENDING_MACHINE_ADVANCED, 1, tileEntity.getBlockMetadata());
        else itemStack = new ItemStack(VendingBlocks.BLOCK_VENDING_MACHINE, 1, tileEntity.getBlockMetadata());
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString("ownerName", ownerName);
        tagCompound.setBoolean("infinite", infinite);
        tagCompound.setBoolean("preConfigured", true);
        itemStack.setTagCompound(tagCompound);
        player.inventory.addItemStackToInventory(itemStack);
    }
}
