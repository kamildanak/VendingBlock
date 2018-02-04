package info.jbcs.minecraft.vending.items.wrapper;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RangedWrapper;

class SingleInvWrapper extends RangedWrapper {
    SingleInvWrapper(IItemHandlerModifiable compose, int slot) {
        super(compose, slot, slot + 1);
    }
}
