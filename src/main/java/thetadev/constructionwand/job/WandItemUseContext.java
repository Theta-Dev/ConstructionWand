package thetadev.constructionwand.job;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import thetadev.constructionwand.basics.WandUtil;

public class WandItemUseContext extends BlockItemUseContext
{
	public WandItemUseContext(WandJob job, BlockPos pos) {
		super(job.world, job.player, Hand.MAIN_HAND, new ItemStack(job.placeItem), new BlockRayTraceResult(getBlockHitVec(job, pos), job.rayTraceResult.getFace(), pos, false));
	}

	private static Vector3d getBlockHitVec(WandJob job, BlockPos pos) {
		Vector3d hitVec = job.rayTraceResult.getHitVec(); // Absolute coords of hit target

		Vector3d blockDelta = WandUtil.blockPosVec(job.rayTraceResult.getPos()).subtract(WandUtil.blockPosVec(pos)); // Vector between start and current block

		return blockDelta.add(hitVec); // Absolute coords of current block hit target
	}

	@Override
	public boolean canPlace() {
		return replaceClicked;
	}
}
