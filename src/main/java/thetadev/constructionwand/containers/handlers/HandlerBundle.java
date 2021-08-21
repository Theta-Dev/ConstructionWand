package thetadev.constructionwand.containers.handlers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thetadev.constructionwand.api.IContainerHandler;
import thetadev.constructionwand.basics.WandUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class HandlerBundle implements IContainerHandler
{
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCount() == 1 && inventoryStack.getItem() == Items.BUNDLE;
    }

    @Override
    public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return getContents(inventoryStack).filter((stack) -> WandUtil.stackEquals(stack, itemStack))
                .map(ItemStack::getCount).reduce(0, Integer::sum);
    }

    @Override
    public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
        AtomicInteger newCount = new AtomicInteger(count);

        List<ItemStack> itemStacks = getContents(inventoryStack).filter((stack -> {
            if(WandUtil.stackEquals(stack, itemStack)) {
                int toTake = Math.min(newCount.get(), stack.getCount());
                stack.shrink(toTake);
                newCount.set(newCount.get() - toTake);
            }
            return !stack.isEmpty();
        })).toList();

        setItemList(inventoryStack, itemStacks);

        return newCount.get();
    }

    private Stream<ItemStack> getContents(ItemStack bundleStack) {
        CompoundTag compoundtag = bundleStack.getTag();
        if(compoundtag == null) {
            return Stream.empty();
        }
        else {
            ListTag listtag = compoundtag.getList("Items", 10);
            return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }

    private void setItemList(ItemStack itemStack, List<ItemStack> itemStacks) {
        CompoundTag rootTag = itemStack.getOrCreateTag();
        ListTag listTag = new ListTag();
        rootTag.put("Items", listTag);

        for(ItemStack stack : itemStacks) {
            CompoundTag itemTag = new CompoundTag();
            stack.save(itemTag);
            listTag.add(itemTag);
        }
    }
}
