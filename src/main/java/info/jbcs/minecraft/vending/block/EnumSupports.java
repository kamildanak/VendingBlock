package info.jbcs.minecraft.vending.block;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.IStringSerializable;

public enum EnumSupports implements IStringSerializable
{
    STONE(0, "stone", "stone", Blocks.stone, Blocks.stone),
    COBBLE_STONE(1, "stoneBrick", "stoneBrick", Blocks.cobblestone, Blocks.cobblestone),
    STONE_BRICK(2, "stoneBrickSmooth", "stoneBrickSmooth", Blocks.stonebrick, Blocks.stonebrick),
    PLANKS(3, "wood", "wood", Blocks.planks, Blocks.planks),
    CRAFTING_TABLE(4, "workbench", "workbench", Blocks.crafting_table, Blocks.crafting_table),
    GRAVEL(5, "gravel", "gravel", Blocks.gravel, Blocks.gravel),
    NOTEBLOCK(6, "musicBlock", "musicBlock", Blocks.noteblock, Blocks.noteblock),
    SANDSTONE(7, "sandStone", "sandStone", Blocks.sandstone, Blocks.sandstone),
    GOLD(8, "blockGold", "blockGold", Blocks.gold_block, Items.gold_ingot),
    IRON(9, "blockIron", "blockIron", Blocks.iron_block, Items.iron_ingot),
    BRICK(10, "brick", "brick", Blocks.brick_block, Blocks.brick_block),
    COBBLESTONE_MOSSY(11, "stoneMoss", "stoneMoss", Blocks.mossy_cobblestone, Blocks.mossy_cobblestone),
    OBSIDIAN(12, "obsidian", "obsidian", Blocks.obsidian, Blocks.obsidian),
    DIAMOND(13, "blockDiamond", "blockDiamond", Blocks.diamond_block, Items.diamond),
    EMERALD(14, "blockEmerald", "blockEmerald", Blocks.emerald_block, Items.emerald),
    LAPIS(15, "blockLapis", "blockLapis", Blocks.lapis_block, Blocks.lapis_block);
    private static final EnumSupports[] META_LOOKUP = new EnumSupports[values().length];
    private final int meta;
    private final String name;
    private final String unlocalizedName;
    private final Block supportBlock;
    private final Object reagent;
    public final static int length = values().length;

    EnumSupports(int meta, String name, String unlocalizedName, Block supportBlock, Object reagent)
    {
        this.meta = meta;
        this.name = name;
        this.unlocalizedName = unlocalizedName;
        this.supportBlock = supportBlock;
        this.reagent = reagent;
    }

    public int getMetadata()
    {
        return this.meta;
    }


    public String getUnlocalizedName()
    {
        return this.unlocalizedName;
    }

    public static EnumSupports byMetadata(int meta)
    {
        if (meta < 0 || meta >= META_LOOKUP.length) meta = 0;
        return META_LOOKUP[meta];
    }

    public String toString()
    {
        return this.unlocalizedName;
    }

    public String getName()
    {
        return this.name;
    }

    public Block getSupportBlock()
    {
        return supportBlock;
    }

    public Object getReagent()
    {
        return reagent;
    }

    static
    {
        EnumSupports[] var0 = values();
        for (EnumSupports var3 : var0) {
            META_LOOKUP[var3.getMetadata()] = var3;
        }
    }
}