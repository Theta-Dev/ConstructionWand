package thetadev.constructionwand.job;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.items.ItemWand;

public class TransductionJob extends WandJob
{
	public TransductionJob(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, ItemStack wand) {
		super(player, world, rayTraceResult, wand);
	}

	@Override
	protected void getBlockPositionList() {
		Direction placeDirection = rayTraceResult.getFace();
		BlockPos currentPos = rayTraceResult.getPos();
		BlockState supportingBlock = world.getBlockState(currentPos);

		for(int i=0; i<wandItem.angelDistance; i++) {
			currentPos = currentPos.offset(placeDirection.getOpposite());
			if(canPlace(currentPos)) {
				placeSnapshots.add(new PlaceSnapshot(currentPos, supportingBlock));
				break;
			}
		}
	}
}
