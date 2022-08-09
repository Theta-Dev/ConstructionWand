package thetadev.constructionwand.items;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.crafting.RecipeWandUpgrade;
import thetadev.constructionwand.items.core.ItemCoreAngel;
import thetadev.constructionwand.items.core.ItemCoreDestruction;
import thetadev.constructionwand.items.wand.ItemWand;
import thetadev.constructionwand.items.wand.ItemWandBasic;
import thetadev.constructionwand.items.wand.ItemWandInfinity;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ConstructionWand.MODID);

    // Wands
    public static final RegistryObject<Item> WAND_STONE = ITEMS.register("stone_wand", () -> new ItemWandBasic(propWand(), Tiers.STONE));
    public static final RegistryObject<Item> WAND_IRON = ITEMS.register("iron_wand", () -> new ItemWandBasic(propWand(), Tiers.IRON));
    public static final RegistryObject<Item> WAND_DIAMOND = ITEMS.register("diamond_wand", () -> new ItemWandBasic(propWand(), Tiers.DIAMOND));
    public static final RegistryObject<Item> WAND_INFINITY = ITEMS.register("infinity_wand", () -> new ItemWandInfinity(propWand()));

    // Cores
    public static final RegistryObject<Item> CORE_ANGEL = ITEMS.register("core_angel", () -> new ItemCoreAngel(propUpgrade()));
    public static final RegistryObject<Item> CORE_DESTRUCTION = ITEMS.register("core_destruction", () -> new ItemCoreDestruction(propUpgrade()));

    // Collections
    public static final RegistryObject<Item>[] WANDS = new RegistryObject[] {WAND_STONE, WAND_IRON, WAND_DIAMOND, WAND_INFINITY};
    public static final RegistryObject<Item>[] CORES = new RegistryObject[] {CORE_ANGEL, CORE_DESTRUCTION};

    public static Item.Properties propWand() {
        return new Item.Properties().tab(CreativeModeTab.TAB_TOOLS);
    }

    private static Item.Properties propUpgrade() {
        return new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1);
    }

    @SubscribeEvent
    public static void registerRecipeSerializers(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, registry -> {
            registry.register("wand_upgrade", RecipeWandUpgrade.SERIALIZER);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerModelProperties() {
        for(RegistryObject<Item> itemSupplier : WANDS) {
            Item item = itemSupplier.get();
            ItemProperties.register(
                    item, ConstructionWand.loc("using_core"),
                    (stack, world, entity, n) -> entity == null || !(stack.getItem() instanceof ItemWand) ? 0 :
                            new WandOptions(stack).cores.get().getColor() > -1 ? 1 : 0
            );
        }
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for(RegistryObject<Item> itemSupplier : WANDS) {
            Item item = itemSupplier.get();
            event.register((stack, layer) -> (layer == 1 && stack.getItem() instanceof ItemWand) ?
                    new WandOptions(stack).cores.get().getColor() : -1, item);
        }
    }
}
