package info.jbcs.minecraft.vending;

import com.kamildanak.minecraft.foamflower.gui.GuiHandler;
import com.kamildanak.minecraft.foamflower.inventory.DummyContainer;
import info.jbcs.minecraft.vending.gui.GuiAdvancedVendingMachine;
import info.jbcs.minecraft.vending.gui.GuiMultipleVendingMachine;
import info.jbcs.minecraft.vending.gui.GuiVendingMachine;
import info.jbcs.minecraft.vending.gui.GuiWrenchVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerMultipleVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerVendingMachine;
import info.jbcs.minecraft.vending.proxy.CommonProxy;
import info.jbcs.minecraft.vending.settings.ISettings;
import info.jbcs.minecraft.vending.settings.Settings;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

@Mod(modid = Vending.MOD_ID, name = Vending.MOD_NAME, version = Vending.VERSION,
        dependencies = "after:enderpay", acceptedMinecraftVersions = Vending.ACCEPTED_MC_VERSIONS)

public class Vending {
    public static final String MOD_ID = "vending";
    static final String MOD_NAME = "vending";
    static final String VERSION = "{@vendingVersion}";
    static final String ACCEPTED_MC_VERSIONS = "{@mcVersion}";

    public static GuiHandler guiVending;
    public static GuiHandler guiWrench;

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

        guiVending = new GuiHandler("vending") {
            @Override
            public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

                if (!(tileEntity instanceof TileEntityVendingMachine))
                    return null;

                TileEntityVendingMachine e = (TileEntityVendingMachine) tileEntity;

                if (e.isAdvanced())
                    return new ContainerAdvancedVendingMachine(player.inventory, e);

                if (e.isMultiple())
                    return new ContainerMultipleVendingMachine(player.inventory, e);

                return new ContainerVendingMachine(player.inventory, e);
            }

            @Override
            public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

                if (!(tileEntity instanceof TileEntityVendingMachine))
                    return null;

                TileEntityVendingMachine e = (TileEntityVendingMachine) tileEntity;

                if (e.isAdvanced())
                    return new GuiAdvancedVendingMachine(player.inventory, e);

                if (e.isMultiple())
                    return new GuiMultipleVendingMachine(player.inventory, e);

                return new GuiVendingMachine(player.inventory, e);
            }
        };

        guiWrench = new GuiHandler("wrench") {
            @Override
            public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                return new DummyContainer();
            }

            @Override
            public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

                return new GuiWrenchVendingMachine(world, new BlockPos(x, y, z), player);
            }
        };

        GuiHandler.register(this);
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        settings.save();
    }
}



