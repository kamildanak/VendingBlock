package info.jbcs.minecraft.vending;

import info.jbcs.minecraft.utilities.General;
import info.jbcs.minecraft.utilities.Sounds;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ProxyClient extends Proxy implements ITickHandler {
	private Minecraft mc;

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(new Sounds() {
			@Override
			public void addSounds() {
				addSound("vending:cha-ching.ogg");
				addSound("vending:forbidden.ogg");
			}
		});
	}

	@Override
	public void init() {
		mc = FMLClientHandler.instance().getClient();
		TickRegistry.registerTickHandler(this, Side.CLIENT);
		TileEntityRenderer.instance.specialRendererMap.put(TileEntityVendingMachine.class, new TileEntityVendingMachineRenderer());
		RenderingRegistry.registerBlockHandler(new BlockVendingMachineRenderer(RenderingRegistry.getNextAvailableRenderId()));
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	class HintGui extends Gui {
		RenderItem render = new RenderItem();

		void drawNumberForItem(FontRenderer fontRenderer, ItemStack stack, int ux, int uy) {
			if (stack == null || stack.stackSize < 2) {
				return;
			}

			String line = "" + stack.stackSize;
			int x = ux + 19 - 2 - fontRenderer.getStringWidth(line);
			int y = uy + 6 + 3;
			GL11.glTranslatef(0.0f, 0.0f, 50.0f);
			drawString(fontRenderer, line, x + 1, y + 1, 0x888888);
			drawString(fontRenderer, line, x, y, 0xffffff);
			GL11.glTranslatef(0.0f, 0.0f, -50.0f);
		}

		void draw(String seller, ItemStack sold, ItemStack bought, float tick) {
			if (bought == null && sold == null) {
				return;
			}

			ScaledResolution resolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			int screenwidth = resolution.getScaledWidth();
			FontRenderer fontRenderer = mc.fontRenderer;
			int width = resolution.getScaledWidth();
			int height = resolution.getScaledHeight();
			int w = 120;
			int h = 60;
			int centerYOff = -80;
			int cx = width / 2;
			int x = cx - w / 2;
			int y = height / 2 - h / 2 + centerYOff;
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0f, 0.0f, -100.0f);
			drawGradientRect(x, y, x + w, y + h, 0xc0101010, 0xd0101010);
			drawCenteredString(fontRenderer, seller, cx, y + 8, 0xffffff);

			if (bought != null && sold != null) {
				x += 32;
				y += 26;
				drawString(fontRenderer, "is selling", x, y, 0xa0a0a0);
				drawNumberForItem(fontRenderer, sold, x + 46, y - 4);
				drawString(fontRenderer, "for", x + 14, y + 16, 0xa0a0a0);
				drawNumberForItem(fontRenderer, bought, x + 14 + 18, y + 16 - 4);
				render.renderItemIntoGUI(fontRenderer, mc.renderEngine, bought, x + 14 + 18, y + 16 - 4);
				render.renderItemIntoGUI(fontRenderer, mc.renderEngine, sold, x + 46, y - 4);
                GL11.glDisable(GL11.GL_LIGHTING);
			} else if (bought == null) {
				x += 18;
				y += 30;
				drawString(fontRenderer, "is giving", x, y, 0xa0a0a0);
				drawString(fontRenderer, "away", x + 60, y, 0xa0a0a0);
				drawNumberForItem(fontRenderer, sold, x + 42, y - 4);
				render.renderItemIntoGUI(fontRenderer, mc.renderEngine, sold, x + 42, y - 4);
                GL11.glDisable(GL11.GL_LIGHTING);
			} else if (sold == null) {
				x += 22;
				y += 30;
				drawString(fontRenderer, "is accepting", x, y, 0xa0a0a0);
				drawNumberForItem(fontRenderer, bought, x + 62, y - 4);
				render.renderItemIntoGUI(fontRenderer, mc.renderEngine, bought, x + 62, y - 4);
                GL11.glDisable(GL11.GL_LIGHTING);
			}

			GL11.glPopMatrix();
		}
	}

	HintGui gui = new HintGui();

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
			return;
		}

		EntityPlayer player = mc.thePlayer;
		World world = mc.theWorld;
		MovingObjectPosition mop = General.getMovingObjectPositionFromPlayer(world, player, false);

		if (mop == null) {
			return;
		}

		if (mop.typeOfHit != EnumMovingObjectType.TILE) {
			return;
		}

		TileEntity te = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);

		if (te == null) {
			return;
		}

		if (!(te instanceof TileEntityVendingMachine)) {
			return;
		}

		TileEntityVendingMachine tileEntity = (TileEntityVendingMachine) te;
		gui.draw(tileEntity.ownerName, tileEntity.getSoldItem(), tileEntity.getBoughtItem(), (Float) tickData[0]);
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "Vending Block looking to display GUI";
	}
}
