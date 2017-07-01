package info.jbcs.minecraft.vending.network.server;

import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.network.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

public class MessageAdvVenSetItem extends AbstractMessage.AbstractServerMessage<MessageAdvVenSetItem> {
    private int id, count, damage;

    @SuppressWarnings("unused")
    public MessageAdvVenSetItem() {
    }

    public MessageAdvVenSetItem(int id, int count, int damage) {
        this.id = id;
        this.count = count;
        this.damage = damage;
    }

    @Override
    protected void read(PacketBuffer buffer) throws IOException {
        id = buffer.readInt();
        count = buffer.readInt();
        damage = buffer.readInt();
    }

    @Override
    protected void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(id);
        buffer.writeInt(count);
        buffer.writeInt(damage);
    }

    @Override
    public void process(EntityPlayer player, Side side) {
        Container con = player.openContainer;
        if (con == null || !(con instanceof ContainerAdvancedVendingMachine))
            return;
        ContainerAdvancedVendingMachine container = (ContainerAdvancedVendingMachine) con;

        container.entity.inventory.setBoughtItem(id == 0 ? ItemStack.EMPTY : new ItemStack(Item.getItemById(id), count, damage));

    }
}
