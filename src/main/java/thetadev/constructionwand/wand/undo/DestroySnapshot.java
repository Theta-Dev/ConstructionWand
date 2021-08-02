package thetadev.constructionwand.wand.undo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import thetadev.constructionwand.basics.WandUtil;

import javax.annotation.Nullable;

public class DestroySnapshot implements ISnapshot
{
    private final BlockState block;
    private final BlockPos pos;

    public DestroySnapshot(BlockState block, BlockPos pos) {
        this.pos = pos;
        this.block = block;
    }

    @Nullable
    public static DestroySnapshot get(Level world, Player player, BlockPos pos) {
        if(!WandUtil.isBlockRemovable(world, player, pos)) return null;

        return new DestroySnapshot(world.getBlockState(pos), pos);
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
        return ItemStack.EMPTY;
    }

    @Override
    public boolean execute(Level world, Player player, BlockHitResult rayTraceResult) {
        return WandUtil.removeBlock(world, player, block, pos);
    }

    @Override
    public boolean canRestore(Level world, Player player) {
        // Is position out of world?
        if(!world.isInWorldBounds(pos)) return false;

        // Is block modifiable?
        if(!world.mayInteract(player, pos)) return false;

        // Ignore blocks and entities when in creative
        if(player.isCreative()) return true;

        // Is block empty or fluid?
        if(!world.isEmptyBlock(pos) && !world.getBlockState(pos).canBeReplaced(Fluids.EMPTY)) return false;

        return !WandUtil.entitiesCollidingWithBlock(world, block, pos);
    }

    @Override
    public boolean restore(Level world, Player player) {
        return WandUtil.placeBlock(world, player, block, pos, null);
    }

    @Override
    public void forceRestore(Level world) {
        world.setBlockAndUpdate(pos, block);
    }
}
