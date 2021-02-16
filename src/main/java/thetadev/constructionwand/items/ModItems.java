package thetadev.constructionwand.items;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.*;
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
import thetadev.constructionwand.block.ModBlocks;
import thetadev.constructionwand.crafting.RecipeWandUpgrade;
import thetadev.constructionwand.items.core.ItemCoreAngel;
import thetadev.constructionwand.items.reservoir.ItemReservoirRandom;
import thetadev.constructionwand.items.wand.ItemWand;
import thetadev.constructionwand.items.wand.ItemWandBasic;
import thetadev.constructionwand.items.wand.ItemWandInfinity;

import java.util.Arrays;
import java.util.HashSet;

@Mod.EventBusSubscriber(modid = ConstructionWand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
    // Wands
    public static final Item WAND_STONE = new ItemWandBasic("stone_wand", itemprops(), ItemTier.STONE);
    public static final Item WAND_IRON = new ItemWandBasic("iron_wand", itemprops(), ItemTier.IRON);
    public static final Item WAND_DIAMOND = new ItemWandBasic("diamond_wand", itemprops(), ItemTier.DIAMOND);
    public static final Item WAND_INFINITY = new ItemWandInfinity("infinity_wand", itemprops());

    // Upgrades
    public static final Item CORE_ANGEL = new ItemCoreAngel("core_angel", unstackable());
    public static final Item RESERVOIR_RANDOM = new ItemReservoirRandom("reservoir_random", unstackable());

    // Collections
    public static final Item[] WANDS = {WAND_STONE, WAND_IRON, WAND_DIAMOND, WAND_INFINITY};
    public static final HashSet<Item> ALL_ITEMS = new HashSet<>();


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        r.registerAll(WANDS);
        ALL_ITEMS.addAll(Arrays.asList(WANDS));

        registerItem(r, CORE_ANGEL);
        registerItem(r, RESERVOIR_RANDOM);

        // BlockItems
        for(Block block : ModBlocks.ALL_BLOCKS) {
            BlockItem item = new BlockItem(block, itemprops());
            item.setRegistryName(block.getRegistryName());
            registerItem(r, item);
        }
    }

    public static Item.Properties itemprops() {
        return new Item.Properties().group(ItemGroup.TOOLS);
    }

    private static Item.Properties unstackable() {
        return itemprops().maxStackSize(1);
    }

    private static void registerItem(IForgeRegistry<Item> reg, Item item) {
        reg.register(item);
        ALL_ITEMS.add(item);
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

    private static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, String name, IForgeRegistryEntry<V> thing) {
        reg.register(thing.setRegistryName(ConstructionWand.loc(name)));
    }
}
