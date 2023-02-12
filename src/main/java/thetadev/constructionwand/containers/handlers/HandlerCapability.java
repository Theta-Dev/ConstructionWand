package thetadev.constructionwand.containers.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import thetadev.constructionwand.api.IContainerHandler;
import thetadev.constructionwand.basics.WandUtil;

import java.util.Optional;

public class HandlerCapability implements IContainerHandler
{
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent();
    }

    @Override
    public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        Optional<IItemHandler> itemHandlerOptional = inventoryStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if(itemHandlerOptional.isEmpty()) return 0;

        int total = 0;

        IItemHandler itemHandler = itemHandlerOptional.get();

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
        Optional<IItemHandler> itemHandlerOptional = inventoryStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if(itemHandlerOptional.isEmpty()) return 0;

        IItemHandler itemHandler = itemHandlerOptional.get();

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
