package thetadev.constructionwand.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.ConfigHandler;
import thetadev.constructionwand.ConstructionWand;

public class ItemWandInfinity extends ItemWand
{
	public ItemWandInfinity(int limit)
	{
		super(new Item.Properties().maxStackSize(1));
		maxBlocks = limit;
	}

	@Override
	public int getLimit(PlayerEntity player, ItemStack stack) {
		return player.isCreative() ? ConfigHandler.LIMIT_CREATIVE.get() : maxBlocks;
	}
}
