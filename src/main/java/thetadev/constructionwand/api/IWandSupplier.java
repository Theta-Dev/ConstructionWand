package thetadev.constructionwand.api;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nullable;

public interface IWandSupplier
{
    void getSupply(@Nullable BlockItem target);

    /**
     * Tries to create a new PlaceSnapshot at the specified position.
     * Returns null if there aren't any blocks available that can be placed
     * in that position.
     */
    @Nullable
    PlaceSnapshot getPlaceSnapshot(World world, BlockPos pos, BlockRayTraceResult rayTraceResult,
                                   @Nullable BlockState supportingBlock);

    /**
     * Consumes an item stack if the placement was successful
     */
    int takeItemStack(ItemStack stack);
}
