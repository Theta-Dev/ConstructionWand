package thetadev.constructionwand.wand.undo;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;

import javax.annotation.Nullable;

public class BlockgenSnapshot extends PlaceSnapshot
{
    public BlockgenSnapshot(BlockState block, BlockPos pos) {
        super(block, pos, null);
    }

    @Nullable
    public static BlockgenSnapshot get(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                       BlockPos pos, BlockItem item,
                                       @Nullable BlockState supportingBlock, @Nullable WandOptions options) {
        BlockState blockState = WandUtil.getPlaceBlockstate(world, player, rayTraceResult, pos, item, supportingBlock, options);
        if(blockState == null) return null;
        return new BlockgenSnapshot(blockState, pos);
    }

    @Override
    public ItemStack getRequiredItems() {
        return ItemStack.EMPTY;
    }
}
