package thetadev.constructionwand.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.api.IContainerHandler;

import java.util.HashSet;

public class ContainerManager {
	private HashSet<IContainerHandler> handlers;

	public ContainerManager() {
		handlers = new HashSet<IContainerHandler>();
	}

	public boolean register(IContainerHandler handler) {
		return handlers.add(handler);
	}

	public int countItems(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack) {
		for(IContainerHandler handler : handlers) {
			if(handler.matches(player, itemStack, inventoryStack)) {
				return handler.countItems(player,itemStack, inventoryStack);
			}
		}
		return 0;
	}

	public int useItems(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack, int count) {
		for(IContainerHandler handler : handlers) {
			if(handler.matches(player, itemStack, inventoryStack)) {
				return handler.useItems(player, itemStack, inventoryStack, count);
			}
		}
		return count;
	}
}