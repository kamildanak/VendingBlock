package info.jbcs.minecraft.vending.proxy;

import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.block.EnumSupports;
import info.jbcs.minecraft.vending.network.PacketDispatcher;
import info.jbcs.minecraft.vending.network.server.MessageAdvVenSetItem;
import info.jbcs.minecraft.vending.network.server.MessageSetLock;
import info.jbcs.minecraft.vending.network.server.MessageWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
    public void registerEventHandlers() {
    }

    public void registerPackets() {
        PacketDispatcher.registerMessage(MessageAdvVenSetItem.class);
        PacketDispatcher.registerMessage(MessageWrench.class);
        PacketDispatcher.registerMessage(MessageSetLock.class);
    }

    /**
     * Returns a side-appropriate EntityPlayer for use during message handling
     */
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

    /**
     * Returns the current thread based on side during message handling,
     * used for ensuring that the message is being handled by the main thread
     */
    public IThreadListener getThreadFromContext(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity.getServerWorld();
    }


    public void registerRenderers() {
    }

    public void registerCraftingRecipes() {
        for (int i = 0; i < EnumSupports.length; i++) {
            CraftingManager.getInstance().addRecipe(new ItemStack(Vending.blockVendingMachine, 1, i),
                    "XXX", "XGX", "*R*",
                    'X', Blocks.GLASS,
                    'G', Items.GOLD_INGOT,
                    'R', Items.REDSTONE,
                    '*', EnumSupports.byMetadata(i).getReagent());

            CraftingManager.getInstance().addRecipe(new ItemStack(Vending.blockAdvancedVendingMachine, 1, i),
                    "XXX", "XGX", "*R*",
                    'X', Blocks.GLASS,
                    'G', Items.GOLD_INGOT,
                    'R', Items.REPEATER,
                    '*', EnumSupports.byMetadata(i).getReagent());

            CraftingManager.getInstance().addRecipe(new ItemStack(Vending.blockMultipleVendingMachine, 1, i),
                    "XXX", "XGX", "*R*",
                    'X', Blocks.GLASS,
                    'G', Items.GOLD_INGOT,
                    'R', Blocks.DISPENSER,
                    '*', EnumSupports.byMetadata(i).getReagent());
        }
    }
}
