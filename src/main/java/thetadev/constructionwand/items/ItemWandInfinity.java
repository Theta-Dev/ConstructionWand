package thetadev.constructionwand.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thetadev.constructionwand.basics.ConfigHandler;

public class ItemWandInfinity extends ItemWand
{
	public ItemWandInfinity(int maxBlocks, int angelDistance)
	{
		//func_234689_a_(): Dont burn like Netherite
		super(new Item.Properties().maxStackSize(1).isBurnable(), maxBlocks, angelDistance);
	}

	@Override
	public int getLimit(PlayerEntity player, ItemStack stack) {
		return player.isCreative() ? ConfigHandler.LIMIT_CREATIVE.get() : maxBlocks;
	}
}
