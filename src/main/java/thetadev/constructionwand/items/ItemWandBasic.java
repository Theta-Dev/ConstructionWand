package thetadev.constructionwand.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class ItemWandBasic extends ItemWand
{
	public ItemWandBasic(Ingredient material, int durability, int maxBlocks, int angelDistance)
	{
		super(new Properties().maxDamage(durability), material, maxBlocks, angelDistance);
	}

	@Override
	public int getLimit(PlayerEntity player, ItemStack stack) {
		return Math.min(stack.getMaxDamage() - stack.getDamage(), maxBlocks);
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return material.test(repair);
	}
}
