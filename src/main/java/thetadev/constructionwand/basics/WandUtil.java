package thetadev.constructionwand.basics;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.options.EnumMode;
import thetadev.constructionwand.basics.options.IEnumOption;
import thetadev.constructionwand.basics.options.WandOptions;
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

	public static Vec3d entityPositionVec(Entity entity) {
		return new Vec3d(entity.getPosX(), entity.getPosY() - entity.getYOffset() + entity.getHeight()/2, entity.getPosZ());
	}
}
