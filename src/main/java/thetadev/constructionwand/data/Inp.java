package thetadev.constructionwand.data;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;

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

    public static Inp fromItem(IItemProvider in) {
        return new Inp(in.asItem().getRegistryName().getPath(), Ingredient.fromItems(in), ItemPredicate.Builder.create().item(in).build());
    }
    public static Inp fromTag(ITag.INamedTag<Item> in) {
        return new Inp(in.getName().getPath(), Ingredient.fromTag(in), ItemPredicate.Builder.create().tag(in).build());
    }
}