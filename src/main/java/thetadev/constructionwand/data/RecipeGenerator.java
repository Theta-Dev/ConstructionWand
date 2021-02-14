package thetadev.constructionwand.data;

import net.minecraft.data.*;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.crafting.RecipeWandUpgrade;
import thetadev.constructionwand.items.ModItems;

import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider
{
    public RecipeGenerator(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        wandRecipe(consumer, ModItems.WAND_STONE, Inp.fromTag(ItemTags.field_232909_aa_)); //stone_tool_materials
        wandRecipe(consumer, ModItems.WAND_IRON, Inp.fromTag(Tags.Items.INGOTS_IRON));
        wandRecipe(consumer, ModItems.WAND_DIAMOND, Inp.fromTag(Tags.Items.GEMS_DIAMOND));
        wandRecipe(consumer, ModItems.WAND_INFINITY, Inp.fromTag(Tags.Items.NETHER_STARS));

        specialRecipe(consumer, RecipeWandUpgrade.SERIALIZER);
    }

    private void wandRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider wand, Inp material) {
        ShapedRecipeBuilder.shapedRecipe(wand)
                .key('X', material.ingredient)
                .key('#', Tags.Items.RODS_WOODEN)
                .patternLine("  X")
                .patternLine(" # ")
                .patternLine("#  ")
                .addCriterion("has_item", hasItem(material.predicate))
                .build(consumer);
    }

    private void specialRecipe(Consumer<IFinishedRecipe> consumer, SpecialRecipeSerializer<?> serializer) {
        ResourceLocation name = Registry.RECIPE_SERIALIZER.getKey(serializer);
        CustomRecipeBuilder.customRecipe(serializer).build(consumer, ConstructionWand.loc("dynamic/" + name.getPath()).toString());
    }

    @Override
    public String getName() {
        return ConstructionWand.MODID + " crafting recipes";
    }
}