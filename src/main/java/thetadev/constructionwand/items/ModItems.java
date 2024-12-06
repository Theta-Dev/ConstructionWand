package thetadev.constructionwand.items;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.items.core.ItemCoreAngel;
import thetadev.constructionwand.items.core.ItemCoreDestruction;
import thetadev.constructionwand.items.wand.ItemWand;
import thetadev.constructionwand.items.wand.ItemWandBasic;
import thetadev.constructionwand.items.wand.ItemWandInfinity;

@EventBusSubscriber(modid = ConstructionWand.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModItems
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ConstructionWand.MODID);

    // Wands
    public static final DeferredItem<Item> WAND_STONE = ITEMS.register("stone_wand", () -> new ItemWandBasic(propWand(), Tiers.STONE));
    public static final DeferredItem<Item> WAND_IRON = ITEMS.register("iron_wand", () -> new ItemWandBasic(propWand(), Tiers.IRON));
    public static final DeferredItem<Item> WAND_DIAMOND = ITEMS.register("diamond_wand", () -> new ItemWandBasic(propWand(), Tiers.DIAMOND));
    public static final DeferredItem<Item> WAND_INFINITY = ITEMS.register("infinity_wand", () -> new ItemWandInfinity(propWand()));

    // Cores
    public static final DeferredItem<Item> CORE_ANGEL = ITEMS.register("core_angel", () -> new ItemCoreAngel(propUpgrade()));
    public static final DeferredItem<Item> CORE_DESTRUCTION = ITEMS.register("core_destruction", () -> new ItemCoreDestruction(propUpgrade()));

    // Collections
    public static final DeferredItem<Item>[] WANDS = new DeferredItem[] {WAND_STONE, WAND_IRON, WAND_DIAMOND, WAND_INFINITY};
    public static final DeferredItem<Item>[] CORES = new DeferredItem[] {CORE_ANGEL, CORE_DESTRUCTION};

    public static Item.Properties propWand() {
        return new Item.Properties();
    }

    private static Item.Properties propUpgrade() {
        return new Item.Properties().stacksTo(1);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerModelProperties() {
        for(DeferredItem<Item> itemSupplier : WANDS) {
            Item item = itemSupplier.get();
            ItemProperties.register(
                    item, ConstructionWand.loc("using_core"),
                    (stack, world, entity, n) -> entity == null || !(stack.getItem() instanceof ItemWand) ? 0 :
                            new WandOptions(stack).cores.get().getColor() > -1 ? 1 : 0
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for(DeferredItem<Item> itemSupplier : WANDS) {
            Item item = itemSupplier.get();
            event.register((stack, layer) -> (layer == 1 && stack.getItem() instanceof ItemWand) ?
                    new WandOptions(stack).cores.get().getColor() : -1, item);
        }
    }

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for(DeferredItem<Item> itemSupplier : WANDS) {
                event.accept(itemSupplier);
            }
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            for(DeferredItem<Item> itemSupplier : CORES) {
                event.accept(itemSupplier);
            }
        }
    }
}
