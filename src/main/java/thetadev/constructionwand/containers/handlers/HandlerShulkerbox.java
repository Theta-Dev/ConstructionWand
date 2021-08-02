package thetadev.constructionwand.containers.handlers;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.util.Constants;
import thetadev.constructionwand.api.IContainerHandler;
import thetadev.constructionwand.basics.WandUtil;

public class HandlerShulkerbox implements IContainerHandler
{
    private final int SLOTS = 27;

    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCount() == 1 && Block.byItem(inventoryStack.getItem()) instanceof ShulkerBoxBlock;
    }

    @Override
    public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        int count = 0;

        for(ItemStack stack : getItemList(inventoryStack)) {
            if(WandUtil.stackEquals(stack, itemStack)) count += stack.getCount();
        }

        return count;
    }

    @Override
    public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
        NonNullList<ItemStack> itemList = getItemList(inventoryStack);
        boolean changed = false;

        for(ItemStack stack : itemList) {
            if(WandUtil.stackEquals(stack, itemStack)) {
                int toTake = Math.min(count, stack.getCount());
                stack.shrink(toTake);
                count -= toTake;
                changed = true;
                if(count == 0) break;
            }
        }
        if(changed) {
            setItemList(inventoryStack, itemList);
            player.getInventory().setChanged();
        }

        return count;
    }

    private NonNullList<ItemStack> getItemList(ItemStack itemStack) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        CompoundTag rootTag = itemStack.getTag();
        if(rootTag != null && rootTag.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
            CompoundTag entityTag = rootTag.getCompound("BlockEntityTag");
            if(entityTag.contains("Items", Constants.NBT.TAG_LIST)) {
                ContainerHelper.loadAllItems(entityTag, itemStacks);
            }
        }
        return itemStacks;
    }

    private void setItemList(ItemStack itemStack, NonNullList<ItemStack> itemStacks) {
        CompoundTag rootTag = itemStack.getOrCreateTag();
        if(!rootTag.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
            rootTag.put("BlockEntityTag", new CompoundTag());
        }
        ContainerHelper.saveAllItems(rootTag.getCompound("BlockEntityTag"), itemStacks);
    }
}
