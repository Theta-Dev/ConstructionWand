package thetadev.constructionwand.items;

import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.registry.Registry;
import thetadev.constructionwand.ConstructionWand;

public class ModItems
{
	public static final ItemWand WAND_STONE = new ItemWandBasic("stone_wand", ToolMaterials.STONE);
	public static final ItemWand WAND_IRON = new ItemWandBasic("iron_wand", ToolMaterials.IRON);
	public static final ItemWand WAND_DIAMOND = new ItemWandBasic("diamond_wand", ToolMaterials.DIAMOND);
	public static final ItemWand WAND_INFINITY = new ItemWandInfinity("infinity_wand");

	public static final ItemWand[] WANDS = {WAND_STONE, WAND_IRON, WAND_DIAMOND, WAND_INFINITY};

	public static void register() {
		for(ItemWand wand : WANDS) {
			Registry.register(Registry.ITEM, ConstructionWand.loc(wand.name), wand);
		}
	}

	public static void registerModelProperties() {
		for(Item item : WANDS) {
			FabricModelPredicateProviderRegistry.register(
					item, ConstructionWand.loc("wand_mode"),
					(stack, world, entity) -> entity == null || !(stack.getItem() instanceof ItemWand) ? 0 : ItemWand.getWandMode(stack)
			);
		}
	}
}
