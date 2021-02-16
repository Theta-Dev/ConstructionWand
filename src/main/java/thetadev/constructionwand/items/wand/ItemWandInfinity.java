package thetadev.constructionwand.items.wand;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.basics.ConfigServer;

public class ItemWandInfinity extends ItemWand
{
    public ItemWandInfinity(String name, Properties properties) {
        super(name, properties.maxStackSize(1).isBurnable());
    }

    @Override
    public int getLimit(PlayerEntity player, ItemStack stack) {
        return player.isCreative() ? ConfigServer.LIMIT_CREATIVE.get() : getLimit();
    }
}
