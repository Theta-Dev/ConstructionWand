package thetadev.constructionwand.wand.undo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface ISnapshot
{
    BlockPos getPos();

    BlockState getBlockState();

    ItemStack getRequiredItems();

    boolean execute(Level world, Player player, BlockHitResult rayTraceResult);

    boolean canRestore(Level world, Player player);

    boolean restore(Level world, Player player);

    void forceRestore(Level world);
}
