package thetadev.constructionwand.job;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.IFluidBlock;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.EnumLock;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.containers.ContainerManager;
import thetadev.constructionwand.items.ItemWand;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class WandJob
{
	private PlayerEntity player;
	private World world;
	private BlockRayTraceResult rayTraceResult;
	private ItemStack wand;
	private int maxBlocks;

	private HashMap<Item, Integer> placeItems;
	private LinkedList<BlockPos> blocksToPlace;
	private LinkedList<PlaceSnapshot> placeSnapshots;

	public WandJob(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, ItemStack itemStack)
	{
		this.player = player;
		this.world = world;
		this.rayTraceResult = rayTraceResult;
		placeItems = new HashMap<>();
		blocksToPlace = new LinkedList<>();

		// Get wand
		if(itemStack == null || itemStack == ItemStack.EMPTY || !(itemStack.getItem() instanceof ItemWand)) return;
		wand = itemStack;

		ItemWand wandItem = (ItemWand) wand.getItem();

		// Target block + item
		BlockPos targetPos = rayTraceResult.getPos();
		BlockState targetState = world.getBlockState(targetPos);
		Block targetBlock = targetState.getBlock();
		Item item = targetBlock.getPickBlock(targetState, rayTraceResult, world, targetPos, player).getItem();

		// Get substitutions (e.g. Grass -> Dirt)
		placeItems.put(item, 0);
		SubstitutionManager substitutionManager = ConstructionWand.instance.substitutionManager;
		for(Item it : substitutionManager.getSubstitutions(item)) {
			placeItems.put(it, 0);
		}

		// Get inventory supply
		maxBlocks = Math.min(countItems(), wandItem.getLimit(player, wand));

		// Get list of block positions
		getBlockPositionList();
	}

	public LinkedList<BlockPos> getBlockPositions() { return blocksToPlace; }

	public BlockRayTraceResult getRayTraceResult() { return rayTraceResult; }

	public BlockPos getTargetPos() { return rayTraceResult.getPos(); }

	public PlayerEntity getPlayer() { return player; }

	public void setPlayer(PlayerEntity player) { this.player = player; }

	public World getWorld() { return world; }

	public void setWorld(World world) { this.world = world; }

	public ItemStack getWand() { return wand; }

	private int countItem(Item item)
	{
		int total = 0;

		if(player.inventory == null || player.inventory.mainInventory == null) return 0;
		if(player.isCreative()) return Integer.MAX_VALUE;

		ContainerManager containerManager = ConstructionWand.instance.containerManager;

		for(ItemStack stack : player.inventory.mainInventory) {
			if(stack == null) continue;

			if(WandUtil.stackEquals(stack, item)) {
				total += Math.max(0, stack.getCount());
			}
			else {
				int amount = containerManager.countItems(player, new ItemStack(item), stack);
				if(amount == Integer.MAX_VALUE) return Integer.MAX_VALUE;
				total += amount;
			}
		}
		return total;
	}

	private int countItems()
	{
		int total = 0;

		if(placeItems.size() == 0 || player.inventory == null || player.inventory.mainInventory == null) return 0;
		if(player.isCreative()) return Integer.MAX_VALUE;

		for(Item item : placeItems.keySet()) {
			int amount = countItem(item);
			if(amount == Integer.MAX_VALUE) return Integer.MAX_VALUE;
			total += amount;
		}

		return total;
	}

	// Attempts to take specified number of items, returns number of items taken
	private int takeItem(Item item, int count)
	{
		int total = 0;

		if(player.inventory == null || player.inventory.mainInventory == null) return 0;
		if(player.isCreative()) return count;

		ContainerManager containerManager = ConstructionWand.instance.containerManager;

		for(int i = player.inventory.mainInventory.size()- 1; i >= 0; i--) {
			if(count == 0) break;

			ItemStack stack = player.inventory.mainInventory.get(i);

			if(WandUtil.stackEquals(stack, item)) {
				int toTake = Math.min(count, stack.getCount());
				stack.shrink(toTake);
				count -= toTake;
				total += toTake;
				player.inventory.markDirty();
			}
			else {
				int nCount = containerManager.useItems(player, new ItemStack(item), stack, count);
				total += (count - nCount);
				count = nCount;
			}
		}
		return total;
	}

	private int takeItems(int count)
	{
		int total = 0;
		if(placeItems.size() == 0 || player.inventory == null || player.inventory.mainInventory == null) return 0;

		for(Item item : placeItems.keySet()) {
			if(count == 0) break;

			int amount = takeItem(item, count);
			placeItems.put(item, amount);
			total += amount;
			count -= amount;
		}
		return total;
	}

	private boolean shouldContinue(BlockPos currentCandidate, BlockState targetBlock, BlockState candidateSupportingBlock) {
		if(!world.isAirBlock(currentCandidate)){
			Block currrentCandidateBlock = world.getBlockState(currentCandidate).getBlock();
			if(!(currrentCandidateBlock instanceof IFluidBlock || currrentCandidateBlock instanceof FlowingFluidBlock)) return false;
		};

		if(!targetBlock.getBlock().equals(candidateSupportingBlock.getBlock())) return false;
		//if(!placeItems.containsKey(candidateSupportingBlock.getBlock().getPickBlock(candidateSupportingBlock, rayTraceResult, world, supportingPoint, player).getItem())) return false;

		BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, Hand.MAIN_HAND, new BlockRayTraceResult(rayTraceResult.getHitVec(), rayTraceResult.getFace(), currentCandidate, false)));

		if(!ctx.canPlace()) return false;

		BlockState blockState = targetBlock.getBlock().getStateForPlacement(ctx);
		if(blockState == null) return false;
		if(!targetBlock.isValidPosition(world, currentCandidate)) return false;

		AxisAlignedBB blockBB = new AxisAlignedBB(currentCandidate);
		return world.getEntitiesWithinAABB(LivingEntity.class, blockBB, EntityPredicates.NOT_SPECTATING).isEmpty();
	}

	private void getBlockPositionList() {
		EnumLock directionLock = EnumLock.NOLOCK;
		EnumLock faceLock = EnumLock.NOLOCK;

		LinkedList<BlockPos> candidates = new LinkedList<>();
		HashSet<BlockPos> allCandidates = new HashSet<>();

		Direction placeDirection = rayTraceResult.getFace();
		BlockState targetBlock = world.getBlockState(rayTraceResult.getPos());
		BlockPos startingPoint = rayTraceResult.getPos().offset(placeDirection);

		int directionMaskInt = directionLock.mask;
		int faceMaskInt = faceLock.mask;

		if (((directionLock != EnumLock.HORIZONTAL && directionLock != EnumLock.VERTICAL) || (placeDirection != Direction.UP && placeDirection != Direction.DOWN))
				&& (directionLock != EnumLock.NORTHSOUTH || (placeDirection != Direction.NORTH && placeDirection != Direction.SOUTH))
				&& (directionLock != EnumLock.EASTWEST || (placeDirection != Direction.EAST && placeDirection != Direction.WEST))) {
			candidates.add(startingPoint);
		}
		while(!candidates.isEmpty() && blocksToPlace.size() < maxBlocks) {

			BlockPos currentCandidate = candidates.removeFirst();
			try {
				BlockPos supportingPoint = currentCandidate.offset(placeDirection.getOpposite());
				BlockState candidateSupportingBlock = world.getBlockState(supportingPoint);

				if (shouldContinue(currentCandidate, targetBlock, candidateSupportingBlock) && allCandidates.add(currentCandidate)) {
					blocksToPlace.add(currentCandidate);

					switch (placeDirection) {
						case DOWN:
						case UP:
							if ((faceMaskInt & EnumLock.UP_DOWN_MASK) > 0) {
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
							}
							break;
						case NORTH:
						case SOUTH:
							if ((faceMaskInt & EnumLock.NORTH_SOUTH_MASK) > 0) {
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
							}
							break;
						case WEST:
						case EAST:
							if ((faceMaskInt & EnumLock.EAST_WEST_MASK) > 0) {
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
			}
			catch(Exception e) {
				// Can't do anything, could be anything.
				// Skip if anything goes wrong.
			}
		}
	}

	private boolean placeBlock(BlockPos blockPos, Item blockItem) {
		BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, Hand.MAIN_HAND, new BlockRayTraceResult(rayTraceResult.getHitVec(), rayTraceResult.getFace(), blockPos, false)));
		if(!ctx.canPlace()) return false;
		BlockState placeBlock = Block.getBlockFromItem(blockItem).getStateForPlacement(ctx);
		if(placeBlock == null) return false;

		BlockState targetBlock = world.getBlockState(rayTraceResult.getPos());

		if(targetBlock.has(HorizontalBlock.HORIZONTAL_FACING)) {
			placeBlock = placeBlock.with(HorizontalBlock.HORIZONTAL_FACING, targetBlock.get(HorizontalBlock.HORIZONTAL_FACING));
		}
		if(targetBlock.has(BlockStateProperties.AXIS)) {
			placeBlock = placeBlock.with(BlockStateProperties.AXIS, targetBlock.get(BlockStateProperties.AXIS));
		}

		BlockSnapshot snapshot = new BlockSnapshot(world, blockPos, placeBlock);
		BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(snapshot, placeBlock, player);
		MinecraftForge.EVENT_BUS.post(placeEvent);
		if(placeEvent.isCanceled()) return false;

		if(!world.setBlockState(blockPos, placeBlock)) return false;
		world.notifyNeighbors(blockPos, placeBlock.getBlock());

		player.addStat(Stats.ITEM_USED.get(blockItem));
		SoundType sound = placeBlock.getSoundType();
		world.playSound(player, blockPos, sound.getPlaceSound(), SoundCategory.BLOCKS, sound.volume, sound.pitch);

		return true;
	}

	public boolean doIt() {
		placeSnapshots = new LinkedList<>();

		takeItems(blocksToPlace.size());
		int iB = 0;

		place_items:
		for(Item item : placeItems.keySet()) {
			for(int i=0; i<placeItems.get(item); i++) {
				ItemWand wandItem = (ItemWand) wand.getItem();
				if(iB >= blocksToPlace.size() || wand.isEmpty() || wandItem.getLimit(player, wand) == 0) break place_items;
				BlockPos pos = blocksToPlace.get(iB);

				if(placeBlock(pos, item)) {
					//placeSnapshots.add(BlockSnapshot.getBlockSnapshot(world, pos));
					placeSnapshots.add(new PlaceSnapshot(world, pos, item));
					wand.damageItem(1, player, (e) -> e.sendBreakAnimation(player.swingingHand));
				}
				else {
					ConstructionWand.LOGGER.info("[CWand] Place error. Return item: "+item.toString());
					ItemStack stack = new ItemStack(item);
					if(!player.inventory.addItemStackToInventory(stack)) {
						player.dropItem(stack, false);
					}
					player.inventory.markDirty();
				}

				iB++;
			}
		}

		if(!placeSnapshots.isEmpty()) {
			ConstructionWand.instance.jobHistory.add(this);
			return true;
		}
		return false;
	}

	public boolean undo() {
		for(PlaceSnapshot snapshot : placeSnapshots) {
			BlockState currentBlock = world.getBlockState(snapshot.pos);

			// If placed block is still present and can be broken, break it and return item
			if(currentBlock.equals(snapshot.block) && world.isBlockModifiable(player, snapshot.pos) && (player.isCreative() || currentBlock.getBlockHardness(world, snapshot.pos) > -1)) {
				BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, snapshot.pos, currentBlock, player);
				MinecraftForge.EVENT_BUS.post(breakEvent);
				if(breakEvent.isCanceled()) continue;

				if(!world.setBlockState(snapshot.pos, Blocks.AIR.getDefaultState())) continue;
				world.notifyNeighbors(snapshot.pos, Blocks.AIR);

				ItemStack stack = new ItemStack(snapshot.item);
				if(!player.inventory.addItemStackToInventory(stack)) {
					player.dropItem(stack, false);
				}
			}
		}
		player.inventory.markDirty();
		return true;
	}
}
