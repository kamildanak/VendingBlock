package info.jbcs.minecraft.vending.renderer;

import info.jbcs.minecraft.vending.General;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TileEntityVendingMachineRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f, int p_180535_9_) {
        TileEntityVendingMachine machine = (TileEntityVendingMachine) tileEntity;

        //noinspection ConstantConditions
        if (machine == null || machine.getBlockType() == null) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
        int A = 0;
        int notNullSold = General.countNotNull(machine.getSoldItems());
        for (ItemStack itemStack : machine.getSoldItems()) {
            if (itemStack == ItemStack.EMPTY) {
                continue;
            }
            EntityItem entity = new EntityItem(tileEntity.getWorld(), x, y, z, itemStack);
            entity.hoverStart = 0;

            GL11.glPushMatrix();
            //noinspection ConstantConditions
            if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().player != null) {
                short animationTicks = (short) Minecraft.getMinecraft().player.ticksExisted;

                float f1 = MathHelper.sin((animationTicks + f) / 10.0F) * 0.1F + 0.1F;
                float f2 = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(itemStack, tileEntity.getWorld(), null)
                        .getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;

                GlStateManager.translate(((notNullSold == 1) ? 0 : -0.15 + (A % 2) * 0.3),
                        f1 + 0.25F * f2 + 0.1,
                        (notNullSold == 1) ? 0 : -0.15 + (A < 2 ? 0 : 1) * 0.3);
                float f3 = ((animationTicks + f) / 20.0F) * (180F / (float)Math.PI);
                GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            }

            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack, ItemCameraTransforms.TransformType.GROUND);

            A = A + 1;
            GL11.glPopMatrix();
        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
}
