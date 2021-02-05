package thetadev.constructionwand.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemTier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

	@OnlyIn(Dist.CLIENT)
	public static void registerModelProperties() {
		for(Item item : WANDS) {
			ItemModelsProperties.func_239418_a_(
					item, ConstructionWand.loc("using_core"),
					(stack, world, entity) -> entity == null || !(stack.getItem() instanceof ItemWand) ? 0 : ItemWand.getWandMode(stack)
			);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerItemColors() {
		ItemColors colors = Minecraft.getInstance().getItemColors();

		for(Item item : WANDS) {
			colors.register((stack, layer) -> layer == 1 ? 0xFF0000 : -1, item);
		}
	}
}
