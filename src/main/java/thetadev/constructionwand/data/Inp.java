package thetadev.constructionwand.data;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class Inp
{
    public final String name;
    public final Ingredient ingredient;
    public final ItemPredicate predicate;

    public Inp(String name, Ingredient ingredient, ItemPredicate predicate) {
        this.name = name;
        this.ingredient = ingredient;
        this.predicate = predicate;
    }

    public static Inp fromItem(ItemLike in) {
        return new Inp(BuiltInRegistries.ITEM.getKey(in.asItem()).getPath(), Ingredient.of(in), ItemPredicate.Builder.item().of(in).build());
    }

    public static Inp fromTag(TagKey<Item> in) {
        return new Inp(in.location().getPath(), Ingredient.of(in), ItemPredicate.Builder.item().of(in).build());
    }
}