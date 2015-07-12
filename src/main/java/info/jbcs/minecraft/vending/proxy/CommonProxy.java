package info.jbcs.minecraft.vending.proxy;

import cpw.mods.fml.relauncher.Side;
import info.jbcs.minecraft.vending.CommonEventHandler;
import info.jbcs.minecraft.vending.network.MessagePipeline;
import info.jbcs.minecraft.vending.network.MsgAdvVenSetItem;
import info.jbcs.minecraft.vending.network.MsgWrench;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
	public void registerEventHandlers()
	{
	}

	public void registerPackets(MessagePipeline pipeline)
	{
		pipeline.registerMessage(MsgAdvVenSetItem.Handler.class, MsgAdvVenSetItem.class, 0, Side.SERVER);
		pipeline.registerMessage(MsgWrench.Handler.class, MsgWrench.class, 1, Side.SERVER);
	}

	public void registerRenderers()
	{
	}
}
