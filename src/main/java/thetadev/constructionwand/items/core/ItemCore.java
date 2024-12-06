package thetadev.constructionwand.items.core;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IWandCore;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class ItemCore extends Item implements IWandCore
{
    public ItemCore(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack itemstack, Level worldIn, @Nonnull List<Component> lines, @Nonnull TooltipFlag extraInfo) {
        lines.add(
                Component.translatable(ConstructionWand.MODID + ".option.cores." + getRegistryName().toString() + ".desc")
                        .withStyle(ChatFormatting.GRAY)
        );
        lines.add(
                Component.translatable(ConstructionWand.MODID + ".tooltip.core_tip").withStyle(ChatFormatting.AQUA)
        );
    }
}
