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

public class PlaceSnapshot implements ISnapshot
{
    public final BlockState block;
    public final BlockPos pos;
    public final BlockItem item;

    public PlaceSnapshot(BlockState block, BlockPos pos, BlockItem item) {
        this.block = block;
        this.pos = pos;
        this.item = item;
    }

    @Nullable
    public static PlaceSnapshot get(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                    BlockPos pos, BlockItem item,
                                    @Nullable BlockState supportingBlock, @Nullable WandOptions options) {
        BlockState blockState = WandUtil.getPlaceBlockstate(world, player, rayTraceResult, pos, item, supportingBlock, options);
        if(blockState == null) return null;
        return new PlaceSnapshot(blockState, pos, item);
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public BlockState getBlockState() {
        return block;
    }

    @Override
    public ItemStack getRequiredItems() {
        return new ItemStack(item);
    }

    @Override
    public boolean execute(World world, PlayerEntity player) {
        return WandUtil.placeBlock(world, player, block, pos, item);
    }

    @Override
    public boolean canRestore(World world, PlayerEntity player) {
        return true;
    }

    @Override
    public boolean restore(World world, PlayerEntity player) {
        return WandUtil.removeBlock(world, player, block, pos);
    }

    @Override
    public void forceRestore(World world) {
        world.removeBlock(pos, false);
    }
}
