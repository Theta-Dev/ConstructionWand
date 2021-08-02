package thetadev.constructionwand.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IContainerHandler
{
    boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack);

    int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack);

    int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count);
}
