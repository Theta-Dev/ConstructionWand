package thetadev.constructionwand.basics;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import thetadev.constructionwand.items.ItemWand;
import thetadev.constructionwand.job.ConstructionJob;
import thetadev.constructionwand.job.TransductionJob;
import thetadev.constructionwand.job.WandJob;

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

	public static Ingredient ingFromTag(String tag) {
		return Ingredient.fromTag(new ItemTags.Wrapper(new ResourceLocation(tag)));
	}

	public static WandJob getJob(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, ItemStack itemStack) {
		IEnumOption mode = new WandOptions(itemStack).getOption(EnumMode.DEFAULT);

		if(mode == EnumMode.ANGEL) return new TransductionJob(player, world, rayTraceResult, itemStack);
		else return new ConstructionJob(player, world, rayTraceResult, itemStack);
	}
}
