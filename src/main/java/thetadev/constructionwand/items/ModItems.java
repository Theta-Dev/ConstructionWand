package thetadev.constructionwand.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.crafting.RecipeWandUpgrade;
import thetadev.constructionwand.items.core.ItemCoreAngel;
import thetadev.constructionwand.items.reservoir.ItemReservoirRandom;
import thetadev.constructionwand.items.wand.ItemWand;
import thetadev.constructionwand.items.wand.ItemWandBasic;
import thetadev.constructionwand.items.wand.ItemWandInfinity;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
    public static final Item WAND_STONE = new ItemWandBasic(itemprops(), "stone_wand", ItemTier.STONE);
    public static final Item WAND_IRON = new ItemWandBasic(itemprops(), "iron_wand", ItemTier.IRON);
    public static final Item WAND_DIAMOND = new ItemWandBasic(itemprops(), "diamond_wand", ItemTier.DIAMOND);
    public static final Item WAND_INFINITY = new ItemWandInfinity(itemprops(), "infinity_wand");

    public static final Item CORE_ANGEL = new ItemCoreAngel(unstackable(), "core_angel");

    public static final Item RESERVOIR_RANDOM = new ItemReservoirRandom(unstackable(), "reservoir_random");

    public static final Item[] WANDS = {WAND_STONE, WAND_IRON, WAND_DIAMOND, WAND_INFINITY};
    public static final Item[] UPGRADES = {CORE_ANGEL, RESERVOIR_RANDOM};

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        r.registerAll(WANDS);
        r.registerAll(UPGRADES);
    }

    public static Item.Properties itemprops() {
        return new Item.Properties().group(ItemGroup.TOOLS);
    }

    private static Item.Properties unstackable() {
        return itemprops().maxStackSize(1);
    }

    private static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, String name, IForgeRegistryEntry<V> thing) {
        reg.register(thing.setRegistryName(ConstructionWand.loc(name)));
    }

    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> r = event.getRegistry();
        register(r, "wand_upgrade", RecipeWandUpgrade.SERIALIZER);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerModelProperties() {
        for(Item item : WANDS) {
            ItemModelsProperties.func_239418_a_(
                    item, ConstructionWand.loc("using_core"),
                    (stack, world, entity) -> entity == null || !(stack.getItem() instanceof ItemWand) ? 0 :
                            new WandOptions(stack).cores.get().getColor() > -1 ? 1 : 0
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerItemColors() {
        ItemColors colors = Minecraft.getInstance().getItemColors();

        for(Item item : WANDS) {
            colors.register((stack, layer) -> (layer == 1 && stack.getItem() instanceof ItemWand) ?
                    new WandOptions(stack).cores.get().getColor() : -1, item);
        }
    }
}
