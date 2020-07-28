package thetadev.constructionwand.containers.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thetadev.constructionwand.api.IContainerHandler;

/**
 * Created by james on 28/12/16.
 */
public class HandlerCapability implements IContainerHandler
{
    @Override
    public boolean matches(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent();
    }

    @Override
    public int countItems(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack) {
        LazyOptional<IItemHandler> itemHandlerLazyOptional = inventoryStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if(!itemHandlerLazyOptional.isPresent()) return 0;

        int total = 0;

        IItemHandler itemHandler = itemHandlerLazyOptional.orElse(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getDefaultInstance());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack containerStack = itemHandler.getStackInSlot(i);
            if (containerStack != null && itemStack.isItemEqual(containerStack)) {
                total += Math.max(0, containerStack.getCount());
            }

            // Already in a container. Don't inception this thing.
        }
        return total;
    }

    @Override
    public int useItems(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack, int count) {
        int toUse = itemStack.getCount();

        LazyOptional<IItemHandler> itemHandlerLazyOptional = inventoryStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if(!itemHandlerLazyOptional.isPresent()) return 0;

        IItemHandler itemHandler = itemHandlerLazyOptional.orElse(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getDefaultInstance());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack handlerStack = itemHandler.getStackInSlot(i);
            if(handlerStack != null && handlerStack.isItemEqual(itemStack)) {
                ItemStack extracted = itemHandler.extractItem(i, count, false);
                if(extracted != null) {
                    count -= extracted.getCount();
                }
                if(count <= 0) {
                    break;
                }
            }
        }
        return count;
    }
}
