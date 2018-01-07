package info.jbcs.minecraft.vending.proxy;

import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.init.VendingBlocks;
import info.jbcs.minecraft.vending.init.VendingItems;
import info.jbcs.minecraft.vending.network.PacketDispatcher;
import info.jbcs.minecraft.vending.network.server.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

import static info.jbcs.minecraft.vending.Vending.tabVending;

public class CommonProxy {
    public void registerEventHandlers() {
    }

    public void registerPackets() {
        PacketDispatcher.registerMessage(MessageAdvVenSetItem.class);
        PacketDispatcher.registerMessage(MessageWrench.class);
        PacketDispatcher.registerMessage(MessageSetLock.class);
        PacketDispatcher.registerMessage(MessageAdvVenSetBanknote.class);
        PacketDispatcher.registerMessage(MessageWrenchToInventory.class);
    }

    /**
     * Returns a side-appropriate EntityPlayer for use during message handling
     */
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }

    /**
     * Returns the current thread based on side during message handling,
     * used for ensuring that the message is being handled by the main thread
     */
    public IThreadListener getThreadFromContext(MessageContext ctx) {
        return ctx.getServerHandler().player.getServerWorld();
    }


    public void registerRenderers() {
    }

    public void setCreativeTabs() {
        if (Vending.settings.shouldUseCustomCreativeTab()) {
            tabVending = new CreativeTabs("tabVending") {
                @Override
                @Nonnull
                public ItemStack getIconItemStack() {
                    return new ItemStack(VendingBlocks.BLOCK_VENDING_MACHINE, 1, 4);
                }

                @Override
                @Nonnull
                public ItemStack getTabIconItem() {
                    return new ItemStack(VendingBlocks.BLOCK_VENDING_MACHINE, 1, 4);
                }
            };
        } else {
            tabVending = CreativeTabs.DECORATIONS;
        }

        VendingBlocks.BLOCK_VENDING_MACHINE.setCreativeTab(tabVending);
        VendingBlocks.BLOCK_VENDING_MACHINE_ADVANCED.setCreativeTab(tabVending);
        VendingBlocks.BLOCK_VENDING_MACHINE_MULTIPLE.setCreativeTab(tabVending);
        VendingBlocks.BLOCK_VENDING_STORAGE_ATTACHMENT.setCreativeTab(tabVending);
        VendingItems.ITEM_WRENCH.setCreativeTab(tabVending);
    }
}
