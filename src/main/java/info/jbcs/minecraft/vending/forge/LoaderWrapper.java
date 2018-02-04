package info.jbcs.minecraft.vending.forge;

import net.minecraftforge.fml.common.Loader;

public class LoaderWrapper {

    private static LoaderWrapper instance;

    private static LoaderWrapper instance() {
        if (instance == null) {
            instance = new LoaderWrapper();
        }

        return instance;
    }

    public static boolean isEnderPayLoaded() {
        return instance().isModLoaded("enderpay");
    }

    public static boolean isWAILALoaded() {
        return instance().isModLoaded("waila");
    }

    public static void setTestWrapper(LoaderWrapper wrapper) {
        instance = wrapper;
    }

    public boolean isModLoaded(String modName) {
        return Loader.isModLoaded(modName);
    }
}
