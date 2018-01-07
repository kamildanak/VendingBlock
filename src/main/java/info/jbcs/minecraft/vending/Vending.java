package info.jbcs.minecraft.vending;

import com.kamildanak.minecraft.foamflower.gui.GuiHandler;
import info.jbcs.minecraft.vending.gui.HandlerStorage;
import info.jbcs.minecraft.vending.gui.HandlerVending;
import info.jbcs.minecraft.vending.gui.HandlerWrench;
import info.jbcs.minecraft.vending.proxy.CommonProxy;
import info.jbcs.minecraft.vending.settings.ISettings;
import info.jbcs.minecraft.vending.settings.Settings;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingStorageAttachment;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Vending.MOD_ID, name = Vending.MOD_NAME, version = Vending.VERSION,
        dependencies = "after:enderpay;required-after:foamflower",
        acceptedMinecraftVersions = Vending.ACCEPTED_MC_VERSIONS)

public class Vending {
    public static final String MOD_ID = "vending";
    static final String MOD_NAME = "vending";
    static final String VERSION = "{@vendingVersion}";
    static final String ACCEPTED_MC_VERSIONS = "{@mcVersion}";

    public static GuiHandler guiVending;
    public static GuiHandler guiWrench;
    public static GuiHandler guiStorage;

    public static CreativeTabs tabVending;
    @SidedProxy(clientSide = "info.jbcs.minecraft.vending.proxy.ClientProxy", serverSide = "info.jbcs.minecraft.vending.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static ISettings settings;

    public Vending() {

    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        settings = new Settings();
        settings.loadConfig(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerPackets();
        proxy.registerEventHandlers();
        proxy.registerRenderers();
        proxy.setCreativeTabs();

        GameRegistry.registerTileEntity(TileEntityVendingMachine.class, "containerVendingMachine");
        GameRegistry.registerTileEntity(TileEntityVendingStorageAttachment.class, "containerStorageAttachment");

        guiVending = new HandlerVending("vending");
        guiWrench = new HandlerWrench("wrench");
        guiStorage = new HandlerStorage("storage");
        GuiHandler.register(this);
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        settings.save();
    }
}



