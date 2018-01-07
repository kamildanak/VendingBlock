package info.jbcs.minecraft.vending.init;

import info.jbcs.minecraft.vending.Vending;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class VendingSoundEvents {
    public static final SoundEvent PROCESSED;
    public static final SoundEvent FORBIDDEN;
    public static final SoundEvent STORAGE_OPEN;
    public static final SoundEvent STORAGE_CLOSE;
    static final SoundEvent[] SOUNDS;

    static {
        ResourceLocation res_sound_processed = new ResourceLocation(Vending.MOD_ID, "sound_processed");
        ResourceLocation res_sound_forbidden = new ResourceLocation(Vending.MOD_ID, "sound_forbidden");
        ResourceLocation res_safe_open = new ResourceLocation(Vending.MOD_ID, "storage_open");
        ResourceLocation res_safe_close = new ResourceLocation(Vending.MOD_ID, "storage_close");
        PROCESSED = new SoundEvent(res_sound_processed).setRegistryName(res_sound_processed);
        FORBIDDEN = new SoundEvent(res_sound_forbidden).setRegistryName(res_sound_forbidden);
        STORAGE_OPEN = new SoundEvent(res_safe_open).setRegistryName(res_safe_open);
        STORAGE_CLOSE = new SoundEvent(res_safe_close).setRegistryName(res_safe_close);
        SOUNDS = new SoundEvent[]{PROCESSED, FORBIDDEN, STORAGE_OPEN, STORAGE_CLOSE};
    }
}
