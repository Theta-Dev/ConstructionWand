package thetadev.constructionwand.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.items.ModItems;
import thetadev.constructionwand.wand.WandJob;
import thetadev.constructionwand.wand.supplier.SupplierInventory;
import thetadev.constructionwand.wand.undo.ISnapshot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BlockConjured extends AbstractGlassBlock
{
    private static final AbstractBlock.IExtendedPositionPredicate<EntityType<?>> NO_SPAWN = (state, world, pos, et) -> false;
    private static final AbstractBlock.IPositionPredicate NOT_SOLID = (state, world, pos) -> false;

    public BlockConjured(String name) {
        super(AbstractBlock.Properties.create(Material.SNOW).hardnessAndResistance(0.2F)
                .sound(SoundType.CLOTH).notSolid().setAllowsSpawn(NO_SPAWN)
                .setOpaque(NOT_SOLID).setSuffocates(NOT_SOLID).setBlocksVision(NOT_SOLID));
        setRegistryName(ConstructionWand.MODID, name);
    }

    @Override
    public boolean isReplaceable(@Nonnull BlockState state, @Nonnull BlockItemUseContext useContext) {
        return useContext.getItem().getItem() != ModBlocks.CONJURED_BLOCK.asItem();
    }

    @Override
    public void harvestBlock(@Nonnull World worldIn, @Nonnull PlayerEntity player, @Nonnull BlockPos pos,
                             @Nonnull BlockState state, @Nullable TileEntity te, @Nonnull ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);

        if(player.isSneaking()) {
            HashSet<BlockPos> adjacentBlocks = getAdjacentBlocks(worldIn, pos, 64);
            adjacentBlocks.forEach(blockPos -> WandUtil.removeBlock(worldIn, player, null, blockPos));
        }
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos,
                                             @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        ItemStack handStack = player.getHeldItem(handIn);

        if(!(handStack.getItem() instanceof BlockItem))
            return super.onBlockActivated(state, world, pos, player, handIn, hit);
        BlockItem blockItem = (BlockItem) handStack.getItem();
        if(blockItem.getBlock() == ModBlocks.CONJURED_BLOCK)
            return super.onBlockActivated(state, world, pos, player, handIn, hit);

        WandJob job = new WandJob(player, world, hit, new ItemStack(ModItems.WAND_INFINITY));
        job.getPlaceSnapshots(new ActionConjuredBlocks(job), new SupplierInventory(job), blockItem);
        return job.doIt() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
    }

    public static HashSet<BlockPos> getAdjacentBlocks(World world, BlockPos pos, int maxBlocks) {
        HashSet<BlockPos> blockPositions = new HashSet<>();
        LinkedList<BlockPos> candidates = new LinkedList<>();
        HashSet<BlockPos> allCandidates = new HashSet<>();
        candidates.add(pos);

        while(!candidates.isEmpty() && blockPositions.size() < maxBlocks) {
            BlockPos currentPos = candidates.removeFirst();
            allCandidates.add(currentPos);

            if(world.getBlockState(currentPos).getBlock() == ModBlocks.CONJURED_BLOCK || currentPos.equals(pos)) {
                blockPositions.add(currentPos);

                for(Direction dir : Direction.values()) {
                    BlockPos nPos = currentPos.offset(dir);
                    if(allCandidates.add(nPos)) candidates.add(nPos);
                }
            }
        }

        return blockPositions;
    }

    private static class ActionConjuredBlocks implements IWandAction
    {
        private final World world;
        private final BlockRayTraceResult rayTraceResult;

        public ActionConjuredBlocks(WandJob wandJob) {
            world = wandJob.world;
            rayTraceResult = wandJob.rayTraceResult;
        }

        @Override
        public List<ISnapshot> getSnapshots(IWandSupplier supplier) {
            HashSet<BlockPos> adjacentBlocks = BlockConjured.getAdjacentBlocks(world, rayTraceResult.getPos(), supplier.getMaxBlocks());

            return adjacentBlocks.stream().map(blockPos -> supplier.getPlaceSnapshot(blockPos, null))
                    .filter(Objects::nonNull).collect(Collectors.toList());
        }
    }
}
