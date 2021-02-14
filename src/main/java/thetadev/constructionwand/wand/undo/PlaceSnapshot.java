package thetadev.constructionwand.wand.undo;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.WandUtil;

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
    public boolean restore(World world, PlayerEntity player) {
        return WandUtil.removeBlock(world, player, block, pos);
    }

    @Override
    public void forceRestore(World world) {
        world.removeBlock(pos, false);
    }
}
