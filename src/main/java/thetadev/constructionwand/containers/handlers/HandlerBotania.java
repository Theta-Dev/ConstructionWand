package thetadev.constructionwand.containers.handlers;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IContainerHandler;
import vazkii.botania.api.item.IBlockProvider;

/**
 * Created by james on 28/12/16.
 */
public class HandlerBotania implements IContainerHandler
{
    @Override
    public boolean matches(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCount() == 1 && inventoryStack.getItem() instanceof IBlockProvider;
    }

    @Override
    public int countItems(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack) {
        IBlockProvider prov = (IBlockProvider) inventoryStack.getItem();
        int provCount = prov.getBlockCount(player, itemStack, inventoryStack, Block.getBlockFromItem(itemStack.getItem()));
        if(provCount == -1)
            return Integer.MAX_VALUE;
        return provCount;
    }

    @Override
    public int useItems(PlayerEntity player, ItemStack itemStack, ItemStack inventoryStack, int count) {
        IBlockProvider prov = (IBlockProvider) inventoryStack.getItem();
        if(prov.provideBlock(player, itemStack, inventoryStack, Block.getBlockFromItem(itemStack.getItem()), true))
            return 0;
        return count;
    }
}
