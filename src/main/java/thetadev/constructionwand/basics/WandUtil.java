package thetadev.constructionwand.basics;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import thetadev.constructionwand.items.ItemWand;

public class WandUtil
{
	public static boolean stackEquals(ItemStack stackA, ItemStack stackB) {
		return ItemStack.areItemsEqual(stackA, stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
	}

	public static boolean stackEquals(ItemStack stackA, Item item) {
		ItemStack stackB = new ItemStack(item);
		return stackEquals(stackA, stackB);
	}

	public static ItemStack holdingWand(PlayerEntity player) {
		if(player.getHeldItem(Hand.MAIN_HAND) != ItemStack.EMPTY && player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof ItemWand) {
			return player.getHeldItem(Hand.MAIN_HAND);
		}
		else if(player.getHeldItem(Hand.OFF_HAND) != ItemStack.EMPTY && player.getHeldItem(Hand.OFF_HAND).getItem() instanceof ItemWand) {
			return player.getHeldItem(Hand.OFF_HAND);
		}
		return null;
	}

	public static int range(int in, int min, int max) {
		return Math.min(Math.max(in, min), max);
	}
}
