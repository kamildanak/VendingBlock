package info.jbcs.minecraft.vending.gui;

import com.kamildanak.minecraft.foamflower.gui.GuiHandler;
import info.jbcs.minecraft.vending.inventory.ContainerVendingStorageAttachment;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingStorageAttachment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HandlerStorage extends GuiHandler {
    public HandlerStorage(String name) {
        super(name);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if (!(tileEntity instanceof TileEntityVendingStorageAttachment))
            return null;
        TileEntityVendingStorageAttachment e = (TileEntityVendingStorageAttachment) tileEntity;

        return new ContainerVendingStorageAttachment(player.inventory, e, e.haveAccess(player));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if (!(tileEntity instanceof TileEntityVendingStorageAttachment))
            return null;

        TileEntityVendingStorageAttachment e = (TileEntityVendingStorageAttachment) tileEntity;

        return new GuiVendingStorageAttachment(player, e);
    }
}
