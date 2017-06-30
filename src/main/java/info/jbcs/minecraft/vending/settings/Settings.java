package info.jbcs.minecraft.vending.settings;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Settings implements ISettings {
    private Configuration config;
    private boolean use_custom_creative_tab;
    private boolean close_on_sold_out;
    private boolean close_on_partial_sold_out;
    private boolean block_placing_next_to_doors;
    private boolean transfer_to_inventory;
    private int offsetY;


    public Settings() {

    }

    private void loadConfig(Configuration config) {
        use_custom_creative_tab = config.get("general", "use_custom_creative_tab", true, "Add a new tab to creative mode and put all vending blocks there.").getBoolean(true);
        close_on_sold_out = config.get("general", "close_on_sold_out", false, "Stop accepting item after last item is sold out.").getBoolean(false);
        close_on_partial_sold_out = config.get("general", "close_on_partial_sold_out", false,
                "Stop accepting item after some item were sold out.").getBoolean(false);
        block_placing_next_to_doors = config.get("general", "block_placing_next_to_doors", false,
                "Check for nearby doors when block is placed " +
                        "(Use specialized mod if you want more advanced restrictions)").getBoolean(false);

        transfer_to_inventory = config.get("general", "transfer_to_inventory", false,
                "Transfer sold item directly to player's inventory.").getBoolean(false);

        int defaultOffset = (Loader.isModLoaded("waila"))?40:15;
        offsetY = config.get("general", "offsetY", defaultOffset,
                "Set Y offset of HUD").getInt(defaultOffset);
    }

    public void loadConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        loadConfig(config);
    }

    public void save() {
        config.save();
    }

    public boolean shouldUseCustomCreativeTab() {
        return use_custom_creative_tab;
    }

    public boolean shouldCloseOnSoldOut() {
        return close_on_sold_out;
    }

    public boolean shouldCloseOnPartialSoldOut() {
        return close_on_partial_sold_out;
    }

    public boolean isPlacingNextToDoorsBlocked() {
        return block_placing_next_to_doors;
    }

    public boolean shouldTransferToInventory() {
        return transfer_to_inventory;
    }

    public int getOffsetY() {
        return offsetY;
    }
}
