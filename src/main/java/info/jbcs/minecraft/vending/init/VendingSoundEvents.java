package info.jbcs.minecraft.vending.init;

import info.jbcs.minecraft.vending.Vending;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class VendingSoundEvents {
    public static final SoundEvent PROCESSED;
    public static final SoundEvent FORBIDDEN;
    static final SoundEvent[] SOUNDS;

    static {
        ResourceLocation res_sound_processed = new ResourceLocation(Vending.MOD_ID, "sound_processed");
        ResourceLocation res_sound_forbidden = new ResourceLocation(Vending.MOD_ID, "sound_forbidden");
        PROCESSED = new SoundEvent(res_sound_processed).setRegistryName(res_sound_processed);
        FORBIDDEN = new SoundEvent(res_sound_forbidden).setRegistryName(res_sound_forbidden);
        SOUNDS = new SoundEvent[]{PROCESSED, FORBIDDEN};
    }
}
