package thetadev.constructionwand.basics;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.containers.ContainerManager;
import thetadev.constructionwand.items.wand.ItemWand;
import thetadev.constructionwand.wand.WandItemUseContext;
import thetadev.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WandUtil
{
    public static boolean stackEquals(ItemStack stackA, ItemStack stackB) {
        return ItemStack.areItemsEqual(stackA, stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
    }

    public static boolean stackEquals(ItemStack stackA, Item item) {
        ItemStack stackB = new ItemStack(item);
        return stackEquals(stackA, stackB);
    }

    public static ItemStack holdingWand(PlayerEntity player) {
        if(player.getHeldItem(Hand.MAIN_HAND) != ItemStack.EMPTY && player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof ItemWand) {
            return player.getHeldItem(Hand.MAIN_HAND);
        }
        else
            if(player.getHeldItem(Hand.OFF_HAND) != ItemStack.EMPTY && player.getHeldItem(Hand.OFF_HAND).getItem() instanceof ItemWand) {
                return player.getHeldItem(Hand.OFF_HAND);
            }
        return null;
    }

    public static BlockPos playerPos(PlayerEntity player) {
        return new BlockPos(player.getPositionVec());
    }

    public static Vector3d entityPositionVec(Entity entity) {
        return new Vector3d(entity.getPosX(), entity.getPosY() - entity.getYOffset() + entity.getHeight() / 2, entity.getPosZ());
    }

    public static Vector3d blockPosVec(BlockPos pos) {
        return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static List<ItemStack> getHotbar(PlayerEntity player) {
        return player.inventory.mainInventory.subList(0, 9);
    }

    public static List<ItemStack> getHotbarWithOffhand(PlayerEntity player) {
        ArrayList<ItemStack> inventory = new ArrayList<>(player.inventory.offHandInventory);
        inventory.addAll(player.inventory.mainInventory.subList(0, 9));
        return inventory;
    }

    public static List<ItemStack> getMainInv(PlayerEntity player) {
        return player.inventory.mainInventory.subList(9, player.inventory.mainInventory.size());
    }

    public static List<ItemStack> getFullInv(PlayerEntity player) {
        ArrayList<ItemStack> inventory = new ArrayList<>(player.inventory.offHandInventory);
        inventory.addAll(player.inventory.mainInventory);
        return inventory;
    }

    public static int maxRange(BlockPos p1, BlockPos p2) {
        return Math.max(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getZ() - p2.getZ()));
    }

    public static boolean isTEAllowed(BlockState state) {
        if(!state.hasTileEntity()) return true;

        ResourceLocation name = state.getBlock().getRegistryName();
        if(name == null) return false;

        String fullId = name.toString();
        String modId = name.getNamespace();

        boolean inList = ConfigServer.TE_LIST.get().contains(fullId) || ConfigServer.TE_LIST.get().contains(modId);
        boolean isWhitelist = ConfigServer.TE_WHITELIST.get();

        return isWhitelist == inList;
    }

    public static boolean placeBlock(World world, PlayerEntity player, BlockState block, BlockPos pos, BlockItem item) {
        if(!world.setBlockState(pos, block)) {
            ConstructionWand.LOGGER.info("Block could not be placed");
            return false;
        }

        // Remove block if placeEvent is canceled
        BlockSnapshot snapshot = BlockSnapshot.create(world.func_234923_W_(), world, pos);
        BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(snapshot, block, player);
        MinecraftForge.EVENT_BUS.post(placeEvent);
        if(placeEvent.isCanceled()) {
            world.removeBlock(pos, false);
            return false;
        }

        ItemStack stack;
        if(item == null) stack = new ItemStack(block.getBlock().asItem());
        else {
            stack = new ItemStack(item);
            player.addStat(Stats.ITEM_USED.get(item));
        }

        // Call OnBlockPlaced method
        block.getBlock().onBlockPlacedBy(world, pos, block, player, stack);

        return true;
    }

    public static boolean removeBlock(World world, PlayerEntity player, BlockState block, BlockPos pos) {
        BlockState currentBlock = world.getBlockState(pos);

        if(world.isBlockModifiable(player, pos) &&
                (player.isCreative() ||
                        (currentBlock.getBlockHardness(world, pos) > -1 && world.getTileEntity(pos) == null &&
                                ReplacementRegistry.matchBlocks(currentBlock.getBlock(), block.getBlock())))) {

            BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, pos, currentBlock, player);
            MinecraftForge.EVENT_BUS.post(breakEvent);
            if(breakEvent.isCanceled()) return false;

            world.removeBlock(pos, false);
            return true;
        }
        return false;
    }

    public static int countItem(PlayerEntity player, Item item) {
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

    public static boolean matchBlocks(WandOptions options, Block b1, Block b2) {
        switch(options.match.get()) {
            case EXACT:
                return b1 == b2;
            case SIMILAR:
                return ReplacementRegistry.matchBlocks(b1, b2);
            case ANY:
                return b1 != Blocks.AIR && b2 != Blocks.AIR;
        }
        return false;
    }

    public static boolean isPositionPlaceable(World world, PlayerEntity player, BlockPos pos,
                                              BlockRayTraceResult rayTraceResult, WandOptions options) {
        // Is position out of world?
        if(!world.isBlockPresent(pos)) return false;

        // Is block modifiable?
        if(!world.isBlockModifiable(player, pos)) return false;

        // If replace mode is off, target has to be air
        if(!options.replace.get() && !world.isAirBlock(pos)) return false;

        // Limit placement range
        if(ConfigServer.MAX_RANGE.get() > 0 && WandUtil.maxRange(rayTraceResult.getPos(), pos) > ConfigServer.MAX_RANGE.get())
            return false;

        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    public static PlaceSnapshot getPlaceSnapshot(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                                 BlockPos pos, BlockItem item,
                                                 BlockState supportingBlock, WandOptions options) {
        // Is block at pos replaceable?
        BlockItemUseContext ctx = new WandItemUseContext(world, player, rayTraceResult, pos, item);
        if(!ctx.canPlace()) return null;

        // Can block be placed?
        BlockState blockState = item.getBlock().getStateForPlacement(ctx);
        if(blockState == null) return null;

        // Forbidden Tile Entity?
        if(!isTEAllowed(blockState)) return null;

        // No entities colliding?
        VoxelShape shape = blockState.getCollisionShape(world, pos);
        if(!shape.isEmpty()) {
            AxisAlignedBB blockBB = shape.getBoundingBox().offset(pos);
            if(!world.getEntitiesWithinAABB(LivingEntity.class, blockBB, EntityPredicates.NOT_SPECTATING).isEmpty())
                return null;
        }

        // Adjust blockstate to neighbors
        blockState = Block.getValidBlockForPosition(blockState, world, pos);
        if(blockState.getBlock() == Blocks.AIR || !blockState.isValidPosition(world, pos)) return null;

        // Copy block properties from supporting block
        if(options.direction.get() == WandOptions.DIRECTION.TARGET) {
            // Block properties to be copied (alignment/rotation properties)

            for(Property property : new Property[]{
                    BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.FACING, BlockStateProperties.FACING_EXCEPT_UP,
                    BlockStateProperties.ROTATION_0_15, BlockStateProperties.AXIS, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE}) {
                if(supportingBlock.hasProperty(property) && blockState.hasProperty(property)) {
                    blockState = blockState.with(property, supportingBlock.get(property));
                }
            }

            // Dont dupe double slabs
            if(supportingBlock.hasProperty(BlockStateProperties.SLAB_TYPE) && blockState.hasProperty(BlockStateProperties.SLAB_TYPE)) {
                SlabType slabType = supportingBlock.get(BlockStateProperties.SLAB_TYPE);
                if(slabType != SlabType.DOUBLE) blockState = blockState.with(BlockStateProperties.SLAB_TYPE, slabType);
            }
        }

        return new PlaceSnapshot(blockState, pos, item);
    }

    public static Direction fromVector(Vector3d vector) {
        return Direction.getFacingFromVector(vector.x, vector.y, vector.z);
    }
}
