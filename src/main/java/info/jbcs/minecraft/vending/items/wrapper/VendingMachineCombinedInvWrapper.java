package info.jbcs.minecraft.vending.items.wrapper;

import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class VendingMachineCombinedInvWrapper extends CombinedInvWrapper {
    public VendingMachineCombinedInvWrapper(VendingMachineStorageInvWrapper machineStorage,
                                            StorageAttachmentStorageInvWrapper attchmentStorage) {
        super(machineStorage, attchmentStorage);
    }
}
