package thetadev.constructionwand.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.RecipeSerializer;
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
import thetadev.constructionwand.items.core.ItemCoreDestruction;
import thetadev.constructionwand.items.wand.ItemWand;
import thetadev.constructionwand.items.wand.ItemWandBasic;
import thetadev.constructionwand.items.wand.ItemWandInfinity;

import java.util.Arrays;
import java.util.HashSet;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
    // Wands
    public static final Item WAND_STONE = new ItemWandBasic("stone_wand", propWand(), Tiers.STONE);
    public static final Item WAND_IRON = new ItemWandBasic("iron_wand", propWand(), Tiers.IRON);
    public static final Item WAND_DIAMOND = new ItemWandBasic("diamond_wand", propWand(), Tiers.DIAMOND);
    public static final Item WAND_INFINITY = new ItemWandInfinity("infinity_wand", propWand());

    // Cores
    public static final Item CORE_ANGEL = new ItemCoreAngel("core_angel", propUpgrade());
    public static final Item CORE_DESTRUCTION = new ItemCoreDestruction("core_destruction", propUpgrade());

    // Collections
    public static final Item[] WANDS = {WAND_STONE, WAND_IRON, WAND_DIAMOND, WAND_INFINITY};
    public static final HashSet<Item> ALL_ITEMS = new HashSet<>();


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        r.registerAll(WANDS);
        ALL_ITEMS.addAll(Arrays.asList(WANDS));

        registerItem(r, CORE_ANGEL);
        registerItem(r, CORE_DESTRUCTION);
    }

    public static Item.Properties propWand() {
        return new Item.Properties().tab(CreativeModeTab.TAB_TOOLS);
    }

    private static Item.Properties propUpgrade() {
        return new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1);
    }

    private static void registerItem(IForgeRegistry<Item> reg, Item item) {
        reg.register(item);
        ALL_ITEMS.add(item);
    }

    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        IForgeRegistry<RecipeSerializer<?>> r = event.getRegistry();
        register(r, "wand_upgrade", RecipeWandUpgrade.SERIALIZER);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerModelProperties() {
        for(Item item : WANDS) {
            ItemProperties.register(
                    item, ConstructionWand.loc("using_core"),
                    (stack, world, entity, n) -> entity == null || !(stack.getItem() instanceof ItemWand) ? 0 :
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

    private static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, String name, IForgeRegistryEntry<V> thing) {
        reg.register(thing.setRegistryName(ConstructionWand.loc(name)));
    }
}
