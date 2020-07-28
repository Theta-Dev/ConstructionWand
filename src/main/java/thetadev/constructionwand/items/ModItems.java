package thetadev.constructionwand.items;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thetadev.constructionwand.ConfigHandler;
import thetadev.constructionwand.ConstructionWand;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
				register(new ItemWandBasic(ConfigHandler.DURABILITY_STONE.get(), ConfigHandler.LIMIT_STONE.get()), "stone_wand"),
				register(new ItemWandBasic(ConfigHandler.DURABILITY_IRON.get(), ConfigHandler.LIMIT_IRON.get()), "iron_wand"),
				register(new ItemWandBasic(ConfigHandler.DURABILITY_DIAMOND.get(), ConfigHandler.LIMIT_DIAMOND.get()), "diamond_wand"),
				register(new ItemWandInfinity(ConfigHandler.LIMIT_INFINITY.get()), "infinity_wand")
		);
	}

	public static <T extends IForgeRegistryEntry<T>> T register(final T entry, final String name) {
		return register(entry, new ResourceLocation(ConstructionWand.MODID, name));
	}

	public static <T extends IForgeRegistryEntry<T>> T register(final T entry, final ResourceLocation registryName) {
		entry.setRegistryName(registryName);
		return entry;
	}
}