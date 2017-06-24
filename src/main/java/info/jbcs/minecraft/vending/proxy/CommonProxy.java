package info.jbcs.minecraft.vending.proxy;

import info.jbcs.minecraft.vending.network.PacketDispatcher;
import info.jbcs.minecraft.vending.network.server.MessageAdvVenSetItem;
import info.jbcs.minecraft.vending.network.server.MessageSetLock;
import info.jbcs.minecraft.vending.network.server.MessageWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
    public void registerEventHandlers() {
    }

    public void registerPackets() {
        PacketDispatcher.registerMessage(MessageAdvVenSetItem.class);
        PacketDispatcher.registerMessage(MessageWrench.class);
        PacketDispatcher.registerMessage(MessageSetLock.class);
    }

    /**
     * Returns a side-appropriate EntityPlayer for use during message handling
     */
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }

    /**
     * Returns the current thread based on side during message handling,
     * used for ensuring that the message is being handled by the main thread
     */
    public IThreadListener getThreadFromContext(MessageContext ctx) {
        return ctx.getServerHandler().player.getServerWorld();
    }


    public void registerRenderers() {
    }

    public void registerCraftingRecipes() {
        /*
        Ingredient ingredient_glass = Ingredient.fromItem(Item.getItemFromBlock(Blocks.GLASS));
        Ingredient ingredient_gold_ingot = Ingredient.fromItem(Items.GOLD_INGOT);
        Ingredient ingredient_redstone = Ingredient.fromItem(Items.REDSTONE);
        Ingredient ingredient_repeater = Ingredient.fromItem(Items.REPEATER);
        Ingredient ingredient_dispenser = Ingredient.fromItem(Item.getItemFromBlock(Blocks.DISPENSER));
        NonNullList<Ingredient> ingredients = NonNullList.<Ingredient>create();
        for(int i =0; i<9; i++) ingredients.add(ingredient_glass);
        ingredients.set(4, ingredient_gold_ingot);

        for (int i = 0; i < EnumSupports.length; i++) {
            Ingredient ingredient_reagent = Ingredient.fromItem(EnumSupports.byMetadata(i).getReagent());
            ingredients.set(6, ingredient_reagent);
            ingredients.set(7, ingredient_redstone);
            ingredients.set(8, ingredient_reagent);
            ShapedRecipes recipe = new ShapedRecipes("", 3, 3,
                                        ingredients,
                                        new ItemStack(Vending.blockVendingMachine, 1, i));
            recipe.setRegistryName("vending_machine_"+EnumSupports.byMetadata(i).getName());
            ForgeRegistries.RECIPES.register(recipe);

            ingredients.set(7, ingredient_repeater);
            recipe = new ShapedRecipes("", 3, 3,
                    ingredients,
                    new ItemStack(Vending.blockAdvancedVendingMachine, 1, i));
            recipe.setRegistryName("advanced_vending_machine_"+EnumSupports.byMetadata(i).getName());
            ForgeRegistries.RECIPES.register(recipe);

            ingredients.set(7, ingredient_dispenser);
            recipe = new ShapedRecipes("", 3, 3,
                    ingredients,
                    new ItemStack(Vending.blockMultipleVendingMachine, 1, i));
            recipe.setRegistryName("multiple_vending_machine_"+EnumSupports.byMetadata(i).getName());
            ForgeRegistries.RECIPES.register(recipe);
        }
        */
    }
}
