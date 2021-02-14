package thetadev.constructionwand.api;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thetadev.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nullable;

public interface IWandSupplier
{
    void getSupply(@Nullable BlockItem target);

    int getMaxBlocks();

    @Nullable
    PlaceSnapshot getPlaceSnapshot(BlockPos pos, BlockState supportingBlock);

    int takeItemStack(ItemStack stack);
}
