package info.jbcs.minecraft.vending.settings;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface ISettings {
    boolean shouldUseCustomCreativeTab();

    boolean shouldCloseOnSoldOut();

    boolean shouldCloseOnPartialSoldOut();

    boolean isPlacingNextToDoorsBlocked();

    boolean shouldTransferToInventory();

    int getOffsetY();

    void loadConfig(FMLPreInitializationEvent event);

    void save();
}
