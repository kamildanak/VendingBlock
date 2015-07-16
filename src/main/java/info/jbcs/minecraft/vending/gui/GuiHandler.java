package info.jbcs.minecraft.vending.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;
import java.util.Collections;

public abstract class GuiHandler implements Comparable {
	static ArrayList<GuiHandler> items = new ArrayList<GuiHandler>();

	int index;
	Object mod;
	String name;

	public GuiHandler(String n) {
		items.add(this);
		name = n;
	}

	public void open(EntityPlayer player, World world, BlockPos blockPos) {
		player.openGui(mod, index, world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	@Override
	public int compareTo(Object a) {
		return name.compareTo(((GuiHandler) a).name);
	}

	public static void register(Object mod) {
		Collections.sort(items);
		int index = 0;

		for (GuiHandler h : items) {
			h.mod = mod;
			h.index = index++;
		}

		NetworkRegistry.INSTANCE.registerGuiHandler(mod, new IGuiHandler() {
			@Override
			public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
				if (id < 0 || id >= items.size()) {
					return null;
				}

				return items.get(id).getServerGuiElement(id, player, world, x, y, z);
			}

			@Override
			public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
				if (id < 0 || id >= items.size()) {
					return null;
				}

				return items.get(id).getClientGuiElement(id, player, world, x, y, z);
			}
		});
	}

	public abstract Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z);

	public abstract Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z);
}
