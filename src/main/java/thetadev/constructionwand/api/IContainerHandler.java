package thetadev.constructionwand.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IContainerHandler
{
    boolean matches(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack);

    int countItems(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack);

    int useItems(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack, int count);
}
