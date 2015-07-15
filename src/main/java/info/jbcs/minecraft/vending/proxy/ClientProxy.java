package info.jbcs.minecraft.vending.proxy;

import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.block.BlockVendingMachine;
import info.jbcs.minecraft.vending.block.EnumSupports;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import info.jbcs.minecraft.vending.gui.HintGui;
import info.jbcs.minecraft.vending.renderer.TileEntityVendingMachineRenderer;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.client.Minecraft;
//import net.minecraft.util.EnumMovingObjectType;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy{
	private Minecraft mc;

	@Override
	public void registerEventHandlers(){
		MinecraftForge.EVENT_BUS.register(new HintGui(Minecraft.getMinecraft()));
	}

	@Override
	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVendingMachine.class, new TileEntityVendingMachineRenderer());
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(Vending.itemWrench, 0, new ModelResourceLocation(Vending.MOD_ID + ":" + "vendingMachineWrench", "inventory"));
		for(int i=0;i<EnumSupports.length;i++){
			renderItem.getItemModelMesher().register(Item.getItemFromBlock(Vending.blockVendingMachine), i,
					new ModelResourceLocation(Vending.MOD_ID + ":" + ((BlockVendingMachine) Vending.blockVendingMachine).getName(),
							"support=" + EnumSupports.byMetadata(i).getUnlocalizedName()));

			renderItem.getItemModelMesher().register(Item.getItemFromBlock(Vending.blockAdvancedVendingMachine), i,
					new ModelResourceLocation(Vending.MOD_ID + ":" + ((BlockVendingMachine) Vending.blockAdvancedVendingMachine).getName(),
							"support=" + EnumSupports.byMetadata(i).getUnlocalizedName()));

			renderItem.getItemModelMesher().register(Item.getItemFromBlock(Vending.blockMultipleVendingMachine), i,
					new ModelResourceLocation(Vending.MOD_ID + ":" + ((BlockVendingMachine) Vending.blockMultipleVendingMachine).getName(),
							"support=" + EnumSupports.byMetadata(i).getUnlocalizedName()));
		}
	}
}
