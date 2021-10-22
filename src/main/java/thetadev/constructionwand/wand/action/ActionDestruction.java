package thetadev.constructionwand.wand.action;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.wand.undo.DestroySnapshot;
import thetadev.constructionwand.wand.undo.ISnapshot;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ActionDestruction implements IWandAction
{
    @Override
    public int getLimit(ItemStack wand) {
        return ConfigServer.getWandProperties(wand.getItem()).getDestruction();
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshots(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                        ItemStack wand, WandOptions options, IWandSupplier supplier, int limit) {
        LinkedList<ISnapshot> destroySnapshots = new LinkedList<>();
        // Current list of block positions to process
        LinkedList<BlockPos> candidates = new LinkedList<>();
        // All positions that were processed (dont process blocks multiple times)
        HashSet<BlockPos> allCandidates = new HashSet<>();

        // Block face the wand was pointed at
        Direction breakFace = rayTraceResult.getFace();
        // Block the wand was pointed at
        BlockPos startingPoint = rayTraceResult.getPos();
        BlockState targetBlock = world.getBlockState(rayTraceResult.getPos());

        // Is break direction allowed by lock?
        // Tried to break blocks from top/bottom face, so the wand should allow breaking in NS/EW direction
        if(breakFace == Direction.UP || breakFace == Direction.DOWN) {
            if(options.testLock(WandOptions.LOCK.NORTHSOUTH) || options.testLock(WandOptions.LOCK.EASTWEST))
                candidates.add(startingPoint);
        }
        // Tried to break blocks from side face, so the wand should allow breaking in horizontal/vertical direction
        else if(options.testLock(WandOptions.LOCK.HORIZONTAL) || options.testLock(WandOptions.LOCK.VERTICAL))
            candidates.add(startingPoint);

        // Process current candidates, stop when none are avaiable or block limit is reached
        while(!candidates.isEmpty() && destroySnapshots.size() < limit) {
            BlockPos currentCandidate = candidates.removeFirst();

            // Only break blocks facing the player, with no collidable blocks in between
            if(!WandUtil.isBlockPermeable(world, currentCandidate.offset(breakFace))) continue;

            try {
                BlockState candidateBlock = world.getBlockState(currentCandidate);

                // If target and candidate blocks match and the current candidate has not been processed
                if(WandUtil.matchBlocks(options, targetBlock.getBlock(), candidateBlock.getBlock()) &&
                        allCandidates.add(currentCandidate)) {
                    DestroySnapshot snapshot = DestroySnapshot.get(world, player, currentCandidate);
                    if(snapshot == null) continue;
                    destroySnapshots.add(snapshot);

                    switch(breakFace) {
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
        return destroySnapshots;
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshotsFromAir(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                               ItemStack wand, WandOptions options, IWandSupplier supplier, int limit) {
        return new ArrayList<>();
    }
}
