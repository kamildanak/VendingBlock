package info.jbcs.minecraft.vending;

import cpw.mods.fml.common.network.FMLEventChannel;
import info.jbcs.minecraft.vending.block.BlockVendingMachine;
import info.jbcs.minecraft.vending.gui.*;
import info.jbcs.minecraft.vending.inventory.ContainerAdvancedVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerMultipleVendingMachine;
import info.jbcs.minecraft.vending.inventory.ContainerVendingMachine;
import info.jbcs.minecraft.vending.inventory.DummyContainer;
import info.jbcs.minecraft.vending.item.ItemMetaBlock;
import info.jbcs.minecraft.vending.network.MessagePipeline;
import info.jbcs.minecraft.vending.proxy.CommonProxy;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid=Vending.MOD_ID, name=Vending.MOD_NAME, version=Vending.VERSION) // dependencies = "required-after:autoutils"
public class Vending {
	public static final String MOD_ID = "vending";
	public static final String MOD_NAME = "vending";
	public static final String VERSION = "1.2.3";

	@Instance(MOD_ID)
	public static Vending	instance;

	public static FMLEventChannel Channel;

	public static Block blockVendingMachine;
	public static Block blockAdvancedVendingMachine;
	public static Block blockMultipleVendingMachine;
	public static Item itemWrench;
	
	public static GuiHandler guiVending;
	public static GuiHandler guiWrench;

	public static CreativeTabs	tabVending;
	
	static Configuration config;
	public MessagePipeline messagePipeline;

	public static Block[] supports={
			Blocks.stone,
			Blocks.cobblestone,
			Blocks.stonebrick,
			Blocks.planks,
			Blocks.crafting_table,
			Blocks.gravel,
			Blocks.noteblock,
			Blocks.sandstone,
			Blocks.gold_block,
			Blocks.iron_block,
			Blocks.brick_block,
			Blocks.mossy_cobblestone,
			Blocks.obsidian,
			Blocks.diamond_block,
			Blocks.emerald_block,
			Blocks.lapis_block,
	};
	static Object[] reagents={
			Blocks.stone,
			Blocks.cobblestone,
			Blocks.stonebrick,
			Blocks.planks,
			Blocks.crafting_table,
			Blocks.gravel,
			Blocks.noteblock,
			Blocks.sandstone,
			Items.gold_ingot,
			Items.iron_ingot,
			Blocks.brick_block,
			Blocks.mossy_cobblestone,
			Blocks.obsidian,
			Items.diamond,
			Items.emerald,
			Blocks.lapis_block,
	};

	@SidedProxy(clientSide = "info.jbcs.minecraft.vending.proxy.ClientProxy", serverSide = "info.jbcs.minecraft.vending.proxy.CommonProxy")
	public static CommonProxy commonProxy;

	public Vending(){
		messagePipeline = new MessagePipeline();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		commonProxy.registerPackets(messagePipeline);
		commonProxy.registerEventHandlers();
		commonProxy.registerRenderers();

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

		blockVendingMachine = new BlockVendingMachine(supports,false,false);
		GameRegistry.registerBlock(blockVendingMachine, ItemMetaBlock.class, "vendingMachine");

		blockAdvancedVendingMachine = new BlockVendingMachine(supports,true,false).setBlockName("vendingMachineAdvanced");
		GameRegistry.registerBlock(blockAdvancedVendingMachine, ItemMetaBlock.class, "vendingMachineAdvanced");

		blockMultipleVendingMachine = new BlockVendingMachine(supports,false,true).setBlockName("vendingMachineMultiple");
		GameRegistry.registerBlock(blockMultipleVendingMachine, ItemMetaBlock.class, "vendingMachineMultiple");

		itemWrench = new Item().setUnlocalizedName("vendingMachineWrench").setCreativeTab(tabVending).setTextureName("Vending:wrench");
		GameRegistry.registerItem(itemWrench, "vendingMachineWrench");
		
        GameRegistry.registerTileEntity(TileEntityVendingMachine.class, "containerVendingMachine");

		for(int i=0;i<supports.length;i++){
			CraftingManager.getInstance().addRecipe(new ItemStack(blockVendingMachine,1,i),
					new Object[] { "XXX", "XGX", "*R*",
					'X', Blocks.glass,
					'G', Items.gold_ingot,
					'R', Items.redstone,
					'*', reagents[i],
				});
			
			CraftingManager.getInstance().addRecipe(new ItemStack(blockAdvancedVendingMachine,1,i),
					new Object[] { "XXX", "XGX", "*R*",
					'X', Blocks.glass,
					'G', Items.gold_ingot,
					'R', Items.repeater,
					'*', reagents[i],
				});

			CraftingManager.getInstance().addRecipe(new ItemStack(blockMultipleVendingMachine,1,i),
					new Object[] { "XXX", "XGX", "*R*",
							'X', Blocks.glass,
							'G', Items.gold_ingot,
							'R', Blocks.dispenser,
							'*', reagents[i],
					});
		}
		
		guiVending=new GuiHandler("vending"){
			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		        TileEntity tileEntity = world.getTileEntity(x, y, z);

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
                TileEntity tileEntity = world.getTileEntity(x, y, z);

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
               
                return new GuiWrenchVendingMachine(world,x,y,z,player);
			}
		};

		GuiHandler.register(this);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
}



