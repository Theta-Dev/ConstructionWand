package thetadev.constructionwand.basics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.containers.ContainerManager;
import thetadev.constructionwand.items.wand.ItemWand;
import thetadev.constructionwand.wand.WandItemUseContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WandUtil
{
    public static boolean stackEquals(ItemStack stackA, ItemStack stackB) {
        return ItemStack.isSameItemSameComponents(stackA, stackB);
    }

    public static boolean stackEquals(ItemStack stackA, Item item) {
        ItemStack stackB = new ItemStack(item);
        return stackEquals(stackA, stackB);
    }

    public static ItemStack holdingWand(Player player) {
        if(player.getItemInHand(InteractionHand.MAIN_HAND) != ItemStack.EMPTY && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemWand) {
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        }
        else if(player.getItemInHand(InteractionHand.OFF_HAND) != ItemStack.EMPTY && player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ItemWand) {
            return player.getItemInHand(InteractionHand.OFF_HAND);
        }
        return null;
    }

    public static BlockPos posFromVec(Vec3 vec) {
        return new BlockPos(
                (int) Math.round(vec.x), (int) Math.round(vec.y), (int) Math.round(vec.z));
    }

    public static Vec3 entityPositionVec(Entity entity) {
        return new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ());
    }

    public static Vec3 blockPosVec(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public static List<ItemStack> getHotbar(Player player) {
        return player.getInventory().items.subList(0, 9);
    }

    public static List<ItemStack> getHotbarWithOffhand(Player player) {
        ArrayList<ItemStack> inventory = new ArrayList<>(player.getInventory().items.subList(0, 9));
        inventory.addAll(player.getInventory().offhand);
        return inventory;
    }

    public static List<ItemStack> getMainInv(Player player) {
        return player.getInventory().items.subList(9, player.getInventory().items.size());
    }

    public static List<ItemStack> getFullInv(Player player) {
        ArrayList<ItemStack> inventory = new ArrayList<>(player.getInventory().offhand);
        inventory.addAll(player.getInventory().items);
        return inventory;
    }

    public static int blockDistance(BlockPos p1, BlockPos p2) {
        return Math.max(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getZ() - p2.getZ()));
    }

    public static boolean isTEAllowed(BlockState state) {
        if(!state.hasBlockEntity()) return true;

        ResourceLocation name = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if(name == null) return false;

        String fullId = name.toString();
        String modId = name.getNamespace();

        boolean inList = ConfigServer.TE_LIST.get().contains(fullId) || ConfigServer.TE_LIST.get().contains(modId);
        boolean isWhitelist = ConfigServer.TE_WHITELIST.get();

        return isWhitelist == inList;
    }

    public static boolean placeBlock(Level world, Player player, BlockState block, BlockPos pos, @Nullable BlockItem item) {
        if(!world.setBlockAndUpdate(pos, block)) {
            ConstructionWand.LOGGER.info("Block could not be placed");
            return false;
        }

        // Remove block if placeEvent is canceled
        BlockSnapshot snapshot = BlockSnapshot.create(world.dimension(), world, pos);
        BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(snapshot, block, player);
        NeoForge.EVENT_BUS.post(placeEvent);
        if(placeEvent.isCanceled()) {
            world.removeBlock(pos, false);
            return false;
        }

        ItemStack stack;
        if(item == null) stack = new ItemStack(block.getBlock().asItem());
        else {
            stack = new ItemStack(item);
            player.awardStat(Stats.ITEM_USED.get(item));
        }

        // Call OnBlockPlaced method
        block.getBlock().setPlacedBy(world, pos, block, player, stack);

        return true;
    }

    public static boolean removeBlock(Level world, Player player, @Nullable BlockState block, BlockPos pos) {
        BlockState currentBlock = world.getBlockState(pos);

        if(!world.mayInteract(player, pos)) return false;

        if(!player.isCreative()) {
            if(currentBlock.getDestroySpeed(world, pos) <= -1 || world.getBlockEntity(pos) != null) return false;

            if(block != null)
                if(!ReplacementRegistry.matchBlocks(currentBlock.getBlock(), block.getBlock())) return false;
        }

        BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, pos, currentBlock, player);
        NeoForge.EVENT_BUS.post(breakEvent);
        if(breakEvent.isCanceled()) return false;

        world.removeBlock(pos, false);
        return true;
    }

    public static int countItem(Player player, Item item) {
        if(player.getInventory().items == null) return 0;
        if(player.isCreative()) return Integer.MAX_VALUE;

        int total = 0;
        ContainerManager containerManager = ConstructionWand.instance.containerManager;
        List<ItemStack> inventory = WandUtil.getFullInv(player);

        for(ItemStack stack : inventory) {
            if(stack == null || stack.isEmpty()) continue;

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

    private static boolean isPositionModifiable(Level world, Player player, BlockPos pos) {
        // Is position out of world?
        if(!world.isInWorldBounds(pos)) return false;

        // Is block modifiable?
        if(!world.mayInteract(player, pos)) return false;

        // Limit range
        if(ConfigServer.MAX_RANGE.get() > 0 &&
                WandUtil.blockDistance(player.blockPosition(), pos) > ConfigServer.MAX_RANGE.get()) return false;

        return true;
    }

    /**
     * Tests if a wand can place a block at a certain position.
     * This check is independent of the used block.
     */
    public static boolean isPositionPlaceable(Level world, Player player, BlockPos pos, boolean replace) {
        if(!isPositionModifiable(world, player, pos)) return false;

        // If replace mode is off, target has to be air
        if(world.isEmptyBlock(pos)) return true;

        // Otherwise, check if the block can be replaced by a generic block
        return replace && world.getBlockState(pos).canBeReplaced(
                new WandItemUseContext(world, player,
                        new BlockHitResult(new Vec3(0, 0, 0), Direction.DOWN, pos, false),
                        pos, (BlockItem) Items.STONE));
    }

    public static boolean isBlockRemovable(Level world, Player player, BlockPos pos) {
        if(!isPositionModifiable(world, player, pos)) return false;

        if(!player.isCreative()) {
            return !(world.getBlockState(pos).getDestroySpeed(world, pos) <= -1) && world.getBlockEntity(pos) == null;
        }
        return true;
    }

    public static boolean isBlockPermeable(Level world, BlockPos pos) {
        return world.isEmptyBlock(pos) || world.getBlockState(pos).getCollisionShape(world, pos).isEmpty();
    }

    public static boolean entitiesCollidingWithBlock(Level world, BlockState blockState, BlockPos pos) {
        VoxelShape shape = blockState.getCollisionShape(world, pos);
        if(!shape.isEmpty()) {
            AABB blockBB = shape.bounds().move(pos);
            return !world.getEntitiesOfClass(LivingEntity.class, blockBB, Predicate.not(Entity::isSpectator)).isEmpty();
        }
        return false;
    }

    public static Direction fromVector(Vec3 vector) {
        return Direction.getNearest(vector.x, vector.y, vector.z);
    }
}
