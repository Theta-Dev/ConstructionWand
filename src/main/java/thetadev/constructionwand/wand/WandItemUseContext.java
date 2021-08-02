package thetadev.constructionwand.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import thetadev.constructionwand.basics.WandUtil;

public class WandItemUseContext extends BlockPlaceContext
{
    public WandItemUseContext(Level world, Player player, BlockHitResult rayTraceResult, BlockPos pos, BlockItem item) {
        super(world, player, InteractionHand.MAIN_HAND, new ItemStack(item),
                new BlockHitResult(getBlockHitVec(rayTraceResult, pos), rayTraceResult.getDirection(), pos, false));
    }

    private static Vec3 getBlockHitVec(BlockHitResult rayTraceResult, BlockPos pos) {
        Vec3 hitVec = rayTraceResult.getLocation(); // Absolute coords of hit target

        Vec3 blockDelta = WandUtil.blockPosVec(rayTraceResult.getBlockPos()).subtract(WandUtil.blockPosVec(pos)); // Vector between start and current block

        return blockDelta.add(hitVec); // Absolute coords of current block hit target
    }

    @Override
    public boolean canPlace() {
        return replaceClicked;
    }
}
