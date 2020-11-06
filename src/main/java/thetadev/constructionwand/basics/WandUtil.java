package thetadev.constructionwand.basics;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thetadev.constructionwand.items.ItemWand;

import java.util.ArrayList;
import java.util.List;

public class WandUtil
{
	public static boolean stackEquals(ItemStack stackA, ItemStack stackB) {
		return ItemStack.areItemsEqual(stackA, stackB) && ItemStack.areTagsEqual(stackA, stackB);
	}

	public static boolean stackEquals(ItemStack stackA, Item item) {
		ItemStack stackB = new ItemStack(item);
		return stackEquals(stackA, stackB);
	}

	public static ItemStack holdingWand(PlayerEntity player) {
		if(player.getStackInHand(Hand.MAIN_HAND) != ItemStack.EMPTY && player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ItemWand) {
			return player.getStackInHand(Hand.MAIN_HAND);
		}
		else if(player.getStackInHand(Hand.OFF_HAND) != ItemStack.EMPTY && player.getStackInHand(Hand.OFF_HAND).getItem() instanceof ItemWand) {
			return player.getStackInHand(Hand.OFF_HAND);
		}
		return null;
	}

	public static BlockPos playerPos(PlayerEntity player) {
		return new BlockPos(player.getPos());
	}
	
	public static Vec3d entityPositionVec(Entity entity) {
		return new Vec3d(entity.getX(), entity.getY() - entity.getHeightOffset() + entity.getHeight()/2, entity.getZ());
	}

	public static Vec3d blockPosVec(BlockPos pos) {
		return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
	}

	public static List<ItemStack> getHotbar(PlayerEntity player) {
		return player.inventory.main.subList(0, 9);
	}

	public static List<ItemStack> getHotbarWithOffhand(PlayerEntity player) {
		ArrayList<ItemStack> inventory = new ArrayList<>(player.inventory.offHand);
		inventory.addAll(player.inventory.main.subList(0, 9));
		return inventory;
	}

	public static List<ItemStack> getMainInv(PlayerEntity player) {
		return player.inventory.main.subList(9, player.inventory.main.size());
	}

	public static List<ItemStack> getFullInv(PlayerEntity player) {
		ArrayList<ItemStack> inventory = new ArrayList<>(player.inventory.offHand);
		inventory.addAll(player.inventory.main);
		return inventory;
	}

	public static int maxRange(BlockPos p1, BlockPos p2) {
		return Math.max(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getZ() - p2.getZ()));
	}
}
