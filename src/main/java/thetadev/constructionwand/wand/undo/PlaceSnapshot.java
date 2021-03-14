package thetadev.constructionwand.wand.undo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.wand.WandItemUseContext;

import javax.annotation.Nullable;

public class PlaceSnapshot implements ISnapshot
{
    private BlockState block;
    private final BlockPos pos;
    private final BlockItem item;
    private final BlockState supportingBlock;
    private final boolean targetMode;

    public PlaceSnapshot(BlockState block, BlockPos pos, BlockItem item, BlockState supportingBlock, boolean targetMode) {
        this.block = block;
        this.pos = pos;
        this.item = item;
        this.supportingBlock = supportingBlock;
        this.targetMode = targetMode;
    }

    public static PlaceSnapshot get(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                    BlockPos pos, BlockItem item,
                                    @Nullable BlockState supportingBlock, @Nullable WandOptions options) {
        boolean targetMode = options != null && supportingBlock != null && options.direction.get() == WandOptions.DIRECTION.TARGET;
        BlockState blockState = getPlaceBlockstate(world, player, rayTraceResult, pos, item, supportingBlock, targetMode);
        if(blockState == null) return null;

        return new PlaceSnapshot(blockState, pos, item, supportingBlock, targetMode);
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public BlockState getBlockState() {
        return block;
    }

    @Override
    public ItemStack getRequiredItems() {
        return new ItemStack(item);
    }

    @Override
    public boolean execute(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult) {
        // Recalculate PlaceBlockState, because other blocks might be placed nearby
        // Not doing this may cause game crashes (StackOverflowException) when placing lots of blocks
        // with changing orientation like panes, iron bars or redstone.
        block = getPlaceBlockstate(world, player, rayTraceResult, pos, item, supportingBlock, targetMode);
        if(block == null) return false;
        return WandUtil.placeBlock(world, player, block, pos, item);
    }

    @Override
    public boolean canRestore(World world, PlayerEntity player) {
        return true;
    }

    @Override
    public boolean restore(World world, PlayerEntity player) {
        return WandUtil.removeBlock(world, player, block, pos);
    }

    @Override
    public void forceRestore(World world) {
        world.removeBlock(pos, false);
    }

    /**
     * Tests if a certain block can be placed by the wand.
     * If it can, returns the blockstate to be placed.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    private static BlockState getPlaceBlockstate(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                                 BlockPos pos, BlockItem item,
                                                 @Nullable BlockState supportingBlock, boolean targetMode) {
        // Is block at pos replaceable?
        BlockItemUseContext ctx = new WandItemUseContext(world, player, rayTraceResult, pos, item);
        if(!ctx.canPlace()) return null;

        // Can block be placed?
        BlockState blockState = item.getBlock().getStateForPlacement(ctx);
        if(blockState == null) return null;

        // Forbidden Tile Entity?
        if(!WandUtil.isTEAllowed(blockState)) return null;

        // No entities colliding?
        if(WandUtil.entitiesCollidingWithBlock(world, blockState, pos)) return null;

        // Adjust blockstate to neighbors
        blockState = Block.getValidBlockForPosition(blockState, world, pos);
        if(blockState.getBlock() == Blocks.AIR || !blockState.isValidPosition(world, pos)) return null;

        // Copy block properties from supporting block
        if(targetMode && supportingBlock != null) {
            // Block properties to be copied (alignment/rotation properties)

            for(Property property : new Property[]{
                    BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.FACING, BlockStateProperties.FACING_EXCEPT_UP,
                    BlockStateProperties.ROTATION_0_15, BlockStateProperties.AXIS, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE}) {
                if(supportingBlock.has(property) && blockState.has(property)) {
                    blockState = blockState.with(property, supportingBlock.get(property));
                }
            }

            // Dont dupe double slabs
            if(supportingBlock.has(BlockStateProperties.SLAB_TYPE) && blockState.has(BlockStateProperties.SLAB_TYPE)) {
                SlabType slabType = supportingBlock.get(BlockStateProperties.SLAB_TYPE);
                if(slabType != SlabType.DOUBLE) blockState = blockState.with(BlockStateProperties.SLAB_TYPE, slabType);
            }
        }
        return blockState;
    }
}
