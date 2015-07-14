package info.jbcs.minecraft.vending.network;


import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePipeline extends SimpleNetworkWrapper{
    public MessagePipeline(){
        super("Vending");
    };
}
