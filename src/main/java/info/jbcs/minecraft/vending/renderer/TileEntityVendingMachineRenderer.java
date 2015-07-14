package info.jbcs.minecraft.vending.renderer;

import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TileEntityVendingMachineRenderer extends TileEntitySpecialRenderer {
	RenderItem renderer = new RenderItem();

	public TileEntityVendingMachineRenderer() {
		renderer.setRenderManager(RenderManager.instance);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		TileEntityVendingMachine machine = (TileEntityVendingMachine) tileentity;

		if (machine == null || machine.getBlockType() == null) {
			return;
		}
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.35F, (float) z + 0.5F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		int A=0;
		for(ItemStack itemStack: machine.getSoldItems()) {
			if (itemStack == null) { //|| itemstack.itemID < 0 || itemstack.itemID >= Item.itemsList.length || Item.itemsList[itemstack.itemID] == null) {
				continue;
			}
			EntityItem entity = new EntityItem(null, x, y, z, itemStack);
			entity.hoverStart = 0;

			if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null) {
				entity.age = Minecraft.getMinecraft().thePlayer.ticksExisted;
			}

			int meta = tileentity.getBlockMetadata();

			try {
				renderer.doRender(entity, -0.1+(A%2)*0.2, 0, -0.1+(A<2?0:1)*0.2, f, f);
			} catch (Throwable e) {
			}
			A=A+1;
		}
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
}
