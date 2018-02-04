package info.jbcs.minecraft.vending.items.wrapper;

import info.jbcs.minecraft.vending.inventory.InventoryVendingStorageAttachment;

public class StorageAttachmentInvWrapper {
    private IItemHandlerAdvanced inputWrapper;
    private IItemHandlerAdvanced outputWrapper;
    private IItemHandlerAdvanced storageWrapper;

    public StorageAttachmentInvWrapper(InventoryVendingStorageAttachment inventory) {
        inputWrapper = new AdvancedInventoryWrapper(new StorageAttachmentInputInvWrapper(inventory));
        outputWrapper = new AdvancedInventoryWrapper(new StorageAttachmentOutputInvWrapper(inventory));
        storageWrapper = new AdvancedInventoryWrapper(new StorageAttachmentStorageInvWrapper(inventory));
    }

    public IItemHandlerAdvanced getInputWrapper() {
        return inputWrapper;
    }

    public IItemHandlerAdvanced getOutputWrapper() {
        return outputWrapper;
    }

    public IItemHandlerAdvanced getStorageWrapper() {
        return storageWrapper;
    }
}
