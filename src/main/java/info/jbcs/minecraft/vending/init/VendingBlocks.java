package info.jbcs.minecraft.vending.init;

import info.jbcs.minecraft.vending.block.BlockVendingMachine;
import info.jbcs.minecraft.vending.block.BlockVendingStorageAttachment;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class VendingBlocks {
    public static final BlockVendingMachine BLOCK_VENDING_MACHINE;
    public static final BlockVendingMachine BLOCK_VENDING_MACHINE_ADVANCED;
    public static final BlockVendingMachine BLOCK_VENDING_MACHINE_MULTIPLE;
    public static final BlockVendingStorageAttachment BLOCK_VENDING_STORAGE_ATTACHMENT;
    static final Block[] BLOCKS;

    static {
        BLOCK_VENDING_MACHINE = new BlockVendingMachine(false, false, "vendingMachine");
        BLOCK_VENDING_MACHINE_ADVANCED = new BlockVendingMachine(true, false, "vendingMachineAdvanced");
        BLOCK_VENDING_MACHINE_MULTIPLE = new BlockVendingMachine(false, true, "vendingMachineMultiple");
        BLOCK_VENDING_STORAGE_ATTACHMENT = new BlockVendingStorageAttachment("vendingStorageAttachment", Material.IRON);
        BLOCKS = new Block[]{BLOCK_VENDING_MACHINE, BLOCK_VENDING_MACHINE_ADVANCED, BLOCK_VENDING_MACHINE_MULTIPLE,
                BLOCK_VENDING_STORAGE_ATTACHMENT};
    }
}
