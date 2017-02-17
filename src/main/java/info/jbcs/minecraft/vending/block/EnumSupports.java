package info.jbcs.minecraft.vending.block;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.IStringSerializable;

public enum EnumSupports implements IStringSerializable {
    STONE(0, "stone", "stone", Blocks.STONE, Blocks.STONE),
    COBBLE_STONE(1, "stonebrick", "stonebrick", Blocks.COBBLESTONE, Blocks.COBBLESTONE),
    STONE_BRICK(2, "stonebricksmooth", "stonebricksmooth", Blocks.STONEBRICK, Blocks.STONEBRICK),
    PLANKS(3, "wood", "wood", Blocks.PLANKS, Blocks.PLANKS),
    CRAFTING_TABLE(4, "workbench", "workbench", Blocks.CRAFTING_TABLE, Blocks.CRAFTING_TABLE),
    GRAVEL(5, "gravel", "gravel", Blocks.GRAVEL, Blocks.GRAVEL),
    NOTEBLOCK(6, "musicblock", "musicblock", Blocks.NOTEBLOCK, Blocks.NOTEBLOCK),
    SANDSTONE(7, "sandstone", "sandstone", Blocks.SANDSTONE, Blocks.SANDSTONE),
    GOLD(8, "blockgold", "blockgold", Blocks.GOLD_BLOCK, Items.GOLD_INGOT),
    IRON(9, "blockiron", "blockiron", Blocks.IRON_BLOCK, Items.IRON_INGOT),
    BRICK(10, "brick", "brick", Blocks.BRICK_BLOCK, Blocks.BRICK_BLOCK),
    COBBLESTONE_MOSSY(11, "stonemoss", "stonemoss", Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE),
    OBSIDIAN(12, "obsidian", "obsidian", Blocks.OBSIDIAN, Blocks.OBSIDIAN),
    DIAMOND(13, "blockdiamond", "blockdiamond", Blocks.DIAMOND_BLOCK, Items.DIAMOND),
    EMERALD(14, "blockemerald", "blockemerald", Blocks.EMERALD_BLOCK, Items.EMERALD),
    LAPIS(15, "blocklapis", "blocklapis", Blocks.LAPIS_BLOCK, Blocks.LAPIS_BLOCK);
    public final static int length = values().length;
    private static final EnumSupports[] META_LOOKUP = new EnumSupports[values().length];

    static {
        EnumSupports[] var0 = values();
        for (EnumSupports var3 : var0) {
            META_LOOKUP[var3.getMetadata()] = var3;
        }
    }

    private final int meta;
    private final String name;
    private final String unlocalizedName;
    private final Block supportBlock;
    private final Object reagent;

    EnumSupports(int meta, String name, String unlocalizedName, Block supportBlock, Object reagent) {
        this.meta = meta;
        this.name = name;
        this.unlocalizedName = unlocalizedName;
        this.supportBlock = supportBlock;
        this.reagent = reagent;
    }

    public static EnumSupports byMetadata(int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length) meta = 0;
        return META_LOOKUP[meta];
    }

    public int getMetadata() {
        return this.meta;
    }

    public String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    public String toString() {
        return this.unlocalizedName;
    }

    public String getName() {
        return this.name;
    }

    public Block getSupportBlock() {
        return supportBlock;
    }

    public Object getReagent() {
        return reagent;
    }
}