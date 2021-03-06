package thetadev.constructionwand.api;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thetadev.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nullable;

public interface IWandSupplier
{
    int getMaxBlocks();

    @Nullable
    PlaceSnapshot getPlaceSnapshot(BlockPos pos, @Nullable BlockState supportingBlock);

    int takeItemStack(ItemStack stack);
}
