package thetadev.constructionwand.crafting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import thetadev.constructionwand.ConstructionWand;

import java.util.function.Supplier;

public class ModRecipes
{

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ConstructionWand.MODID);

    public static final Supplier<SimpleCraftingRecipeSerializer<RecipeWandUpgrade>> WAND_UPGRADE = RECIPE_SERIALIZERS.register("wand_upgrade", () -> new SimpleCraftingRecipeSerializer<>(RecipeWandUpgrade::new));
}
