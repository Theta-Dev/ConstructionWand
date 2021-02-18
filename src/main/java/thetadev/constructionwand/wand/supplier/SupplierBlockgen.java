package thetadev.constructionwand.wand.supplier;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.wand.WandJob;
import thetadev.constructionwand.wand.undo.BlockgenSnapshot;
import thetadev.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nullable;

public abstract class SupplierBlockgen implements IWandSupplier
{
    private final PlayerEntity player;
    private final World world;
    private final BlockRayTraceResult rayTraceResult;
    private final WandOptions options;
    private final int wandLimit;

    public SupplierBlockgen(WandJob job) {
        player = job.player;
        world = job.world;
        rayTraceResult = job.rayTraceResult;
        options = job.options;
        wandLimit = job.wandItem.getLimit(player, job.wand);
    }

    @Override
    public void getSupply(@Nullable BlockItem target) {
    }

    @Override
    public int getMaxBlocks() {
        return wandLimit;
    }

    @Nullable
    @Override
    public PlaceSnapshot getPlaceSnapshot(BlockPos pos, @Nullable BlockState supportingBlock) {
        if(!WandUtil.isPositionPlaceable(world, player, pos, rayTraceResult, options)) return null;

        return BlockgenSnapshot.get(world, player, rayTraceResult, pos, getBlockItem(), supportingBlock, options);
    }

    @Override
    public int takeItemStack(ItemStack stack) {
        return 0;
    }

    protected abstract BlockItem getBlockItem();
}
