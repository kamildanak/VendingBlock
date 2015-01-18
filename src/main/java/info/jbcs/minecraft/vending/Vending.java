package info.jbcs.minecraft.vending;

import info.jbcs.minecraft.utilities.DummyContainer;
import info.jbcs.minecraft.utilities.GuiHandler;
import info.jbcs.minecraft.utilities.ItemMetaBlock;
import info.jbcs.minecraft.utilities.packets.PacketHandler;
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
import net.minecraftforge.common.MinecraftForge;
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
	public static final String VERSION = "1.2.0b";

	public static FMLEventChannel Channel;

	public static Block blockVendingMachine;
	public static Block blockAdvancedVendingMachine;
	public static Item itemWrench;
	
	public static GuiHandler guiVending;
	public static GuiHandler guiWrench;

	public static CreativeTabs	tabVending;
	
	static Configuration config;

	static Block[] supports={
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

	@Instance("Vending")
	public static Vending instance;

	@SidedProxy(clientSide = "info.jbcs.minecraft.vending.ProxyClient", serverSide = "info.jbcs.minecraft.vending.Proxy")
	public static Proxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();


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
		
		blockVendingMachine = new BlockVendingMachine(supports,false);
		GameRegistry.registerBlock(blockVendingMachine, ItemMetaBlock.class, "vendingMachine");

		blockAdvancedVendingMachine = new BlockVendingMachine(supports,true).setBlockName("vendingMachineAdvanced");
		GameRegistry.registerBlock(blockAdvancedVendingMachine, ItemMetaBlock.class, "vendingMachineAdvanced");

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
		
		Packets.advancedMachine.create();
		Packets.wrench.create();
		PacketHandler.register(this);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
}



