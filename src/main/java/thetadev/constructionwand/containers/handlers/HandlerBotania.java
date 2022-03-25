package thetadev.constructionwand.containers.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import thetadev.constructionwand.api.IContainerHandler;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.item.IBlockProvider;

import java.util.Optional;

public class HandlerBotania implements IContainerHandler
{
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER).isPresent();
    }

    @Override
    public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        Optional<IBlockProvider> provOptional = inventoryStack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER).resolve();
        if(provOptional.isEmpty()) return 0;

        IBlockProvider prov = provOptional.get();
        int provCount = prov.getBlockCount(player, inventoryStack, Block.byItem(itemStack.getItem()));
        if(provCount == -1)
            return Integer.MAX_VALUE;
        return provCount;
    }

    @Override
    public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
        Optional<IBlockProvider> provOptional = inventoryStack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER).resolve();
        if(provOptional.isEmpty()) return 0;

        IBlockProvider prov = provOptional.get();
        if(prov.provideBlock(player, inventoryStack, Block.byItem(itemStack.getItem()), true))
            return 0;
        return count;
    }
}