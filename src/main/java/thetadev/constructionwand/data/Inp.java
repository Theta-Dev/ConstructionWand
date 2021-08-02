package thetadev.constructionwand.data;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.tags.Tag;
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
        return new Inp(in.asItem().getRegistryName().getPath(), Ingredient.of(in), ItemPredicate.Builder.item().of(in).build());
    }

    public static Inp fromTag(Tag.Named<Item> in) {
        return new Inp(in.getName().getPath(), Ingredient.of(in), ItemPredicate.Builder.item().of(in).build());
    }
}