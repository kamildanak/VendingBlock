package info.jbcs.minecraft.vending.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;

import info.jbcs.minecraft.vending.*;
import net.minecraft.client.Minecraft;
//import net.minecraft.util.EnumMovingObjectType;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy{
	private Minecraft mc;

	@Override
	public void registerEventHandlers(){
		MinecraftForge.EVENT_BUS.register(new HintGui(Minecraft.getMinecraft()));
	}

	@Override
	public void registerRenderers() {
		BlockVendingMachineRenderer.id = RenderingRegistry.getNextAvailableRenderId();

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVendingMachine.class, new TileEntityVendingMachineRenderer());

		RenderingRegistry.registerBlockHandler(new BlockVendingMachineRenderer());
	}

}
