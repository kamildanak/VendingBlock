package info.jbcs.minecraft.vending.gui;

import com.kamildanak.minecraft.foamflower.gui.GuiHandler;
import com.kamildanak.minecraft.foamflower.inventory.DummyContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HandlerWrench extends GuiHandler {
    public HandlerWrench(String name) {
        super(name);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new DummyContainer();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        return new GuiWrenchVendingMachine(world, new BlockPos(x, y, z), player);
    }
}
