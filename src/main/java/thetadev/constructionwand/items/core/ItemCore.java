package thetadev.constructionwand.items.core;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IWandCore;
import thetadev.constructionwand.items.ItemBase;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class ItemCore extends ItemBase implements IWandCore
{
    public ItemCore(String name, Properties properties) {
        super(name, properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack itemstack, World worldIn, @Nonnull List<ITextComponent> lines, @Nonnull ITooltipFlag extraInfo) {
        lines.add(new TranslationTextComponent(ConstructionWand.MODID + ".option.cores." + getRegistryName().toString() + ".desc")
                .applyTextStyle(TextFormatting.GRAY));
        lines.add(new TranslationTextComponent(ConstructionWand.MODID + ".tooltip.core_tip")
                .applyTextStyle(TextFormatting.AQUA));
    }
}
