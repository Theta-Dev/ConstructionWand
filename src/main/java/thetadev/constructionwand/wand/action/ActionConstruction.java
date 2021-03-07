package thetadev.constructionwand.wand.action;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.wand.WandJob;
import thetadev.constructionwand.wand.undo.ISnapshot;
import thetadev.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Default WandAction. Extends your building on the side you're facing.
 */
public class ActionConstruction implements IWandAction
{
    @Nonnull
    @Override
    public List<ISnapshot> getSnapshots(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                        WandOptions options, ConfigServer.WandProperties properties, int limit,
                                        IWandSupplier supplier) {
        LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();
        LinkedList<BlockPos> candidates = new LinkedList<>();
        HashSet<BlockPos> allCandidates = new HashSet<>();

        Direction placeDirection = rayTraceResult.getFace();
        BlockState targetBlock = world.getBlockState(rayTraceResult.getPos());
        BlockPos startingPoint = rayTraceResult.getPos().offset(placeDirection);

        // Is place direction allowed by lock?
        if(placeDirection == Direction.UP || placeDirection == Direction.DOWN) {
            if(options.testLock(WandOptions.LOCK.NORTHSOUTH) || options.testLock(WandOptions.LOCK.EASTWEST))
                candidates.add(startingPoint);
        }
        else
            if(options.testLock(WandOptions.LOCK.HORIZONTAL) || options.testLock(WandOptions.LOCK.VERTICAL))
                candidates.add(startingPoint);

        while(!candidates.isEmpty() && placeSnapshots.size() < limit) {
            BlockPos currentCandidate = candidates.removeFirst();
            try {
                BlockPos supportingPoint = currentCandidate.offset(placeDirection.getOpposite());
                BlockState candidateSupportingBlock = world.getBlockState(supportingPoint);

                if(WandUtil.matchBlocks(options, targetBlock.getBlock(), candidateSupportingBlock.getBlock()) &&
                        allCandidates.add(currentCandidate)) {
                    PlaceSnapshot snapshot = supplier.getPlaceSnapshot(world, currentCandidate, rayTraceResult, candidateSupportingBlock);
                    if(snapshot == null) continue;
                    placeSnapshots.add(snapshot);

                    switch(placeDirection) {
                        case DOWN:
                        case UP:
                            if(options.testLock(WandOptions.LOCK.NORTHSOUTH)) {
                                candidates.add(currentCandidate.offset(Direction.NORTH));
                                candidates.add(currentCandidate.offset(Direction.SOUTH));
                            }
                            if(options.testLock(WandOptions.LOCK.EASTWEST)) {
                                candidates.add(currentCandidate.offset(Direction.EAST));
                                candidates.add(currentCandidate.offset(Direction.WEST));
                            }
                            if(options.testLock(WandOptions.LOCK.NORTHSOUTH) && options.testLock(WandOptions.LOCK.EASTWEST)) {
                                candidates.add(currentCandidate.offset(Direction.NORTH).offset(Direction.EAST));
                                candidates.add(currentCandidate.offset(Direction.NORTH).offset(Direction.WEST));
                                candidates.add(currentCandidate.offset(Direction.SOUTH).offset(Direction.EAST));
                                candidates.add(currentCandidate.offset(Direction.SOUTH).offset(Direction.WEST));
                            }
                            break;
                        case NORTH:
                        case SOUTH:
                            if(options.testLock(WandOptions.LOCK.HORIZONTAL)) {
                                candidates.add(currentCandidate.offset(Direction.EAST));
                                candidates.add(currentCandidate.offset(Direction.WEST));
                            }
                            if(options.testLock(WandOptions.LOCK.VERTICAL)) {
                                candidates.add(currentCandidate.offset(Direction.UP));
                                candidates.add(currentCandidate.offset(Direction.DOWN));
                            }
                            if(options.testLock(WandOptions.LOCK.HORIZONTAL) && options.testLock(WandOptions.LOCK.VERTICAL)) {
                                candidates.add(currentCandidate.offset(Direction.UP).offset(Direction.EAST));
                                candidates.add(currentCandidate.offset(Direction.UP).offset(Direction.WEST));
                                candidates.add(currentCandidate.offset(Direction.DOWN).offset(Direction.EAST));
                                candidates.add(currentCandidate.offset(Direction.DOWN).offset(Direction.WEST));
                            }
                            break;
                        case EAST:
                        case WEST:
                            if(options.testLock(WandOptions.LOCK.HORIZONTAL)) {
                                candidates.add(currentCandidate.offset(Direction.NORTH));
                                candidates.add(currentCandidate.offset(Direction.SOUTH));
                            }
                            if(options.testLock(WandOptions.LOCK.VERTICAL)) {
                                candidates.add(currentCandidate.offset(Direction.UP));
                                candidates.add(currentCandidate.offset(Direction.DOWN));
                            }
                            if(options.testLock(WandOptions.LOCK.HORIZONTAL) && options.testLock(WandOptions.LOCK.VERTICAL)) {
                                candidates.add(currentCandidate.offset(Direction.UP).offset(Direction.NORTH));
                                candidates.add(currentCandidate.offset(Direction.UP).offset(Direction.SOUTH));
                                candidates.add(currentCandidate.offset(Direction.DOWN).offset(Direction.NORTH));
                                candidates.add(currentCandidate.offset(Direction.DOWN).offset(Direction.SOUTH));
                            }
                            break;
                    }
                }
            } catch(Exception e) {
                // Can't do anything, could be anything.
                // Skip if anything goes wrong.
            }
        }
        return placeSnapshots;
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshotsFromAir(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                               WandOptions options, ConfigServer.WandProperties properties, int limit,
                                               IWandSupplier supplier) {
        return new ArrayList<>();
    }
}
