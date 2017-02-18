package info.jbcs.minecraft.vending.gui.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;

public abstract class GuiHandler implements Comparable<GuiHandler> {
    static ArrayList<GuiHandler> guiHandlers = new ArrayList<>();

    int index;
    Object mod;
    String name;

    public GuiHandler(String name) {
        guiHandlers.add(this);
        this.name = name;
    }

    public static void register(Object mod) {
        Collections.sort(guiHandlers);
        int index = 0;

        for (GuiHandler guiHandler : guiHandlers) {
            guiHandler.mod = mod;
            guiHandler.index = index++;
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(mod, new IGuiHandler() {
            @Override
            public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                if (ID < 0 || ID >= guiHandlers.size()) {
                    return null;
                }
                return guiHandlers.get(ID).getServerGuiElement(ID, player, world, x, y, z);
            }

            @Override
            public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                if (ID < 0 || ID >= guiHandlers.size()) {
                    return null;
                }
                return guiHandlers.get(ID).getClientGuiElement(ID, player, world, x, y, z);
            }
        });
    }

    public void open(EntityPlayer entityPlayer, World world, BlockPos blockPos) {
        entityPlayer.openGui(mod, index, world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public int compareTo(@Nonnull GuiHandler guiHandler) {
        return name.compareTo(guiHandler.name);
    }

    public abstract Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z);

    public abstract Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z);
}
