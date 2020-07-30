package thetadev.constructionwand.job;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.EnumLock;

import java.util.HashSet;
import java.util.LinkedList;

public class ConstructionJob extends WandJob
{
	public ConstructionJob(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, ItemStack itemStack) {
		super(player, world, rayTraceResult, itemStack);
	}

	@Override
	protected void getBlockPositionList() {
		EnumLock directionLock = (EnumLock) options.getOption(EnumLock.NOLOCK);
		int directionMaskInt = directionLock.mask;

		LinkedList<BlockPos> candidates = new LinkedList<>();
		HashSet<BlockPos> allCandidates = new HashSet<>();

		Direction placeDirection = rayTraceResult.getFace();
		BlockState targetBlock = world.getBlockState(rayTraceResult.getPos());
		BlockPos startingPoint = rayTraceResult.getPos().offset(placeDirection);

		if (((directionLock != EnumLock.HORIZONTAL && directionLock != EnumLock.VERTICAL) || (placeDirection != Direction.UP && placeDirection != Direction.DOWN))
				&& (directionLock != EnumLock.NORTHSOUTH || (placeDirection != Direction.NORTH && placeDirection != Direction.SOUTH))
				&& (directionLock != EnumLock.EASTWEST || (placeDirection != Direction.EAST && placeDirection != Direction.WEST))) {
			candidates.add(startingPoint);
		}
		while(!candidates.isEmpty() && placeSnapshots.size() < maxBlocks)
		{
			BlockPos currentCandidate = candidates.removeFirst();
			try {
				BlockPos supportingPoint = currentCandidate.offset(placeDirection.getOpposite());
				BlockState candidateSupportingBlock = world.getBlockState(supportingPoint);

				if (shouldContinue(currentCandidate, targetBlock, candidateSupportingBlock) && allCandidates.add(currentCandidate)) {
					placeSnapshots.add(new PlaceSnapshot(currentCandidate, candidateSupportingBlock));

					switch (placeDirection) {
						case DOWN:
						case UP:
							if ((directionMaskInt & EnumLock.NORTH_SOUTH_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.NORTH));
							if ((directionMaskInt & EnumLock.EAST_WEST_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.EAST));
							if ((directionMaskInt & EnumLock.NORTH_SOUTH_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.SOUTH));
							if ((directionMaskInt & EnumLock.EAST_WEST_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.WEST));
							if ((directionMaskInt & EnumLock.NORTH_SOUTH_MASK) > 0 && (directionMaskInt & EnumLock.EAST_WEST_MASK) > 0) {
								candidates.add(currentCandidate.offset(Direction.NORTH).offset(Direction.EAST));
								candidates.add(currentCandidate.offset(Direction.NORTH).offset(Direction.WEST));
								candidates.add(currentCandidate.offset(Direction.SOUTH).offset(Direction.EAST));
								candidates.add(currentCandidate.offset(Direction.SOUTH).offset(Direction.WEST));
							}
							break;
						case NORTH:
						case SOUTH:
							if ((directionMaskInt & EnumLock.UP_DOWN_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.UP));
							if ((directionMaskInt & EnumLock.EAST_WEST_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.EAST));
							if ((directionMaskInt & EnumLock.UP_DOWN_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.DOWN));
							if ((directionMaskInt & EnumLock.EAST_WEST_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.WEST));
							if ((directionMaskInt & EnumLock.UP_DOWN_MASK) > 0 && (directionMaskInt & EnumLock.EAST_WEST_MASK) > 0) {
								candidates.add(currentCandidate.offset(Direction.UP).offset(Direction.EAST));
								candidates.add(currentCandidate.offset(Direction.UP).offset(Direction.WEST));
								candidates.add(currentCandidate.offset(Direction.DOWN).offset(Direction.EAST));
								candidates.add(currentCandidate.offset(Direction.DOWN).offset(Direction.WEST));
							}
							break;
						case WEST:
						case EAST:
							if ((directionMaskInt & EnumLock.UP_DOWN_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.UP));
							if ((directionMaskInt & EnumLock.NORTH_SOUTH_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.NORTH));
							if ((directionMaskInt & EnumLock.UP_DOWN_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.DOWN));
							if ((directionMaskInt & EnumLock.NORTH_SOUTH_MASK) > 0)
								candidates.add(currentCandidate.offset(Direction.SOUTH));
							if ((directionMaskInt & EnumLock.UP_DOWN_MASK) > 0 && (directionMaskInt & EnumLock.NORTH_SOUTH_MASK) > 0) {
								candidates.add(currentCandidate.offset(Direction.UP).offset(Direction.NORTH));
								candidates.add(currentCandidate.offset(Direction.UP).offset(Direction.SOUTH));
								candidates.add(currentCandidate.offset(Direction.DOWN).offset(Direction.NORTH));
								candidates.add(currentCandidate.offset(Direction.DOWN).offset(Direction.SOUTH));
							}
					}
				}
			}
			catch(Exception e) {
				// Can't do anything, could be anything.
				// Skip if anything goes wrong.
			}
		}
	}
}
