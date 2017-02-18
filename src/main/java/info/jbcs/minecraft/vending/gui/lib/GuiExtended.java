package info.jbcs.minecraft.vending.gui.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class GuiExtended extends Gui {
    private static HashMap<String, ResourceLocation> resources = new HashMap<>();
    private Minecraft mc;

    public GuiExtended(Minecraft mc) {
        this.mc = mc;
    }

    public void bind(String textureName) {
        ResourceLocation res = resources.get(textureName);

        if (res == null) {
            res = new ResourceLocation(textureName);
            resources.put(textureName, res);
        }

        mc.getTextureManager().bindTexture(res);
    }
}
