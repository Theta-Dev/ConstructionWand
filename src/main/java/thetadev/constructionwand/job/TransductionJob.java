package thetadev.constructionwand.job;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.ConfigServer;

public class TransductionJob extends WandJob
{
	public TransductionJob(PlayerEntity player, World world, BlockHitResult hitResult, ItemStack wand) {
		super(player, world, hitResult, wand);
	}

	@Override
	protected void getBlockPositionList() {
		Direction placeDirection = hitResult.getSide();
		BlockPos currentPos = hitResult.getBlockPos();
		BlockState supportingBlock = world.getBlockState(currentPos);

		for(int i = 0; i< ConfigServer.getWandProperties(wandItem).getAngel(); i++) {
			currentPos = currentPos.offset(placeDirection.getOpposite());

			PlaceSnapshot snapshot = getPlaceSnapshot(currentPos, supportingBlock);
			if(snapshot != null) {
				placeSnapshots.add(snapshot);
				break;
			}
		}
	}
}
