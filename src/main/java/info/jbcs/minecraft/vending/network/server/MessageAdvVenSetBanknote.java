package info.jbcs.minecraft.vending.network.server;

import info.jbcs.minecraft.vending.EnderPayApiUtils;
import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.network.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class MessageAdvVenSetBanknote extends AbstractMessage.AbstractServerMessage<MessageAdvVenSetBanknote> {
    private long value;

    @SuppressWarnings("unused")
    public MessageAdvVenSetBanknote(){
    }

    public MessageAdvVenSetBanknote(long value) {
        this.value = value;
    }

    @Override
    protected void read(PacketBuffer buffer) {
        this.value = buffer.readLong();
    }

    @Override
    protected void write(PacketBuffer buffer) {
        buffer.writeLong(this.value);
    }

    @Override
    public void process(EntityPlayer player, Side side) {
        Container con = player.openContainer;
        if (con == null || !(con instanceof ContainerAdvancedVendingMachine))
            return;
        ContainerAdvancedVendingMachine container = (ContainerAdvancedVendingMachine) con;

        container.entity.getInventoryWrapper().setBoughtItem(EnderPayApiUtils.getBanknote(this.value));
    }
}