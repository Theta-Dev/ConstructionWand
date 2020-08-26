package thetadev.constructionwand.job;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.*;
import thetadev.constructionwand.basics.options.*;
import thetadev.constructionwand.containers.ContainerManager;
import thetadev.constructionwand.items.ItemWand;

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
	protected boolean doReplace;
	protected boolean targetDirection;

	protected BlockItem placeItem;
	protected LinkedList<PlaceSnapshot> placeSnapshots;

	public WandJob(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, ItemStack wand)
	{
		this.player = player;
		this.world = world;
		this.rayTraceResult = rayTraceResult;
		placeSnapshots = new LinkedList<>();

		// Get wand
		if(wand == null || wand == ItemStack.EMPTY || !(wand.getItem() instanceof ItemWand)) return;
		this.wand = wand;

		wandItem = (ItemWand) wand.getItem();

		// Get options
		options = new WandOptions(wand);
		doReplace = options.getOption(EnumReplace.YES) == EnumReplace.YES;
		targetDirection = options.getOption(EnumDirection.TARGET) == EnumDirection.TARGET;

		BlockPos targetPos = rayTraceResult.getPos();
		BlockState targetState = world.getBlockState(targetPos);
		Block targetBlock = targetState.getBlock();
		ItemStack offhandStack = player.getHeldItem(Hand.OFF_HAND);

		// Get place item
		Item item;
		if(!offhandStack.isEmpty() && offhandStack.getItem() instanceof BlockItem) item = offhandStack.getItem();
		else {
			//item = targetBlock.getBlock().getItem(world, targetPos, targetState).getItem();
			item = targetBlock.getPickBlock(targetState, rayTraceResult, world, targetPos, player).getItem();
		}
		if(!(item instanceof BlockItem)) return;
		placeItem = (BlockItem) item;

		// Get inventory supply
		maxBlocks = Math.min(countItems(), wandItem.getLimit(player, wand));
		if(maxBlocks == 0) return;

		getBlockPositionList();
	}

	public static WandJob getJob(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, ItemStack itemStack) {
		IEnumOption mode = new WandOptions(itemStack).getOption(EnumMode.DEFAULT);

		if(mode == EnumMode.ANGEL) return new TransductionJob(player, world, rayTraceResult, itemStack);
		else return new ConstructionJob(player, world, rayTraceResult, itemStack);
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

	protected int countItems()
	{
		if(player.inventory == null || player.inventory.mainInventory == null) return 0;
		if(player.isCreative()) return Integer.MAX_VALUE;

		int total = 0;
		ContainerManager containerManager = ConstructionWand.instance.containerManager;
		LinkedList<ItemStack> inventory = new LinkedList<>(player.inventory.offHandInventory);
		inventory.addAll(player.inventory.mainInventory);

		for(ItemStack stack : inventory) {
			if(stack == null) continue;

			if(WandUtil.stackEquals(stack, placeItem)) {
				total += Math.max(0, stack.getCount());
			}
			else {
				int amount = containerManager.countItems(player, new ItemStack(placeItem), stack);
				if(amount == Integer.MAX_VALUE) return Integer.MAX_VALUE;
				total += amount;
			}
		}
		return total;
	}

	// Attempts to take specified number of items, returns number of missing items
	protected int takeItems(int count)
	{
		if(player.inventory == null || player.inventory.mainInventory == null) return count;
		if(player.isCreative()) return 0;

		LinkedList<ItemStack> hotbar = new LinkedList<>(player.inventory.mainInventory.subList(0, 9));
		hotbar.addAll(player.inventory.offHandInventory);
		LinkedList<ItemStack> mainInv = new LinkedList<>(player.inventory.mainInventory.subList(9, player.inventory.mainInventory.size()));

		// Take items from main inv, loose items first
		count = takeItemsInvList(count, mainInv, false);
		count = takeItemsInvList(count, mainInv, true);

		// Take items from hotbar, containers first
		count = takeItemsInvList(count, hotbar, true);
		count = takeItemsInvList(count, hotbar, false);

		return count;
	}

	private int takeItemsInvList(int count, LinkedList<ItemStack> inv, boolean container) {
		ContainerManager containerManager = ConstructionWand.instance.containerManager;

		for(ItemStack stack : inv) {
			if(count == 0) break;

			if(container) {
				int nCount = containerManager.useItems(player, new ItemStack(placeItem), stack, count);
				count = nCount;
			}

			if(!container && WandUtil.stackEquals(stack, placeItem)) {
				int toTake = Math.min(count, stack.getCount());
				stack.shrink(toTake);
				count -= toTake;
				player.inventory.markDirty();
			}
		}
		return count;
	}

	protected abstract void getBlockPositionList();

	protected boolean canPlace(BlockPos pos) {
		// Is position out of world?
		if(!world.isBlockPresent(pos)) return false;

		// Is block at pos replaceable?
		//BlockItemUseContext ctx = new WandItemUseContext(world, player, new ItemStack(placeItem),  new BlockRayTraceResult(rayTraceResult.getHitVec(), rayTraceResult.getFace(), pos, false));
		BlockItemUseContext ctx = new WandItemUseContext(this, pos);
		if(!ctx.canPlace()) return false;

		// If replace mode is off, target has to be air
		if(!doReplace && !world.isAirBlock(pos)) return false;

		// Can block be placed?
		BlockState blockState = placeItem.getBlock().getStateForPlacement(ctx);
		if(blockState == null) return false;
		blockState = Block.getValidBlockForPosition(blockState, world, pos);
		if(blockState.getBlock() == Blocks.AIR || !blockState.isValidPosition(world, pos)) return false;

		// No entities in area?
		AxisAlignedBB blockBB = blockState.getCollisionShape(world, pos).getBoundingBox().offset(pos);
		return world.getEntitiesWithinAABB(LivingEntity.class, blockBB, EntityPredicates.NOT_SPECTATING).isEmpty();
	}

	private boolean placeBlock(PlaceSnapshot placeSnapshot) {
		BlockPos blockPos = placeSnapshot.pos;

		//BlockItemUseContext ctx = new WandItemUseContext(world, player, new ItemStack(placeItem),  new BlockRayTraceResult(rayTraceResult.getHitVec(), rayTraceResult.getFace(), blockPos, false));
		BlockItemUseContext ctx = new WandItemUseContext(this, blockPos);
		if(!ctx.canPlace()) return false;

		BlockState placeBlock = Block.getBlockFromItem(placeItem).getStateForPlacement(ctx);
		if(placeBlock == null) return false;
		placeBlock = Block.getValidBlockForPosition(placeBlock, world, blockPos);
		if(placeBlock.getBlock() == Blocks.AIR) return false;

		BlockState supportingBlock = placeSnapshot.supportingBlock;

		if(targetDirection && placeBlock.getBlock() == supportingBlock.getBlock()) {
			// Block properties to be copied (alignment/rotation properties)
			for(IProperty property : new IProperty[] {
					BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.FACING, BlockStateProperties.FACING_EXCEPT_UP,
					BlockStateProperties.ROTATION_0_15, BlockStateProperties.AXIS, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE})
			{
				if(supportingBlock.has(property)) {
					placeBlock = placeBlock.with(property, supportingBlock.get(property));
				}
			}

			// Dont dupe double slabs
			if(supportingBlock.has(BlockStateProperties.SLAB_TYPE)) {
				SlabType slabType = supportingBlock.get(BlockStateProperties.SLAB_TYPE);
				if(slabType != SlabType.DOUBLE) placeBlock = placeBlock.with(BlockStateProperties.SLAB_TYPE, slabType);
			}
		}
		// Abort if placeEvent is canceled
		BlockSnapshot snapshot = new BlockSnapshot(world, blockPos, placeBlock);
		BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(snapshot, placeBlock, player);
		MinecraftForge.EVENT_BUS.post(placeEvent);
		if(placeEvent.isCanceled()) return false;

		// Place the block
		if(!world.setBlockState(blockPos, placeBlock)) {
			ConstructionWand.LOGGER.info("Block could not be placed");
			return false;
		}
		world.notifyNeighbors(blockPos, placeBlock.getBlock());

		// Update stats
		player.addStat(Stats.ITEM_USED.get(placeItem));
		player.addStat(ModStats.USE_WAND);

		placeSnapshot.block = placeBlock;
		return true;
	}

	public boolean doIt() {
		LinkedList<PlaceSnapshot> placed = new LinkedList<>();

		for(PlaceSnapshot snapshot : placeSnapshots) {
			if(wand.isEmpty() || wandItem.getLimit(player, wand) == 0) continue;

			BlockPos pos = snapshot.pos;

			if(placeBlock(snapshot)) {
				wand.damageItem(1, player, (e) -> e.sendBreakAnimation(player.swingingHand));

				// If the item cant be taken, undo the placement
				if(takeItems(1) == 0) placed.add(snapshot);
				else {
					ConstructionWand.LOGGER.info("Item could not be taken. Remove block: "+placeItem.toString());
					world.removeBlock(pos, false);
				}
			}
		}
		placeSnapshots = placed;

		// Play place sound
		if(!placeSnapshots.isEmpty()) {
			SoundType sound = placeSnapshots.getFirst().block.getSoundType();
			world.playSound(null, player.getPosition(), sound.getPlaceSound(), SoundCategory.BLOCKS, sound.volume, sound.pitch);
		}

		// Add to job history for undo
		if(placeSnapshots.size() > 1) ConstructionWand.instance.jobHistory.add(this);

		return !placeSnapshots.isEmpty();
	}

	public boolean undo() {
		for(PlaceSnapshot snapshot : placeSnapshots) {
			BlockState currentBlock = world.getBlockState(snapshot.pos);

			// If placed block is still present and can be broken, break it and return item
			if(world.isBlockModifiable(player, snapshot.pos) &&
					(player.isCreative() ||
					(currentBlock.getBlockHardness(world, snapshot.pos) > -1 && world.getTileEntity(snapshot.pos) == null && currentBlock.getBlock() == snapshot.block.getBlock())))
			{
				BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, snapshot.pos, currentBlock, player);
				MinecraftForge.EVENT_BUS.post(breakEvent);
				if(breakEvent.isCanceled()) continue;

				world.removeBlock(snapshot.pos, false);

				if(!player.isCreative()) {
					ItemStack stack = new ItemStack(placeItem);
					if(!player.inventory.addItemStackToInventory(stack)) {
						player.dropItem(stack, false);
					}
				}
			}
		}
		player.inventory.markDirty();

		// Play teleport sound
		SoundEvent sound = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
		world.playSound(null, player.getPosition(), sound, SoundCategory.PLAYERS, 1.0F, 1.0F);

		return true;
	}
}
