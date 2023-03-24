package thetadev.constructionwand.integrations.jei;

import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.ConfigClient;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.items.ModItems;

import javax.annotation.Nonnull;

@JeiPlugin
public class ConstructionWandJeiPlugin implements IModPlugin
{
    private static final ResourceLocation pluginId = new ResourceLocation(ConstructionWand.MODID, ConstructionWand.MODID);
    private static final String baseKey = ConstructionWand.MODID + ".description.";
    private static final String baseKeyItem = "item." + ConstructionWand.MODID + ".";

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return pluginId;
    }

    private Component keyComboComponent(boolean shiftOpt, Component optkeyComponent) {
        String key = shiftOpt ? "sneak_opt" : "sneak";
        return Component.translatable(baseKey + "key." + key, optkeyComponent).withStyle(ChatFormatting.BLUE);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        Component optkeyComponent = Component.translatable(InputConstants.getKey(ConfigClient.OPT_KEY.get(), -1).getName())
                .withStyle(ChatFormatting.BLUE);
        Component wandModeComponent = keyComboComponent(ConfigClient.SHIFTOPT_MODE.get(), optkeyComponent);
        Component wandGuiComponent = keyComboComponent(ConfigClient.SHIFTOPT_GUI.get(), optkeyComponent);

        for(RegistryObject<Item> wandSupplier : ModItems.WANDS) {
            Item wand = wandSupplier.get();
            ConfigServer.WandProperties wandProperties = ConfigServer.getWandProperties(wand);

            String durabilityKey = wand == ModItems.WAND_INFINITY.get() ? "unlimited" : "limited";
            Component durabilityComponent = Component.translatable(baseKey + "durability." + durabilityKey, wandProperties.getDurability());

            registration.addIngredientInfo(new ItemStack(wand), VanillaTypes.ITEM_STACK,
                    Component.translatable(baseKey + "wand",
                            Component.translatable(baseKeyItem + ForgeRegistries.ITEMS.getKey(wand).getPath()),
                            wandProperties.getLimit(), durabilityComponent, optkeyComponent, wandModeComponent, wandGuiComponent)
            );
        }

        for(RegistryObject<Item> coreSupplier : ModItems.CORES) {
            Item core = coreSupplier.get();
            registration.addIngredientInfo(new ItemStack(core), VanillaTypes.ITEM_STACK,
                    Component.translatable(baseKey + ForgeRegistries.ITEMS.getKey(core).getPath())
                            .append("\n\n")
                            .append(Component.translatable(baseKey + "core", wandModeComponent))
            );
        }
    }
}
