package thetadev.constructionwand.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thetadev.constructionwand.api.IWandUpgrade;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.items.wand.ItemWand;

import javax.annotation.Nonnull;

public class RecipeWandUpgrade extends CustomRecipe
{
    public RecipeWandUpgrade(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@Nonnull CraftingInput inv, @Nonnull Level worldIn) {
        ItemStack wand = null;
        IWandUpgrade upgrade = null;

        for(int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getItem(i);
            if(!stack.isEmpty()) {
                if(wand == null && stack.getItem() instanceof ItemWand) wand = stack;
                else if(upgrade == null && stack.getItem() instanceof IWandUpgrade)
                    upgrade = (IWandUpgrade) stack.getItem();
                else return false;
            }
        }

        if(wand == null || upgrade == null) return false;
        return !new WandOptions(wand).hasUpgrade(upgrade) && ConfigServer.getWandProperties(wand.getItem()).isUpgradeable();
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingInput input, HolderLookup.Provider registries) {
        ItemStack wand = null;
        IWandUpgrade upgrade = null;

        for(int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() instanceof ItemWand) wand = stack;
                else if(stack.getItem() instanceof IWandUpgrade) upgrade = (IWandUpgrade) stack.getItem();
            }
        }

        if(wand == null || upgrade == null) return ItemStack.EMPTY;

        ItemStack newWand = wand.copy();
        new WandOptions(newWand).addUpgrade(upgrade);
        return newWand;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WAND_UPGRADE.get();
    }
}
