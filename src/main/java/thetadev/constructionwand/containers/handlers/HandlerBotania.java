/*
TODO: Reenable this when Botania gets ported to 1.17

package thetadev.constructionwand.containers.handlers;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thetadev.constructionwand.api.IContainerHandler;
import vazkii.botania.api.item.IBlockProvider;

public class HandlerBotania implements IContainerHandler
{
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCount() == 1 && inventoryStack.getItem() instanceof IBlockProvider;
    }

    @Override
    public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        IBlockProvider prov = (IBlockProvider) inventoryStack.getItem();
        int provCount = prov.getBlockCount(player, itemStack, inventoryStack, Block.byItem(itemStack.getItem()));
        if(provCount == -1)
            return Integer.MAX_VALUE;
        return provCount;
    }

    @Override
    public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
        IBlockProvider prov = (IBlockProvider) inventoryStack.getItem();
        if(prov.provideBlock(player, itemStack, inventoryStack, Block.byItem(itemStack.getItem()), true))
            return 0;
        return count;
    }
}
*/