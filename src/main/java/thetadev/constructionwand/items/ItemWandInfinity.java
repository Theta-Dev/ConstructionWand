package thetadev.constructionwand.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.basics.ConfigServer;

public class ItemWandInfinity extends ItemWand
{
	public ItemWandInfinity(String name)
	{
		super(name, new Settings().maxCount(1).fireproof());
	}

	@Override
	public int getLimit(PlayerEntity player, ItemStack stack) {
		return player.isCreative() ? ConfigServer.LIMIT_CREATIVE.get() : getLimit();
	}
}
