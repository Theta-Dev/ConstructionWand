package thetadev.constructionwand.items.wand;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.basics.ConfigServer;

public class ItemWandInfinity extends ItemWand
{
    public ItemWandInfinity(Properties properties, String name) {
        super(properties.maxStackSize(1).isBurnable(), name);
    }

    @Override
    public int getLimit(PlayerEntity player, ItemStack stack) {
        return player.isCreative() ? ConfigServer.LIMIT_CREATIVE.get() : getLimit();
    }
}
