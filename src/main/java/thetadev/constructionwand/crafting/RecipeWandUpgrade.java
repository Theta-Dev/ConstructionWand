package thetadev.constructionwand.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thetadev.constructionwand.api.IWandUpgrade;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.items.wand.ItemWand;

import javax.annotation.Nonnull;

public class RecipeWandUpgrade extends SpecialRecipe
{
    public static final SpecialRecipeSerializer<RecipeWandUpgrade> SERIALIZER = new SpecialRecipeSerializer<>(RecipeWandUpgrade::new);

    public RecipeWandUpgrade(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World worldIn) {
        ItemStack wand = null;
        IWandUpgrade upgrade = null;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(wand == null && stack.getItem() instanceof ItemWand) wand = stack;
                else
                    if(upgrade == null && stack.getItem() instanceof IWandUpgrade)
                        upgrade = (IWandUpgrade) stack.getItem();
                    else return false;
            }
        }

        if(wand == null || upgrade == null) return false;
        return !new WandOptions(wand).hasUpgrade(upgrade);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
        ItemStack wand = null;
        IWandUpgrade upgrade = null;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() instanceof ItemWand) wand = stack;
                else
                    if(stack.getItem() instanceof IWandUpgrade) upgrade = (IWandUpgrade) stack.getItem();
            }
        }

        if(wand == null || upgrade == null) return ItemStack.EMPTY;

        ItemStack newWand = wand.copy();
        new WandOptions(newWand).addUpgrade(upgrade);
        return newWand;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
