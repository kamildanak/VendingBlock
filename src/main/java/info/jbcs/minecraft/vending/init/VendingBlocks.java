package info.jbcs.minecraft.vending.init;

import info.jbcs.minecraft.vending.block.BlockVendingMachine;
import net.minecraft.block.Block;

public class VendingBlocks {
    public static final BlockVendingMachine BLOCK_VENDING_MACHINE;
    public static final BlockVendingMachine BLOCK_VENDING_MACHINE_ADVANCED;
    public static final BlockVendingMachine BLOCK_VENDING_MACHINE_MULTIPLE;
    static final Block[] BLOCKS;

    static {
        BLOCK_VENDING_MACHINE = new BlockVendingMachine(false, false, "vendingMachine");
        BLOCK_VENDING_MACHINE_ADVANCED = new BlockVendingMachine(true, false, "vendingMachineAdvanced");
        BLOCK_VENDING_MACHINE_MULTIPLE = new BlockVendingMachine(false, true, "vendingMachineMultiple");
        BLOCKS = new Block[]{BLOCK_VENDING_MACHINE, BLOCK_VENDING_MACHINE_ADVANCED, BLOCK_VENDING_MACHINE_MULTIPLE};
    }
}
