package info.jbcs.minecraft.vending.items.wrapper.transactions;

public enum VendingStatus {
    OPEN,
    CLOSED,
    WRONG_ITEM_OFFERED_TYPE,
    NOT_ENOUGH_ITEM_OFFERED,
    NOT_ENOUGH_CREDITS_IN_STORAGE,
    NOT_ENOUGH_CREDITS_OFFERED,
    NOT_ENOUGH_SPACE_TO_STORE_ITEMS,
    NO_BANKNOTE_IN_INVENTORY,
    NOTHING_TO_TRADE,
}
