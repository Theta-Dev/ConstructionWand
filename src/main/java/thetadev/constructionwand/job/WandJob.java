package thetadev.constructionwand.job;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.IProperty;
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
import thetadev.constructionwand.basics.*;
import thetadev.constructionwand.containers.ContainerManager;
import thetadev.constructionwand.items.ItemWand;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public abstract class WandJob
{
	protected PlayerEntity player;
	protected World world;
	protected BlockRayTraceResult rayTraceResult;
	protected ItemStack wand;
	protected ItemWand wandItem;
	protected WandOptions options;

	protected int maxBlocks;
	protected boolean ignoreFluid;
	protected boolean targetDirection;

	protected LinkedHashMap<Item, Integer> placeItems;
	protected LinkedList<PlaceSnapshot> placeSnapshots;

	public WandJob(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, ItemStack wand)
	{
		this.player = player;
		this.world = world;
		this.rayTraceResult = rayTraceResult;
		placeItems = new LinkedHashMap<>();
		placeSnapshots = new LinkedList<>();

		// Get wand
		if(wand == null || wand == ItemStack.EMPTY || !(wand.getItem() instanceof ItemWand)) return;
		this.wand = wand;

		wandItem = (ItemWand) wand.getItem();

		// Get options
		options = new WandOptions(wand);
		ignoreFluid = options.getOption(EnumFluidLock.IGNORE) == EnumFluidLock.IGNORE;
		targetDirection = options.getOption(EnumDirection.TARGET) == EnumDirection.TARGET;

		// Target block + item
		BlockPos targetPos = rayTraceResult.getPos();
		BlockState targetState = world.getBlockState(targetPos);
		Block targetBlock = targetState.getBlock();
		Item item;
		ItemStack offhandStack = player.getHeldItem(Hand.OFF_HAND);

		if(!offhandStack.isEmpty() && offhandStack.getItem() instanceof BlockItem) item = offhandStack.getItem();
		else item = targetBlock.getBlock().getItem(world, targetPos, targetState).getItem();
		if(item == Items.AIR) return;

		placeItems.put(item, 0);

		// Get substitutions (e.g. Grass -> Dirt)
		SubstitutionManager substitutionManager = ConstructionWand.instance.substitutionManager;
		for(Item it : substitutionManager.getSubstitutions(item)) {
			placeItems.put(it, 0);
		}

		// Get inventory supply
		maxBlocks = Math.min(countItems(), wandItem.getLimit(player, wand));
		ConstructionWand.LOGGER.info("MaxBlocks: "+maxBlocks);
		if(maxBlocks == 0) return;

		getBlockPositionList();
	}

	public LinkedList<BlockPos> getBlockPositions() {
		LinkedList<BlockPos> res = new LinkedList<>();

		for(PlaceSnapshot snapshot : placeSnapshots) {
			res.add(snapshot.pos);
		}
		return res;
	}

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

	protected int countItems()
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

	protected int takeItems(int count)
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

	protected abstract void getBlockPositionList();

	protected boolean shouldContinue(BlockPos currentCandidate, BlockState targetBlock, BlockState candidateSupportingBlock) {
		// Can place block on currentCandidate position?
		if(!world.isBlockPresent(currentCandidate)) return false;

		if(!world.isAirBlock(currentCandidate)){
			Block currrentCandidateBlock = world.getBlockState(currentCandidate).getBlock();
			if(!(ignoreFluid && (currrentCandidateBlock instanceof IFluidBlock || currrentCandidateBlock instanceof FlowingFluidBlock))) return false;
		};

		// Is supporting block equal to target block?
		if(!targetBlock.getBlock().equals(candidateSupportingBlock.getBlock())) return false;

		// Can block be placed?
		BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, Hand.MAIN_HAND, new BlockRayTraceResult(rayTraceResult.getHitVec(), rayTraceResult.getFace(), currentCandidate, false)));
		if(!ctx.canPlace()) return false;

		BlockState blockState = targetBlock.getBlock().getStateForPlacement(ctx);
		if(blockState == null) return false;
		if(!targetBlock.isValidPosition(world, currentCandidate)) return false;

		// No entities in area?
		AxisAlignedBB blockBB = new AxisAlignedBB(currentCandidate);
		return world.getEntitiesWithinAABB(LivingEntity.class, blockBB, EntityPredicates.NOT_SPECTATING).isEmpty();
	}

	private boolean placeBlock(PlaceSnapshot placeSnapshot) {
		Item blockItem = placeSnapshot.item;
		BlockPos blockPos = placeSnapshot.pos;

		BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, Hand.MAIN_HAND, new BlockRayTraceResult(rayTraceResult.getHitVec(), rayTraceResult.getFace(), blockPos, false)));
		if(!ctx.canPlace()) return false;
		BlockState placeBlock = Block.getBlockFromItem(blockItem).getStateForPlacement(ctx);
		if(placeBlock == null) return false;

		BlockState supportingBlock = placeSnapshot.supportingBlock;

		if(targetDirection && placeBlock.getBlock() == supportingBlock.getBlock()) {

			// Block properties to be copied (alignment/rotation properties)
			for(IProperty property : new IProperty[] {
					BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.FACING, BlockStateProperties.FACING_EXCEPT_UP, BlockStateProperties.ROTATION_0_15, BlockStateProperties.AXIS})
			{
				if(supportingBlock.has(property)) {
					placeBlock = placeBlock.with(property, supportingBlock.get(property));
				}
			}
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

		placeSnapshot.block = placeBlock;
		return true;
	}

	public boolean doIt() {
		takeItems(placeSnapshots.size());
		int iB = 0;

		place_items:
		for(Item item : placeItems.keySet()) {
			for(int i=0; i<placeItems.get(item); i++) {
				ItemWand wandItem = (ItemWand) wand.getItem();
				if(iB >= placeSnapshots.size() || wand.isEmpty() || wandItem.getLimit(player, wand) == 0) break place_items;

				PlaceSnapshot snapshot = placeSnapshots.get(iB);
				BlockPos pos = snapshot.pos;
				snapshot.item = item;

				if(placeBlock(snapshot)) {
					wand.damageItem(1, player, (e) -> e.sendBreakAnimation(player.swingingHand));
				}
				else if(!player.isCreative()) {
					ConstructionWand.LOGGER.info("[CWand] Place error. Return item: "+item.toString());
					ItemStack stack = new ItemStack(item);
					if(!player.inventory.addItemStackToInventory(stack)) {
						player.dropItem(stack, false);
					}
					player.inventory.markDirty();
					placeSnapshots.remove(snapshot);
				}

				iB++;
			}
		}

		// Remove snapshots that were not placed
		while(iB < placeSnapshots.size()) placeSnapshots.remove(iB);

		// Add to job history for undo
		if(placeSnapshots.size() > 1) ConstructionWand.instance.jobHistory.add(this);

		return !placeSnapshots.isEmpty();
	}

	public boolean undo() {
		for(PlaceSnapshot snapshot : placeSnapshots) {
			BlockState currentBlock = world.getBlockState(snapshot.pos);

			// If placed block is still present and can be broken, break it and return item
			if(currentBlock.getBlock() == snapshot.block.getBlock() && world.isBlockModifiable(player, snapshot.pos) &&
					(player.isCreative() || (currentBlock.getBlockHardness(world, snapshot.pos) > -1 && world.getTileEntity(snapshot.pos) == null))) {
				BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, snapshot.pos, currentBlock, player);
				MinecraftForge.EVENT_BUS.post(breakEvent);
				if(breakEvent.isCanceled()) continue;

				if(!world.setBlockState(snapshot.pos, Blocks.AIR.getDefaultState())) continue;
				world.notifyNeighbors(snapshot.pos, Blocks.AIR);

				if(!player.isCreative()) {
					ItemStack stack = new ItemStack(snapshot.item);
					if(!player.inventory.addItemStackToInventory(stack)) {
						player.dropItem(stack, false);
					}
				}
			}
		}
		player.inventory.markDirty();
		return true;
	}
}
