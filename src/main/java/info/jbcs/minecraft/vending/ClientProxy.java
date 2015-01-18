package info.jbcs.minecraft.vending;

import cpw.mods.fml.client.registry.ClientRegistry;

import java.util.EnumSet;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy{
	private Minecraft mc;

	@Override
	public void preInit() {
	}

	@Override
	public void init() {
		Vending.Channel.register(new ClientPacketHandler());
		BlockVendingMachineRenderer.id = RenderingRegistry.getNextAvailableRenderId();

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVendingMachine.class, new TileEntityVendingMachineRenderer());

		RenderingRegistry.registerBlockHandler(new BlockVendingMachineRenderer());

		MinecraftForge.EVENT_BUS.register(new HintGui(Minecraft.getMinecraft()));
	}

}
