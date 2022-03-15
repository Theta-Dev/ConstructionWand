package thetadev.constructionwand.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.crafting.RecipeWandUpgrade;
import thetadev.constructionwand.items.ModItems;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider
{
    public RecipeGenerator(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
        wandRecipe(consumer, ModItems.WAND_STONE.get(), Inp.fromTag(ItemTags.STONE_TOOL_MATERIALS));
        wandRecipe(consumer, ModItems.WAND_IRON.get(), Inp.fromTag(Tags.Items.INGOTS_IRON));
        wandRecipe(consumer, ModItems.WAND_DIAMOND.get(), Inp.fromTag(Tags.Items.GEMS_DIAMOND));
        wandRecipe(consumer, ModItems.WAND_INFINITY.get(), Inp.fromTag(Tags.Items.NETHER_STARS));

        coreRecipe(consumer, ModItems.CORE_ANGEL.get(), Inp.fromTag(Tags.Items.FEATHERS), Inp.fromTag(Tags.Items.INGOTS_GOLD));
        coreRecipe(consumer, ModItems.CORE_DESTRUCTION.get(), Inp.fromTag(Tags.Items.STORAGE_BLOCKS_DIAMOND), Inp.fromItem(Items.DIAMOND_PICKAXE));

        specialRecipe(consumer, RecipeWandUpgrade.SERIALIZER);
    }

    private void wandRecipe(Consumer<FinishedRecipe> consumer, ItemLike wand, Inp material) {
        ShapedRecipeBuilder.shaped(wand)
                .define('X', material.ingredient)
                .define('#', Tags.Items.RODS_WOODEN)
                .pattern("  X")
                .pattern(" # ")
                .pattern("#  ")
                .unlockedBy("has_item", inventoryTrigger(material.predicate))
                .save(consumer);
    }

    private void coreRecipe(Consumer<FinishedRecipe> consumer, ItemLike core, Inp item1, Inp item2) {
        ShapedRecipeBuilder.shaped(core)
                .define('O', item1.ingredient)
                .define('X', item2.ingredient)
                .define('#', Tags.Items.GLASS_PANES)
                .pattern(" #X")
                .pattern("#O#")
                .pattern("X# ")
                .unlockedBy("has_item", inventoryTrigger(item1.predicate))
                .save(consumer);
    }

    private void specialRecipe(Consumer<FinishedRecipe> consumer, SimpleRecipeSerializer<?> serializer) {
        ResourceLocation name = ForgeRegistries.RECIPE_SERIALIZERS.getKey(serializer);
        SpecialRecipeBuilder.special(serializer).save(consumer, ConstructionWand.loc("dynamic/" + name.getPath()).toString());
    }

    @Nonnull
    @Override
    public String getName() {
        return ConstructionWand.MODID + " crafting recipes";
    }
}