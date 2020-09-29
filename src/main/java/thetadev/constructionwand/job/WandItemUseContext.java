package thetadev.constructionwand.job;

import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;

public class WandItemUseContext extends BlockItemUseContext
{
	public WandItemUseContext(WandJob job, BlockPos pos, BlockItem item) {
		super(job.world, job.player, Hand.MAIN_HAND, new ItemStack(item), new BlockRayTraceResult(getBlockHitVec(job, pos), job.rayTraceResult.getFace(), pos, false));
	}

	private static Vec3d getBlockHitVec(WandJob job, BlockPos pos) {
		Vec3d hitVec = job.rayTraceResult.getHitVec(); // Absolute coords of hit target
		Vec3d blockDelta = new Vec3d(job.rayTraceResult.getPos()).subtract(new Vec3d(pos)); // Vector between start and current block

		return blockDelta.add(hitVec); // Absolute coords of current block hit target
	}

	@Override
	public boolean canPlace() {
		return replaceClicked;
	}
}
