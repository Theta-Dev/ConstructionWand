package thetadev.constructionwand.wand.undo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
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

    public static PlaceSnapshot get(Level world, Player player, BlockHitResult rayTraceResult,
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
    public boolean execute(Level world, Player player, BlockHitResult rayTraceResult) {
        // Recalculate PlaceBlockState, because other blocks might be placed nearby
        // Not doing this may cause game crashes (StackOverflowException) when placing lots of blocks
        // with changing orientation like panes, iron bars or redstone.
        block = getPlaceBlockstate(world, player, rayTraceResult, pos, item, supportingBlock, targetMode);
        if(block == null) return false;
        return WandUtil.placeBlock(world, player, block, pos, item);
    }

    @Override
    public boolean canRestore(Level world, Player player) {
        return true;
    }

    @Override
    public boolean restore(Level world, Player player) {
        return WandUtil.removeBlock(world, player, block, pos);
    }

    @Override
    public void forceRestore(Level world) {
        world.removeBlock(pos, false);
    }

    /**
     * Tests if a certain block can be placed by the wand.
     * If it can, returns the blockstate to be placed.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    private static BlockState getPlaceBlockstate(Level world, Player player, BlockHitResult rayTraceResult,
                                                 BlockPos pos, BlockItem item,
                                                 @Nullable BlockState supportingBlock, boolean targetMode) {
        // Is block at pos replaceable?
        BlockPlaceContext ctx = new WandItemUseContext(world, player, rayTraceResult, pos, item);
        if(!ctx.canPlace()) return null;

        // Can block be placed?
        BlockState blockState = item.getBlock().getStateForPlacement(ctx);
        if(blockState == null || !blockState.canSurvive(world, pos)) return null;

        // Forbidden Tile Entity?
        if(!WandUtil.isTEAllowed(blockState)) return null;

        // No entities colliding?
        if(WandUtil.entitiesCollidingWithBlock(world, blockState, pos)) return null;

        // Copy block properties from supporting block
        if(targetMode && supportingBlock != null) {
            // Block properties to be copied (alignment/rotation properties)

            for(Property property : new Property[]{
                    BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.FACING, BlockStateProperties.FACING_HOPPER,
                    BlockStateProperties.ROTATION_16, BlockStateProperties.AXIS, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE}) {
                if(supportingBlock.hasProperty(property) && blockState.hasProperty(property)) {
                    blockState = blockState.setValue(property, supportingBlock.getValue(property));
                }
            }

            // Dont dupe double slabs
            if(supportingBlock.hasProperty(BlockStateProperties.SLAB_TYPE) && blockState.hasProperty(BlockStateProperties.SLAB_TYPE)) {
                SlabType slabType = supportingBlock.getValue(BlockStateProperties.SLAB_TYPE);
                if(slabType != SlabType.DOUBLE)
                    blockState = blockState.setValue(BlockStateProperties.SLAB_TYPE, slabType);
            }
        }
        return blockState;
    }
}
