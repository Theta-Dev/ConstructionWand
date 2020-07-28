package thetadev.constructionwand.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemStack;

public class ItemWandBasic extends ItemWand
{
	public ItemWandBasic(int durability, int limit)
	{
		super(new Properties().maxDamage(durability));
		maxBlocks = limit;
	}

	@Override
	public int getLimit(PlayerEntity player, ItemStack stack) {
		return Math.min(stack.getMaxDamage() - stack.getDamage(), maxBlocks);
	}
}
