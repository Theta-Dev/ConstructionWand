package thetadev.constructionwand.containers.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import thetadev.constructionwand.api.IContainerHandler;
import thetadev.constructionwand.basics.WandUtil;

public class HandlerCapability implements IContainerHandler
{
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCapability(Capabilities.ItemHandler.ITEM) != null;
    }

    @Override
    public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        IItemHandler itemHandler = inventoryStack.getCapability(Capabilities.ItemHandler.ITEM);
        if(itemHandler != null) return 0;

        int total = 0;

        for(int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack containerStack = itemHandler.getStackInSlot(i);
            if(WandUtil.stackEquals(itemStack, containerStack)) {
                total += Math.max(0, containerStack.getCount());
            }
        }
        return total;
    }

    @Override
    public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
        IItemHandler itemHandler = inventoryStack.getCapability(Capabilities.ItemHandler.ITEM);
        if(itemHandler != null) return 0;

        for(int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack handlerStack = itemHandler.getStackInSlot(i);
            if(WandUtil.stackEquals(itemStack, handlerStack)) {
                ItemStack extracted = itemHandler.extractItem(i, count, false);
                count -= extracted.getCount();
                if(count <= 0) break;
            }
        }
        return count;
    }
}
