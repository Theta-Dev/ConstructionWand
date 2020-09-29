package thetadev.constructionwand.job;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;

public class AngelJob extends WandJob
{
	public AngelJob(PlayerEntity player, World world, ItemStack wand) {
		super(player, world, new BlockRayTraceResult(player.getLookVec(), fromVector(player.getLookVec()), player.getPosition(), false), wand);
	}

	private static Direction fromVector(Vec3d vector) {
		return Direction.getFacingFromVector(vector.x, vector.y, vector.z);
	}

	@Override
	protected void getBlockPositionList() {
		if(options.mode.get() != WandOptions.MODE.ANGEL || ConfigServer.getWandProperties(wandItem).getAngel() == 0) return;

		if(!player.isCreative() && !ConfigServer.ANGEL_FALLING.get() && player.fallDistance > 10) return;

		Vec3d playerVec = WandUtil.entityPositionVec(player);
		Vec3d lookVec = player.getLookVec().mul(2, 2, 2);
		Vec3d placeVec = playerVec.add(lookVec);

		BlockPos currentPos = new BlockPos(placeVec);

		PlaceSnapshot snapshot = getPlaceSnapshot(currentPos, Blocks.AIR.getDefaultState());
		if(snapshot != null) {
			placeSnapshots.add(snapshot);
		}
	}
}
