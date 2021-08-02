package thetadev.constructionwand.items.core;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
    public void appendHoverText(@Nonnull ItemStack itemstack, Level worldIn, @Nonnull List<Component> lines, @Nonnull TooltipFlag extraInfo) {
        lines.add(new TranslatableComponent(ConstructionWand.MODID + ".option.cores." + getRegistryName().toString() + ".desc")
                .withStyle(ChatFormatting.GRAY));
        lines.add(new TranslatableComponent(ConstructionWand.MODID + ".tooltip.core_tip")
                .withStyle(ChatFormatting.AQUA));
    }
}
