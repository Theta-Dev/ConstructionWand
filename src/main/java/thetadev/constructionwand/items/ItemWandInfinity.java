package thetadev.constructionwand.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.ConstructionWand;

public class ItemWandInfinity extends ItemWand
{
	public ItemWandInfinity(String name)
	{
		super(name, new Settings().maxCount(1).fireproof());
	}

	@Override
	public int getLimit(PlayerEntity player, ItemStack stack) {
		return player.isCreative() ? ConstructionWand.instance.config.LIMIT_CREATIVE : getLimit();
	}
}
