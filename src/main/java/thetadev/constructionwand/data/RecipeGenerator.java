package thetadev.constructionwand.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import thetadev.constructionwand.ConstructionWand;
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

    @Override
    public String getName() {
        return ConstructionWand.MODID + " crafting recipes";
    }
}