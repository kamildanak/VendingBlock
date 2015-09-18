package info.jbcs.minecraft.vending;

import info.jbcs.minecraft.vending.block.BlockVendingMachine;
import info.jbcs.minecraft.vending.gui.*;
import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerMultipleVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerVendingMachine;
import info.jbcs.minecraft.vending.inventory.DummyContainer;
import info.jbcs.minecraft.vending.network.MessagePipeline;
import info.jbcs.minecraft.vending.proxy.CommonProxy;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid=Vending.MOD_ID, name=Vending.MOD_NAME, version=Vending.VERSION) // dependencies = "required-after:autoutils"
public class Vending {
	public static final String MOD_ID = "vending";
	public static final String MOD_NAME = "vending";
	public static final String VERSION = "1.3.1";

	@Instance(MOD_ID)
	public static Vending	instance;

	public static Block blockVendingMachine;
	public static Block blockAdvancedVendingMachine;
	public static Block blockMultipleVendingMachine;
	public static Item itemWrench;
	
	public static GuiHandler guiVending;
	public static GuiHandler guiWrench;

	public static CreativeTabs	tabVending;
	
	static Configuration config;
	public MessagePipeline messagePipeline;

	@SidedProxy(clientSide = "info.jbcs.minecraft.vending.proxy.ClientProxy", serverSide = "info.jbcs.minecraft.vending.proxy.CommonProxy")
	public static CommonProxy commonProxy;

	public Vending(){
		messagePipeline = new MessagePipeline();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		blockVendingMachine = new BlockVendingMachine(false, false, "vendingMachine");
		blockAdvancedVendingMachine = new BlockVendingMachine(true, false, "vendingMachineAdvanced");
		blockMultipleVendingMachine = new BlockVendingMachine(false, true, "vendingMachineMultiple");

		itemWrench = new Item().setUnlocalizedName("vendingMachineWrench").setCreativeTab(tabVending).setContainerItem(itemWrench);
		GameRegistry.registerItem(itemWrench, "vendingMachineWrench", Vending.MOD_ID);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		commonProxy.registerPackets(messagePipeline);
		commonProxy.registerEventHandlers();
		commonProxy.registerRenderers();
		commonProxy.registerCraftingRecipes();

		if(config.get("general", "use custom creative tab", true, "Add a new tab to creative mode and put all vending blocks there.").getBoolean(true)){
			tabVending = new CreativeTabs("tabVending") {
				@Override
				public ItemStack getIconItemStack() {
					return new ItemStack(blockVendingMachine, 1, 4);
				}

				@Override
				public Item getTabIconItem() {
					return new ItemStack(blockVendingMachine, 1, 4).getItem();
				}
			};
		} else{
			tabVending = CreativeTabs.tabDecorations;
		}
		blockVendingMachine.setCreativeTab(tabVending);
		blockAdvancedVendingMachine.setCreativeTab(tabVending);
		blockMultipleVendingMachine.setCreativeTab(tabVending);
		itemWrench.setCreativeTab(tabVending);

        GameRegistry.registerTileEntity(TileEntityVendingMachine.class, "containerVendingMachine");
		
		guiVending=new GuiHandler("vending"){
			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		        if(! (tileEntity instanceof TileEntityVendingMachine))
		        	return null;
		        
		        TileEntityVendingMachine e=(TileEntityVendingMachine) tileEntity;
		        
		        if(e.advanced)
		        	return new ContainerAdvancedVendingMachine(player.inventory, e);

				if(e.multiple)
					return new ContainerMultipleVendingMachine(player.inventory, e);

		        return new ContainerVendingMachine(player.inventory, e);
			}

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		        if(! (tileEntity instanceof TileEntityVendingMachine))
		        	return null;
		        
		        TileEntityVendingMachine e=(TileEntityVendingMachine) tileEntity;
		        
		        if(e.advanced)
                    return new GuiAdvancedVendingMachine(player.inventory, e);

				if(e.multiple)
					return new GuiMultipleVendingMachine(player.inventory, e);

                return new GuiVendingMachine(player.inventory, e);
			}
		};
		
		guiWrench=new GuiHandler("wrench"){
			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		        return new DummyContainer();
			}

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
               
                return new GuiWrenchVendingMachine(world, new BlockPos(x,y,z) ,player);
			}
		};

		GuiHandler.register(this);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
}



