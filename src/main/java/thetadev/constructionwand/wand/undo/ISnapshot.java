package thetadev.constructionwand.wand.undo;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISnapshot
{
    BlockPos getPos();

    BlockState getBlockState();

    ItemStack getRequiredItems();

    boolean execute(World world, PlayerEntity player);

    boolean canRestore(World world, PlayerEntity player);

    boolean restore(World world, PlayerEntity player);

    void forceRestore(World world);
}
