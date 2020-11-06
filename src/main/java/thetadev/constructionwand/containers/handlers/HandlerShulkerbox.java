package thetadev.constructionwand.containers.handlers;

import net.fabricmc.fabric.mixin.item.ItemStackMixin;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import thetadev.constructionwand.api.IContainerHandler;
import thetadev.constructionwand.basics.WandUtil;

public class HandlerShulkerbox implements IContainerHandler
{
	private final int SLOTS = 27;

	@Override
	public boolean matches(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack)
	{
		return inventoryStack != null && inventoryStack.getCount() == 1 && Block.getBlockFromItem(inventoryStack.getItem()) instanceof ShulkerBoxBlock;
	}

	@Override
	public int countItems(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack)
	{
		int count = 0;

		for(ItemStack stack : getItemList(inventoryStack)) {
			if(WandUtil.stackEquals(stack, itemStack)) count += stack.getCount();
		}

		return count;
	}

	@Override
	public int useItems(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack, int count)
	{
		DefaultedList<ItemStack> itemList = getItemList(inventoryStack);
		boolean changed = false;

		for(ItemStack stack : itemList) {
			if(WandUtil.stackEquals(stack, itemStack)) {
				int toTake = Math.min(count, stack.getCount());
				stack.decrement(toTake);
				count -= toTake;
				changed = true;
				if(count == 0) break;
			}
		}
		if(changed) {
			setItemList(inventoryStack, itemList);
			player.inventory.markDirty();
		}

		return count;
	}

	private DefaultedList<ItemStack> getItemList(ItemStack itemStack) {
		DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(SLOTS, ItemStack.EMPTY);
		CompoundTag rootTag = itemStack.getTag();
		if (rootTag != null && rootTag.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
			CompoundTag entityTag = rootTag.getCompound("BlockEntityTag");
			if (entityTag.contains("Items", Constants.NBT.TAG_LIST)) {
				ItemStackHelper.loadAllItems(entityTag, itemStacks);
			}
		}
		return itemStacks;
	}

	private void setItemList(ItemStack itemStack, DefaultedList<ItemStack> itemStacks) {
		CompoundTag rootTag = itemStack.getOrCreateTag();
		if (!rootTag.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
			rootTag.put("BlockEntityTag", new CompoundTag());
		}
		ItemStackHelper.saveAllItems(rootTag.getCompound("BlockEntityTag"), itemStacks);
	}
}
