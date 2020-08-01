package thetadev.constructionwand.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import thetadev.constructionwand.basics.ConfigHandler;

public class ItemWandInfinity extends ItemWand
{
	public ItemWandInfinity(Ingredient material, int maxBlocks, int angelDistance)
	{
		super(new Item.Properties().maxStackSize(1), material, maxBlocks, angelDistance);
	}

	@Override
	public int getLimit(PlayerEntity player, ItemStack stack) {
		return player.isCreative() ? ConfigHandler.LIMIT_CREATIVE.get() : maxBlocks;
	}
}
