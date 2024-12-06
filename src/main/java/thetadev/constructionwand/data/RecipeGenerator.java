package thetadev.constructionwand.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.crafting.ModRecipes;
import thetadev.constructionwand.crafting.RecipeWandUpgrade;
import thetadev.constructionwand.items.ModItems;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider provider) {
        wandRecipe(output, ModItems.WAND_STONE.get(), Inp.fromTag(ItemTags.STONE_TOOL_MATERIALS));
        wandRecipe(output, ModItems.WAND_IRON.get(), Inp.fromTag(Tags.Items.INGOTS_IRON));
        wandRecipe(output, ModItems.WAND_DIAMOND.get(), Inp.fromTag(Tags.Items.GEMS_DIAMOND));
        wandRecipe(output, ModItems.WAND_INFINITY.get(), Inp.fromTag(Tags.Items.NETHER_STARS));

        coreRecipe(output, ModItems.CORE_ANGEL.get(), Inp.fromTag(Tags.Items.FEATHERS), Inp.fromTag(Tags.Items.INGOTS_GOLD));
        coreRecipe(output, ModItems.CORE_DESTRUCTION.get(), Inp.fromTag(Tags.Items.STORAGE_BLOCKS_DIAMOND), Inp.fromItem(Items.DIAMOND_PICKAXE));

        specialRecipe(output, ModRecipes.WAND_UPGRADE.get());
    }

    private void wandRecipe(RecipeOutput output, ItemLike wand, Inp material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, wand)
                .define('X', material.ingredient)
                .define('#', Tags.Items.RODS_WOODEN)
                .pattern("  X")
                .pattern(" # ")
                .pattern("#  ")
                .unlockedBy("has_item", inventoryTrigger(material.predicate))
                .save(output);
    }

    private void coreRecipe(RecipeOutput output, ItemLike core, Inp item1, Inp item2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, core)
                .define('O', item1.ingredient)
                .define('X', item2.ingredient)
                .define('#', Tags.Items.GLASS_PANES)
                .pattern(" #X")
                .pattern("#O#")
                .pattern("X# ")
                .unlockedBy("has_item", inventoryTrigger(item1.predicate))
                .save(output);
    }

    private void specialRecipe(RecipeOutput output, SimpleCraftingRecipeSerializer<?> serializer) {
        ResourceLocation name = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
        SpecialRecipeBuilder.special(RecipeWandUpgrade::new).save(output, ConstructionWand.loc("dynamic/" + name.getPath()).toString());
    }
}