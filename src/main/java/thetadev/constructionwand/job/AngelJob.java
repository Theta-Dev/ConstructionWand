package thetadev.constructionwand.job;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.EnumMode;

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
		if(options.getOption(EnumMode.DEFAULT) != EnumMode.ANGEL) return;

		BlockItem item = (BlockItem) placeItems.keySet().iterator().next();
		BlockState supportingBlock = item.getBlock().getDefaultState();

		BlockPos currentPos = rayTraceResult.getPos();

		for(int i=0; i<3; i++) {
			currentPos = currentPos.offset(rayTraceResult.getFace());
			if(shouldContinue(currentPos, supportingBlock, supportingBlock)) {
				placeSnapshots.add(new PlaceSnapshot(currentPos, supportingBlock));
				break;
			}
		}
	}
}
