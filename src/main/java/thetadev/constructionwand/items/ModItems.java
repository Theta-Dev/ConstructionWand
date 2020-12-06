package thetadev.constructionwand.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thetadev.constructionwand.ConstructionWand;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
	public static final Item WAND_STONE = new ItemWandBasic("stone_wand", ItemTier.STONE);
	public static final Item WAND_IRON = new ItemWandBasic("iron_wand", ItemTier.IRON);
	public static final Item WAND_DIAMOND = new ItemWandBasic("diamond_wand", ItemTier.DIAMOND);
	public static final Item WAND_INFINITY = new ItemWandInfinity("infinity_wand");

	public static final Item[] WANDS = {WAND_STONE, WAND_IRON, WAND_DIAMOND, WAND_INFINITY};

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(WANDS);
	}
}
