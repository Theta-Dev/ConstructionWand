package thetadev.constructionwand.job;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thetadev.constructionwand.basics.WandUtil;

public class WandItemUseContext extends ItemPlacementContext
{
	public WandItemUseContext(WandJob job, BlockPos pos, BlockItem item) {
		super(job.world, job.player, Hand.MAIN_HAND, new ItemStack(item), new BlockHitResult(getBlockHitVec(job, pos), job.hitResult.getSide(), pos, false));
	}

	private static Vec3d getBlockHitVec(WandJob job, BlockPos pos) {
		Vec3d hitVec = job.hitResult.getPos(); // Absolute coords of hit target

		Vec3d blockDelta = WandUtil.blockPosVec(job.hitResult.getBlockPos()).subtract(WandUtil.blockPosVec(pos)); // Vector between start and current block

		return blockDelta.add(hitVec); // Absolute coords of current block hit target
	}

	@Override
	public boolean canPlace() {
		return canReplaceExisting;
	}
}
