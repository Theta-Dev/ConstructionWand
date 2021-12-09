package thetadev.constructionwand.integrations.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

    private ITextComponent keyComboComponent(boolean shiftOpt, ITextComponent optkeyComponent) {
        String key = shiftOpt ? "sneak_opt" : "sneak";
        return new TranslationTextComponent(baseKey + "key." + key, optkeyComponent).mergeStyle(TextFormatting.BLUE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ITextComponent optkeyComponent = new TranslationTextComponent(InputMappings.getInputByCode(ConfigClient.OPT_KEY.get(), -1).getTranslationKey())
                .mergeStyle(TextFormatting.BLUE);
        ITextComponent wandModeComponent = keyComboComponent(ConfigClient.SHIFTOPT_MODE.get(), optkeyComponent);
        ITextComponent wandGuiComponent = keyComboComponent(ConfigClient.SHIFTOPT_GUI.get(), optkeyComponent);

        for(Item wand : ModItems.WANDS) {
            ConfigServer.WandProperties wandProperties = ConfigServer.getWandProperties(wand);

            String durabilityKey = wand == ModItems.WAND_INFINITY ? "unlimited" : "limited";
            ITextComponent durabilityComponent = new TranslationTextComponent(baseKey + "durability." + durabilityKey, wandProperties.getDurability());

            registration.addIngredientInfo(new ItemStack(wand), VanillaTypes.ITEM,
                    new TranslationTextComponent(baseKey + "wand",
                            new TranslationTextComponent(baseKeyItem + wand.getRegistryName().getPath()),
                            wandProperties.getLimit(), durabilityComponent,
                            optkeyComponent, wandModeComponent, wandGuiComponent)
            );
        }

        for(Item core : ModItems.CORES) {
            registration.addIngredientInfo(new ItemStack(core), VanillaTypes.ITEM,
                    new TranslationTextComponent(baseKey + core.getRegistryName().getPath()),
                    new TranslationTextComponent(baseKey + "core", wandModeComponent)
            );
        }
    }
}
