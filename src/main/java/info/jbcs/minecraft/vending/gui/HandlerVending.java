package info.jbcs.minecraft.vending.gui;

import com.kamildanak.minecraft.foamflower.gui.GuiHandler;
import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerMultipleVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HandlerVending extends GuiHandler {
    public HandlerVending(String name) {
        super(name);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if (!(tileEntity instanceof TileEntityVendingMachine))
            return null;

        TileEntityVendingMachine e = (TileEntityVendingMachine) tileEntity;

        if (e.isAdvanced())
            return new ContainerAdvancedVendingMachine(player.inventory, e);

        if (e.isMultiple())
            return new ContainerMultipleVendingMachine(player.inventory, e);

        return new ContainerVendingMachine(player.inventory, e);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if (!(tileEntity instanceof TileEntityVendingMachine))
            return null;

        TileEntityVendingMachine e = (TileEntityVendingMachine) tileEntity;

        if (e.isAdvanced())
            return new GuiAdvancedVendingMachine(player.inventory, e);

        if (e.isMultiple())
            return new GuiMultipleVendingMachine(player.inventory, e);

        return new GuiVendingMachine(player.inventory, e);
    }
}
