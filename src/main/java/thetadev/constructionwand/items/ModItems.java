package thetadev.constructionwand.items;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thetadev.constructionwand.basics.ConfigHandler;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.WandUtil;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
	public static final Item WAND_INFINITY = new ItemWandInfinity(ingFromTag("forge:nether_stars"), ConfigHandler.LIMIT_INFINITY.get(), ConfigHandler.ANGEL_INFINITY.get());
	public static final Item WAND_DIAMOND = new ItemWandBasic(ingFromTag("forge:gems/diamond"), ConfigHandler.DURABILITY_DIAMOND.get(), ConfigHandler.LIMIT_DIAMOND.get(), ConfigHandler.ANGEL_DIAMOND.get());
	public static final Item WAND_IRON = new ItemWandBasic(ingFromTag("forge:ingots/iron"), ConfigHandler.DURABILITY_IRON.get(), ConfigHandler.LIMIT_IRON.get(), ConfigHandler.ANGEL_IRON.get());
	public static final Item WAND_STONE = new ItemWandBasic(ingFromTag("forge:cobblestone"), ConfigHandler.DURABILITY_STONE.get(), ConfigHandler.LIMIT_STONE.get(), ConfigHandler.ANGEL_STONE.get());

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
				register(WAND_STONE, "stone_wand"),
				register(WAND_IRON, "iron_wand"),
				register(WAND_DIAMOND, "diamond_wand"),
				register(WAND_INFINITY, "infinity_wand")
		);
	}

	public static <T extends IForgeRegistryEntry<T>> T register(final T entry, final String name) {
		return register(entry, new ResourceLocation(ConstructionWand.MODID, name));
	}

	public static <T extends IForgeRegistryEntry<T>> T register(final T entry, final ResourceLocation registryName) {
		entry.setRegistryName(registryName);
		return entry;
	}

	public static Ingredient ingFromTag(String tag) {
		return Ingredient.fromTag(new ItemTags.Wrapper(new ResourceLocation(tag)));
	}
}
