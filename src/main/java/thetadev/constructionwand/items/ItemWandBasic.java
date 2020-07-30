package thetadev.constructionwand.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class ItemWandBasic extends ItemWand
{
	public ItemWandBasic(int durability, int maxBlocks, int angelDistance)
	{
		super(new Properties().maxDamage(durability), maxBlocks, angelDistance);
	}

	@Override
	public int getLimit(PlayerEntity player, ItemStack stack) {
		return Math.min(stack.getMaxDamage() - stack.getDamage(), maxBlocks);
	}
}
