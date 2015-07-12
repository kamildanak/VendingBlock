package info.jbcs.minecraft.vending.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemMetaBlock extends ItemBlock {
	public ItemMetaBlock(Block b) {
		super(b);
		setMaxDamage(0);
		setHasSubtypes(true);
	}
	@Override
	public int getMetadata(int i) {
		return i;
	}
}
