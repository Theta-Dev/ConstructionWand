package thetadev.constructionwand.job;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.*;
import thetadev.constructionwand.basics.options.*;
import thetadev.constructionwand.containers.ContainerManager;
import thetadev.constructionwand.items.ItemWand;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class WandJob
{
	protected PlayerEntity player;
	protected World world;
	protected BlockRayTraceResult rayTraceResult;
	protected ItemStack wand;
	protected ItemWand wandItem;
	protected WandOptions options;

	// Wand options
	protected int maxBlocks;
	protected boolean doReplace;
	protected boolean targetDirection;
	protected boolean randomMode;
	protected EnumMatch matchMode;

	protected LinkedHashMap<BlockItem, Integer> itemCounts;
	protected HashMap<BlockItem, Integer> itemWeights;

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
		randomMode = options.getOption(EnumRandom.NO) == EnumRandom.YES;
		matchMode = (EnumMatch) options.getOption(EnumMatch.SIMILAR);

		// Get place item
		addBlockItems();
		if(itemCounts.isEmpty()) return;

		// Get inventory supply
		for(int v : itemCounts.values()) {
			try {
				maxBlocks = Math.addExact(maxBlocks, v);
			}
			catch(ArithmeticException e) {
				maxBlocks = Integer.MAX_VALUE;
				break;
			}
		}

		maxBlocks = Math.min(maxBlocks, wandItem.getLimit(player, wand));
		if(maxBlocks == 0) return;

		getBlockPositionList();
	}

	public static WandJob getJob(PlayerEntity player, World world, BlockRayTraceResult rayTraceResult, ItemStack itemStack) {
		IEnumOption mode = new WandOptions(itemStack).getOption(EnumMode.DEFAULT);

		if(mode == EnumMode.ANGEL) return new TransductionJob(player, world, rayTraceResult, itemStack);
		else return new ConstructionJob(player, world, rayTraceResult, itemStack);
	}

	public Set<BlockPos> getBlockPositions() {
		return placeSnapshots.stream().map(snapshot -> snapshot.pos).collect(Collectors.toSet());
	}

	public BlockRayTraceResult getRayTraceResult() { return rayTraceResult; }

	public BlockPos getTargetPos() { return rayTraceResult.getPos(); }

	public PlayerEntity getPlayer() { return player; }

	public void setPlayer(PlayerEntity player) { this.player = player; }

	public World getWorld() { return world; }

	public void setWorld(World world) { this.world = world; }

	public ItemStack getWand() { return wand; }

	private void addBlockItem(BlockItem item) {
		int count = countItem(item);
		if(count > 0) itemCounts.put(item, count);
	}

	private void addBlockItems() {
		itemCounts = new LinkedHashMap<>();
		itemWeights = new HashMap<>();

		BlockPos targetPos = rayTraceResult.getPos();
		BlockState targetState = world.getBlockState(targetPos);
		Block targetBlock = targetState.getBlock();
		ItemStack offhandStack = player.getHeldItem(Hand.OFF_HAND);

		if(randomMode) {
			for(ItemStack stack : WandUtil.getHotbar(player)) {
				if(stack.getItem() instanceof BlockItem) {
					BlockItem item = (BlockItem) stack.getItem();
					addBlockItem(item);
					itemWeights.compute(item, (k, v) -> (v == null) ? 1 : v+1);
				}
			}
		}
		else if(!offhandStack.isEmpty() && offhandStack.getItem() instanceof BlockItem) {
			// Block in offhand -> override
			addBlockItem((BlockItem) offhandStack.getItem());
		}

		// Otherwise use target block
		if(itemCounts.isEmpty()) {
			Item item = targetBlock.getPickBlock(targetState, rayTraceResult, world, targetPos, player).getItem();
			if(item instanceof BlockItem) {
				addBlockItem((BlockItem) item);

				// Add replacement items
				if(matchMode != EnumMatch.EXACT) {
					for(Item it : ReplacementRegistry.getMatchingSet(item)) {
						if(it instanceof BlockItem) addBlockItem((BlockItem) it);
					}
				}
			}
			randomMode = false;
		}
	}

	private int countItem(Item item) {
		if(player.inventory == null || player.inventory.mainInventory == null) return 0;
		if(player.isCreative()) return Integer.MAX_VALUE;

		int total = 0;
		ContainerManager containerManager = ConstructionWand.instance.containerManager;
		List<ItemStack> inventory = WandUtil.getFullInv(player);

		for(ItemStack stack : inventory) {
			if(stack == null) continue;

			if(WandUtil.stackEquals(stack, item)) {
				total += stack.getCount();
			}
			else {
				int amount = containerManager.countItems(player, new ItemStack(item), stack);
				if(amount == Integer.MAX_VALUE) return Integer.MAX_VALUE;
				total += amount;
			}
		}
		return total;
	}

	// Attempts to take specified number of items, returns number of missing items
	private int takeItems(Item item, int count)
	{
		if(player.inventory == null || player.inventory.mainInventory == null) return count;
		if(player.isCreative()) return 0;

		List<ItemStack> hotbar = WandUtil.getHotbarWithOffhand(player);
		List<ItemStack> mainInv = WandUtil.getMainInv(player);

		// Take items from main inv, loose items first
		count = takeItemsInvList(count, item, mainInv, false);
		count = takeItemsInvList(count, item, mainInv, true);

		// Take items from hotbar, containers first
		count = takeItemsInvList(count, item, hotbar, true);
		count = takeItemsInvList(count, item, hotbar, false);

		return count;
	}

	private int takeItemsInvList(int count, Item item, List<ItemStack> inv, boolean container) {
		ContainerManager containerManager = ConstructionWand.instance.containerManager;

		for(ItemStack stack : inv) {
			if(count == 0) break;

			if(container) {
				int nCount = containerManager.useItems(player, new ItemStack(item), stack, count);
				count = nCount;
			}

			if(!container && WandUtil.stackEquals(stack, item)) {
				int toTake = Math.min(count, stack.getCount());
				stack.shrink(toTake);
				count -= toTake;
				player.inventory.markDirty();
			}
		}
		return count;
	}

	protected abstract void getBlockPositionList();

	@Nullable
	private BlockState getPlaceBlockstate(BlockPos pos, BlockItem item, BlockState supportingBlock) {
		// Is position out of world?
		if(!world.isBlockPresent(pos)) return null;

		// Is block at pos replaceable?
		BlockItemUseContext ctx = new WandItemUseContext(this, pos, item);
		if(!ctx.canPlace()) return null;

		// If replace mode is off, target has to be air
		if(!doReplace && !world.isAirBlock(pos)) return null;

		// Can block be placed?
		BlockState placeBlock = Block.getBlockFromItem(item).getStateForPlacement(ctx);
		if(placeBlock == null) return null;
		placeBlock = Block.getValidBlockForPosition(placeBlock, world, pos);
		if(placeBlock.getBlock() == Blocks.AIR || !placeBlock.isValidPosition(world, pos)) return null;

		// No entities colliding?
		VoxelShape shape = placeBlock.getCollisionShape(world, pos);
		if(!shape.isEmpty()) {
			AxisAlignedBB blockBB = shape.getBoundingBox().offset(pos);
			if(!world.getEntitiesWithinAABB(LivingEntity.class, blockBB, EntityPredicates.NOT_SPECTATING).isEmpty()) return null;
		}

		// Copy certain properties of supporting block (save the effort when running preview on client)
		if(targetDirection && !world.isRemote) {
			// Block properties to be copied (alignment/rotation properties)
			for(Property property : new Property[] {
					BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.FACING, BlockStateProperties.FACING_EXCEPT_UP,
					BlockStateProperties.ROTATION_0_15, BlockStateProperties.AXIS, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE})
			{
				if(supportingBlock.hasProperty(property) && placeBlock.hasProperty(property)) {
					placeBlock = placeBlock.with(property, supportingBlock.get(property));
				}
			}

			// Dont dupe double slabs
			if(supportingBlock.hasProperty(BlockStateProperties.SLAB_TYPE) && placeBlock.hasProperty(BlockStateProperties.SLAB_TYPE)) {
				SlabType slabType = supportingBlock.get(BlockStateProperties.SLAB_TYPE);
				if(slabType != SlabType.DOUBLE) placeBlock = placeBlock.with(BlockStateProperties.SLAB_TYPE, slabType);
			}
		}
		return placeBlock;
	}

	@Nullable
	protected PlaceSnapshot getPlaceSnapshot(BlockPos pos, BlockState supportingBlock) {
		ArrayList<BlockItem> items = new ArrayList<>(itemCounts.keySet());
		if(randomMode) {
			for(BlockItem item : itemWeights.keySet()) {
				int weight = itemWeights.get(item);
				for(int i=0; i<weight-1; i++) items.add(item);
			}

			Collections.shuffle(items, player.getRNG());
		}

		for(BlockItem item : items) {
			int count = itemCounts.get(item);
			if(count == 0) continue;

			BlockState placeBlock = getPlaceBlockstate(pos, item, supportingBlock);
			if(placeBlock == null) continue;

			if(count < Integer.MAX_VALUE) itemCounts.merge(item, -1, Integer::sum);
			return new PlaceSnapshot(pos, placeBlock, item);
		}
		return null;
	}

	private boolean placeBlock(PlaceSnapshot placeSnapshot) {
		BlockPos blockPos = placeSnapshot.pos;
		BlockState placeBlock = placeSnapshot.block;

		// Place the block
		if(!world.setBlockState(blockPos, placeBlock)) {
			ConstructionWand.LOGGER.info("Block could not be placed");
			return false;
		}

		// Remove block if placeEvent is canceled
		BlockSnapshot snapshot = BlockSnapshot.create(world.func_234923_W_(), world, blockPos);
		BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(snapshot, placeBlock, player);
		MinecraftForge.EVENT_BUS.post(placeEvent);
		if(placeEvent.isCanceled()) {
			world.removeBlock(blockPos, false);
			return false;
		}

		// Update neighbor block states
		world.notifyNeighborsOfStateChange(blockPos, placeBlock.getBlock());

		// Update stats
		player.addStat(Stats.ITEM_USED.get(placeSnapshot.item));
		player.addStat(ModStats.USE_WAND);

		return true;
	}

	protected boolean matchBlocks(Block b1, Block b2) {
		switch(matchMode) {
			case EXACT: return b1 == b2;
			case SIMILAR: return ReplacementRegistry.matchBlocks(b1, b2);
			case ANY: return b1 != Blocks.AIR && b2 != Blocks.AIR;
		}
		return false;
	}

	public boolean doIt() {
		LinkedList<PlaceSnapshot> placed = new LinkedList<>();

		for(PlaceSnapshot snapshot : placeSnapshots) {
			if(wand.isEmpty() || wandItem.getLimit(player, wand) == 0) continue;

			BlockPos pos = snapshot.pos;
			BlockItem placeItem = snapshot.item;

			if(placeBlock(snapshot)) {
				wand.damageItem(1, player, (e) -> e.sendBreakAnimation(player.swingingHand));

				// If the item cant be taken, undo the placement
				if(takeItems(placeItem, 1) == 0) placed.add(snapshot);
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
			world.playSound(null, WandUtil.playerPos(player), sound.getPlaceSound(), SoundCategory.BLOCKS, sound.volume, sound.pitch);
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
					(currentBlock.getBlockHardness(world, snapshot.pos) > -1 && world.getTileEntity(snapshot.pos) == null && ReplacementRegistry.matchBlocks(currentBlock.getBlock(), snapshot.block.getBlock()))))
			{
				BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, snapshot.pos, currentBlock, player);
				MinecraftForge.EVENT_BUS.post(breakEvent);
				if(breakEvent.isCanceled()) continue;

				world.removeBlock(snapshot.pos, false);

				if(!player.isCreative()) {
					ItemStack stack = new ItemStack(snapshot.item);
					if(!player.inventory.addItemStackToInventory(stack)) {
						player.dropItem(stack, false);
					}
				}
			}
		}
		player.inventory.markDirty();

		// Play teleport sound
		SoundEvent sound = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
		world.playSound(null, WandUtil.playerPos(player), sound, SoundCategory.PLAYERS, 1.0F, 1.0F);

		return true;
	}
}
