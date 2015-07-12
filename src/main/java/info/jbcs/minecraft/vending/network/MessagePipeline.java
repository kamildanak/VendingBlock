package info.jbcs.minecraft.vending.network;


import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class MessagePipeline extends SimpleNetworkWrapper{
    public MessagePipeline(){
        super("Vending");
    };
}
