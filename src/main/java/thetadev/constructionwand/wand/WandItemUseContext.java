package thetadev.constructionwand.wand;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.WandUtil;

public class WandItemUseContext extends BlockItemUseContext
{
    public WandItemUseContext(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult, BlockPos pos, BlockItem item) {
        super(world, player, Hand.MAIN_HAND, new ItemStack(item),
                new BlockRayTraceResult(getBlockHitVec(rayTraceResult, pos), rayTraceResult.getFace(), pos, false));
    }

    private static Vec3d getBlockHitVec(BlockRayTraceResult rayTraceResult, BlockPos pos) {
        Vec3d hitVec = rayTraceResult.getHitVec(); // Absolute coords of hit target

        Vec3d blockDelta = WandUtil.blockPosVec(rayTraceResult.getPos()).subtract(WandUtil.blockPosVec(pos)); // Vector between start and current block

        return blockDelta.add(hitVec); // Absolute coords of current block hit target
    }

    @Override
    public boolean canPlace() {
        return replaceClicked;
    }
}
